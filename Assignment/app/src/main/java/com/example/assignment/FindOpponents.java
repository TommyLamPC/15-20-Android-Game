package com.example.assignment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FindOpponents extends AppCompatActivity {
    private TextView tvOppPlayerName;
    private ImageView ivOppPlayerPic;
    private FetchPageTask task = null;

    private String pInfo, pName;
    private int pID;
    private static final int REQUEST_CODE_Start = 1212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_opponents);

        tvOppPlayerName = (TextView) findViewById(R.id.tvOppPlayerName);
        ivOppPlayerPic = (ImageView) findViewById(R.id.ivOppPlayerPic);
        task = new FetchPageTask();
        task.execute();
    }

    public void change(View view) {
        task = new FetchPageTask();
        task.execute();
    }

    private class FetchPageTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... values) { // parameters not
            String result = "";
            URL url = null;
            InputStream inputStream = null;

            try {
                url = new URL("https://4qm49vppc3.execute-api.us-east-1.amazonaws.com/Prod/itp4501_api/opponent/0");
                HttpURLConnection con = (HttpURLConnection)
                        url.openConnection();
                // Make GET request
                con.setRequestMethod("GET");
                con.connect();

                // Get the response
                inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream));

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line + "\n";
                }
                inputStream.close();
            } catch (Exception e) {
                result = e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Parse to get weather information
            try {
                JSONObject jObj = new JSONObject(result);
                if(pID != jObj.getInt("id")) {
                    String pCountry;
                    pCountry = jObj.getString("country");
                    pName = jObj.getString("name");
                    pInfo = pName + ", " + pCountry;
                    tvOppPlayerName.setText(pName + ", " + pCountry);
                    pID = jObj.getInt("id");
                    if (pID == 2 || pID == 3 || pID == 5) {
                        ivOppPlayerPic.setImageResource(R.drawable.female);
                    } else {
                        ivOppPlayerPic.setImageResource(R.drawable.male);
                    }
                }else{
                    task = new FetchPageTask();
                    task.execute();
                }
            } catch (Exception e) {
                String error = e.getMessage();
                Toast.makeText(FindOpponents.this, error, Toast.LENGTH_LONG).show();
                //textDescription.setText(error);
            }
        }
    }

    public void start(android.view.View v) {
        Intent intent = new Intent(this, GameStart.class);
        intent.putExtra("pID", pID);
        intent.putExtra("pName", pName);
        intent.putExtra("pInfo", pInfo);
        startActivityForResult(intent, REQUEST_CODE_Start);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_Start) {
            if (data.hasExtra("gameDate") && data.hasExtra("gameTime") && data.hasExtra("opponentName") && data.hasExtra("winOrLost")) {
                //Toast.makeText(FindOpponents.this, data.getStringExtra("gameDate") + data.getStringExtra("gameTime") + data.getStringExtra("opponentName") + data.getStringExtra("winOrLost"), Toast.LENGTH_LONG).show();
                String gameDate, gameTime, opponentName, winOrLost;
                gameDate = data.getStringExtra("gameDate");
                gameTime = data.getStringExtra("gameTime");
                opponentName = data.getStringExtra("opponentName");
                winOrLost = data.getStringExtra("winOrLost");
                if(gameDate != null) {
                    SQLiteDatabase db;
                    try {
                        db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/Gameslog", null, SQLiteDatabase.CREATE_IF_NECESSARY);
                        db.execSQL("CREATE TABLE IF NOT EXISTS GamesLog( gameDate VARCHAR(10) NOT NULL, gameTime VARCHAR(8) NOT NULL, opponentName VARCHAR(10) NOT NULL, winOrLost VARCHAR(4) NOT NULL);");
                        db.execSQL("INSERT INTO gameslog (gameDate, gameTime, opponentName, winOrLost) VALUES ('" + gameDate + "', '" + gameTime + "', '" + opponentName + "', '" + winOrLost + "');");
                    } catch (SQLiteException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    public void exit(android.view.View v) {
        super.finish();
    }
}
