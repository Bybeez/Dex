package com.felkertech.n.dex.ui;

/**
 * Created by N on 12/22/2014.
 */
import android.content.Context;
import android.support.wearable.view.GridPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felkertech.dexc.data.ParsedCsv;
import com.felkertech.dexc.data.Pokemon;
import com.felkertech.n.dex.R;

import java.util.ArrayList;
public class MainRecycler extends GridPagerAdapter {
    private String TAG = "CardAdapter";
    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<Pokemon> p;
    private ParsedCsv pokemon_species;
    private MainRecyclerInterface sb;
    public MainRecycler(Context c, ArrayList<Pokemon> p, ParsedCsv pokemon_species, MainRecyclerInterface sb) {
        mContext = c;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.p = p;
        this.pokemon_species = pokemon_species;
        this.sb = sb;
    }

    @Override
    public int getRowCount() {
        return p.size();
    }

    @Override
    public int getColumnCount(int i) {
        return 1;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position, int col) {
        LinearLayout convertView = (LinearLayout) mInflator.inflate(R.layout.list_card_3, null, false);

        ((TextView) convertView.findViewById(R.id.species_id)).setText("#"+p.get(position).species_id);
        ((TextView) convertView.findViewById(R.id.identifier)).setText(p.get(position).getFormName());
        ((TextView) convertView.findViewById(R.id.height)).setText(p.get(position).getHeightFt());
        ((TextView) convertView.findViewById(R.id.weight)).setText(p.get(position).getWeightLbs());
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
        ImageButton s = (ImageButton) convertView.findViewById(R.id.search_button);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.onSearchClick();
            }
        });
        convertView.findViewById(R.id.more_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.onInfoClick();
            }
        });
        viewGroup.addView(convertView);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int row, int col, Object view) {
        container.removeView((View)view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
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
    public interface MainRecyclerInterface {
        public void onSearchClick();
        public void onInfoClick();
    }
}
