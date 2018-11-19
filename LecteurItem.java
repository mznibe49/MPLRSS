package com.example.simoz.mplrss;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LecteurItem extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String lien;
    AccessDonnees access_donnees;
    SimpleCursorAdapter adapter;
    LoaderManager manager;
    WebView webview;

    public final static String authority = "fr.simo.bdprojet";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecteur_item);
        access_donnees = new AccessDonnees(this);
        //webview = (WebView) findViewById(R.id.webview);
        Intent intent = getIntent();
        this.lien = intent.getStringExtra("lien");

        Cursor cursor = getItems(this.lien);

        String [] nom_colonne = {"titre","description"}; // on suppose que qu'on on clicque sur la zone on vas sur le lien qui vas avec...

        int [] layout = {android.R.id.text1,android.R.id.text2};

        adapter= new SimpleCursorAdapter(this,
                android.R.layout.two_line_list_item,
                cursor,
                nom_colonne,
                layout);

        setListAdapter(adapter);
        manager = getLoaderManager(); //manager = getLoaderManager();
        manager.initLoader(0, null, this);
    }

    public Cursor getItems(String lien){
        Cursor c = access_donnees.getItems(lien);
        return c;
    }


    private void allerVersPageWeb(String adresse){
        //webview.setWebViewClient(new WebViewClient());
        //webview.loadUrl(adresse);
        /*Uri uri = Uri.parse(adresse);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);*/
        Intent intent = new Intent(this,WebViewActivity.class);
        intent.putExtra("adresse",adresse);
        startActivity(intent);
    }

    protected void onListItemClick(ListView l, View v, int position, long id){
        Cursor c = (Cursor) getListAdapter().getItem(position); // pour recuperer l'element selectionner
        String adresse = c.getString(c.getColumnIndex("adresse"));
        allerVersPageWeb(adresse);
        manager.restartLoader(0,null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("itemRss").appendPath(lien);
        Uri uri = builder.build();
        return new CursorLoader(this, uri, new String[]{"_id","nom"},
                "lien = ?", new String []{lien}, null);
        //return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data); // les données prêtes, associer le Cursor
        adapter.notifyDataSetChanged(); // avec adapter pour faire afficher
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        adapter.notifyDataSetChanged();
    }
}
