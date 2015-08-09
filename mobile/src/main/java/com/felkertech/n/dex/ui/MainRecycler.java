package com.felkertech.n.dex.ui;

/**
 * Created by N on 12/22/2014.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felkertech.n.dex.R;
import com.felkertech.n.dex.data.ParsedCsv;
import com.felkertech.n.dex.data.Pokemon;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.net.MalformedURLException;
import java.util.*;
public class MainRecycler extends RecyclerView.Adapter<MainRecycler.ViewHolder> {
    private String TAG = "CardAdapter";
    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<Pokemon> p;
    private ParsedCsv pokemon_species;
    private PokeCardInterface pci;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mTextView;
        public ViewHolder(LinearLayout v) {
            super(v);
            mTextView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainRecycler(Context c, ArrayList<Pokemon> p, ParsedCsv pokemon_species, PokeCardInterface pokeCardInterface) {
        mContext = c;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.p = p;
        this.pokemon_species = pokemon_species;
        pci = pokeCardInterface;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainRecycler.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        LinearLayout convertView = (LinearLayout) mInflator.inflate(R.layout.list_card_3, parent, false);
        ViewHolder vh = new ViewHolder(convertView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View convertView = holder.itemView;
        ((TextView) convertView.findViewById(R.id.species_id)).setText("#"+p.get(position).species_id);
        ((TextView) convertView.findViewById(R.id.identifier)).setText(p.get(position).getFormName());
        ((TextView) convertView.findViewById(R.id.height)).setText(p.get(position).getHeightFt());
        ((TextView) convertView.findViewById(R.id.weight)).setText(p.get(position).getWeightLbs());
//        ((TextView) convertView.findViewById(R.id.base_xp)).setText(p.get(position).base_xp+"xp");
        if(p.get(position).self_types.size() == 1)
        {
            ((TextView) convertView.findViewById(R.id.type1)).setText(p.get(position).self_types.get(0)+"");
            int c = getColorFromType(p.get(position).self_types.get(0));
            ((TextView) convertView.findViewById(R.id.type1)).setTextColor(c);
            ((TextView) convertView.findViewById(R.id.type2)).setText(" ");
        }
        else {
            ((TextView) convertView.findViewById(R.id.type1)).setText(p.get(position).self_types.get(0)+"");
            ((TextView) convertView.findViewById(R.id.type2)).setText(" "+p.get(position).self_types.get(1));
            int c = getColorFromType(p.get(position).self_types.get(0));
            ((TextView) convertView.findViewById(R.id.type1)).setTextColor(c);
            int c2 = getColorFromType(p.get(position).self_types.get(1));
            ((TextView) convertView.findViewById(R.id.type2)).setTextColor(c2);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pci.onCardClick(position);
            }
        });

        try {
//            ONLINE::
                    Ion.with(((ImageView) convertView.findViewById(R.id.sprite)))
                    .animateGif(AnimateGifMode.ANIMATE)
                            .animateGif(AnimateGifMode.ANIMATE)

                    .load(p.get(position).getModelURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return p.size();
    }
    public int getColorFromType(String type) {
        int c;
        if(type.equals("grass"))
            c = R.color.grass;
        else if(type.equals("poison"))
            c = R.color.poison;
        else if(type.equals("fire"))
            c = R.color.fire;
        else if(type.equals("water"))
            c = R.color.water;
        else if(type.equals("bug"))
            c = R.color.bug;
        else if(type.equals("flying"))
            c = R.color.flying;
        else if(type.equals("electric"))
            c = R.color.electric;
        else if(type.equals("ground"))
            c = R.color.ground;
        else if(type.equals("fighting"))
            c = R.color.fighting;
        else if(type.equals("rock"))
            c = R.color.rock;
        else if(type.equals("steel"))
            c = R.color.steel;
        else if(type.equals("dark"))
            c = R.color.dark;
        else if(type.equals("fairy"))
            c = R.color.fairy;
        else if(type.equals("dragon"))
            c = R.color.dragon;
        else if(type.equals("psychic"))
            c = R.color.psychic;
        else if(type.equals("ghost"))
            c = R.color.ghost;
        else if(type.equals("ice"))
            c = R.color.ice;
        else
            c = R.color.normal;
        return mContext.getResources().getColor(c);
    }
    public interface PokeCardInterface {
        public void onCardClick(int position);
    }
/*    public LinearLayout getCard(int index) {
        return views.get(index);
    }*/
}