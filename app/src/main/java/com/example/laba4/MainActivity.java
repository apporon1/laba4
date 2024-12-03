package com.example.laba4;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private TextView tv;
    private DatabaseHelper dbHelper;
    private final Handler handler = new Handler();
    private String lastTrack = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);
        dbHelper = new DatabaseHelper(this);

        if (!isInternetAvailable()) {
            Toast.makeText(this, "Нет подключения к Интернету. Автономный режим.", Toast.LENGTH_LONG).show();
            showRecords();
        } else {
            startTrackMonitoring();
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void startTrackMonitoring() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new TrackFetchTask().execute();
                handler.postDelayed(this, 20000);
            }
        }, 20000);
    }

    private void saveTrack(String artist, String title) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO tracks (artist, title, timestamp) VALUES (?, ?, CURRENT_TIMESTAMP)",
                new Object[]{artist, title});
    }

    private void showRecords() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tracks", null);
        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor.getColumnIndex("artist"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
            builder.append(artist).append(" - ").append(title).append(" (").append(timestamp).append(")\n");
        }
        cursor.close();
        tv.setText(builder.toString());
    }

    private class TrackFetchTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return ApiHelper.fetchCurrentTrack();
        }

        @Override
        protected void onPostExecute(String result) {
            tv.setText(result);
        }
    }
}
