package com.example.assignment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GameStart extends AppCompatActivity {
    private MediaPlayer winSound, loseSound, bgm;
    private ImageView ivOppPlayerPhoto;
    private TextView tvOppPlayerInfo, tvMyName, tvMyGuess, tvOppGuess, tvTurn;
    private ImageView ivOppLeftHand, ivOppRightHand;
    private ImageView ivMyLeftHand, ivMyRightHand;
    private Button btnSubmit, btnGuess0, btnGuess5, btnGuess10, btnGuess15, btnGuess20;
    private FetchPageTask task = null;

    private int pID, oppLeft, oppGuess, oppRight;
    private int myLeft, myRight, myGuess;
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat date_df = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat time_df = new SimpleDateFormat("HH:mm:ss");
    private String finishDate, finishTime;
    private String pInfo, pName;
    private boolean isYourTurn, isWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start);

        bgm = MediaPlayer.create(this, R.raw.bgm);
        bgm.setLooping(true);
        bgm.start();
        pID = getIntent().getIntExtra("pID", 0);
        if(pID == 0){
            super.finish();
        }else{
            tvTurn = (TextView)findViewById(R.id.tvTurn);
            ivOppPlayerPhoto = (ImageView)findViewById(R.id.ivOppPlayerPhoto);
            tvOppPlayerInfo = (TextView)findViewById(R.id.tvOppPlayerInfo);
            tvMyName =  (TextView)findViewById(R.id.tvMyName);
            tvMyGuess = (TextView)findViewById(R.id.tvMyGuess);
            tvOppGuess = (TextView)findViewById(R.id.tvOppGuess);
            btnSubmit = (Button)findViewById(R.id.btnSubmit);
            ivOppLeftHand = (ImageView)findViewById(R.id.ivOppLeftHand);
            ivOppRightHand = (ImageView)findViewById(R.id.ivOppRightHand);
            ivMyLeftHand = (ImageView)findViewById(R.id.ivMyLeftHand);
            ivMyRightHand = (ImageView)findViewById(R.id.ivMyRightHand);
            btnGuess0 = (Button)findViewById(R.id.btnGuess0);
            btnGuess5 = (Button)findViewById(R.id.btnGuess5);
            btnGuess10 = (Button)findViewById(R.id.btnGuess10);
            btnGuess15 = (Button)findViewById(R.id.btnGuess15);
            btnGuess20 = (Button)findViewById(R.id.btnGuess20);
            tvTurn.setText("You Guess");
            btnSubmit.setText("GO");
            isYourTurn = true;
            myLeft =  myRight = myGuess = 123;
            tvMyName.setText(getSharedPreferences("account_of_1520", 0).getString("PlayerName", ""));
            pInfo = getIntent().getStringExtra("pInfo");
            pName = getIntent().getStringExtra("pName");
            tvOppPlayerInfo.setText(pName);
            if (pID == 2 || pID == 3 || pID == 5) {
                ivOppPlayerPhoto.setImageResource(R.drawable.female);
            } else {
                ivOppPlayerPhoto.setImageResource(R.drawable.male);
            }
        }
    }

    private class FetchPageTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... values) { // parameters not
            String result = "";
            URL url = null;
            InputStream inputStream = null;

            try {
                url = new URL("https://4qm49vppc3.execute-api.us-east-1.amazonaws.com/Prod/itp4501_api/opponent/" + Integer.toString(pID));
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
                oppLeft = jObj.getInt("left");
                oppGuess = jObj.getInt("guess");
                oppRight = jObj.getInt("right");
                setOppPlay(oppLeft, oppRight);
            } catch (Exception e) {
                String error = e.getMessage();
                Toast.makeText(GameStart.this, error, Toast.LENGTH_LONG).show();
                //textDescription.setText(error);
            }
        }
    }

    public void LeftOpen(android.view.View v){
        setMyLeftHand(true);
    }
    public void LeftClose(android.view.View v){
        setMyLeftHand(false);
    }
    public void RightOpen(android.view.View v){
        setMyRightHand(true);
    }
    public void RightClose(android.view.View v){
        setMyRightHand(false);
    }

    public void guess0(android.view.View v){
        setMyGuess(0);
    }

    public void guess5(android.view.View v){
        setMyGuess(5);
    }

    public void guess10(android.view.View v){
        setMyGuess(10);
    }

    public void guess15(android.view.View v){
        setMyGuess(15);
    }

    public void guess20(android.view.View v){
        setMyGuess(20);
    }

    public void setOppPlay(int leftNum, int rightNum){
        oppLeft = leftNum;
        oppRight = rightNum;
        if(leftNum == 5) {
            ivOppLeftHand.setImageResource(R.drawable.opp_open_hand_l);
        }else if(leftNum == 0) {
            ivOppLeftHand.setImageResource(R.drawable.opp_close_hand_l);
        }else{
            ivOppLeftHand.setImageResource(0);
        }
        if(rightNum == 5) {
            ivOppRightHand.setImageResource(R.drawable.opp_open_hand_r);
        }else if(rightNum == 0) {
            ivOppRightHand.setImageResource(R.drawable.opp_close_hand_r);
        }else{
            ivOppRightHand.setImageResource(0);
        }
        play();
    }

    public void setMyGuess(int guessNum){
        myGuess = guessNum;
        tvMyGuess.setText(Integer.toString(guessNum));
    }

    public void setMyLeftHand(boolean isOpen){
        if(isOpen){
            ivMyLeftHand.setImageResource(0);
            ivMyLeftHand.setImageResource(R.drawable.my_open_hand_l);
            myLeft = 5;
        }else{
            ivMyLeftHand.setImageResource(0);
            ivMyLeftHand.setImageResource(R.drawable.my_close_hand_l);
            myLeft = 0;
        }
    }

    public void setMyRightHand(boolean isOpen){
        if(isOpen){
            ivMyRightHand.setImageResource(0);
            ivMyRightHand.setImageResource(R.drawable.my_open_hand_r);
            myRight = 5;
        }else{
            ivMyRightHand.setImageResource(0);
            ivMyRightHand.setImageResource(R.drawable.my_close_hand_r);
            myRight = 0;
        }
    }

    public void play(){
        btnSubmit.setText("OK");
        //Toast.makeText(GameStart.this, ",OL=" + Integer.toString(oppLeft)+ ",OG=" + Integer.toString(oppGuess)+ ",OR=" + Integer.toString(oppRight)+ ",ML=" + Integer.toString(myLeft)+ ",MG=" + Integer.toString(myGuess)+ ",MR=" + Integer.toString(myRight), Toast.LENGTH_LONG).show();
        if(isYourTurn){
            if ((oppLeft + oppRight + myLeft + myRight) == myGuess) {
                bgm.stop();
                Toast.makeText(GameStart.this, "Congratulations! You won!\nPlease click continue.", Toast.LENGTH_LONG).show();
                btnSubmit.setText("Continue");
                isWin = true;
                winSound = MediaPlayer.create(this, R.raw.you_win);
                winSound.start();
            }
        }else{
            tvOppGuess.setText(Integer.toString(oppGuess));
            if ((oppLeft + oppRight + myLeft + myRight) == oppGuess) {
                bgm.stop();
                Toast.makeText(GameStart.this, "Oops! You lose!\nPlease click continue.", Toast.LENGTH_LONG).show();
                btnSubmit.setText("Continue");
                isWin = false;
                loseSound = MediaPlayer.create(this, R.raw.you_lose);
                loseSound.start();
            }
        }
        if(btnSubmit.getText() == "Continue"){
            finishDate = date_df.format(c.getTime());
            finishTime = time_df.format(c.getTime());
            //Toast.makeText(GameStart.this, finishDate + "\n" + finishTime, Toast.LENGTH_LONG).show();
        }
        isYourTurn = !isYourTurn;
    }

    public void submit(android.view.View v){
        if(btnSubmit.getText() == "GO") {
            if(myLeft != 123 && (myGuess != 123 || isYourTurn == false) && myRight != 123){
                task = new FetchPageTask();
                task.execute();
            }else {
                Toast.makeText(GameStart.this, "Please Click all the required button first!!", Toast.LENGTH_LONG).show();
            }
        }else if(btnSubmit.getText() == "OK") {
            tvMyGuess.setText("");
            ivMyLeftHand.setImageResource(0);
            ivMyRightHand.setImageResource(0);
            tvOppGuess.setText("");
            ivOppLeftHand.setImageResource(0);
            ivOppRightHand.setImageResource(0);
            btnGuess0.setEnabled(isYourTurn);
            btnGuess5.setEnabled(isYourTurn);
            btnGuess10.setEnabled(isYourTurn);
            btnGuess15.setEnabled(isYourTurn);
            btnGuess20.setEnabled(isYourTurn);
            oppLeft = oppRight = myLeft = myRight = oppGuess = myGuess = 123;
            btnSubmit.setText("GO");
            if(isYourTurn){
                tvTurn.setText("You Guess");
            }else{
                if (pID == 2 || pID == 3 || pID == 5) {
                    tvTurn.setText("She Guess");
                } else {
                    tvTurn.setText("He Guess");
                }
            }
        }else if(btnSubmit.getText() == "Continue"){
            finish();
        }
    }

    public void finish() {
        Intent result = new Intent();
        result.putExtra("gameDate", finishDate);
        result.putExtra("gameTime", finishTime);
        result.putExtra("opponentName", pName);
        if(isWin) {
            result.putExtra("winOrLost", "Win");
        }else{
            result.putExtra("winOrLost", "Lose");
        }
        setResult(RESULT_OK, result);
        super.finish();
    }
}
