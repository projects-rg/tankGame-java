package com.example.thomas.tankwar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    TankWarView tankWarView;        //the game view
    Display display;                //the device display
    Point size;                     //used to get the size of the display
    Context context;
    RelativeLayout layout;          //used to change the background based on the background image

    int images[];           //holds the current set of tank sprites
    public static final int IMAGE_REQUEST = 1;      //request code for the tank change intent
    public static final String EXTRA_MESSAGE = "android.example.thomas.tankwar.extra.SPRITE";

    int diffback;           //holds the current background of the game
    public static final  int BACK_REQUEST = 2;      //request code for the background change intent
    public static final String DIFFBACK_MESSAGE = "android.example.thomas.tankwar.extra.BACKGROUND";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        images = new int[]{
                R.drawable.tankleft,
                R.drawable.tankright,
                R.drawable.tankup,
                R.drawable.tankdown,
                R.drawable.tankupl,
                R.drawable.tankupr,
                R.drawable.tankdownr,
                R.drawable.tankdownl,
        };

        diffback = R.drawable.background;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        context = this;

        display.getSize(size);
        layout = (RelativeLayout) findViewById(R.id.menuBackGround);

        configurePlay();
        configureSettings();
        configureBackButton();
        configureBackground();
    }

    /**
     * sets up the tank customise menu and sends the intent to retrieve the newly chosen sprites
     */
    private void configureSettings() {
        Button settingsButton = (Button) findViewById(R.id.buttonTank);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, settingsView.class);
                intent.putExtra(EXTRA_MESSAGE, images);
                intent.putExtra(DIFFBACK_MESSAGE, diffback);
                startActivityForResult(intent, IMAGE_REQUEST);
            }
        });
    }

    /**
     * used to retrieve data from other views - changing tank sprites and changing the background
     * @param requestCode will be 1 or 2 depending on the intent - 1 for tank change, 2 for background change
     * @param data will either return an integer or integer array depending on the request code result
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                images = data.getIntArrayExtra(settingsView.EXTRA_REPLY);
            }
        }

        if(requestCode == BACK_REQUEST){
            if(resultCode == RESULT_OK) {
                diffback = data.getIntExtra(backgroundView.EXTRA_THING, diffback);
                layout.setBackgroundResource(diffback);
            }
        }
    }


    private void configurePlay(){
        Button playButton = (Button) findViewById(R.id.buttonPlay);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tankWarView = new TankWarView(context, size.x, (size.y-(size.y/10)), images, diffback);
                setContentView(tankWarView);
            }
        });
    }

    /**
     * sets up the back button to close the game
     */
    private void configureBackButton(){
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

    /**
     * sets up the button to send user to the background window, with intent to retrieve the new background image
     */
    private void configureBackground(){
        Button bgroundButton = (Button) findViewById(R.id.buttonBackground);
        bgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, backgroundView.class);
                intent.putExtra(EXTRA_MESSAGE, images);
                intent.putExtra(DIFFBACK_MESSAGE, diffback);
                startActivityForResult(intent, BACK_REQUEST);
            }
        });
    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        if(tankWarView != null)
            tankWarView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        if(tankWarView != null)
            tankWarView.pause();
    }
}