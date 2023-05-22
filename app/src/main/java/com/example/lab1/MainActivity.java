package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifDrawable;


import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btn, btn2, btn3;
    ImageView add_btn,next_btn,del_btn,gifImageView;
    TextView qtn_view,text_question,textCount;
    String qtn,ans1,ans2,ans3;
    int index = 0;
    int y;
    String row = String.valueOf(index);
    String[][] lastQtn = new String[1][4];

    private Handler handler;
    private GifDrawable gifDrawable;

    //variable qui contiendra la liste des questions et reponses
    String[][] ListQtn = new String[1][4];

    private static final long counter = 30000;
    private ColorStateList textColorDefaultRb;
    private ColorStateList getTextColorDefaultCd;
    private CountDownTimer countDownTimer;
    private long timeIsOver;


    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeIsOver,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeIsOver = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeIsOver = 0;
                updateCountDownText();

            }
        }.start();
    }

    private void updateCountDownText(){
        int minutes = (int) ((timeIsOver/1000)/60);
        int seconds = (int) ((timeIsOver/1000)%60);

        String time = String.format(Locale.getDefault(),"%02d",seconds);
        textCount.setText(time);

        if(timeIsOver < 10000)
        {
            textCount.setTextColor(Color.RED);
        }
        else
        {
            textCount.setTextColor(getTextColorDefaultCd);
        }
    }

    private void loadGifAnimation() {
        try {
            // Load the GIF animation from the raw resource
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.raw.confetti);

            // Set the GIF drawable to the ImageView
            gifImageView.setImageDrawable(gifDrawable);

            // Start the animation
            gifDrawable.start();


            // Stop the animation after 5 seconds (adjust the delay as needed)
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //stopGifAnimation();
                    gifImageView.setVisibility(View.INVISIBLE);
                }
            }, 2000); // 5 seconds

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn = findViewById(R.id.btn_answer1);
        btn2 = findViewById(R.id.btn_answer2);
        btn3 = findViewById(R.id.btn_answer3);
        add_btn = findViewById(R.id.ic_add);
        next_btn = findViewById(R.id.ic_next);
        text_question = findViewById(R.id.text_question);
        del_btn = findViewById(R.id.ic_del);
        textCount = findViewById(R.id.count);
        getTextColorDefaultCd = textCount.getTextColors();
        gifImageView = findViewById(R.id.gif_image_view);

        //Compteur
        timeIsOver = counter;
        startCountDown();

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                loadGifAnimation();
                if (view.getId() == R.id.btn_answer2) {
                    if(view.getId() == R.id.btn_answer2 )
                    {
                        btn2.setBackgroundColor(Color.parseColor("#FF0000"));
                        btn3.setBackgroundColor(Color.parseColor("#FFBB86FC"));

                    }

                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                if(view.getId() == R.id.btn_answer3 )
                {
                    btn3.setBackgroundColor(Color.parseColor("#FF0000"));
                    btn2.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                if(view.getId() == R.id.btn_answer1 )
                {
                    btn.setBackgroundColor(Color.parseColor("#00FF00"));
                    btn2.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                    btn3.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                }
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                Intent i = new Intent(new Intent(getApplicationContext(),AddActivity.class));
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });



        next_btn.setOnClickListener(new View.OnClickListener() {

            Database db = new Database(getApplicationContext(),"flashcard",null,1);
            @Override
            public void onClick(View view) {

                y = db.lengthRow();
                Animation slideOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out);
                Animation slideInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in);


                //reinistialiser la couleur des boutons
                btn.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                btn2.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                btn3.setBackgroundColor(Color.parseColor("#FFBB86FC"));

                //Compteur pr repondre
                timeIsOver = counter;
                countDownTimer.cancel();
                startCountDown();
                //Condition pour eviter que l'app plante quand il n'y a plus de ligne a lire dans la BD
                if(index < db.lengthRow()){

                    for (int i = 0; i < 1; i++) {
                        for (int j = 0; j < 4; j++) {
                            ListQtn[i][j] = db.callQuestion(index)[0][j];
                            lastQtn[i][j] = db.callQuestion(y)[0][j];

                        }

                    }

                    text_question.setText(ListQtn[0][0]);
                    btn.setText(ListQtn[0][1]);
                    btn2.setText(ListQtn[0][2]);
                    btn3.setText(ListQtn[0][3]);

                    // Application de l'animation de sortie sur la vue actuelle
                    text_question.startAnimation(slideOutAnimation);
                    btn.startAnimation(slideOutAnimation);
                    btn2.startAnimation(slideOutAnimation);
                    btn3.startAnimation(slideOutAnimation);

                }
                else {

                    for (int i = 0; i < 1; i++) {
                        for (int j = 0; j < 4; j++) {
                            ListQtn[i][j] = db.callQuestion(index)[0][j];
                            lastQtn[i][j] = db.callQuestion(y)[0][j];
                        }

                    }

                    //Creation du snackbar
                    Snackbar snackbar = Snackbar.make( btn3,"\t\t\t\t\t\t\t\t Il n'y a plus de question",Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#F11515"));
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                    params.setMargins(180,0,180,300);
                    snackbarView.setLayoutParams(params);
                    snackbar.show();

                    //Bloquer sur une question qd il n'y plus de question dans la base de donnee
                    text_question.setText("Who is the 44th President of the United States?");
                    btn.setText("Barack Obama");
                    btn2.setText("Bill Clinton");
                    btn3.setText("George H. W. Bush");

                    next_btn.setVisibility(View.INVISIBLE);
                }

                db.close();
                index++;

            }
        });


        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Database db = new Database(getApplicationContext(),"flashcard",null,1);
                Log.i("a","index "+index);
                db.delete(index);
                Toast.makeText(MainActivity.this, "Suppression reussie", Toast.LENGTH_SHORT).show();
                y = db.lengthRow();

                //Condition pour afficher la ligne suivante quand on supprime un ligne
                if(index < y){
                    Log.i("","if1");
                    for (int i = 0; i < 1; i++) {
                        for (int j = 0; j < 4; j++) {
                            ListQtn[i][j] = db.callQuestion(index-1)[0][j];

                        }

                    }

                    text_question.setText(ListQtn[0][0]);
                    btn.setText(ListQtn[0][1]);
                    btn2.setText(ListQtn[0][2]);
                    btn3.setText(ListQtn[0][3]);

                }
                else {
                    Log.i("","if2");
                    for (int i = 0; i < 1; i++) {
                        for (int j = 0; j < 4; j++) {
                            ListQtn[i][j] = db.callQuestion(index+1)[0][j];
                            lastQtn[i][j] = db.callQuestion(y)[0][j];
                        }

                    }

                    //Creation du snackbar
                    Snackbar snackbar = Snackbar.make( btn3,"\t\t\t\t\t\t\t\t Il n'y a plus de question",Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.parseColor("#F11515"));
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                    params.setMargins(180,0,180,300);
                    snackbarView.setLayoutParams(params);
                    snackbar.show();

                    //Bloquer sur une question qd il n'y plus de question dans la base de donnee
                    text_question.setText("Who is the 44th President of the United States?");
                    btn.setText("Barack Obama");
                    btn2.setText("Bill Clinton");
                    btn3.setText("George H. W. Bush");

                    next_btn.setVisibility(View.INVISIBLE);
                }

                db.close();
                index++;

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer !=null )
        {
            countDownTimer.cancel();
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}