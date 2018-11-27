package com.example.simoz.mplrss;

import android.app.DownloadManager;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ProgBar extends Thread  {

    boolean downloading = true;
    private boolean estAnnuler = false;
    private ProgressBar pb;
    private Button annuler;
    private final long id;
    private DownloadManager dm;

    public ProgBar(ProgressBar pb, Button b, final long id, DownloadManager dm){
        this.pb = pb;
        this.annuler = b;
        this.id = id;
        this.dm = dm;
    }

    public boolean getEstAnnuler(){
        return this.estAnnuler;
    }

    @Override
    public void run(){
        Log.d("Dans le RUn ","yes");
        /*while(downloading){
            //MainActivity.this.estAnnuler = false;
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(id);
            Cursor cursor = dm.query(q);
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
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    pb.setProgress((int) dl_progress);
                    annuler.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pb.setProgress(0);
                            dm.remove(id);
                            downloading = false;
                            estAnnuler = true;
                            //Toast.makeText(MainActivity.this, "Téléchargement annuler", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };
        }*/
    }
}
