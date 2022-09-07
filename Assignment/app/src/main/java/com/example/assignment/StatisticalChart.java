package com.example.assignment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class StatisticalChart extends Activity {
    int winNum, loseNum, totalNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        winNum = getIntent().getIntExtra("winNum", 0);
        loseNum = getIntent().getIntExtra("loseNum", 0);
        totalNum = winNum + loseNum;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new Panel(this));
    }

    int panelHeight;
    int panelWidth;


    class Panel extends View {
        public Panel(Context context) {
            super(context);
        }

        @Override
        public void onDraw(Canvas c) {
            super.onDraw(c);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFFFFFFA0);
            c.drawPaint(paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(50);
            paint.setTypeface(Typeface.SERIF);
            c.drawText("Statistical Charts : ", 20, 50, paint);

            int winPercentage = (int)(((double)winNum/(double)totalNum)*100);
            int losePercentage = 0;
            if(winPercentage!=0) {
                losePercentage = 100 - winPercentage;
            }else{
                losePercentage = (int)(((double)loseNum/(double)totalNum)*100);
            }
            paint.setColor(0xFF00FF00);
            c.drawRect(150, 600 - (winPercentage*5), 200, 600, paint);
            paint.setColor(0xFFFF0000);
            c.drawRect(350, 600 - (losePercentage*5), 400, 600, paint);

            paint.setColor(0xFF006400);
            paint.setTextSize(40);
            paint.setTypeface(Typeface.SERIF);
            c.drawText("Win : ", 40, 590, paint);

            paint.setColor(0xFFFF0000);
            paint.setTextSize(40);
            paint.setTypeface(Typeface.SERIF);
            c.drawText("Lose : ", 230, 590, paint);

            paint.setColor(0xFF006400);
            paint.setTextSize(20);
            paint.setTypeface(Typeface.SERIF);
            c.drawText(winPercentage + "%, win game = " + winNum, 40, 630, paint);

            paint.setColor(0xFFFF0000);
            paint.setTextSize(20);
            paint.setTypeface(Typeface.SERIF);
            c.drawText(losePercentage + "%, lose game = " + loseNum, 230, 630, paint);

            paint.setColor(0xFF0000FF);
            paint.setTextSize(40);
            paint.setTypeface(Typeface.SERIF);
            c.drawText("Total game played = " + totalNum, 20, 730, paint);
        }
    }
}
