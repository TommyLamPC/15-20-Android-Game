package com.example.assignment;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ShowGameLog extends ListActivity {
    SQLiteDatabase db;
    String[] record;
    String[][] recordInfo;
    String[] nullRecord = {""};
    String[][] nullRecordInfo = {{"", ""}};
    int countRecord = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_game_log);

        setRecord();
    }

    public void setRecord(){
        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/Gameslog", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL("CREATE TABLE IF NOT EXISTS GamesLog( gameDate VARCHAR(10) NOT NULL, gameTime VARCHAR(8) NOT NULL, opponentName VARCHAR(10) NOT NULL, winOrLost VARCHAR(4) NOT NULL);");
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        Cursor cursor = null;
        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/Gameslog",
                    null, SQLiteDatabase.OPEN_READONLY);
            cursor = db.rawQuery("SELECT count(*) AS 'recordNum' FROM GamesLog;", null);
            if (cursor.moveToNext()) {
                countRecord = cursor.getInt(cursor.getColumnIndex("recordNum"));
            };
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if(countRecord != 0) {
            try {
                db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/Gameslog",
                        null, SQLiteDatabase.OPEN_READONLY);
                String dataStr = "";
                cursor = db.rawQuery("SELECT * /*, count(*) AS 'recordNum'*/ FROM GamesLog;", null);
                record = new String[countRecord];
                recordInfo = new String[countRecord][2];
                int i = 0;
                while (cursor.moveToNext()) {
                    String gameDate = cursor.getString(cursor.getColumnIndex("gameDate"));
                    String gameTime = cursor.getString(cursor.getColumnIndex("gameTime"));
                    String opponentName = cursor.getString(cursor.getColumnIndex("opponentName"));
                    String winOrLost = cursor.getString(cursor.getColumnIndex("winOrLost"));
                    dataStr = "Date&Time : " + gameDate + ", " + gameTime + "\nOpponent : " + opponentName + ", Result : " + winOrLost;
                    recordInfo[i][0] = gameDate;
                    recordInfo[i][1] = gameTime;
                    record[i++] = dataStr;
                }
                db.close();
            } catch (SQLiteException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else{
            record = nullRecord;
            recordInfo = nullRecordInfo;
        }
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , record));
    }

    public void onListItemClick(ListView parent, View v, int position, long id){
        //Toast.makeText(this, record[position], Toast.LENGTH_LONG).show();
        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.assignment/Gameslog", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL("DELETE FROM GamesLog WHERE gameDate = '" + recordInfo[position][0] + "' AND gameTime = '" + recordInfo[position][1] + "';");
            Toast.makeText(this, "Record(" + recordInfo[position][0] + ", " + recordInfo[position][1] + ") was deleted successfully!", Toast.LENGTH_LONG).show();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        setRecord();
    }

    public void exit(android.view.View v) {
        super.finish();
    }
}
