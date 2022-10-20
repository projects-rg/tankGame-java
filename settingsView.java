package com.example.thomas.tankwar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class settingsView extends AppCompatActivity {

    int images[];       //array holding image pointers to different sets of tank sprites
    int diffback;       //pointer to background image
    public static final String EXTRA_REPLY  = "android.example.thomas.tankwar.extra.REPLY";     //used for intent to send back image sprites
    RelativeLayout layout;      //layout reference to carry over background image changes
    ImageView tank;             //on screen image of tank that changes with colours



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_view);
        Intent intent = getIntent();

        layout = (RelativeLayout) findViewById(R.id.menuBackGround);
        images = intent.getIntArrayExtra(MainActivity.EXTRA_MESSAGE);
        diffback = intent.getIntExtra(MainActivity.DIFFBACK_MESSAGE, diffback);
        tank = (ImageView) findViewById(R.id.imageTank);

        setBackground();
        setTank();

        configureBackButton();
        tankColorChange();
    }

    /**
     * sets the background image to one carried between windows
     */
    private void setBackground() {
        layout.setBackgroundResource(diffback);
    }

    /**
     * sets the tank image onscreen to the corresponding new colour
     */
    private void setTank(){
        tank.setImageResource(images[0]);
    }

    /**
     * sets up the listeners for the colour change buttons
     */
    private void tankColorChange() {
        Button tankRed = (Button) findViewById(R.id.tankRed);
        tankRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images = new int[]{
                        R.drawable.enemy1,
                        R.drawable.enemy2,
                        R.drawable.enemy3,
                        R.drawable.enemy4,
                        R.drawable.enemy5,
                        R.drawable.enemy6,
                        R.drawable.enemy7,
                        R.drawable.enemy8
                };
                setTank();
                returnReply(v);
            }
        });

        Button tankBlue = (Button) findViewById(R.id.tankBlue);
        tankBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images = new int[]{
                        R.drawable.blue1,
                        R.drawable.blue2,
                        R.drawable.blue3,
                        R.drawable.blue4,
                        R.drawable.blue5,
                        R.drawable.blue6,
                        R.drawable.blue7,
                        R.drawable.blue8
                };
                setTank();
                returnReply(v);
            }
        });

        Button tankGreen = (Button) findViewById(R.id.tankGreen);
        tankGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                images = new int[]{
                        R.drawable.tankleft,
                        R.drawable.tankright,
                        R.drawable.tankup,
                        R.drawable.tankdown,
                        R.drawable.tankupl,
                        R.drawable.tankupr,
                        R.drawable.tankdownr,
                        R.drawable.tankdownl
                };
                setTank();
                returnReply(v);
            }
        });
    }

    /**
     * sets up the back button to go to the previous screen
     */
    private void configureBackButton(){
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * the reply to the intent that spawned the window, sending back the new tank sprites
     */
    public void returnReply(View view) {
        Intent replyIntent = new Intent();

        replyIntent.putExtra(EXTRA_REPLY, images);
        setResult(RESULT_OK, replyIntent);
    }
}