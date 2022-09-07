package com.example.assignment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] emailType={"<other>", "@yahoo.com", "@qq.com", "@hotmail.com", "@gmail.com"};
    String emailSelectedType = "<other>";

    public static final String PREFS_NAME = "account_of_1520";

    private EditText edName, edDOB, edPhoneNum, edEmail;
    DateFormat fmtDate = DateFormat.getDateInstance();
    Calendar myCalendar= Calendar.getInstance();

    private String playerName, phoneNum, email, DOB;


    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edName = (EditText) findViewById(R.id.edName);
        edDOB = (EditText) findViewById(R.id.edDOB);
        edPhoneNum = (EditText) findViewById(R.id.edPhoneNum);
        edEmail = (EditText) findViewById(R.id.edEmail);

        Spinner spiEmail=(Spinner)findViewById(R.id.spiEmail);
        spiEmail.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa=new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, emailType);
        aa.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spiEmail.setAdapter(aa);

        settings = getSharedPreferences(PREFS_NAME, 0);
        playerName = settings.getString("PlayerName", "");
        DOB = settings.getString("DOB", "");
        phoneNum = settings.getString("PhoneNum", "");
        email = settings.getString("Email", "");

        edName.setText(playerName);
        edPhoneNum.setText(phoneNum);
        edDOB.setText(DOB);
        edEmail.setText(email);
    }

    public void onItemSelected(AdapterView parent, View v, int position, long id){
        emailSelectedType = emailType[position];
    }
    public void onNothingSelected(AdapterView parent){
        emailSelectedType = "<other>";
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            edDOB.setText(fmtDate.format(myCalendar.getTime()));
        }
    };


    public void setDate(android.view.View v) {
        new DatePickerDialog(Register.this, d,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void reset(android.view.View v){
        edName.setText("");
        edDOB.setText("");
        edPhoneNum.setText("");
        edEmail.setText("");
    }

    public void save(android.view.View v){
        playerName = edName.getText().toString();
        phoneNum = edPhoneNum.getText().toString();
        DOB = edDOB.getText().toString();
        email = edEmail.getText().toString();
        if(playerName.length() <= 0 || phoneNum.length() <= 0 || DOB.length() <= 0 || email.length() <= 0 ){
            Toast.makeText(Register.this, "Please fill in all the information first!", Toast.LENGTH_LONG).show();
        }else if(playerName.length() > 8){
            Toast.makeText(Register.this, "The player name can't more than 8 characters!", Toast.LENGTH_LONG).show();
        }else {
            if(emailSelectedType == "<other>") {
                settings.edit()
                        .putString("PlayerName", playerName)
                        .putString("DOB", DOB)
                        .putString("PhoneNum", phoneNum)
                        .putString("Email", email)
                        .commit();
            }else{
                settings.edit()
                        .putString("PlayerName", playerName)
                        .putString("DOB", DOB)
                        .putString("PhoneNum", phoneNum)
                        .putString("Email", email+emailSelectedType)
                        .commit();
            }
            Intent result = new Intent();
            result.putExtra("PlayerName", playerName);
            result.putExtra("PlayerName", playerName);
            setResult(RESULT_OK, result);
            super.finish();
        }
    }
}
