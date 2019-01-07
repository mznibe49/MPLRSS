package com.example.simoz.mplrss;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

public class AccessDonnees {

    ContentResolver resolver;

    public final static String authority = "fr.simo.bdprojet";
    public final static String COLONNE_ADRESSE = "adresse";
    public final static String COLONNE_TITRE = "titre";
    public final static String COLONNE_LIEN = "lien";
    public final static String COLONNE_DESCRIPTION = "description";
    public final static String COLONNE_DATE_MODIF = "derniere_modification";
    public final static String COLONNE_FAVORIS = "favoris";



    public AccessDonnees(Context context){
        resolver = context.getContentResolver();
    }

    public void ajoutRss(String lien, String titre, String dm){

        ContentValues values = new ContentValues();
        values.put(COLONNE_LIEN,lien);
        values.put(COLONNE_TITRE,titre);
        //values.put(COLONNE_DESCRIPTION,description);
        values.put(COLONNE_DATE_MODIF,dm);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("fic_rss");
        Uri uri = builder.build();
        uri = resolver.insert(uri,values);
    }

    public void ajoutItem(String lien, String adresse, String titre, String desc, int favoris){
        ContentValues values = new ContentValues();
        values.put(COLONNE_LIEN,lien);
        values.put(COLONNE_TITRE,titre);
        values.put(COLONNE_ADRESSE,adresse);
        values.put(COLONNE_DESCRIPTION,desc);
        values.put(COLONNE_FAVORIS,favoris);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        Uri uri = builder.build();
        uri = resolver.insert(uri,values);
    }

    public Cursor getTableFile(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("fic_rss");
        Uri uri = builder.build();
        return resolver.query(uri,null,null,null,null);
    }

    //Rss : lien,titre,dm
    //Item : lien,adresse,titre,desc,dm
   /* public void init(){
        ajoutRss("https://www.lemonde.fr/rss/une.xml",
                "Le Monde.fr - Actualités et Infos en France et dans le monde",
                "Le Monde.fr - 1er site d'information. Les articles du journal et toute l'actualité en continu : International, France, Société, Economie, Culture, Environnement, Blogs ...",
                "Tue, 13 Nov 2018 13:23:12");
        ajoutItem("https://www.lemonde.fr/rss/une.xml",
                "https://www.lemonde.fr/societe/article/2018/11/13/perquisitions-a-la-mairie-de-marseille-apres-l-effondrement-de-plusieurs-immeubles_5382901_3224.html?xtor=RSS-3208",
                "Effondrement d’immeubles : des perquisitions en cours à la mairie et à Marseille Habitat",
                "Huit personnes sont mortes dans l’écroulement le 5 novembre de deux immeubles du quartier de Noailles, l’un appartenant à la mairie via Marseille Habitat, l’autre à une copropriété privée.",
                0);
        ajoutItem("https://www.lemonde.fr/rss/une.xml",
                "https://www.lemonde.fr/pixels/article/2018/11/13/13-novembre-rescapes-de-l-attaque-au-bataclan-ils-se-reconstruisent-avec-le-jeu-video_5382892_4408996.html?xtor=RSS-3208",
                "13-Novembre : rescapés de l’attaque au Bataclan, ils se reconstruisent avec le jeu vidéo",
                "Flash-back, problèmes de concentration, agoraphobie… des victimes de stress post-traumatique ont trouvé refuge dans les mondes virtuels.",
                0);
        ajoutItem("https://www.lemonde.fr/rss/une.xml",
                "https://www.lemonde.fr/politique/article/2018/11/13/gilets-jaunes-le-gouvernement-n-acceptera-aucun-blocage-total-le-17-novembre_5382748_823448.html?xtor=RSS-3208",
                "« Gilets jaunes » : le gouvernement n’acceptera aucun « blocage total »",
                "Des centaines de collectifs ont appelé à une journée de blocage des routes samedi pour protester contre la hausse du prix des carburants.",
                0);


        ajoutRss("https://www.lemonde.fr/videos/rss_full.xml",
        "Vidéos : Toute l'actualité sur Le Monde.fr.",
        "Vidéos - Découvrez gratuitement tous les articles, les vidéos et les infographies de la rubrique Vidéos sur Le Monde.fr.",
        "Tue, 13 Nov 2018 13:19:42");
        ajoutItem("https://www.lemonde.fr/videos/rss_full.xml",
                "https://www.lemonde.fr/disparitions/video/2018/11/12/mort-de-stan-lee-ses-apparitions-dans-les-films-marvel_5382618_3382.html",
                "Mort de Stan Lee : ses apparitions dans les films Marvel",
                "Stan Lee est mort à l’âge de 95 ans. Ces dernières années, le créateur des personnages Spider-Man, Hulk et Iron Man avait imaginé un petit jeu. Faire de discrètes apparitions (caméos) dans tous les films tirés de la licence Marvel.",
                0);
        ajoutItem("https://www.lemonde.fr/videos/rss_full.xml",
                "https://www.lemonde.fr/sciences/video/2018/11/12/pourquoi-le-kilo-va-changer-de-poids_5382615_1650684.html",
                "Pourquoi le kilo va changer de masse",
                "Le kilogramme n'est plus. Vive le nouveau kilogramme ! Une nouvelle définition de l’unité de mesure doit être avalisée le 16 novembre. Explication, en vidéo.",
                0);
        ajoutItem("https://www.lemonde.fr/videos/rss_full.xml",
                "https://www.lemonde.fr/planete/video/2018/11/12/incendies-en-californie-les-stars-menacees-par-les-flammes-temoignent_5382603_3244.html",
                "Incendies en Californie : les stars menacées par les flammes témoignent",
                "Depuis le 8 novembre, la Californie est ravagée par des incendies meurtriers. Sur les réseaux sociaux, les stars habitant la région comme Will Smith, Robin Thicke ou les Kardashian ont médiatisé leurs évacuations.",
                0);
        //ajoutDonnees();
    }*/

    public Cursor getItemsLinkedToRss(String lien){ // recuperer les item qui ont un lien commun
        Uri.Builder builder = new Uri.Builder();
        //Log.d("LIEN",lien);
        builder.scheme("content").authority(authority).appendPath("itemRss").appendPath(lien);
        Uri uri = builder.build();
        String selection="lien = ?";
        String [] args = {lien};
        Cursor cursor = resolver.query(uri, null, selection, args,null);;
        return cursor;
    }

    public String getDescFromAdresse(String adresse, String argument){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("descItem").appendPath(adresse);
        Uri uri = builder.build();
        String selection="adresse = ?";
        String [] args = {adresse};
        Cursor cursor = resolver.query(uri, null, selection, args,null);
        String res = "";

        if(cursor.moveToFirst()){
            switch (argument){
                case "lien":
                    res = cursor.getString(cursor.getColumnIndex(COLONNE_LIEN));
                    break;
                case "description":
                    res = cursor.getString(cursor.getColumnIndex(COLONNE_DESCRIPTION));
                    break;
                case "titre":
                    res = cursor.getString(cursor.getColumnIndex(COLONNE_TITRE));
                    break;
            }
        }
        return  res;
    }


    /*public Cursor getItems(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("item");
        Uri uri = builder.build();
        Cursor cursor = resolver.query(uri, null, null, null,null);
        return cursor;
    }

    public Cursor getFavItem(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("itemRss").appendPath("1");
        Uri uri = builder.build();
        String selection = "favoris = ?";
        String [] s_args = {"1"};
        Cursor cursor = resolver.query(uri, null, selection, s_args,null);
        return cursor;
    }*/

    public Cursor getSearchableResult(String tmp){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("itemRss").appendPath(tmp);
        Uri uri = builder.build();
        String selection = "titre LIKE '%"+tmp+"%' or description LIKE '%"+tmp+"%' ";
        Cursor cursor = resolver.query(uri, null, selection, null,null);
        return cursor;
    }

    boolean isExistingLink(String lien){
        Uri.Builder builder = new Uri.Builder();
        Log.d("Lien in EXISTINGLING",""+lien);
        builder.scheme("content").authority(authority).appendPath("lienInfo").appendPath(lien);
        Uri uri = builder.build();
        String selection="lien = ?";
        String[] args={lien};
        Cursor cursor = resolver.query(uri, null, selection, args,null);
        if(cursor != null && cursor.getCount()==1 ) return true;
        return false; // cmt je fait cursor.close ici O.o
    }

    String getLinkFromItem(String adresse){ // recuperer le lien d'un item a partir de l adresse de l'item
        Uri.Builder builder = new Uri.Builder();
        //Log.d("Lien in EXISTINGLING",""+lien);
        builder.scheme("content").authority(authority).appendPath("itemRss").appendPath(adresse);
        Uri uri = builder.build();
        String selection="adresse = ?";
        String[] args={adresse};
        Cursor cursor = resolver.query(uri, null, selection, args,null);
        String lien = "";
        if(cursor.moveToFirst()){
            lien = cursor.getString(cursor.getColumnIndex(COLONNE_LIEN));
            cursor.close();
        }
        cursor.close();
        return lien;
    }

    public int delete(Cursor cursor){ // delete with cursor from loader  -  dans le SuppAc
        Uri.Builder builder = new Uri.Builder();
        String arg = cursor.getString(cursor.getColumnIndex("lien"));
        builder.scheme("content").authority(authority).appendPath("supprimeFic").appendPath(arg);
        Uri uri = builder.build();
        String where="lien = ?";
        String[] selection={arg};
        int cpt = resolver.delete(uri,where,selection);
        return cpt;
    }

    public int deleteAfterMin(int min){
        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("supprimeFic").appendPath(""+min);
        Uri uri = builder.build();*/
        Cursor cursor = getTableFile();
        Parseur p = new Parseur();
        long minute = min * 60000; // 60 sec = 60k msec ou min = 1
        while(cursor.moveToNext()){
            String date = cursor.getString(cursor.getColumnIndex(COLONNE_DATE_MODIF));
            Date d = p.convertirDate(date);
            String lien = cursor.getString(cursor.getColumnIndex(COLONNE_LIEN));
            Date now = new Date();
            Log.d("date from table rss ",""+d.getTime());
            Log.d("date from now ",now.getTime()+"");
            long time =  now.getTime() - d.getTime();
            Log.d("date from time ",""+time);
            if( time > minute){
                Log.d("date from inside ","condition "+minute);
                delete(lien);
            }
        }
        return -1;
    }

    public int delete(String lien){ // delete with link from bcr  -  dans le MainAc
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("supprimeFic").appendPath(lien);
        Uri uri = builder.build();
        String where="lien = ?";
        String[] selection={lien};
        int cpt = resolver.delete(uri,where,selection);
        return cpt;
    }

    public int deleteIem(String adresse){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("supprimeItem").appendPath(adresse);
        Uri uri = builder.build();

        String where="adresse = ?";
        String[] selection={adresse};
        int cpt = resolver.delete(uri,where,selection);
        return cpt;
    }

    public String getDateFromRss(String url){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("dateInfo").appendPath(url);
        Uri uri = builder.build();
        String where="lien = ?";
        String[] selection={url};
        Cursor cursor = resolver.query(uri,null,where,selection,null);
        String date = "none";
        if(cursor.moveToFirst()){
            date = cursor.getString(cursor.getColumnIndex(COLONNE_DATE_MODIF));
            cursor.close();
        }
        cursor.close();
        return date;
    }

    public void changeToFav(String adresse, int favorisValue){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("toFav");
        Uri uri = builder.build();
        ContentValues values = new ContentValues();
        values.put(COLONNE_FAVORIS,favorisValue);
        String selection = "adresse = ?";
        String [] argsS = {adresse};
        resolver.update(uri, values , selection ,argsS);
    }

    public int getItemFavoris(String adresse){ // pr reconnaitre le favoris de cet item (si 0 ou 1)
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("itemRss").appendPath(adresse);
        Uri uri = builder.build();
        String selection = "adresse = ?";
        String [] argsS = {adresse};
        Cursor cursor = resolver.query(uri, null, selection, argsS,null);
        int favoris = 0;
        if(cursor.moveToFirst()){
            favoris = Integer.parseInt(cursor.getString(cursor.getColumnIndex("favoris")));
        }
        return favoris;
    }


}
