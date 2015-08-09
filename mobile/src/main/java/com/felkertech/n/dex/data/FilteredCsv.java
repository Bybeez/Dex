package com.felkertech.n.dex.data;

import android.inputmethodservice.Keyboard;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Nick on 8/9/2015.
 * Unlike a regular ParsedCsv, this class will only whitelist particular rows, saving you much memory
 */
public class FilteredCsv extends ParsedCsv {
    public static int CONTAINS = 3;
    public static int EQUALS = 5;
    public FilteredCsv(InputStream inputStream, int column, int conjunction, String object) {
        int i = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] RowData = line.split(",");
                if(conjunction == CONTAINS && RowData[column].contains(object)) {
                    mtx.add(new CsvRow());
                    for(String row: RowData) {
                        mtx.get(i).add(row);
                    }
                    i++;
                } else if(conjunction == EQUALS && RowData[column].equals(object)) {
                    mtx.add(new CsvRow());
                    Log.d("dex:FCSV", "New item found "+RowData[2]);
                    for(String row: RowData) {
                        mtx.get(i).add(row);
                    }
                    i++;
                }/* else if(RowData[column].equals("1")){
                    Log.d("dex:FCSV", RowData[column]+"="+object+", "+column);
                }*/
            }
        }
        catch (IOException ex) {
            // handle exception
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                // handle exception
            }
            fieldNames = mtx.get(0);
        }
    }
}
