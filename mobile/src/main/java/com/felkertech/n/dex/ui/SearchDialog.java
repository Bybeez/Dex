package com.felkertech.n.dex.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.felkertech.n.dex.R;

/**
 * Created by N on 12/29/2014.
 */
public class SearchDialog extends MaterialDialog {
    private SearchInterface si;
    public SearchDialog(Context c) {
        super(new MaterialDialog.Builder(c));
//        this.si = si;
    }
    public void setSearchInterface(SearchInterface si) {
        this.si = si;
    }
    @Override
    public void onCreate(Bundle saved) {
        setTitle("Search");
        //TODO True voice button
        final LinearLayout cv = (LinearLayout) getLayoutInflater().inflate(R.layout.search, null, false);
//        setView(cv);
        setContentView(cv);
        Log.d("dex::Searchdialog", cv.findViewById(R.id.voice_search).toString());
        cv.findViewById(R.id.voice_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                si.voiceSearch();
            }
        });
        cv.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = ((EditText) cv.findViewById(R.id.text_search)).getText().toString();
                si.submitText(search);
            }
        });
    }
    public interface SearchInterface {
        public void voiceSearch();
        public void submitText(String t);
    }
}
