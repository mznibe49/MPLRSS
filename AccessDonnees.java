package com.example.simoz.mplrss;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AccessDonnees {

    ContentResolver resolver;

    public final static String authority = "fr.simo.bdprojet";
    public final static String COLONNE_ADRESSE = "adresse";
    public final static String COLONNE_TITRE = "titre";
    public final static String COLONNE_LIEN = "lien";
    public final static String COLONNE_DESCRIPTION = "description";
    public final static String COLONNE_DATE_MODIF = "derniere_modification";


    public AccessDonnees(Context context){
        resolver = context.getContentResolver();
    }

    public void ajoutRss(String lien, String titre,String description, String dm){

        ContentValues values = new ContentValues();
        values.put(COLONNE_LIEN,lien);
        values.put(COLONNE_TITRE,titre);
        values.put(COLONNE_DESCRIPTION,description);
        values.put(COLONNE_DATE_MODIF,dm);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("fic_rss");
        Uri uri = builder.build();
        uri = resolver.insert(uri,values);
    }

    public void ajoutItem(String lien, String adresse, String titre, String desc, String dm){
        ContentValues values = new ContentValues();
        values.put(COLONNE_LIEN,lien);
        values.put(COLONNE_TITRE,titre);
        values.put(COLONNE_ADRESSE,adresse);
        values.put(COLONNE_DESCRIPTION,desc);
        values.put(COLONNE_DATE_MODIF,dm);
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

    //Rss : lien,titre,desc,dm
    //Item : lien,adresse,titre,desc,dm
    public void init(){
        ajoutRss("https://www.lemonde.fr/rss/une.xml",
                "Le Monde.fr - Actualités et Infos en France et dans le monde",
                "Le Monde.fr - 1er site d'information. Les articles du journal et toute l'actualité en continu : International, France, Société, Economie, Culture, Environnement, Blogs ...",
                "Tue, 13 Nov 2018 13:23:12");
        ajoutItem("https://www.lemonde.fr/rss/une.xml",
                "https://www.lemonde.fr/societe/article/2018/11/13/perquisitions-a-la-mairie-de-marseille-apres-l-effondrement-de-plusieurs-immeubles_5382901_3224.html?xtor=RSS-3208",
                "Effondrement d’immeubles : des perquisitions en cours à la mairie et à Marseille Habitat",
                "Huit personnes sont mortes dans l’écroulement le 5 novembre de deux immeubles du quartier de Noailles, l’un appartenant à la mairie via Marseille Habitat, l’autre à une copropriété privée.",
                "Tue, 13 Nov 2018 13:04:19");
        ajoutItem("https://www.lemonde.fr/rss/une.xml",
                "https://www.lemonde.fr/pixels/article/2018/11/13/13-novembre-rescapes-de-l-attaque-au-bataclan-ils-se-reconstruisent-avec-le-jeu-video_5382892_4408996.html?xtor=RSS-3208",
                "13-Novembre : rescapés de l’attaque au Bataclan, ils se reconstruisent avec le jeu vidéo",
                "Flash-back, problèmes de concentration, agoraphobie… des victimes de stress post-traumatique ont trouvé refuge dans les mondes virtuels.",
                "Tue, 13 Nov 2018 12:40:11");
        ajoutItem("https://www.lemonde.fr/rss/une.xml",
                "https://www.lemonde.fr/politique/article/2018/11/13/gilets-jaunes-le-gouvernement-n-acceptera-aucun-blocage-total-le-17-novembre_5382748_823448.html?xtor=RSS-3208",
                "« Gilets jaunes » : le gouvernement n’acceptera aucun « blocage total »",
                "Des centaines de collectifs ont appelé à une journée de blocage des routes samedi pour protester contre la hausse du prix des carburants.",
                "Tue, 13 Nov 2018 09:59:13");


        ajoutRss("https://www.lemonde.fr/videos/rss_full.xml",
        "Vidéos : Toute l'actualité sur Le Monde.fr.",
        "Vidéos - Découvrez gratuitement tous les articles, les vidéos et les infographies de la rubrique Vidéos sur Le Monde.fr.",
        "Tue, 13 Nov 2018 13:19:42");
        ajoutItem("https://www.lemonde.fr/videos/rss_full.xml",
                "https://www.lemonde.fr/disparitions/video/2018/11/12/mort-de-stan-lee-ses-apparitions-dans-les-films-marvel_5382618_3382.html",
                "Mort de Stan Lee : ses apparitions dans les films Marvel",
                "Stan Lee est mort à l’âge de 95 ans. Ces dernières années, le créateur des personnages Spider-Man, Hulk et Iron Man avait imaginé un petit jeu. Faire de discrètes apparitions (caméos) dans tous les films tirés de la licence Marvel.",
                "Mon, 12 Nov 2018 21:01:56");
        ajoutItem("https://www.lemonde.fr/videos/rss_full.xml",
                "https://www.lemonde.fr/sciences/video/2018/11/12/pourquoi-le-kilo-va-changer-de-poids_5382615_1650684.html",
                "Pourquoi le kilo va changer de masse",
                "Le kilogramme n'est plus. Vive le nouveau kilogramme ! Une nouvelle définition de l’unité de mesure doit être avalisée le 16 novembre. Explication, en vidéo.",
                "Mon, 12 Nov 2018 20:58:11");
        ajoutItem("https://www.lemonde.fr/videos/rss_full.xml",
                "https://www.lemonde.fr/planete/video/2018/11/12/incendies-en-californie-les-stars-menacees-par-les-flammes-temoignent_5382603_3244.html",
                "Incendies en Californie : les stars menacées par les flammes témoignent",
                "Depuis le 8 novembre, la Californie est ravagée par des incendies meurtriers. Sur les réseaux sociaux, les stars habitant la région comme Will Smith, Robin Thicke ou les Kardashian ont médiatisé leurs évacuations.",
                "Mon, 12 Nov 2018 20:09:25");
        //ajoutDonnees();
    }

    public Cursor getItems(String lien){ // recuperer les item qui ont un lien commun
        Uri.Builder builder = new Uri.Builder();
        Log.d("LIEN",lien);
        builder.scheme("content").authority(authority).appendPath("itemRss").appendPath(lien);
        Uri uri = builder.build();
        String selection="lien = ?";
        String [] args = {lien};
         Cursor cursor = null;
        //try {
        //Cursor cursor = resolver.query(uri, null, selection, args, null);
       /*} catch (Exception e){
            Log.d("EXCP","IN GETITEMS");
        }*/
        try {
            //String selection="adresse = ?";
            //String [] args = {lien};
            cursor = resolver.query(uri, null, selection, args,null);
            Log.d("MSG","AVANT LE WHILE");
            while (cursor.moveToNext()) {
                //Log.d("Pere","lien");
                Log.d("MSG","DANS LE WHILE");
                Log.d("Fils de Lien au dessus ",cursor.getString(cursor.getColumnIndex(COLONNE_ADRESSE)));
            }
            Log.d("MSG","APRES LE WHILE");

        } catch (Exception e){
            Log.d("ERR getItem",e.getMessage());
        }

        return cursor;
    }


}
