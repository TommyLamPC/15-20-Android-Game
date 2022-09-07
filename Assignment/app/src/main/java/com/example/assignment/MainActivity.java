package com.example.assignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PREFS_NAME = "account_of_1520";
    private static final int REQUEST_CODE_Register = 3434;
    String playerName;
    private Button btnStart, btnRegister, btnHistory, btnPercentage;
    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(this);
        btnPercentage = (Button) findViewById(R.id.btnPercentage);
        btnPercentage.setOnClickListener(this);
        tvMsg = (TextView)findViewById(R.id.tvMsg);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        /*settings.edit()
                .putString("PlayerName", "")
                .putString("DOB", "")
                .putString("PhoneNum", "")
                .putString("Email", "")
                .commit();*/
        playerName = settings.getString("PlayerName", "");
        if(playerName.length() > 0){
            tvMsg.setText("Hello, " + playerName + "!");
        }else{
            tvMsg.setText("Hello! Please register a account!");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_Register) {
            if (data.hasExtra("PlayerName")) {
                Toast.makeText(MainActivity.this, "Congratulations! You've successfully registered for this game!", Toast.LENGTH_LONG).show();
                playerName = data.getStringExtra("PlayerName");
                tvMsg.setText("Hello, " + playerName + "!");
            }
        }
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnStart:
                if(playerName.length() <= 0){
                    Toast.makeText(MainActivity.this, "Please registered a account first!", Toast.LENGTH_LONG).show();
                }else{
                    Intent intentFindOpponents = new Intent(this, FindOpponents.class);
                    startActivity(intentFindOpponents);
                }
                break;

            case R.id.btnRegister:
                Intent intentRegister = new Intent(this, Register.class);
                startActivityForResult(intentRegister, REQUEST_CODE_Register);
                break;

            case R.id.btnHistory:
                Intent intentShowGameLog = new Intent(this, ShowGameLog.class);
                startActivity(intentShowGameLog);
                break;

            case R.id.btnPercentage:
                SQLiteDatabase db ;
                Cursor cursor = null;
                int winNum = 0, loseNum = 0;

                try {
                    db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/Gameslog",
                            null, SQLiteDatabase.OPEN_READONLY);
                    cursor = db.rawQuery("SELECT count(*) AS 'winNum' FROM GamesLog WHERE winOrLost = 'Win';", null);
                    if (cursor.moveToNext()) {
                        winNum = cursor.getInt(cursor.getColumnIndex("winNum"));
                    }
                    db.close();
                } catch (SQLiteException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                try {
                    db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/Gameslog",
                            null, SQLiteDatabase.OPEN_READONLY);
                    cursor = db.rawQuery("SELECT count(*) AS 'loseNum' FROM GamesLog WHERE winOrLost = 'Lose';", null);
                    if (cursor.moveToNext()) {
                        loseNum = cursor.getInt(cursor.getColumnIndex("loseNum"));
                    }
                    db.close();
                } catch (SQLiteException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                Intent intentStatisticalChart = new Intent(this, StatisticalChart.class);
                intentStatisticalChart.putExtra("winNum", winNum);
                intentStatisticalChart.putExtra("loseNum", loseNum);
                startActivity(intentStatisticalChart);
                break;

            default:
                break;
        }
    }
}
