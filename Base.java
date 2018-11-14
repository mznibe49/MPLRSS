package com.example.simoz.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Base extends SQLiteOpenHelper {


    public final static int VERSION = 9;

    public final static String DB_NAME = "mplrss";

    public final static String TABLE_FIC_RSS = "fic_rss";

    //public final static String COLONNE_KEY = "id";
    public final static String COLONNE_TITRE = "titre";
    public final static String COLONNE_DESCRIPTION = "description";
    public final static String COLONNE_LIEN = "lien"; // lien du fichier
    public final static String COLONNE_ADRESSE = "adresse"; // lien de l'item dans le fichier
    public final static String COLONNE_DATE_MODIF = "derniere_modification";

    public final static String TABLE_ITEM = "item";



    public final static String CREATE_TABLE_FIC_RSS = "create table " + TABLE_FIC_RSS + "(" +
            //COLONNE_KEY +  " integer primary key autoincrement, " +
            COLONNE_LIEN + " string primary key, " +
            COLONNE_TITRE + " string, " +
            COLONNE_DESCRIPTION + " text, " +
            COLONNE_DATE_MODIF + " string " + ");";

    public final static String CREATE_TABLE_ITEM = "create table " + TABLE_ITEM + "(" +
            COLONNE_LIEN + " string references fic_rss, " + // le lien du fichier
            COLONNE_TITRE + " string, " +
            COLONNE_ADRESSE + " string primary key, " +
            COLONNE_DESCRIPTION + " text, " + // lien de l'item courant
            COLONNE_DATE_MODIF + " string " + ");";

    private static Base ourInstance;

    public static Base getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new Base(context);
        return ourInstance;
    }

    private Base(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FIC_RSS);
        db.execSQL(CREATE_TABLE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TABLE_FIC_RSS);
            db.execSQL("drop table if exists " + TABLE_ITEM);
            onCreate(db);
        }
    }
}
