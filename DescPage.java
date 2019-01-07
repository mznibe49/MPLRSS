package com.example.simoz.mplrss;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DescPage extends AppCompatActivity {

    TextView titre;
    TextView lien;
    TextView desc;
    private String adresse;
    AccessDonnees accessDonnees;
    String bpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc_page);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.titre = (TextView) findViewById(R.id.titre);
        this.lien = (TextView) findViewById(R.id.lien);
        this.desc = (TextView) findViewById(R.id.description);

        final Intent intent = getIntent();
        this.adresse = intent.getStringExtra("adresse");
        this.bpage = intent.getStringExtra("BackPage");
        this.accessDonnees = new AccessDonnees(this);

        String title = getDescFromAdresse(adresse,"titre");
        this.titre.setText("Titre : \n   "+title+"\n");

        String link = getDescFromAdresse(adresse,"lien");
        this.lien.setText("Lien : \n   "+link+"\n");

        String descrip = getDescFromAdresse(adresse,"description");
        this.desc.setText("Description : \n   "+descrip+"\n");

        this.desc.setMovementMethod(new ScrollingMovementMethod());

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case android.R.id.home:
                if(bpage.equals("f")) { // favoris
                    intent = new Intent(this,Favoris.class);
                    startActivity(intent);
                    return true;
                } else { // lecteur Item
                    intent = new Intent(this,LecteurItem.class);
                    String lien = this.accessDonnees.getLinkFromItem(adresse);
                    intent.putExtra("lien",lien);
                    startActivity(intent);
                    return true;
                }
                //finish();
        }
        return  super.onOptionsItemSelected(item);
    }

    void webAc(View view){
        Intent intent = new Intent(this,WebViewActivity.class);
        intent.putExtra("adresse",this.adresse);
        startActivity(intent);
    }

    public String getDescFromAdresse(String adresse, String arg){
        return this.accessDonnees.getDescFromAdresse(adresse,arg);
    }

}
