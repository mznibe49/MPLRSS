package com.example.simoz.mplrss;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.app.DownloadManager.COLUMN_LOCAL_FILENAME;
import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {

    Button remplir;
    Button afficher;
    Button valider;
    Button supp;
    EditText url;
    AccessDonnees access_donnees;

    //private long id_url;
    private DownloadManager dm;
    private String path_file="";
    //long downloadReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        access_donnees = new AccessDonnees(this);
        remplir = (Button) findViewById(R.id.remplir);
        afficher = (Button) findViewById(R.id.afficher);
        valider = (Button) findViewById(R.id.valider);
        url = (EditText) findViewById(R.id.url);
        supp = (Button) findViewById(R.id.supp);

    }

    //long downloadDate(String )

    void valider(View button){
        String lien = this.url.getText().toString();
        if(lien.length() == 0){

            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, "Champs vide  !", duration);
            toast.show();

        } else {

            ConnectivityManager cm =
                    (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                //downloadAdresse();
                String lien_directe = url.getText().toString();//"https://www.lemonde.fr/festival-de-cannes/rss_full.xml"; // on le change avec le edit texte apres
                //boolean lien_Valide = lienValide(lien_directe);
                Uri uri = Uri.parse(lien_directe);

                URL url = null; //new URL(lien_directe) ;
                boolean url_valide = true;
                try {
                    url = new URL(lien_directe);
                } catch (Exception e){
                    Log.d("IN URL CATCH"," :/");
                    url_valide = false;
                }
                //URLConnection mycon = url.openConnection();

                //if (myCon.getContentLength == -1) ;
                //System.out.println("le fichier n'existe pas") ;
                if(url_valide) {
                    this.dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request req = new DownloadManager.Request(uri);

                    req.setDescription("Android Data download using DownloadManager.");

                    req.setDestinationInExternalFilesDir(MainActivity.this,
                            Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment()); // pour recup le nom du fic

                    long id = this.dm.enqueue(req);
                    checkLink(id);
                } else {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(this, "Url invalide", duration);
                    toast.show();
                }
            } else {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, "No internet connexion", duration);
                toast.show();
            }

        }
    }

    void checkLink(final long id){
        Log.d("MSG1","OUT BCR");
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {

            private String url_lien=""; // qui content http://www... recuperer depuis le fichier

            @Override
            public void onReceive(Context context, Intent intent) {
            // récupérer la référence du téléchargement
                Log.d("MSG2","IN BCR");
                long ref = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == ref) {
                // si OK alors plus besoin de BroadcastReceiver
                    MainActivity.this.unregisterReceiver(this);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(id);
                    Cursor cursor = dm.query(query);
                    if (cursor.moveToFirst()) {
                        String path = cursor.getString(cursor
                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                        this.url_lien =  cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                        Log.d("URL_LIEN ",this.url_lien);
                        if(path==null){
                            Toast.makeText(MainActivity.this,"Url invalide",Toast.LENGTH_LONG).show();
                            return;
                        }
                        //Object mListener;
                        Log.d("PATH !!! : ",path);
                        MainActivity.this.path_file = path.replace("file://","");
                        createDocument(MainActivity.this.path_file);
                        //mListener.onFragmentInteraction(path);
                    }
                }
            }

            void createDocument(String path){
                //String path = path_file.replace("file://","");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = null;
                Document document = null;
                Log.e("real path is : ", path);
                try {
                    db = dbf.newDocumentBuilder();
                    document = db.parse(new File(path));
                    Log.e("IN DOC TRY", "enfin !");
                    parseDocument(document);
                } catch (Exception e){
                    Log.d("Err in createDoc : ",e.getMessage());
                }
            }

            void parseDocument(Document document){
                Element racine = document.getDocumentElement();

                NodeList racineNoeuds = null;//racine.getChildNodes();
                try{
                    racineNoeuds = racine.getChildNodes();
                } catch (Exception e){
                    Log.d("ERR in ParseDoc"," :s :s ");
                    /*int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(MainActivity.this, "Votre Url est invalide", duration);
                    toast.show();*/
                }
                String racine_name = racine.getNodeName();
                Log.d("Racine name : ",racine_name);
                if(racine_name.equals("rss")) {
                    // if ( le fichier est un fichier rss apres le parcours des elements !!
                    int nbRacineNoeuds = racineNoeuds.getLength();

                    Node node = null; // channel la premiere balise apres rss
                    for (int i = 0; i < nbRacineNoeuds; i++) {
                        if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                            node = racineNoeuds.item(i);
                            //Log.d("Channel ? ",node.getNodeName());
                        }
                    }
                    NodeList fils = node.getChildNodes(); // les balises dans channel
                    int nb_fils = fils.getLength();

                    ArrayList<Node> efr = new ArrayList<Node>(); // node contenant les elements a ajouter dans la table fic_rss
                    ArrayList<Node> eti = new ArrayList<Node>(); // node contenant les elements a ajouter dans la table item

                    boolean first_item_found = false;
                    for (int j = 0; j < nb_fils; j++) {
                        if (fils.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            final Node node1 = fils.item(j);
                            //Log.d("Node : ", node1.getNodeName());
                            String node1_name = node1.getNodeName(); // on cherche le nom du fils

                            if (node1_name.equals("item")) first_item_found = true;

                            if (!first_item_found) efr.add(node1);
                            else eti.add(node1);
                        }
                    }

                    boolean existe = MainActivity.this.access_donnees.isExistingLink(this.url_lien);
                    Log.d("Ce lien existe ? ", "" + existe);
                    if (existe) {
                        Intent intent = new Intent(MainActivity.this, LecteurItem.class);
                        intent.putExtra("lien", this.url_lien);
                        startActivity(intent);
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, "Ce fic est deja dans la base !", duration);
                        toast.show();
                    } else {
                        ajouterDansFicRss(efr); // ajoute les element de la liste dans chaque table
                        ajouterDansItem(eti);
                        Intent intent = new Intent(MainActivity.this, LecteurItem.class);
                        intent.putExtra("lien", this.url_lien);
                        startActivity(intent);
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, "Votre fic est bien enregistré dans la base !", duration);
                        toast.show();
                    }
                } else {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(MainActivity.this, "Votre Url est invalide IN PARSE DOC", duration);
                    toast.show();
                }
                //Log.d("EFR ET ETI SIZE",""+efr.size()+" "+eti.size());
                /*ajouterDansFicRss(efr); // ajoute les element de la liste dans chaque table
                //Log.d("Middle","btween adds");
                ajouterDansItem(eti);
                // on ouvre directement la liste des items du liens entré
                Intent intent = new Intent(MainActivity.this,LecteurItem.class);
                intent.putExtra("lien",this.link_in_rss_file);
                startActivity(intent);*/
                //Log.d("END","after adds");
                //Log.d("Fin ajout"," fiuuw !");
            }

            void ajouterDansFicRss(ArrayList<Node> list){

                String lien = "";
                String titre = "";
                String desc = "";
                String dm = ""; // date pub ou date de changement

                for(int i = 0; i<list.size(); i++){
                    Node node = list.get(i);
                    Element element = (Element) node;
                    String node_name = node.getNodeName();
                    String contenu_node = element.getTextContent();
                    switch (node_name){
                        case "title":
                            titre = contenu_node;
                            break;
                        case "description":
                            desc = contenu_node;
                            break;
                        case "link":
                            lien = this.url_lien;
                            break;
                        case "pubDate":
                            dm = contenu_node;
                            break;
                        case "lastBuildDate":
                            dm = contenu_node;
                            break;
                    }
                }
                MainActivity.this.access_donnees.ajoutRss(lien,titre,desc,dm);

            }

            void ajouterDansItem(ArrayList<Node> list){

                String adresse = "";
                String titre = "";
                String desc = "";
                String dm = ""; // date pub ou date de changement
                Log.d("Nbr d'item : ",""+list.size());
                //String lien

                for(int i = 0; i<list.size(); i++){
                    //if(fils.item(j).getNodeType() == Node.ELEMENT_NODE) {

                    NodeList list_node = list.get(i).getChildNodes(); // chaque item a une liste d autre element
                    //Log.d("nbr balise dans item : ",""+list_node.getLength());
                    for(int j = 0; j<list_node.getLength();j++){
                        Node node = list_node.item(j);
                        if(node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String node_name = node.getNodeName();
                            String contenu_node = element.getTextContent();
                            switch (node_name) {
                                case "title":
                                    titre = contenu_node;
                                    break;
                                case "description":
                                    desc = contenu_node;
                                    break;
                                case "link":
                                    adresse = contenu_node;
                                    break;
                                case "pubDate":
                                    dm = contenu_node;
                                    break;
                                case "lastBuildDate":
                                    dm = contenu_node;
                                    break;
                            }
                        }
                    }
                    MainActivity.this.access_donnees.ajoutItem(this.url_lien,adresse,titre,desc,dm);
                }
            }

        };
        registerReceiver(receiver, filter);
    }


    void remplirBase(View button){
        try {
            access_donnees.init();
        } catch (Exception e){
            Log.d("ERR",e.getMessage());
        }
    }

    void afficherBase(View button){
        Intent intent = new Intent(this,LecteurBase.class);
        startActivity(intent);
    }

    void suppFicRss(View button){
        Intent intent = new Intent(this,SuppressionFicRss.class);
        startActivity(intent);
    }
}
