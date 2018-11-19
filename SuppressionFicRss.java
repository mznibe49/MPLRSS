package com.example.simoz.mplrss;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SuppressionFicRss extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    AccessDonnees access_donnee;
    SimpleCursorAdapter adapter;
    LoaderManager manager;
    private static final String authority = "fr.simo.bdprojet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppression_fic_rss);

        //list = (ListView)findViewById(R.id.list);
        access_donnee = new AccessDonnees(this);
        Cursor cursor = access_donnee.getTableFile();

        adapter= new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{"titre"},
                new int[]{android.R.id.text1});

        setListAdapter(adapter);

        manager = getLoaderManager(); //manager = getLoaderManager();
        manager.initLoader(0, null, this);
    }

    protected void onListItemClick(ListView l, View v, int position, long id){
        Cursor c = (Cursor) getListAdapter().getItem(position); // pour recuperer l'element selectionner
        int n = access_donnee.delete(c);
        if(n == 0){
            String msg = "Aucun Fic Rss";
            Toast toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT);
            toast.show();
        } else {
            String msg = "le Fic Rss supprimé est  : "+c.getString(c.getColumnIndex("titre"));
            Toast toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT);
            toast.show();
        }
        manager.restartLoader(0,null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority)
                .appendPath("fic_rss").build();
        return new CursorLoader(this, uri, new String[]{"_id", "nom"},
                null, null, null);
        //return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data); // les données prêtes, associer le Cursor
        adapter.notifyDataSetChanged(); // avec adapter pour faire afficher
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
        adapter.notifyDataSetChanged();
    }
}
