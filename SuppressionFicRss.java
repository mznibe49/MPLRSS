package com.example.simoz.mplrss;

import android.app.ListActivity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;



public class SuppressionFicRss extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    AccessDonnees access_donnee;
    SimpleCursorAdapter adapter;
    LoaderManager manager;
    private static final String authority = "fr.simo.bdprojet";
    SwipeMenuListView lv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppression_fic_rss);
        lv = (SwipeMenuListView) findViewById(R.id.supp);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //list = getListView();
        access_donnee = new AccessDonnees(this);
        Cursor cursor = access_donnee.getTableFile();

        adapter= new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{"titre"},
                new int[]{android.R.id.text1});

        lv.setAdapter(adapter);
        manager = getLoaderManager(); //manager = getLoaderManager();
        manager.initLoader(0, null, this);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                deleteItem.setWidth(170);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };

        lv.setMenuCreator(creator);

        lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {


                switch (index) {
                    case 0:
                        // delete
                        Cursor c = (Cursor) SuppressionFicRss.this.adapter.getItem(position); // pour recuperer l'element selectionner
                        int n = access_donnee.delete(c);
                        if(n == 0){
                            String msg = "Aucun Fic Rss";
                            Toast toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            String msg = "le Fic Rss supprimé est  : "+c.getString(c.getColumnIndex("titre"));
                            Toast toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT);
                            toast.show();
                            Intent intent = new Intent();
                            intent.putExtra("keyRes", "maj"); // pour maj le spinner
                            setResult(RESULT_OK, intent);
                            //finish();
                        }
                        manager.restartLoader(0,null, SuppressionFicRss.this);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = (Cursor) SuppressionFicRss.this.list.getAdapter().getItem(i); // pour recuperer l'element selectionner
                int n = access_donnee.delete(c);
                if(n == 0){
                    String msg = "Aucun Fic Rss";
                    Toast toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    String msg = "le Fic Rss supprimé est  : "+c.getString(c.getColumnIndex("titre"));
                    Toast toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent();
                    intent.putExtra("keyRes", "maj"); // pour maj le spinner
                    setResult(RESULT_OK, intent);
                    //finish();
                }
                manager.restartLoader(0,null, SuppressionFicRss.this);
            }
        });*/

    }

   /* protected void onListItemClick(ListView l, View v, int position, long id){
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
            Intent intent = new Intent();
            intent.putExtra("keyRes", "maj"); // pour maj le spinner
            setResult(RESULT_OK, intent);
            //finish();
        }
        manager.restartLoader(0,null, this);
    }*/

   @Override
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority)
                .appendPath("fic_rss").build();
        return new CursorLoader(this, uri, new String[]{"_id", "titre"},
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
