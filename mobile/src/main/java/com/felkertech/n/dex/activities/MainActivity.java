package com.felkertech.n.dex.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.felkertech.dexc.data.CsvFilter;
import com.felkertech.dexc.data.FilteredCsv;
import com.felkertech.dexc.data.ParsedCsv;
import com.felkertech.dexc.data.PokedexEntryCsv;
import com.felkertech.dexc.data.Pokemon;
import com.felkertech.n.dex.R;
import com.felkertech.n.dex.ui.MainRecycler;
import com.felkertech.n.dex.ui.PokemonDialog;
import com.felkertech.n.dex.ui.SearchDialog;
import com.felkertech.n.ui.AboutAppDialogFragment;
import com.felkertech.n.utils.AppUtils;
import com.felkertech.n.utils.ApplicationSettings;
import com.felkertech.n.utils.SettingsManager;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "dex::main";
    private RecyclerView mRecyclerView;
    public static final int SPEECH_REQUEST_CODE = 314;
    ArrayList<Pokemon> pokelist = new ArrayList<Pokemon>();
    ParsedCsv pokemon_abilities;
    ParsedCsv abilities;
    ParsedCsv pokemon_egg_groups;
    ParsedCsv egg_groups;
    ParsedCsv pokemon_evolution;
    ParsedCsv pokemon_species;
    ParsedCsv item_names;
    ParsedCsv pokemon_types;
    ParsedCsv types;
    ParsedCsv pokemon_stats;
    ParsedCsv move_names;
    ParsedCsv location_names;
    ParsedCsv pokemon;
    ParsedCsv pokemon_species_names;
    ParsedCsv pokemon_moves;
    PokedexEntryCsv pokemon_species_flavor_text;
    TextToSpeech tts;
    boolean openDialogs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            InputStream is = getAssets().open("pokemon.csv");
            pokemon = new ParsedCsv(is);
            /*Pokemon b = new Pokemon(pokemon.find("identifier", "bulbasaur"), new ParsedCsv(getAssets().open("pokemon_abilities.csv")),
                    new ParsedCsv(getAssets().open("abilities.csv")), new ParsedCsv(getAssets().open("pokemon_egg_groups.csv")),
                    new ParsedCsv(getAssets().open("egg_groups.csv")), new ParsedCsv(getAssets().open("pokemon_evolution.csv")),
                    new ParsedCsv(getAssets().open("pokemon_species.csv")), new ParsedCsv(getAssets().open("item_names.csv")),
                    new ParsedCsv(getAssets().open("pokemon_types.csv")), new ParsedCsv(getAssets().open("types.csv")),
                    new ParsedCsv(getAssets().open("pokemon_stats.csv")));
            Log.d(TAG, b.ability.get(0));*/
//            Log.d(TAG, pokemon.find("identifier", "bulbasaur").getProperty("species_id"));
            pokemon_types = new ParsedCsv(getAssets().open("pokemon_types.csv"));
            types = new ParsedCsv(getAssets().open("types.csv"));
            //TODO Pokemon.csv - order
            int i = 0;
            for(ParsedCsv.CsvRow pkmn: pokemon.getAll()) {
                i++;
                if(i > 784) //FIXME Hack
                    continue;
                ParsedCsv.CsvRow nextItem = pokemon.find("order", ""+i);
//                Log.d(TAG, nextItem.al().toString());
                try {
                    Pokemon next = new Pokemon(nextItem, pokemon_types, types);
                    pokelist.add(next);
                } catch(Exception e) {
                    Log.d(TAG, i+"");
                    e.printStackTrace();
                    Log.d(TAG, i+", "+ nextItem.al().toString());
                }

//                Log.d(TAG, i+next.species_name);
            }
            configureRecycler();
            /* for(int i = 1; i <= 721; i++) {
                Pokemon next = new Pokemon(pokemon.find("id", ""+i), pokemon_types, types);
                       *//* Pokemon next = new Pokemon(pokemon.find("id", ""+i), pokemon_abilities, abilities,
                                pokemon_egg_groups, egg_groups, pokemon_evolution, pokemon_species, item_names,
                                pokemon_types, types, pokemon_stats, move_names, location_names);
                       *//* *//*int sprite = getApplication().getResources().getIdentifier(next.getModel(), "drawable", getApplication().getPackageName());
                        next.sprite = sprite;*//*
                pokelist.add(next);
                        *//*if(i == 1) {
                            Log.d(TAG, next.getModel());
//                            Log.d(TAG, getResId(next.getModel(), Drawable.class)+"");
                            Log.d(TAG, getApplication().getResources().getIdentifier("bulbasaur", "drawable", getApplication().getPackageName())+"");

                        }*//*
                configureRecycler();
            }*/
                /*}
            });
            t.start();*/
            findViewById(R.id.loading).setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.d("TTS", status+"");
            }
        });

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Threading");
                    parseSecondaryCSVs();
                    Log.d(TAG, "Threading done");
                    openDialogs = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSearch();
            }
        });
        fab.attachToRecyclerView(mRecyclerView);

        if(AppUtils.isTV(this)) {
            fab.setVisibility(View.GONE);
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        index = 0;
        onKeyDown(-1, null);
    }

    private int index = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.d(TAG, keyCode + " was pressed");
        if(mRecyclerView.findViewHolderForAdapterPosition(index) != null) {
            mRecyclerView.findViewHolderForAdapterPosition(index).itemView
                    .findViewById(R.id.parent)
                    .setBackgroundColor(getResources().getColor(R.color.white));
        }
        switch(keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                index = Math.min(index+2, 783);
                mRecyclerView.scrollToPosition((index==779)?index:index+4);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                index = Math.max(index-2, 0);
                mRecyclerView.scrollToPosition((index==5)?index:index-4);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(index%2 == 0 && AppUtils.isTV(this)) {
                    //Overflow
                    String[] options = new String[]{"Search", "Settings"};
                    new MaterialDialog.Builder(MainActivity.this)
                            .title("Overflow")
                            .items(options)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                    switch(i) {
                                        case 0: //Search
//                                            findViewById(R.id.fab).performClick();
                                            launchSearch();
                                            break;
                                        case 1: //Settings
                                            Intent i2 = new Intent(MainActivity.this, ApplicationSettings.class);
                                            startActivity(i2);
                                            break;
                                    }
                                }
                            }).show();
                } else {
                    index = Math.max(index - 1, 0);
                    mRecyclerView.scrollToPosition((index == 5) ? index : index - 4);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                index = Math.min(index+1, 783);
                mRecyclerView.scrollToPosition((index==779)?index:index+4);
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_A:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                mRecyclerView.findViewHolderForAdapterPosition(index).itemView.performClick();
                break;
        }
        getSupportActionBar().hide();
        Log.d(TAG, "Index " + index);
        Log.d(TAG, "For "+mRecyclerView.getAdapter().getItemCount());
        RecyclerView.ViewHolder pokemonCard = mRecyclerView.findViewHolderForAdapterPosition(index);
        if(pokemonCard != null) {
            pokemonCard.itemView
                    .findViewById(R.id.parent)
                    .setBackgroundColor(getResources().getColor(R.color.selected));
        } else {
            Log.d(TAG, "View is null");
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.about:
                new AboutAppDialogFragment().show(getFragmentManager(), "AADF");
                break;
            case R.id.settings:
                Intent i2 = new Intent(this, ApplicationSettings.class);
                startActivity(i2);
                break;
            /*case R.id.ab_search:
                launchSearch();
                break;*/
        }

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
    public void launchSearch() {
        final SearchDialog ad = new SearchDialog(MainActivity.this);
        SearchDialog.SearchInterface searchInterface = new SearchDialog.SearchInterface() {
            @Override
            public void voiceSearch() {
                displaySpeechRecognizer();
                //TODO Close this, open up card
                ad.dismiss();
            }

            @Override
            public void submitText(String t) {
                Log.d(TAG, "submit "+t);
                searchFor(t);
                ad.dismiss();
            }
        };
        ad.setSearchInterface(searchInterface);
        ad.show();
        ad.getCustomView().findViewById(R.id.text_search).requestFocus();
    }

    public void configureRecycler() {
        //Configure RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if(mRecyclerView != null) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, (int) (Math.floor(dpWidth / 720) + 1)));
            mRecyclerView.setAdapter(new MainRecycler(getApplicationContext(), pokelist, pokemon_species, new MainRecycler.PokeCardInterface() {
                @Override
                public void onCardClick(int p) {
                    if (!openDialogs)
                        return;

//                    Toast.makeText(MainActivity.this, "Opening...", Toast.LENGTH_SHORT).show();

                    //If secondary CSVs aren't parsed, do it now. Then, update the Pokelist with this pokemon.
                    Log.d(TAG, "Open Pokemon " + pokelist.get(p).pokemon_id + "");
                    if (pokemon_abilities == null) {
                        try {
                            Log.d(TAG, "Null abilities");
                            parseSecondaryCSVs();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    /*
                        I want to reset as many of these sheets as I can to save memory
                     */
                    try {
                        pokemon_moves = new FilteredCsv(getAssets().open("pokemon_moves.csv"),
                                new CsvFilter[]{new CsvFilter(0, CsvFilter.EQUALS, pokelist.get(p).pokemon_id),
                                        new CsvFilter(1, CsvFilter.EQUALS, "15")}, true, false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Pokemon pTemp = null;
                    Log.d(TAG, pokemon_abilities.rowCount() + " abilities");
                    Log.d(TAG, pokemon.find("id", pokelist.get(p).pokemon_id).al().toString());
                    pTemp = new Pokemon(pokemon.find("id", pokelist.get(p).pokemon_id), pokemon_abilities,
                            abilities, pokemon_egg_groups, egg_groups, pokemon_evolution, pokemon_species, item_names,
                            pokemon_types, types, pokemon_stats, move_names, location_names,
                            pokemon_species_flavor_text, pokemon_species_names, pokemon_moves);
//                        Log.d(TAG, p+""+pTemp.species_name);
                    pokelist.set(p, pTemp);

                    PokemonDialog pd = new PokemonDialog(MainActivity.this, pokelist.get(p), pokemon_species);
                    try {
                        Log.d(TAG, pokelist.get(p).getModelURL());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    pd.show();
                    String narration = pTemp.getSpecies_name() + ", the " + pTemp.genus + " pokemon." + pTemp.pokedex_entry;
                    if(new SettingsManager(getApplicationContext()).getBoolean(R.string.sm_sound)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            tts.speak(narration, TextToSpeech.QUEUE_FLUSH,
                                    null, null);
                        } else {
                            tts.speak(narration, TextToSpeech.QUEUE_FLUSH,
                                    null);
                        }
                    }
                }
            }));
            Handler h = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(AppUtils.isTV(MainActivity.this)) {
                        onKeyDown(-1, null);
                    }
                }
            };
            h.sendEmptyMessageDelayed(0, 500);
        }
    }
    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
    //TODO Filters
    public void searchFor(String search) {
        search = search.toLowerCase();
        //Log.d(TAG, search);
        int i = 0;
        for(Pokemon p: pokelist) {
            if(p.species_name.toLowerCase().contains(search)) {
                Log.d(TAG, "Scroll to "+i+" for "+p.species_name);
                try {
                    Log.d(TAG, p.getModelURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                final int finalI = i;
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.scrollToPosition(finalI);
                        index = finalI;
                        Log.d(TAG, "Now scroll");
                        Handler h = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                //TODO Open right now, but later only when there's one
                                RecyclerView.ViewHolder firstSearchResult = mRecyclerView.findViewHolderForAdapterPosition(finalI);
                                if(firstSearchResult != null)
                                    firstSearchResult.itemView.performClick();
                                onKeyDown(-1, null);
                            }
                        };
                        h.sendEmptyMessageDelayed(0, 300);
                    }
                };

                Handler delayScroll = new Handler(Looper.getMainLooper());
                delayScroll.postDelayed(r, 1000);
                return;
            }
            i++;
        }
    }
    //TODO Settings: Voice, Metric/English
    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            searchFor(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * This will parse the values of various CSV files
     */
    public void parseSecondaryCSVs() throws IOException {
        //Only store English values; the rest are not needed
        pokemon_abilities = new ParsedCsv(getAssets().open("pokemon_abilities.csv"));
//        abilities = new FilteredCsv(getAssets().open("abilities.csv"), 3, CsvFilter.EQUALS, "1");
        abilities = new ParsedCsv(getAssets().open("abilities.csv"));
        pokemon_egg_groups = new ParsedCsv(getAssets().open("pokemon_egg_groups.csv"));
        egg_groups = new ParsedCsv(getAssets().open("egg_groups.csv"));
        pokemon_evolution = new ParsedCsv(getAssets().open("pokemon_evolution.csv"));
        pokemon_species = new ParsedCsv(getAssets().open("pokemon_species.csv"));
        item_names = new FilteredCsv(getAssets().open("item_names.csv"), new CsvFilter[]{new CsvFilter(1, CsvFilter.EQUALS, "9")}, true, false);
        pokemon_stats = new ParsedCsv(getAssets().open("pokemon_stats.csv"));
        move_names = new FilteredCsv(getAssets().open("move_names.csv"), new CsvFilter[]{new CsvFilter(1, CsvFilter.EQUALS, "9")}, true, false);
        location_names = new FilteredCsv(getAssets().open("location_names.csv"), new CsvFilter[]{new CsvFilter(1, CsvFilter.EQUALS, "9")}, true, false);
        pokemon_species_flavor_text = new PokedexEntryCsv(getAssets().open("pokemon_species_flavor_text.csv"));
        pokemon_species_names = new ParsedCsv(getAssets().open("pokemon_species_names.csv"));
    }
}
