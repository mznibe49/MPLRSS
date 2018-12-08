package com.example.simoz.mplrss;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class LecteurBase extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
// pour lire les fichier dans la base apres faudra lire les items de chaque fichier

    AccessDonnees access_donnee;
    SimpleCursorAdapter adapter;
    LoaderManager manager;
    public final static String authority = "fr.simo.bdprojet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecteur_base);

        access_donnee = new AccessDonnees(this);
        Cursor cursor = access_donnee.getTableFile(); // pour la table de mplrss

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
        //String str = access_donnee.getLinkFromFile(c);
        String str = c.getString(c.getColumnIndex("lien"));
        Intent intent = new Intent(this,LecteurItem.class);
        intent.putExtra("lien",str);
        startActivity(intent);
        manager.restartLoader(0,null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority)
                .appendPath("fic_rss").build();
        return new CursorLoader(this, uri, new String[]{"_id","nom"},
                null, null, null);
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
