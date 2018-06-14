package kz.codemaker.www.jumbaq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class ResultActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    ConstraintLayout layout;
    ImageView image;
    TextView tvAnswer;
    TextView tvCoins;
    Button buttonNext;
    Animation slide_down;
    Animation zoom_enter;
    Intent intent;
    int givedCoins;
    int countUp=0;
    int numberOfImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //admob
        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.money);
        mediaPlayer.start();

        //endofadmob
        sharedPreferences = getSharedPreferences("MySaveData", Context.MODE_PRIVATE);
        image = findViewById(R.id.image_result);
        tvAnswer = findViewById(R.id.tv_result);
        tvCoins=findViewById(R.id.tv_result_coin);
        tvCoins.setText("0");
        buttonNext = findViewById(R.id.button_next);
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        zoom_enter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_enter);
         layout = findViewById(R.id.constraintCongrat);
         //get data from shared preference
        givedCoins = sharedPreferences.getInt("giveCoin",0);
        Log.i("Gived Coins",""+givedCoins);
        tvAnswer.setText(sharedPreferences.getString("answer","Жауап"));
        numberOfImage  = sharedPreferences.getInt("jumbaq",0)-1;
        image.setImageResource(Vars.listImages[numberOfImage-1]);
        layout.startAnimation(zoom_enter);
        layout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new CountDownTimer(givedCoins*50,50){
                    @Override
                    public void onTick(long l) {
                        if(givedCoins!=countUp){
                            countUp++;
                         tvCoins.setText(""+countUp);
                        }
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            intent = new Intent(getApplicationContext(),MainActivity.class);
            layout.startAnimation(slide_down);
            layout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    layout.setVisibility(View.INVISIBLE);

                startActivity(intent);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            }
        });
    }
}
