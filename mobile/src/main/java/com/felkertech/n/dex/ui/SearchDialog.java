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
    View customView;
    public SearchDialog(Context c) {
        super(new MaterialDialog.Builder(c)
                .title("Search")
                .customView(R.layout.search, false));
    }
    public void setSearchInterface(SearchInterface si) {
        this.si = si;
    }
    @Override
    public void onCreate(Bundle saved) {
        setTitle("Search");
        customView = getCustomView();
        Log.d("dex::Searchdialog", customView.findViewById(R.id.voice_search).toString());
        customView.findViewById(R.id.voice_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                si.voiceSearch();
            }
        });
        customView.findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = ((EditText) customView.findViewById(R.id.text_search)).getText().toString();
                si.submitText(search);
            }
        });
    }
    public interface SearchInterface {
        public void voiceSearch();
        public void submitText(String t);
    }
}
