package com.example.simoz.mplrss;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

public class Searchable extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String tmp; // la string a chercher
    AccessDonnees access_donnees;
    SimpleCursorAdapter adapter;
    LoaderManager manager;
    String query = "";
    SwipeMenuListView lv;
    public final static String authority = "fr.simo.bdprojet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.access_donnees = new AccessDonnees(this);

        lv = (SwipeMenuListView) findViewById(R.id.listView);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Query " + query, Toast.LENGTH_LONG).show();
            doSearch(query);
        }

    }

    void doSearch(String q){
        //Toast.makeText(this, q, Toast.LENGTH_LONG).show();
        Cursor c = this.access_donnees.getSearchableResult(q);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                c,
                new String[]{"titre"},
                new int[]{android.R.id.text1}, 0);
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

                Cursor c = (Cursor) Searchable.this.adapter.getItem(position); // pour recuperer l'element selectionner
                String adresse = c.getString(c.getColumnIndex("adresse"));
                int favoris = Searchable.this.access_donnees.getItemFavoris(adresse);

                switch (index) {
                    case 0:
                        // open
                        Intent i = new Intent(Searchable.this, DescPage.class);
                        i.putExtra("adresse", adresse);
                        i.putExtra("BackPage","l");
                        startActivity(i);
                        break;
                    case 1:
                        // delete
                        Searchable.this.access_donnees.deleteIem(adresse);
                        Toast.makeText(Searchable.this, "article supprimer", Toast.LENGTH_LONG).show();
                        manager.restartLoader(0,null, Searchable.this);
                        break;
                    case 2:
                        //ajouter a la table de favoris
                        if(favoris == 0) {
                            Searchable.this.access_donnees.changeToFav(adresse,1);
                            Toast.makeText(Searchable.this, "Cet article est ajouté au favoris", Toast.LENGTH_LONG).show();
                        } else {
                            Searchable.this.access_donnees.changeToFav(adresse,0);
                            Toast.makeText(Searchable.this, "Cet article n'est plus dans les favoris", Toast.LENGTH_LONG).show();
                        }

                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return  super.onOptionsItemSelected(item);
    }

    /*public boolean onSearchRequested(){

        return true;
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Log.d("Querry in Searchable ",query);
        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        Uri uri = builder.build();
        return new CursorLoader(this, uri, new String[]{"_id","titre"},
                "titre LIKE '%"+query+"%'", null, null);*/
        return null;
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
