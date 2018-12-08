package com.example.simoz.mplrss;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

public class Favoris extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    AccessDonnees access_donnees;
    SimpleCursorAdapter adapter;
    LoaderManager manager;
    String favoris = "1";
    public final static String authority = "fr.simo.bdprojet";
    SwipeMenuListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        lv = (SwipeMenuListView) findViewById(R.id.listFav);

        this.access_donnees = new AccessDonnees(this);
        Cursor cursor = access_donnees.getFavItem();

        String [] nom_colonne = {"titre"}; // on suppose que qu'on on clicque sur la zone on vas sur le lien qui vas avec...
        int [] layout = {android.R.id.text1};
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                cursor,
                nom_colonne,
                layout);
        lv.setAdapter(adapter);
        manager = getLoaderManager(); //manager = getLoaderManager();
        manager.initLoader(0, null, this);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
                openItem.setWidth(170);
                openItem.setTitle("Open");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem removeFavoris = new SwipeMenuItem(getApplicationContext());
                removeFavoris.setBackground(new ColorDrawable(Color.rgb(0x41,0x69, 0xE1)));
                removeFavoris.setWidth(170);
                removeFavoris.setIcon(R.drawable.ic_removefav);
                menu.addMenuItem(removeFavoris);

            }
        };

        lv.setMenuCreator(creator);

        lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                Cursor c = (Cursor) Favoris.this.adapter.getItem(position); // pour recuperer l'element selectionner
                String adresse = c.getString(c.getColumnIndex("adresse"));
                int favoris = Favoris.this.access_donnees.getItemFavoris(adresse);

                switch (index) {
                    case 0:
                        // open
                        Intent i = new Intent(Favoris.this, DescPage.class);
                        i.putExtra("adresse", adresse);
                        startActivity(i);
                        break;
                    case 1:
                        // remove from favs
                        Favoris.this.access_donnees.changeToFav(adresse,0);
                        Toast.makeText(Favoris.this, "Cet article n'est plus dans les favoris", Toast.LENGTH_LONG).show();
                        manager.restartLoader(0,null, Favoris.this);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

    }

    protected void onListItemClick(ListView l, View v, int position, long id){
        Cursor c = (Cursor) this.adapter.getItem(position); // pour recuperer l'element selectionner
        String titre = c.getString(c.getColumnIndex("titre"));
        Toast.makeText(this, titre/*"go to  : " + " " + lv.getItemAtPosition(position)*/ , Toast.LENGTH_LONG).show();
        manager.restartLoader(0,null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("itemRss").appendPath(favoris);
        Uri uri = builder.build();
        return new CursorLoader(this, uri, new String[]{"_id","nom"},
                "favoris = ?", new String[] {favoris}, null);
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
