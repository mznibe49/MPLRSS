package com.example.simoz.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {

    Base base;

    private static final String authority = "fr.simo.bdprojet";

    private static final int BASE_FIC_RSS = 1;
    private static final int BASE_ITEM = 2;

    private static final int LIGNE_ITEM = 4;


    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(authority, "fic_rss", BASE_FIC_RSS); // list de tout les fichier
        matcher.addURI(authority, "item", BASE_ITEM); // list de tout les item (useless)
        matcher.addURI(authority,"itemRss/*",LIGNE_ITEM); // un bloc de ligne de la table item

    }


    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented IN DELETE");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented in GET TYPE");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        SQLiteDatabase db = base.getWritableDatabase();
        int code = matcher.match(uri); // trouver le code de cette uri
        String path="";
        long id=0;
        switch (code){
            case BASE_FIC_RSS: // inserer des elt dans fic_rss
                path = "fic_rss";
                id = db.insertOrThrow("fic_rss", null, values);
                break;
            case BASE_ITEM:
                path = "item"; // inserer des elt dans item
                id = db.insertOrThrow("item", null, values);
                break;
        }
        Uri.Builder builder = (new Uri.Builder())
                .authority(authority)
                .appendPath(path);
        /* retourner Uri dans ContentResolver */
        return ContentUris.appendId(builder, id).build();
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        base = Base.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteDatabase db= base.getReadableDatabase();
        int code = matcher.match(uri);
        String[] colonne={"rowid as _id","*"};
        Cursor cursor = null;
        Log.d("CODE",code+"");
        switch (code){
            case BASE_FIC_RSS:  // tout fic_rss
                cursor = db.query("fic_rss",colonne,selection,selectionArgs,null,null,sortOrder);
                break;
            case BASE_ITEM: // tout item
                cursor = db.query("item",colonne,selection,selectionArgs,null,null,sortOrder);
                break;
            case LIGNE_ITEM: // plusieurs lignes de  de fic_rss sous condition
                cursor = db.query("item",colonne,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented in QUERY");
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented IN UPDATE");
    }
}
