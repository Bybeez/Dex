package com.felkertech.n.dex.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felkertech.dexc.data.Evolution;
import com.felkertech.dexc.data.Move;
import com.felkertech.dexc.data.ParsedCsv;
import com.felkertech.dexc.data.Pokemon;
import com.felkertech.n.dex.R;

/**
 * Created by N on 12/29/2014.
 */
public class PokemonDialog extends AlertDialog {
    Pokemon p;
    Context mContext;
    ParsedCsv pokemon_species;
    public static final String TAG = "PokemonDialog";

    public PokemonDialog(Context context, Pokemon pokemon, ParsedCsv pokemon_species) {
        super(context);
        p = pokemon;
        mContext = context;
        this.pokemon_species = pokemon_species;
    }

    @Override
    public void onCreate(Bundle saved) {
        LinearLayout cv = (LinearLayout) getLayoutInflater().inflate(R.layout.list_card, null, false);
        //TODO LayoutStuff
        //TODO Export class
        prepare(cv);
        setView(cv);
        setContentView(cv);
    }

    public void prepare(View convertView) {
        setTitle(p.species_name);
        ((TextView) convertView.findViewById(R.id.species_id)).setText("#" + p.species_id);
        ((TextView) convertView.findViewById(R.id.identifier)).setText(p.getFormName());
        ((TextView) convertView.findViewById(R.id.height)).setText(p.getHeightFt());
        ((TextView) convertView.findViewById(R.id.weight)).setText(p.getWeightLbs());
        if (p.self_types.size() == 1) {
            ((TextView) convertView.findViewById(R.id.type1)).setText(p.self_types.get(0) + "");
            int c = getColorFromType(p.self_types.get(0));
            ((TextView) convertView.findViewById(R.id.type1)).setTextColor(c);
            ((TextView) convertView.findViewById(R.id.type2)).setText(" ");
        } else {
            ((TextView) convertView.findViewById(R.id.type1)).setText(p.self_types.get(0) + "");
            ((TextView) convertView.findViewById(R.id.type2)).setText(" " + p.self_types.get(1));
            int c = getColorFromType(p.self_types.get(0));
            ((TextView) convertView.findViewById(R.id.type1)).setTextColor(c);
            int c2 = getColorFromType(p.self_types.get(1));
            ((TextView) convertView.findViewById(R.id.type2)).setTextColor(c2);
        }
        TextView abilityTextView = ((TextView) convertView.findViewById(R.id.abilities));
        abilityTextView.setText("");
        for(String ability: p.ability) {
            abilityTextView.setText(abilityTextView.getText()+"\n"+ability.substring(0,1).toUpperCase()+ability.substring(1));
        }
        TextView eggGroupTextView = ((TextView) convertView.findViewById(R.id.egg_groups));
        eggGroupTextView.setText("");
        for(String egg_group: p.egggroup) {
            eggGroupTextView.setText(eggGroupTextView.getText()+"\n"+egg_group.substring(0,1).toUpperCase()+egg_group.substring(1));
        }
        String ev = "";
        for (Evolution e : p.evolutions) {
//            Log.d(TAG, e.toString());
            String pre = "";
            if(e.getPrevo_id() > -1)
                pre = pokemon_species.find("id", e.getPrevo_id() + "").getProperty("identifier");
            pre = pre.substring(0,1).toUpperCase()+pre.substring(1);

            String evo = pokemon_species.find("id", e.getEvo_id() + "").getProperty("identifier");
            evo = evo.substring(0,1).toUpperCase()+evo.substring(1);
            if (e.getMethod().isEmpty())
                ev = ev + evo;
            else
                ev = ev + pre + " (" + e.getMethod() + ") " + evo;
            ev = ev+"\n";
            /*Log.d(TAG, pre+" -> "+e.getMethod()+" -> "+evo);
            Log.d(TAG, ev);*/
        }

        if (p.evolutions.size() == 1)
            convertView.findViewById(R.id.evolutions).setVisibility(View.GONE);
        ((TextView) convertView.findViewById(R.id.evolutions)).setText(ev); //FIXME interpret

        //TODO Color
        ((TextView) convertView.findViewById(R.id.hp)).setText(p.hp + "");
        ((TextView) convertView.findViewById(R.id.atk)).setText(p.atk + "");
        ((TextView) convertView.findViewById(R.id.def)).setText(p.def + "");
        ((TextView) convertView.findViewById(R.id.spa)).setText(p.spa + "");
        ((TextView) convertView.findViewById(R.id.spd)).setText(p.spd + "");
        ((TextView) convertView.findViewById(R.id.spe)).setText(p.spe + "");

        //Pokedex Entry is for mobile only
        ((TextView) convertView.findViewById(R.id.pokedex_entry)).setText(p.pokedex_entry);
        ((TextView) convertView.findViewById(R.id.genus)).setText(p.genus+" Pkmn");

        convertView.findViewById(R.id.move_list).setVisibility(View.GONE);
        TextView move_list = (TextView) convertView.findViewById(R.id.move_list);
        move_list.setText("");
        for(Move m: p.self_moves) {
            move_list.setText(move_list.getText()+"\n"+m.getName()+" - "+m.getMethod());
        }
    }

    public int getColorFromType(String type) {
        int c;
        if (type.equals("grass"))
            c = R.color.grass;
        else if (type.equals("poison"))
            c = R.color.poison;
        else if (type.equals("fire"))
            c = R.color.fire;
        else if (type.equals("water"))
            c = R.color.water;
        else if (type.equals("bug"))
            c = R.color.bug;
        else if (type.equals("flying"))
            c = R.color.flying;
        else if (type.equals("electric"))
            c = R.color.electric;
        else if (type.equals("ground"))
            c = R.color.ground;
        else if (type.equals("fighting"))
            c = R.color.fighting;
        else if (type.equals("rock"))
            c = R.color.rock;
        else if (type.equals("steel"))
            c = R.color.steel;
        else if (type.equals("dark"))
            c = R.color.dark;
        else if (type.equals("fairy"))
            c = R.color.fairy;
        else if (type.equals("dragon"))
            c = R.color.dragon;
        else if (type.equals("psychic"))
            c = R.color.psychic;
        else if (type.equals("ghost"))
            c = R.color.ghost;
        else if (type.equals("ice"))
            c = R.color.ice;
        else
            c = R.color.normal;
        return mContext.getResources().getColor(c);
    }
}