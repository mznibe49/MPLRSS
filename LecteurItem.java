package com.example.simoz.mplrss;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class LecteurItem extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String lien;
    AccessDonnees access_donnees;
    SimpleCursorAdapter adapter;
    LoaderManager manager;
    SwipeMenuListView lv;

    public final static String authority = "fr.simo.bdprojet";
    private static final String TAG  = "LecteurItemActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecteur_item);

        lv = (SwipeMenuListView) findViewById(R.id.listView);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        access_donnees = new AccessDonnees(this);
        final Intent intent = getIntent();
        this.lien = intent.getStringExtra("lien");
        //Cursor cursor = access_donnees.getItemsLinkedToRss(this.lien);

        String [] nom_colonne = {"titre"}; // on suppose que qu'on on clicque sur la zone on vas sur le lien qui vas avec...
        int [] layout = {android.R.id.text1};
        adapter= new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                nom_colonne,
                new int[]{android.R.id.text1});

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
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                deleteItem.setWidth(170);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);

                // create "favoris" item
                SwipeMenuItem favItem = new SwipeMenuItem(getApplicationContext());
                favItem.setBackground(new ColorDrawable(Color.rgb(0xFF,0xFF, 0x0)));
                favItem.setWidth(170);
                favItem.setIcon(R.drawable.ic_favoris);
                menu.addMenuItem(favItem);

            }
        };

        lv.setMenuCreator(creator);

        lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                Cursor c = (Cursor) LecteurItem.this.adapter.getItem(position); // pour recuperer l'element selectionner
                String adresse = c.getString(c.getColumnIndex("adresse"));
                int favoris = LecteurItem.this.access_donnees.getItemFavoris(adresse);

                switch (index) {
                    case 0:
                        // open
                        Intent i = new Intent(LecteurItem.this, DescPage.class);
                        i.putExtra("adresse", adresse);
                        i.putExtra("BackPage","l");
                        startActivity(i);
                        break;
                    case 1:
                        // delete
                        LecteurItem.this.access_donnees.deleteIem(adresse);
                        Toast.makeText(LecteurItem.this, "article supprimer", Toast.LENGTH_LONG).show();
                        manager.restartLoader(0,null, LecteurItem.this);
                        break;
                    case 2:
                        //ajouter a la table de favoris
                        if(favoris == 0) {
                            LecteurItem.this.access_donnees.changeToFav(adresse,1);
                            Toast.makeText(LecteurItem.this, "Cet article est ajouté au favoris", Toast.LENGTH_LONG).show();
                        } else {
                            LecteurItem.this.access_donnees.changeToFav(adresse,0);
                            Toast.makeText(LecteurItem.this, "Cet article n'est plus dans les favoris", Toast.LENGTH_LONG).show();
                        }

                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        //MenuItem searchItem= menu.findItem(R.id.search_item);
        /*SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView= (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));*/
      //  return true;
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onListItemClick(ListView l, View v, int position, long id){
        Cursor c = (Cursor) this.adapter.getItem(position); // pour recuperer l'element selectionner
        String adresse = c.getString(c.getColumnIndex("adresse"));
        Toast.makeText(this, "go to  : " + " " + lv.getItemAtPosition(position) , Toast.LENGTH_LONG).show();
        manager.restartLoader(0,null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("itemRss").appendPath(lien);
        Uri uri = builder.build();
        return new CursorLoader(this, uri, new String[]{"_id","titre"},
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
