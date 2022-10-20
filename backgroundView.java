package com.example.thomas.tankwar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class backgroundView extends AppCompatActivity {

    int diffback;       //background image that may change
    public static final String EXTRA_THING = "android.example.thomas.tankwar.extra.BACKREPLY";  //used for the intent sending back the background image
    RelativeLayout layout;      //used to access the layout background to change it corresponding to the games new background

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_view);
        Intent intent = getIntent();

        layout = (RelativeLayout) findViewById(R.id.menuBackGround);
        diffback = intent.getIntExtra(MainActivity.DIFFBACK_MESSAGE, diffback);
        setBackground();

        changeBackground();
        configureBackButton();
    }

    /**
     *  reply to the intent that opened the window, sending back the new background image
     */
    public void returnBack(View view) {
        Intent backgroundReply = new Intent();

        backgroundReply.putExtra(EXTRA_THING, diffback);
        setResult(RESULT_OK, backgroundReply);
    }

    /**
     * updates the background of the current page to whatever has been selected
     */
    private void setBackground() {
        layout.setBackgroundResource(diffback);
    }

    /**
     * sets up the background buttons to allow multiple backdrops
     */
    private void changeBackground() {
        Button backgroundIce = (Button) findViewById(R.id.backgroundIce);
        backgroundIce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diffback = R.drawable.ice;
                setBackground();
                returnBack(v);
            }
        });

        Button backgroundGrass = (Button) findViewById(R.id.backgroundGrass);
        backgroundGrass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diffback = R.drawable.background;
                setBackground();
                returnBack(v);
            }
        });

        Button backgroundSand = (Button) findViewById(R.id.backgroundSand);
        backgroundSand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diffback = R.drawable.sand;
                setBackground();
                returnBack(v);
            }
        });
    }

    /**
     * sets up the back button to return to the main menu
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
}