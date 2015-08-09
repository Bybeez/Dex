package com.felkertech.n.dex.data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by N on 12/22/2014.
 */
public class ParsedCsv {
    ArrayList<CsvRow> mtx = new ArrayList<CsvRow>();
    CsvRow fieldNames = new CsvRow();
    public ParsedCsv() {
        //No args constructor for PEC
    }
    public ParsedCsv(InputStream is) {
        int i = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] RowData = line.split(",");
                mtx.add(new CsvRow());
                for(String row: RowData) {
                    mtx.get(i).add(row);
                }
                i++;
            }
        }
        catch (IOException ex) {
            // handle exception
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                // handle exception
            }
            fieldNames = mtx.get(0);
        }
    }
    public CsvRow getRow(int row) {
        return mtx.get(row);
    }
    public CsvRow find(String field, String val) {
        int fid = getFieldId(field);
        if(fid == -1)
            return null;

        for(int i=0;i<rowCount();i++) {
            if(mtx.get(i).get(fid).equals(val)) {
                return mtx.get(i);
            }
        }
        return null;
    }
    public ArrayList<CsvRow> findAll(String field, String val) {
        int fid = getFieldId(field);
        ArrayList<CsvRow> found = new ArrayList<CsvRow>();
        if(fid == -1)
            return found;

        for(int i=0;i<rowCount();i++) {
            if(mtx.get(i).get(fid).equals(val)) {
                found.add(mtx.get(i));
            }
        }
        return found;
    }
    public ArrayList<CsvRow> getAll() {
        return mtx;
    }
    public int indexOf(String field, String val) {
        int fid = getFieldId(field);
        if(fid == -1)
            return -1;

        for(int i=0;i<rowCount();i++) {
            if(mtx.get(i).get(fid).equals(val)) {
                return i;
            }
        }
        return -1;
    }
    public int rowCount() {
        return mtx.size();
    }
    public int colCount() {
        return mtx.get(0).size();
    }
    public int getFieldId(String field) {
        int i = 0;
        for(String f: fieldNames.al()) {
//            Log.d("ParsedCSV", f+" "+field+" "+f.equals(field));
            if(f.equals(field)) {
                return i;
            }
            i++;
        }
        return -1;
    }
    public String getProperty(int row, String field) {
        int fid = getFieldId(field);
        if(fid == -1)
            return "";

        return mtx.get(row).get(fid);
    }
    public class CsvRow {
        private ArrayList<String> data = new ArrayList<String>();
        int rowid;
        public CsvRow() {
            new CsvRow(new ArrayList<String>());
        }
        public CsvRow(ArrayList<String> d) {
            data = d;
            rowid = mtx.size();
        }
        public String getProperty(String field) {
            int fid = getFieldId(field);/*
            Log.d("ParsedCSV", field+" "+fid);*/
            if(fid == -1)
                return "";

            return data.get(fid);
        }
        public void add(String str) {
            data.add(str);
        }
        public String get(int index) {
            return data.get(index);
        }
        public int size() {
            return data.size();
        }
        public ArrayList<String> al() {
            return data;
        }
        public String toString() {
            return data.toString();
        }
    }
}
