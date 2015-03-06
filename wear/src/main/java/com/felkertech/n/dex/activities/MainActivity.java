package com.felkertech.n.dex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.wearable.view.GridViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felkertech.n.dex.R;
import com.felkertech.n.dex.data.Evolution;
import com.felkertech.n.dex.data.ParsedCsv;
import com.felkertech.n.dex.data.PokedexEntryCsv;
import com.felkertech.n.dex.data.Pokemon;
import com.felkertech.n.dex.ui.MainRecycler;
import com.felkertech.n.dex.ui.PokemonDialog;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    public static final String TAG = "Main";
    private GridViewPager mRecyclerView;
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

    boolean openDialogs = false;

    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            InputStream is = getAssets().open("pokemon.csv");
            pokemon = new ParsedCsv(is);
            pokemon_types = new ParsedCsv(getAssets().open("pokemon_types.csv"));
            types = new ParsedCsv(getAssets().open("types.csv"));
            //TODO Pokemon.csv - order
            int i = 0;
            for(ParsedCsv.CsvRow pkmn: pokemon.getAll()) {
                i++;
                if(i > 784) //Hack
                    continue;
                ParsedCsv.CsvRow nextItem = pokemon.find("order", ""+i);
                try {
                    Pokemon next = new Pokemon(nextItem, pokemon_types, types);
                    pokelist.add(next);
                } catch(Exception e) {
                    Log.d(TAG, i+"");
                    e.printStackTrace();
                    Log.d(TAG, i+", "+ nextItem.al().toString());
                }
            }
            configureRecycler();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    parseSecondaryCSVs();
                    openDialogs = true;
                    Log.d(TAG, "Done parse");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    public void configureRecycler() {
        //Configure RecyclerView
        mRecyclerView = new GridViewPager(this);
        mRecyclerView.setAdapter(new MainRecycler(getApplicationContext(), pokelist, pokemon_species, new MainRecycler.MainRecyclerInterface() {
            @Override
            public void onSearchClick() {
//                searchFor("mew");
                displaySpeechRecognizer();
            }

            @Override
            public void onInfoClick() {
                if(!openDialogs)
                    return;

                int p = mRecyclerView.getCurrentItem().y;
//                Log.d(TAG, mRecyclerView.getCurrentItem().x+" "+mRecyclerView.getCurrentItem().y);

                //If secondary CSVs aren't parsed, do it now. Then, update the Pokelist with this pokemon.
                /*if(pokemon_abilities == null) {
                    try {
                        parseSecondaryCSVs();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
                Pokemon pTemp = null;
                pTemp = new Pokemon(pokemon.find("id", pokelist.get(p).pokemon_id), pokemon_abilities,
                        abilities, pokemon_egg_groups, egg_groups, pokemon_evolution, pokemon_species, item_names,
                        pokemon_types, types, pokemon_stats, move_names, location_names,
                        pokemon_species_flavor_text, pokemon_species_names, pokemon_moves);
                Log.d(TAG, p+""+pTemp.species_name+" "+pTemp.pokedex_entry);
                pokelist.set(p, pTemp);

                PokemonDialog pd = new PokemonDialog(MainActivity.this, pokelist.get(p), pokemon_species);
                pd.show();
            }
        }));
        setContentView(mRecyclerView);
    }

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
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Looks for a certain term and navigates user to that position
     * @param search Text or voice search result
     */
    public void searchFor(String search) {
        search = search.toLowerCase();
        int i = 0;
        for(Pokemon p: pokelist) {
            if(p.species_name.toLowerCase().contains(search)) {
                mRecyclerView.setCurrentItem(i, 1, true);
            }
            i++;
        }
    }
    /**
     * This will parse the values of various CSV files
     */
    public void parseSecondaryCSVs() throws IOException {
        pokemon_abilities = new ParsedCsv(getAssets().open("pokemon_abilities.csv"));
        abilities = new ParsedCsv(getAssets().open("abilities.csv"));
        pokemon_egg_groups = new ParsedCsv(getAssets().open("pokemon_egg_groups.csv"));
        egg_groups = new ParsedCsv(getAssets().open("egg_groups.csv"));
        pokemon_evolution = new ParsedCsv(getAssets().open("pokemon_evolution.csv"));
        pokemon_species = new ParsedCsv(getAssets().open("pokemon_species.csv"));
        item_names = new ParsedCsv(getAssets().open("item_names.csv"));
        pokemon_stats = new ParsedCsv(getAssets().open("pokemon_stats.csv"));
        move_names = new ParsedCsv(getAssets().open("move_names.csv"));
        location_names = new ParsedCsv(getAssets().open("location_names.csv"));
        pokemon_species_flavor_text = new PokedexEntryCsv(getAssets().open("pokemon_species_flavor_text.csv"));
        pokemon_species_names = new ParsedCsv(getAssets().open("pokemon_species_names.csv"));
//        pokemon_moves = new ParsedCsv(getAssets().open("pokemon_moves.csv"));
    }
}