package com.example.simoz.mplrss;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.FileDescriptor;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    Button remplir;
    Button afficher;
    Button valider;
    EditText url;
    AccessDonnees access_donnees;
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

    }

    //long downloadDate(String )

    void valider(View button){
        String lien = this.url.getText().toString();
        if(lien.length() == 0){
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, "Champs vide  !", duration);
            toast.show();
        } else {

            DownloadManager dm=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            Uri uri= Uri.parse(lien);
            DownloadManager.Request req= new DownloadManager.Request(uri);
            long id= dm.enqueue(req);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (Exception e){
                Log.d("Document Builder EXCP",e.getMessage());
            }
            //Document document = db.parse()
            /*ParcelFileDescriptor pDesc = null;
            try {
                 pDesc = dm.openDownloadedFile(id);
            } catch (Exception e){
                Log.d("EXCP of DOWNLOAD",e.getMessage());
            }
            FileDescriptor desc=pDesc.getFileDescriptor();*/
            //downloadDate(lien);
            // on remplie

        }
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
}
