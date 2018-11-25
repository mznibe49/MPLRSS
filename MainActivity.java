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
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    Button annuler;
    Button supp;
    EditText url;
    AccessDonnees access_donnees;
    boolean estAnnuler = true;

    private DownloadManager dm;
    private String path_file="";
    private ProgressBar pb;


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
        pb = (ProgressBar) findViewById(R.id.simpleProgressBar); // initiate the progress bar
        annuler = (Button) findViewById(R.id.annuler);

    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnected();
        return  isConnected;
    }

    void valider(View button){
        String lien = this.url.getText().toString();
        if(lien.length() == 0){

            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, "Champs vide  !", duration);
            toast.show();

        } else {

           /* ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&  activeNetwork.isAvailable() && activeNetwork.isConnected();*/
           boolean isConnected = isConnected();
            Log.d("Est connecté ? ",""+isConnected);
            if (isConnected) {
                String lien_directe = url.getText().toString();//"https://www.lemonde.fr/festival-de-cannes/rss_full.xml"; // on le change avec le edit texte apres
                Uri uri = Uri.parse(lien_directe);
                URL url = null; //new URL(lien_directe) ;
                boolean url_valide = true;
                try {
                    url = new URL(lien_directe);
                } catch (Exception e){
                    Log.d("IN URL CATCH"," :/");
                    url_valide = false;
                }

                if(url_valide) {

                    this.dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request req = new DownloadManager.Request(uri);
                    //req.setTitle()
                    req.setDescription("Android Data download using DownloadManager.");
                    req.setDestinationInExternalFilesDir(MainActivity.this,
                            Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment()); // pour recup le nom du fic

                    final long id = this.dm.enqueue(req);
                    //boolean estAnnuler = false;
                    Thread progBar = new Thread(){

                        boolean downloading = true;

                        public void run(){
                            while(downloading){
                                MainActivity.this.estAnnuler = false;
                                DownloadManager.Query q = new DownloadManager.Query();
                                q.setFilterById(id);
                                Cursor cursor = MainActivity.this.dm.query(q);
                                int bytes_total = 0,bytes_downloaded = 0;
                                if(cursor.moveToFirst()) {
                                     bytes_downloaded = cursor.getInt(cursor
                                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                     bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                        downloading = false;
                                        pb.setProgress(0);
                                    }
                                }
                                final int dl_progress = (bytes_total > 0 ? (int) ((bytes_downloaded * 100L) / bytes_total) : 0);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pb.setProgress((int) dl_progress);
                                        MainActivity.this.annuler.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                pb.setProgress(0);
                                                MainActivity.this.dm.remove(id);
                                                downloading = false;
                                                MainActivity.this.estAnnuler = true;
                                                Toast.makeText(MainActivity.this, "Téléchargement annuler", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    };
                    progBar.start();
                    if(estAnnuler == false) checkLink(id);
                } else {
                    Toast.makeText(this, "Url invalide", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("PAS  de CO",":/");
                Toast.makeText(this, "No internet connexion", Toast.LENGTH_SHORT).show();
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
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {

                    Log.d("MSG2", "IN BCR");
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
                            // http://www.lemonde....
                            this.url_lien = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
                            Log.d("URL_LIEN ", this.url_lien);
                            if (path == null) {
                                Toast.makeText(MainActivity.this, "Url invalide", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Log.d("PATH !!! : ", path);
                            MainActivity.this.path_file = path.replace("file://", "");

                            Parseur parseur = new Parseur(MainActivity.this.path_file,this.url_lien,MainActivity.this.access_donnees);
                            boolean bienParser = parseur.lunch();
                            if(bienParser){
                                ArrayList<Node> ficRssNode = parseur.getFicRssListNode();
                                ArrayList<Node> itemNode = parseur.getItemListNode();
                                boolean existe = MainActivity.this.access_donnees.isExistingLink(this.url_lien); // si notre url existe on ouvre sin on enregistre puis on ouvre
                                if (existe) { // on change.. si ça existe (on verifie la date, si la date du site a changé on supp on reajoute, sin on fait rien), sin on rajoute directement
                                    boolean sameTime = parseur.checkDate(); // envoie vrai si l'ancienne et la nv date son kif kif
                                    if(sameTime){
                                        Intent i = new Intent(MainActivity.this, LecteurItem.class);
                                        i.putExtra("lien", this.url_lien);
                                        startActivity(i);
                                        Toast.makeText(MainActivity.this, "meme date des fic rss donc pas de modif", Toast.LENGTH_LONG).show();
                                    } else { // on supp l'ancien on ajoute le nv et puis on ouvre
                                        MainActivity.this.access_donnees.delete(this.url_lien);
                                        parseur.ajouterDansFicRss(ficRssNode); // ajoute les element de la liste dans chaque table
                                        parseur.ajouterDansItem(itemNode);
                                        Intent i = new Intent(MainActivity.this, LecteurItem.class);
                                        i.putExtra("lien", this.url_lien);
                                        startActivity(i);
                                        Toast.makeText(MainActivity.this, "pas la meme date => modif", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    parseur.ajouterDansFicRss(ficRssNode); // ajoute les element de la liste dans chaque table
                                    parseur.ajouterDansItem(itemNode);
                                    Intent i = new Intent(MainActivity.this, LecteurItem.class);
                                    i.putExtra("lien", this.url_lien);
                                    startActivity(i);
                                    Toast.makeText(MainActivity.this, "Votre fic est bien enregistré dans la base !", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Url invalide", Toast.LENGTH_LONG).show();
                            }
                        }
                        cursor.close();
                    }
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
