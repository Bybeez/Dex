package com.felkertech.dexc.data;

import android.util.Log;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by N on 12/22/2014.
 */
public class Pokemon {
    public static final String TAG = "dex:Pokemon";
    //Make sure you include all of the Csv files
    public int species_id;
    public String species_name;
    public String pokemon_id;
    public float height;
    public float weight;
    public int base_xp;
    public ArrayList<String> ability = new ArrayList<String>();
    public ArrayList<String> egggroup = new ArrayList<String>();
    public ArrayList<Evolution> evolutions = new ArrayList<Evolution>();
    public ArrayList<String> self_types = new ArrayList<String>();
    public String pokedex_entry;
    public String gender_rate;
    public String capture_rate;
    public String hatch_counter;
    public String has_gender_differences;
    public String growth_rate_id;
    public String forms_switchable;
    public String genus;
    public ArrayList<Move> self_moves = new ArrayList<Move>();

    public int hp;
    public int atk;
    public int def;
    public int spa;
    public int spd;
    public int spe;
    public int sprite;

    public Pokemon(ParsedCsv.CsvRow pokemon, ParsedCsv pokemon_types, ParsedCsv types) {
        species_id = Integer.parseInt(pokemon.getProperty("species_id"));
        species_name = pokemon.getProperty("identifier");
        pokemon_id = pokemon.getProperty("id");
        height = Integer.parseInt(pokemon.getProperty("height"))/10f;
        weight = Integer.parseInt(pokemon.getProperty("weight"))/10f;
        base_xp = Integer.parseInt(pokemon.getProperty("base_experience"));

        //Types
        for(ParsedCsv.CsvRow r : pokemon_types.findAll("pokemon_id", ""+pokemon_id)) {
            String type = r.getProperty("type_id");
            String type_name = types.find("id", type).getProperty("identifier");
            self_types.add(type_name);
        }
    }
    public Pokemon(ParsedCsv.CsvRow pokemon, ParsedCsv pokemon_abilities, ParsedCsv abilities,
                   ParsedCsv pokemon_egg_groups, ParsedCsv egg_groups,
                   ParsedCsv pokemon_evolution,
                   ParsedCsv pokemon_species,
                   ParsedCsv item_names,
                   ParsedCsv pokemon_types, ParsedCsv types,
                   ParsedCsv pokemon_stats,
                   ParsedCsv move_names, ParsedCsv location_names,
                   PokedexEntryCsv pokemon_species_flavor_text,
                   ParsedCsv pokemon_species_names, ParsedCsv pokemon_moves) {
        species_id = Integer.parseInt(pokemon.getProperty("species_id"));
        species_name = pokemon.getProperty("identifier");
        pokemon_id = pokemon.getProperty("id");
        height = Integer.parseInt(pokemon.getProperty("height"))/10f;
        weight = Integer.parseInt(pokemon.getProperty("weight"))/10f;
        base_xp = Integer.parseInt(pokemon.getProperty("base_experience"));

//        Log.d(TAG, "Found "+pokemon_abilities.findAll("pokemon_id", ""+pokemon_id).size()+" abilities for "+pokemon_id);
        for(ParsedCsv.CsvRow r: pokemon_abilities.findAll("pokemon_id", ""+pokemon_id)) {
            Log.d(TAG, "< "+r.getProperty("ability_id"));
            Log.d(TAG, abilities.fieldNames.toString());
            Log.d(TAG, abilities.find("id", r.getProperty("ability_id")).size()+" items");
            String t_ability = abilities.find("id", r.getProperty("ability_id")).getProperty("identifier");
            t_ability = t_ability.replaceAll("-", " ");
            ability.add(t_ability);
        }

        for(ParsedCsv.CsvRow r: pokemon_egg_groups.findAll("species_id", ""+species_id)) {
            String t_egg = egg_groups.find("id", r.getProperty("egg_group_id")).getProperty("identifier");
            if(t_egg.equals("no-eggs"))
                t_egg = "Can't Breed";
            egggroup.add(t_egg);
        }

        //Evolution
        ParsedCsv.CsvRow speciesRow = pokemon_species.find("id", species_id+"");
//        Log.d(TAG, species_name+" species==null is "+(speciesRow==null));
        int evolution_chain_id = Integer.parseInt(speciesRow.getProperty("evolution_chain_id"));
        //TODO Better evolution support
        //TODO Megas inline
        for(ParsedCsv.CsvRow r: pokemon_species.findAll("evolution_chain_id", ""+evolution_chain_id)) {
            //Get species_id
            int species_id2 = Integer.parseInt(r.getProperty("id"));
            int species_id3 = -1;
            try {
                species_id3 = Integer.parseInt(r.getProperty("evolves_from_species_id"));
            } catch(Exception e) { //This pokemon is in base form.
            }
            //Now look that up in the list
            ParsedCsv.CsvRow evolutionRow = pokemon_evolution.find("evolved_species_id", "" + species_id2);
            if(evolutionRow != null) {
                //Add to a list
                String how = "";

                String level = evolutionRow.getProperty("minimum_level");
                String gender = evolutionRow.getProperty("gender_id");
                String location = evolutionRow.getProperty("location_id");
                String held_item = evolutionRow.getProperty("held_item_id");
                String time_of_day = evolutionRow.getProperty("time_of_day");
                String known_move = evolutionRow.getProperty("known_move_id");
                String known_move_type = evolutionRow.getProperty("known_move_type_id");
                String minimum_happiness = evolutionRow.getProperty("minimum_happiness");
                String minimum_beauty = evolutionRow.getProperty("minimum_beauty");
                String minimum_affection = evolutionRow.getProperty("minimum_affection");
                String relative_physical_stats = evolutionRow.getProperty("relative_physical_stats");
                String party_species_id = evolutionRow.getProperty("party_species_id");
                String party_type_id = evolutionRow.getProperty("party_type_id");
                String trade_species_id = evolutionRow.getProperty("trade_species_id");
                String needs_overworld_rain = evolutionRow.getProperty("needs_overworld_rain");
                String turn_upside_down = evolutionRow.getProperty("turn_upside_down");


                int evolution_trigger_id = Integer.parseInt(evolutionRow.getProperty("evolution_trigger_id"));
                switch(evolution_trigger_id) {
                    case 1:
                        if(level.isEmpty() && !minimum_happiness.isEmpty() && time_of_day.isEmpty()) {
                            how = "Happiness";
                        } else if(!relative_physical_stats.isEmpty()) {
                            if(relative_physical_stats.equals("1"))
                                how = "Level "+level+" & Atk > Def";
                            else if(relative_physical_stats.equals("-1"))
                                how = "Level "+level+" & Atk < Def";
                            else if(relative_physical_stats.equals("0"))
                                how = "Level "+level+" & Atk = Def";
                        } else if(!held_item.isEmpty() && !time_of_day.isEmpty()) {
                            String held_item_name = "";
                            Log.d(TAG, "Evolve w/item");
                            for(ParsedCsv.CsvRow items: item_names.findAll("item_id", held_item)) {
                                Log.d(TAG, "I"+items.getProperty("name"));
                                if(items.getProperty("local_language_id").equals("9")) {
                                    held_item_name = items.getProperty("name");
                                }
                            }
                            how = "Hold "+held_item_name+" in the "+time_of_day;
                        } else if(!known_move.isEmpty()) {
                            for(ParsedCsv.CsvRow moves: move_names.findAll("move_id", known_move)) {
                                if(moves.getProperty("local_language_id").equals("9")) {
                                    how = "Learn "+moves.getProperty("name");
                                }
                            }
                        } else if(!time_of_day.isEmpty() && !minimum_happiness.isEmpty()) {
                            how = "Happiness during the "+time_of_day;
                        } else if(!party_species_id.isEmpty()) {
                            String partner = pokemon_species.find("id", party_species_id).getProperty("identifier");
                            how = "Have "+partner+" in party";
                        } else if(!minimum_beauty.isEmpty()) {
                            how = "Improve beauty";
                        } else if(!level.isEmpty() && !gender.isEmpty()) {
                            if(gender.equals("1")) //F
                                how = "Level "+level+" and female";
                            else if(gender.equals("2")) //M
                                how = "Level "+level+" and male";
                        } else if(!location.isEmpty()) {
                            String location_name = "";
                            for(ParsedCsv.CsvRow areas: location_names.findAll("location_id", location)) {
                                if(areas.getProperty("local_language_id").equals("9")) {
                                    location_name = areas.getProperty("name");
                                }
                            }
                            how = "Level up at "+location_name;
                        } else if(!level.isEmpty() && !party_type_id.isEmpty()) {
                            String t = types.find("id", party_type_id).getProperty("identifier");
                            how = "Level "+level+" & "+t+" type in party";
                        } else if(!level.isEmpty() && !time_of_day.isEmpty()) {
                            how = "Level "+level+" during "+time_of_day;
                        } else if(!level.isEmpty() && needs_overworld_rain.equals("1")) {
                            how = "Level "+level+" in the rain";
                        } else if(!level.isEmpty() && turn_upside_down.equals("1")) {
                            how = "Level "+level+" while upside down";
                        } else if(!minimum_affection.isEmpty() && !known_move_type.isEmpty()) {
                            String t = types.find("id", known_move_type).getProperty("identifier");
                            how = "Know a "+t+" move with a lot of affection";
                        } else if(!level.isEmpty()) {
                            how = "Level " + level;
                        }
                        break;
                    case 2:
                        how = "Trade";
                        if(!held_item.isEmpty()) {
                            String held_item_name = "";
                            for(ParsedCsv.CsvRow items: item_names.findAll("item_id", held_item)) {
                               // Log.d(TAG, items.getProperty("name")+" "+items.getProperty("local_language_id"));
                                if(items.getProperty("local_language_id").equals("9")) {
                                    held_item_name = items.getProperty("name");
                                    //Log.d(TAG, "Save "+held_item_name);
                                }
                            }
//                            Log.d(TAG, "restore "+held_item_name);
                            how = "Trade with "+held_item_name;
                        } else if(!trade_species_id.isEmpty()) {
//                            Log.d(TAG, species_id+" "+trade_species_id)
                            String partner = pokemon_species.find("id", trade_species_id).getProperty("identifier");
                            how = "Trade for a "+partner;
                        }
                        break;
                    case 3:
                        //Item
                        int item_id = Integer.parseInt(evolutionRow.getProperty("trigger_item_id"));
                        String item_name = "";
                        Log.d(TAG, "Evolve w/item");
                        for (ParsedCsv.CsvRow items : item_names.findAll("item_id", item_id + "")) {
                            Log.d(TAG, items.toString());
                            if (items.getProperty("local_language_id").equals("9")) {
                                item_name = items.getProperty("name");
                            }
                        }
                        if(!gender.isEmpty()) {
                            if(gender.equals("1")) //F
                                how = item_name+" and female";
                            else if(gender.equals("2")) //M
                                how = item_name+" and male";
                        } else {
                            how = item_name;
                        }
                        break;
                    case 4:
                        //Shed
                        how = "Shed";
                        break;
                }
                evolutions.add(new Evolution(species_id2, species_id3, how));
            } else {
//                evolutions.add(0, new Evolution(species_id2, ""));
            }
        }

        //Types
        for(ParsedCsv.CsvRow r : pokemon_types.findAll("pokemon_id", ""+pokemon_id)) {
            String type = r.getProperty("type_id");
            String type_name = types.find("id", type).getProperty("identifier");
            self_types.add(type_name);
        }

        //Stats
        for(ParsedCsv.CsvRow r: pokemon_stats.findAll("pokemon_id", ""+pokemon_id)) {
            int base = Integer.parseInt(r.getProperty("base_stat"));
            switch(Integer.parseInt(r.getProperty("stat_id"))) {
                case 1:
                    hp = base;
                    break;
                case 2:
                    atk = base;
                    break;
                case 3:
                    def = base;
                    break;
                case 4:
                    spa = base;
                    break;
                case 5:
                    spd = base;
                    break;
                case 6:
                    spe = base;
                    break;
            }
        }
        gender_rate = speciesRow.getProperty("gender_rate");
        capture_rate = speciesRow.getProperty("capture_rate");
        hatch_counter = speciesRow.getProperty("hatch_counter");
        has_gender_differences = speciesRow.getProperty("has_gender_differences");
        growth_rate_id = speciesRow.getProperty("growth_rate_id");
        forms_switchable = speciesRow.getProperty("forms_switchable");

//        Log.d(TAG, pokemon_species_names.fieldNames.al().toString());
//        Log.d(TAG, pokemon_species_names.getFieldId("pokemon_species_id")+"");
//        Log.d(TAG, ""+species_id);
        for(ParsedCsv.CsvRow entry: pokemon_species_names.findAll("pokemon_species_id", species_id+"")) {
//            Log.d(TAG, entry.al().size()+"");
//            Log.d(TAG, entry.getProperty("genus"));
            if(entry.al().size() != 4)
                continue;
            if(entry.getProperty("local_language_id").equals("9")) {
                genus = entry.getProperty("genus");
            }
        }

        //Pokedex Entry
        //TODO refactor back to Wear
        /*Log.d(TAG, pokemon_species_flavor_text.fieldNames.al().toString());
        Log.d(TAG, pokemon_species_flavor_text.getFieldId("﻿species_id")+"");
        Log.d(TAG, ""+species_id);
        */for(ParsedCsv.CsvRow entries: pokemon_species_flavor_text.findAll("﻿species_id", species_id+"")) {
//            Log.d(TAG, entries.al().toString());
            if(entries.al().size() != 4)
                continue;
            if(entries.getProperty("language_id").equals("9") /*&& entries.getProperty("version_id").equals("24")*/) {
                pokedex_entry = entries.getProperty("flavor_text");
                pokedex_entry = pokedex_entry.replaceAll("\"", "");
//                Log.d(TAG, pokedex_entry);
            }
        }
        Log.d(TAG, "Has "+pokemon_moves.rowCount()+" attacks");
//        Log.d(TAG, pokemon_moves.getRow(0).toString());
//        Log.d(TAG, pokemon_moves.getRow(1).toString());
//        Log.d(TAG, pokemon_moves.fieldNames+"");
//        Log.d(TAG, pokemon_moves.getFieldId("pokemon_id")+"");
//        Log.d(TAG, pokemon_moves.getRow(0).getProperty("pokemon_id")+"");
//        Log.d(TAG, pokemon_moves.getRow(0).getProperty("version_group_id")+"");
//        Log.d(TAG, pokemon_moves.findAll("pokemon_id", pokemon_id+"").size()+" moves found for "+pokemon_id);
        for(ParsedCsv.CsvRow moves: pokemon_moves.findAll("pokemon_id", pokemon_id+"")) {
//            Log.d(TAG, pokemon_moves.findAll("version_group_id", "15").size()+" version moves found");
            if(moves.getProperty("version_group_id").equals("15")) {
                String move_id = moves.getProperty("move_id");
                Log.d(TAG, move_id+" <<");
                String move_name = "";
//                Log.d(TAG, "Find name: "+move_names.findAll("move_id", move_id).size()+"/"+move_names.rowCount()+" items");
                for(ParsedCsv.CsvRow entries: move_names.findAll("move_id", move_id)) {
                    move_name = entries.getProperty("name");
                    Log.d(TAG, move_name);
                    /*if(entries.getProperty("local_language_id").equals("9")) {
                        // *//**//*&& entries.getProperty("version_id").equals("24")*//**//*
                    }*/
                }
                String move_method_id = moves.getProperty("pokemon_move_method_id");
                String level = moves.getProperty("level");
                Move m;
                switch (Integer.parseInt(move_method_id)) {
                    case 1: //level-up
                        m = new Move(move_name, move_id, "Level "+level);
                        break;
                    case 2: //Egg
                        m = new Move(move_name, move_id, "Egg Move");
                        break;
                    case 3: //Tutor
                        m = new Move(move_name, move_id, "Move Tutor");
                        break;
                    case 4: //TM
                        m = new Move(move_name, move_id, "TM/HM");
                        break;
                    case 10: //Form change
                        m = new Move(move_name, move_id, "Form Change");
                        break;
                    default:
                        m = new Move(move_name, move_id, "Other method");
                        break;
                }
                self_moves.add(m);
            }
        }
        //TODO Cries
    }
    public String getModelURL() throws MalformedURLException {
        String nameValidated = species_name.toLowerCase();
        nameValidated = nameValidated.replaceAll("mega (\\w*) ", "$1-mega");
        nameValidated = nameValidated.replaceAll("mega (\\w*)", "$1-mega");
        nameValidated = nameValidated.replaceAll("\\s", "-");
        if(!nameValidated.contains("nido") && !nameValidated.contains("mega"))
//            nameValidated = nameValidated.replaceAll("-(\\w*)", "");
              doNothing();
        else if(nameValidated.contains("nido"))
            nameValidated = nameValidated.replaceAll("-", "_");
        nameValidated = nameValidated.replaceAll("-([xy])", "$1");

        //Defaults
        nameValidated = nameValidated.replaceAll("-average", "").replaceAll("-normal", "").replaceAll("-shield", "").replaceAll("-male", "")
                .replaceAll("-female", "-f").replaceAll("-aria", "").replaceAll("-ordinary", "").replaceAll("-incarnate", "").replaceAll("-standard", "")
                .replaceAll("-red-striped", "").replaceAll("-blue-striped", "-blue").replaceAll("-land", "").replaceAll("-altered", "")
                .replaceAll("-plant", "");

        return "http://www.pkparaiso.com/imagenes/xy/sprites/animados/"+nameValidated+".gif";
    }
    public String getModel() {
        String nameValidated = species_name.toLowerCase();
        nameValidated = nameValidated.replaceAll("mega (\\w*) ", "$1_mega");
        nameValidated = nameValidated.replaceAll("mega (\\w*)", "$1_mega");
        nameValidated = nameValidated.replaceAll("\\s", "_");
        nameValidated = nameValidated.replaceAll("-", "_");
        return nameValidated;
    }

    public String getSpecies_name() {
        String t_species_name = species_name;
        t_species_name = t_species_name.substring(0,1).toUpperCase()+t_species_name.substring(1);
        t_species_name = t_species_name.replaceAll("(\\w*)-mega","Mega $1");
        t_species_name = t_species_name.replaceAll("-(\\w*)", "");
        return t_species_name;
    }
    public String getFormName() {
        String t_species_name = species_name;
        t_species_name = t_species_name.substring(0,1).toUpperCase()+t_species_name.substring(1);
        t_species_name = t_species_name.replaceAll("(\\w*)-mega","Mega $1");
        t_species_name = t_species_name.replaceAll("-(\\w*)", " ($1 Form)");
        return t_species_name;
    }
    private void doNothing() {}

    /**
     * Converts kg to lbs
     * @return weight in pounds
     */
    public String getWeightLbs() {
        return Math.round(weight * 2.20462)+"lbs";
    }

    /**
     * Converts m to ft and inches
     * @return height in English units
     */
    public String getHeightFt() {
        long ft = (long) Math.floor(height * 3.28048);
        double heightLeft = height - (ft/3.28048);
        long in = (long) Math.floor(heightLeft * 3.28048 * 12);
        return ft+"' "+in+"\"";
    }
    //TODO Abilities lose dash
    //TODO Return to - modifiers in a true way
    //TODO Egg groups to lose dash, empty if no-eggs
}