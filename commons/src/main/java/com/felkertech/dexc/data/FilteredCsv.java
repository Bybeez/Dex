package com.felkertech.dexc.data;

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
    private String TAG = "dex:FilteredCsv";
    public FilteredCsv(InputStream inputStream, int column, int conjunction, String object) {
        new FilteredCsv(inputStream, new CsvFilter[]{new CsvFilter(column, conjunction, object)}, true, false);
    }
    public FilteredCsv(InputStream inputStream, int column, int conjunction, String object, boolean log) {
        new FilteredCsv(inputStream, new CsvFilter[]{new CsvFilter(column, conjunction, object)}, true, log);
    }
    public FilteredCsv(InputStream inputStream, CsvFilter[] filters, boolean matchAll) {
        new FilteredCsv(inputStream, filters, true, false);
    }
    public FilteredCsv(InputStream inputStream, CsvFilter[] filters, boolean matchAll, boolean log) {
        if(log) {
            Log.d(TAG, "Start filtering");
            for(CsvFilter c: filters) {
                Log.d(TAG, c.getColumn()+", "+c.getConjunction()+", "+c.getObject());
            }
        }
        int i = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] RowData = line.split(",");
                boolean l = log && RowData[0].equals("1");
                if(i == 0) { //Need to capture fieldnames
                    CsvRow newRow = new CsvRow();
                    for(String row: RowData) {
                        newRow.add(row);
                    }
                    mtx.add(newRow);
                    fieldNames = newRow;
                    i++;
                } else {
                    boolean filterMatch = true;
                    if(l) {
                        Log.d(TAG, "[" + RowData[0] + "," + RowData[1] + "," + RowData[2] + "]");
                        Log.d(TAG, "'"+RowData[filters[0].getColumn()]+"', '"+filters[0].getObject()+"' "
                                +RowData[filters[0].getColumn()].equals(filters[0].getObject())+"  "
                                +filters[0].getConjunction()+", "+CsvFilter.EQUALS+"; "+(filters[0].getConjunction() == CsvFilter.EQUALS));
                    }
                    for(CsvFilter f: filters) {
                        if(f.getConjunction() == CsvFilter.CONTAINS && RowData[f.getColumn()].contains(f.getObject())) {
//                            Log.d(TAG, i+" "+filterMatch+", "+matchAll);
                            filterMatch = (matchAll)?filterMatch:true;
                        } else if((f.getConjunction() == CsvFilter.EQUALS) && RowData[f.getColumn()].equals(f.getObject())) {
                            if(l) {
                                Log.d(TAG, i+" "+filterMatch+", "+matchAll+"  ["+RowData[0]+","+RowData[1]+","+RowData[2]+"] ");
                            }
                            filterMatch = (matchAll)?filterMatch:true;
                        } else {
                            filterMatch = (matchAll)?false:filterMatch;
                        }
                    }
                    if(filterMatch) {
                        CsvRow newRow = new CsvRow();
                        for (String row : RowData) {
                            newRow.add(row);
                        }
                        mtx.add(newRow);
                        i++;
                    }
                }
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
        }
    }
}
