package kz.codemaker.www.jumbaq;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

import static kz.codemaker.www.jumbaq.Vars.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final int REWARD = 150;
    int coins = 0;
    int giveCoin = 100;
    int numberOfLetters = 0;
    ConstraintLayout constraintLayout;
    Button[] buttons;
    TextView[] tvAnswer;
    TextView tvStatus;
    TextView tvJumbaq;
    TextView tvCoin;
    SharedPreferences sharedPreferences;
    String jumbaq;
    String answer;
    int numberOfJumbak;
    int colorGray;
    int colorRed;
    int amountLetter;
    Animation slide_up;
    Animation slide_down;
    HashMap<TextView, Button> answerButtonRelationship;
    SharedPreferences.Editor editor;
    Dialog dialog;
    LinearLayout getCoin;
    InterstitialAd interstitialAd;
    MediaPlayer sound;
    ImageView ivSound;
    Random randomGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVars();
        setTimer();
        rewardInit();
        setMusic();

    }


    private void initVars() {
        dialog = new Dialog(this);

        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        constraintLayout = findViewById(R.id.constraintMain);
        constraintLayout.setAnimation(slide_up);
        buttons = new Button[12];
        amountLetter = 0;
        answerButtonRelationship = new HashMap<>();
        ivSound = findViewById(R.id.imageViewSound);
        tvCoin = findViewById(R.id.tv_coins);
        buttons[0] = findViewById(R.id.buttonTopOne);
        buttons[1] = findViewById(R.id.buttonTopTwo);
        buttons[2] = findViewById(R.id.buttonTopThree);
        buttons[3] = findViewById(R.id.buttonTopFour);
        buttons[4] = findViewById(R.id.buttonTopFive);
        buttons[5] = findViewById(R.id.buttonTopSix);
        buttons[6] = findViewById(R.id.buttonBottomOne);
        buttons[7] = findViewById(R.id.buttonBottomTwo);
        buttons[8] = findViewById(R.id.buttonBottomThree);
        buttons[9] = findViewById(R.id.buttonBottomFour);
        buttons[10] = findViewById(R.id.buttonBottomFive);
        buttons[11] = findViewById(R.id.buttonBottomSix);
        tvAnswer = new TextView[9];
        tvAnswer[0] = findViewById(R.id.tv_letter1);
        tvAnswer[1] = findViewById(R.id.tv_letter2);
        tvAnswer[2] = findViewById(R.id.tv_letter3);
        tvAnswer[3] = findViewById(R.id.tv_letter4);
        tvAnswer[4] = findViewById(R.id.tv_letter5);
        tvAnswer[5] = findViewById(R.id.tv_letter6);
        tvAnswer[6] = findViewById(R.id.tv_letter7);
        tvAnswer[7] = findViewById(R.id.tv_letter8);
        tvAnswer[8] = findViewById(R.id.tv_letter9);
        for (int i = 0; i < tvAnswer.length; i++) {
            tvAnswer[i].setText("");
            tvAnswer[i].setVisibility(View.GONE);
        }
        tvStatus = findViewById(R.id.tv_status);
        tvJumbaq = findViewById(R.id.textViewJumbaq);
        colorGray = getResources().getColor(R.color.gray);
        colorRed = getResources().getColor(R.color.red);



        sharedPreferences = getSharedPreferences("MySaveData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        numberOfJumbak = sharedPreferences.getInt("jumbaq", 0);
        coins = sharedPreferences.getInt("coins",200);
        tvCoin.setText(""+coins);
        if (numberOfJumbak == 0 || numberOfJumbak > 71) {

            editor.putInt("jumbaq", 1);
            editor.apply();
            numberOfJumbak = 1;
        }
        int jumbaqIndex = numberOfJumbak * 2 - 2;
        int answerIndex = numberOfJumbak * 2 - 1;
        jumbaq = listJumbaqAnswer[jumbaqIndex];
        answer = listJumbaqAnswer[answerIndex].toUpperCase();

        tvJumbaq.setText(jumbaq);
        //set answer letters to buttons
         randomGenerator = new Random();
        int i = 0;

        while (i < answer.length()) {
            // int randomNum = rand.nextInt((max - min) + 1) + min;
            int randomNum = randomGenerator.nextInt(buttons.length);
            if (buttons[randomNum].getText().toString().equals("Q")) {
                tvAnswer[i].setVisibility(View.VISIBLE);
                tvAnswer[i].setOnClickListener(this);
                buttons[randomNum].setText("" + answer.charAt(i++));

            }
        }
        for (i = 0; i < buttons.length; i++) {
            if (buttons[i].getText().toString().equals("Q")) {
                int randomNum = randomGenerator.nextInt(listLetters.length);
                buttons[i].setText(listLetters[randomNum]);
            }
            buttons[i].setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view) {
        if(interstitialAd.isLoaded() && numberOfJumbak%3==0){
            interstitialAd.show();
        }
        int id = view.getId();
        Button button = null;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getId() == id) {
                button = buttons[i];
            }
        }
        if (numberOfLetters <= answer.length()) {
            for (int k = 0; k < answer.length() && button != null; k++) {
                if (tvAnswer[k].getText().toString().equals("")) {
                    answerButtonRelationship.put(tvAnswer[k], button);
                    tvAnswer[k].setText(button.getText());
                    button.setVisibility(View.INVISIBLE);
                    numberOfLetters++;

                    break;
                }
            }
        }
        if(numberOfLetters==answer.length()){
            checkAnswer();
        }

        //for textView

        for (int k = 0; k < answer.length(); k++) {
            if(tvAnswer[k].getId()==view.getId()){
                button = answerButtonRelationship.get(tvAnswer[k]);
                if(button!=null) {
                    answerButtonRelationship.remove(tvAnswer[k]);
                    tvAnswer[k].setText("");
                    button.setVisibility(View.VISIBLE);
                    numberOfLetters--;
                        tvStatus.setText("Әріпті ашу үшін үстіне бас");
                        tvStatus.setTextColor(colorGray);

                }else{
                    if(tvAnswer[k].getText().toString().equals(""))showPopUp(tvAnswer[k],k);
                }
            }
        }

    }



    private void checkAnswer(){
        String userAnswer="";
        for (int k = 0; k < answer.length(); k++) {
            userAnswer+=tvAnswer[k].getText().toString();
        }
        if(userAnswer.toUpperCase().equals(answer)){
            Intent intent = new Intent(getApplicationContext(),ResultActivity.class);

            editor.putString("answer",answer);
            editor.putInt("giveCoin",giveCoin);
            editor.putInt("jumbaq",numberOfJumbak+1);
            editor.putInt("coins",coins+giveCoin);
            editor.apply();
            sound.release();
            Log.i(answer,userAnswer);

            startActivity(intent);
        }else{
            tvStatus.setText("Дұрыс емес!");
            tvStatus.setTextColor(colorRed);
        }
    }

    private void setTimer(){
        new CountDownTimer(120000,2000){
            @Override
            public void onTick(long l) {
                giveCoin--;
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void showPopUp(final TextView tv, final int index){
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.popup, null);

        dialog.setContentView(dialoglayout);

        Button open = dialog.findViewById(R.id.button_open);
        Button close = dialog.findViewById(R.id.button_close);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(coins>=100){
                    coins-=100;
                    tvCoin.setText(""+coins);
                    editor.putInt("coins",coins);
                    editor.apply();
                    tv.setText(""+answer.charAt(index));
                    numberOfLetters++;
                    if(numberOfLetters==answer.length()){
                        checkAnswer();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Тиындар саны жеткіліксіз", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();

        dialog.show();
        //builder.show();
    }

    private void rewardInit(){
        getCoin = findViewById(R.id.linearLayoutGetMoney);
        final RewardedVideoAd rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.loadAd("ca-app-pub-4569939855479038/4177680674",new AdRequest.Builder().build());

        MobileAds.initialize(this);

        getCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rewardedVideoAd.isLoaded()){
                    rewardedVideoAd.show();
                }else{
                    Toast.makeText(MainActivity.this, "Смартфоныңыздың интернетке қосылысын тексеріңіз", Toast.LENGTH_SHORT).show();
                    rewardedVideoAd.loadAd("ca-app-pub-4569939855479038/4177680674",new AdRequest.Builder().build());


                }
            }
        });
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                rewardedVideoAd.loadAd("ca-app-pub-4569939855479038/4177680674",new AdRequest.Builder().build());

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                coins+=REWARD;
                tvCoin.setText(""+coins);
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }
        });


        interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId("ca-app-pub-4569939855479038/6461773675");
        interstitialAd.loadAd(new AdRequest.Builder().build());

    }


    private void setMusic() {
        int randomSoundNumber = randomGenerator.nextInt(listOfSounds.length);
        sound = MediaPlayer.create(this, listOfSounds[randomSoundNumber]);
        Log.i("Hello","Hello");
        ivSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundOn=!soundOn;
                ivSound.setImageResource(getSoundImage());
                if(soundOn){
                    Log.i("Sound","Start");
                    sound.start();

                }else {
                    sound.pause();
                }
            }
        });
    }
}
