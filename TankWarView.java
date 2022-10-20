package com.example.thomas.tankwar;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TankWarView extends SurfaceView implements Runnable{

    //region Variables

    //region Drawing

    //rectangles used for touch input borders
    private Rect top;
    private Rect topLeft;
    private Rect topRight;
    private Rect left;
    private Rect right;
    private Rect downLeft;
    private Rect downRight;
    private Rect down;

    private Bitmap bitmapback;  //background image
    private Canvas canvas;      //the canvas to draw the game onto
    private Paint paint;        //paint used to set the colours of rectangles or text
    private int images[];       //array holding the current tank sprites ids
    private int background;     //id of the current background image

    //endregion

    //region Game

    private Tank tank;          //the player tank
    private Tank enemy;         //the enemy tank
    private Bullet bullet;      //the players bullet
    private Bullet eBullet;     //the enemies bullet
    private Bullet triple1;     //the first extra shot of triple shot
    private Bullet triple2;     //the second extra shot of triple shot
    private PowerUp power;      //the players powerups
    private int score = 0;      //the players score
    private int lives = 5;      //the players lives
    private String difficulty = "Easy";     //the current difficulty modifier
    private String time;        //the game time

    //endregion

    //region System

    private Context context;
    private Thread gameThread = null;
    private SurfaceHolder ourHolder;
    private volatile boolean playing;
    private boolean paused = true;
    private int fps;
    private long timeThisFrame;
    private int screenX;                //the end of the screen's x co-ordinate
    private int screenY;                //the end of the screen's Y co-ordinate
    private int game0X;                 //the beginning of the game area on screen's x co-ordinate
    private int game0Y;                 //the beginning of the game area on screen's y co-ordinate
    private int gameX;                  //the end of the game area on screen's x co-ordinate
    private int gameY;                  //the end of the game area on screen's y co-ordinate

    //endregion

    //region Timer

    private int seconds = 0;
    private int minutes;
    private int spawn = 0;      //used to calculate when the next powerup will spawn

    //timer that measure the current game time and allows the powerups to spawn
    private Handler gameHandler = new Handler();
    private Runnable gameRunnable = new Runnable() {

        @Override
        public void run() {
            seconds ++;
            minutes += seconds / 60;
            seconds = seconds % 60;

            time = (String.format("%d:%02d", minutes, seconds));

            //will generate a powerup every ten seconds after one has despawned
            if (power.getActive() == false && ((minutes * 60) + seconds) - spawn == 10) {
                power.generatePower();
                spawnHandler.postDelayed(spawnRunnable, 7000);
            }

            gameHandler.postDelayed(this, 1000);
        }
    };

    //removes the powerup after seven seconds of activity
    private Handler spawnHandler = new Handler();
    private Runnable spawnRunnable = new Runnable() {

        @Override
        public void run() {
            power.initialisePowerup();
            setSpawn();
        }
    };

    //removes the effects of the power up after 10 seconds of usage
    private Handler powerHandler = new Handler();
    private Runnable powerRunnable = new Runnable() {

        @Override
        public void run(){
            power.deactivate();
            spawnHandler.postDelayed(spawnRunnable, 0);
        }
    };

    //endregion

    //endregion

    //region Constructor

    /**
     * base constructor for the tankView, sets the screen co-ordinates, takes in the potential changes to tank and background images
     * and builds the level
     * @param x x co-ordinate at end of screen
     * @param y y co-ordinate at end of screen
     * @param images the set of tank sprites to be used
     * @param diffback the background image to be used
     */
    public TankWarView(Context context, int x, int y, int images[], int diffback) {
        super(context);
        this.context = context;
        this.images = images;
        background = diffback;

        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;
        prepareLevel();

    }

    //endregion

    //region Initial Setup

    /**
     * adds all of the game objects to the screen and starts the timer
     */
    private void prepareLevel(){
        setBorderButtons();

        tank = new Tank(context, screenX, screenY, images);
        bullet = new Bullet(context, screenX);

        enemy = new Tank(context, screenX, screenY, images);
        eBullet = new Bullet(context, screenX);

        power = new PowerUp(context, game0X, game0Y, gameX, gameY);
        power.setPlayer(tank);

        triple1 = new Bullet(context, screenX);
        triple2 = new Bullet(context, screenX);

        tank.setEnemy(enemy);
        enemy.setEnemy(tank);
        enemy.isEnemy();
        AiTank();

        setBackground(background);

        gameHandler.postDelayed(gameRunnable, 0);

        resume();
    }

    /**
     * allows for setting of the background image
     * @param background the image to set the background to
     */
    public void setBackground(int background) {
        bitmapback = BitmapFactory.decodeResource(context.getResources(), background);
        bitmapback = Bitmap.createScaledBitmap(bitmapback, (int) (screenX), (int) (screenY),false);
    }

    /**
     * sets the positions of the white and black outer edge rectangles
     * used for marking where touch inputs are valid
     */
    private void setBorderButtons() {
        top = new Rect();
        topLeft = new Rect();
        topRight = new Rect();
        left = new Rect();
        right = new Rect();
        downLeft = new Rect();
        downRight = new Rect();
        down = new Rect();

        top.top = 0;
        top.left = screenX/7;
        top.bottom = screenY/14;
        top.right = screenX - screenX/7;

        topLeft.top = 0;
        topLeft.left = 0;
        topLeft.bottom = screenY/9;
        topLeft.right = screenX/6;

        topRight.top = 0;
        topRight.left = screenX - screenX/6;
        topRight.bottom = screenY/9;
        topRight.right = screenX;

        left.top = screenY/14;
        left.left = 0;
        left.bottom = screenY - screenY/14;
        left.right = screenX/7;

        right.top = screenY/14;
        right.left = screenX - screenX/7;
        right.bottom = screenY - screenY/14;
        right.right  = screenX;

        downLeft.top = screenY - screenY/9;
        downLeft.left = 0;
        downLeft.bottom = screenY;
        downLeft.right = screenX/6;

        downRight.top = screenY-screenY/9;
        downRight.left = screenX-screenX/6;
        downRight.bottom = screenY;
        downRight.right = screenX;

        down.top = screenY - screenY/14;
        down.left = screenX/7;
        down.bottom = screenY;
        down.right = screenX - screenX/7;

        game0X = left.right;
        game0Y = top.bottom;

        gameX = right.left;
        gameY = down.top;

    }

    /**
     * sets the spawn marker equal to the current time
     */
    public void setSpawn(){
        spawn = (minutes * 60) + seconds;
    }

    //endregion

    //region System

    @Override
    public void run() {
        while (playing) {
            long startFrameTime = System.currentTimeMillis();
            if(!paused)
                update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1)
                fps = (int)(1000 / timeThisFrame);
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void update(){
        tank.update(fps);

        if(bullet.getStatus()) {
            bullet.update(fps);
            bulletCollisions(bullet);
        }

        if(triple1.getStatus()) {
            triple1.triple1Update(fps);
            bulletCollisions(triple1);
        }

        if(triple2.getStatus()) {
            triple2.triple2Update(fps);
            bulletCollisions(triple2);
        }

        checkCollisions(tank);
        //checkCollisions(enemy);
    }

    //endregion

    //region Drawing

    /**
     * draws the objects to screen depending on their level of activity
     */
    private void draw(){
        if (ourHolder.getSurface().isValid()) {

            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 26, 128, 182));
            canvas.drawBitmap(bitmapback, game0X, game0Y, paint);
            canvas.drawBitmap(tank.getBitmap(), tank.getX(), tank.getY(), paint);
            canvas.drawBitmap(enemy.getBitmap(), enemy.getX(), enemy.getY(), paint);

            if(eBullet.getStatus())
                canvas.drawBitmap(eBullet.getBitmap(), eBullet.getX(), eBullet.getY(), paint);

            if(bullet.getStatus())
                canvas.drawBitmap(bullet.getBitmap(), bullet.getX(), bullet.getY(), paint);

            if(triple1.getStatus())
                canvas.drawBitmap(triple1.getBitmap(), triple1.getX(), triple1.getY(), paint);

            if(triple2.getStatus())
                canvas.drawBitmap(triple2.getBitmap(), triple2.getX(), triple2.getY(), paint);

            if(power.getSpawn() == true)
                canvas.drawBitmap(power.getBitmap(), power.getX(), power.getY(), paint);

            drawBorderButtons();
            paint.setColor(Color.argb(255,  249, 129, 0));
            paint.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives + "    Difficulty: " + difficulty + "    Time: " + time, 10,50, paint);

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * draws the border buttons to screen, used to denote where touch actions work
     */
    private void drawBorderButtons() {
        Paint brush = new Paint();

        brush.setColor(Color.argb(255, 0, 0, 0));
        canvas.drawRect(top, brush);
        canvas.drawRect(left, brush);
        canvas.drawRect(right, brush);
        canvas.drawRect(down, brush);

        brush.setColor(Color.argb(255, 255, 255, 255));
        canvas.drawRect(topLeft, brush);
        canvas.drawRect(topRight, brush);
        canvas.drawRect(downLeft, brush);
        canvas.drawRect(downRight, brush);
    }

    //endregion

    //region Interaction

    /**
     * determines game actions based on location of touch event
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                paused = false;
                int x = (int)motionEvent.getX();
                int y = (int)motionEvent.getY();
                Rect touch = new Rect();

                touch.top = y;
                touch.left = x;
                touch.bottom = y+3;
                touch.right = x+3;

                //this set of statements uses the pressed border button to determine which direction the tank should move in
                if(Rect.intersects(touch, topLeft))
                    tank.setTrajectory(tank.UPL);
                else if(Rect.intersects(touch, topRight))
                    tank.setTrajectory(tank.UPR);
                else if(Rect.intersects(touch, downRight))
                    tank.setTrajectory(tank.DOWNR);
                else if(Rect.intersects(touch, downLeft))
                    tank.setTrajectory(tank.DOWNL);
                else if(Rect.intersects(touch, left))
                    tank.setTrajectory(tank.LEFT);
                else if(Rect.intersects(touch, right))
                    tank.setTrajectory(tank.RIGHT);
                else if(Rect.intersects(touch, top))
                    tank.setTrajectory(tank.UP);
                else if(Rect.intersects(touch, down))
                    tank.setTrajectory(tank.DOWN);
                //if it is not a border button pressed then the tank will shoot
                else{
                    //first tripleshot will be checked to allow for the other shells to be fired
                    if(power.getTriple()) {
                        if(!bullet.getStatus() && !triple1.getStatus() && !triple2.getStatus()) {
                            triple1.tripleShot1(tank, tank.getTrajectory());
                            triple2.tripleShot2(tank, tank.getTrajectory());
                            bullet.shoot(tank, tank.getTrajectory());
                        }
                    }
                    else
                        bullet.shoot(tank, tank.getTrajectory());
                }
                break;
            case MotionEvent.ACTION_UP:
                tank.stopTank();
                break;
        }
        return true;
    }

    /**
     * tests if the bullet has collided with a tank or of screen
     * @param bullet the bullet to be checked
     */
    private void bulletCollisions(Bullet bullet) {
        if((bullet.getImpactPointY() < game0Y) || (bullet.getImpactPointY() > gameY) || (bullet.getImpactPointX() < game0X) || (bullet.getImpactPointX() > gameX))
            bullet.setInactive();

        //if the bullet hits the enemy tank, it will add to the score based on the difficulty modifier
        if(Rect.intersects(tank.getEnemy().getRect(), bullet.getRect())){
            bullet.setInactive();
            tank.getEnemy().isEnemy();
            if(difficulty.equals("Easy")){
                score += 100;
            }
            else if(difficulty.equals("Medium")){
                score += 200;
            }
            else if(difficulty.equals("Hard")){
                score += 500;
            }
            else{
                score += 1000;
                lives += 2;
            }
        }

        //if the player is hit, they lose lives
        if(Rect.intersects(tank.getRect(), eBullet.getRect())) {
            if(!power.getActive() && power.getCurrent() != 1) {
                if (lives > 0)
                    lives--;
                else {
                    System.out.println("Start menu");
                    eBullet.setInactive();
                }
            }

            eBullet.setInactive();
        }
    }

    /**
     * checks tank collisions with each other or off screen
     * @param tank the tank to check
     */
    private void checkCollisions(Tank tank){

        int width = tank.getLength() / 2;
        int height = tank.getHeight() / 2;

        if (tank.getX() + width < game0X)
            tank.setX(gameX - width);
        else if (tank.getX() > gameX - width)
            tank.setX(game0X - width);
        else if (tank.getY() + height < game0Y)
            tank.setY(gameY - height);
        else if (tank.getY() > gameY - height)
            tank.setY(game0Y - height);

        //if the tanks collide it moves the two in opposing directions based on the point of collision
        if(Rect.intersects(tank.getRect(), tank.getEnemy().getRect())){
            int x1 = tank.getX();
            int y1 = tank.getY();
            int x2 = tank.getEnemy().getX();
            int y2 = tank.getEnemy().getY();

            switch(tank.getTrajectory()) {
                case 1:
                    tank.setX(x1 + 20);
                    tank.getEnemy().setX(x2 - 20);
                    break;
                case 2:
                    tank.setX(x1 - 20);
                    tank.getEnemy().setX(x2 + 20);
                    break;
                case 3:
                    tank.setY(y1 + 20);
                    tank.getEnemy().setY(y2 - 20);
                    break;
                case 4:
                    tank.setY(y1 - 20);
                    tank.getEnemy().setY(y2 +20);
                    break;
                case 5:
                    tank.setX(x1 + 20);
                    tank.setY(y1 + 20);
                    tank.getEnemy().setX(x2 - 20);
                    tank.getEnemy().setY(y2 - 20);
                    break;
                case 6:
                    tank.setX(x1 - 20);
                    tank.setY(y1 + 20);
                    tank.getEnemy().setX(x2 + 20);
                    tank.getEnemy().setY(y2 - 20);
                    break;
                case 7:
                    tank.setX(x1 - 20);
                    tank.setY(y1 - 20);
                    tank.getEnemy().setX(x2 + 20);
                    tank.getEnemy().setY(y2 + 20);
                    break;
                case 8:
                    tank.setX(x1 + 20);
                    tank.setY(y1 - 20);
                    tank.getEnemy().setX(x2 - 20);
                    tank.getEnemy().setY(y2 + 20);
                    break;
                default:
                    break;
            }
        }
        else if (Rect.intersects(tank.getRect(), power.getRect())) {
            power.activatePower();
            spawnHandler.removeCallbacks(spawnRunnable);
            powerHandler.postDelayed(powerRunnable, 10000);
        }

        tank.setRect();
        enemy.setRect();
    }

    /**
     * method that creates and determines the enemy tanks ai
     */
    private void AiTank(){
        Thread enemyAi = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        if(score < 500){
                            sleep(30);
                        }
                        else if(score < 1500 && score >= 500){
                            sleep(23);
                            difficulty = "Medium";
                        }
                        else if(score < 4000 && score >= 1500){
                            sleep(12);
                            difficulty = "Hard";
                        }
                        else{
                            sleep(7);
                            difficulty = "Survival";
                        }

                        //make enemy shoot
                        if(eBullet.getStatus()){
                            eBullet.update(50);
                            bulletCollisions(eBullet);
                        }
                        enemy.update(500);
                        eBullet.shoot(tank.getEnemy(), tank.getEnemy().getTrajectory());
                        //Enemy movement
                        if (tank.getX() < tank.getEnemy().getX() && tank.getY() < tank.getEnemy().getY()) {
                            tank.getEnemy().setX(tank.getEnemy().getX()-1);
                            tank.getEnemy().setY(tank.getEnemy().getY()-1);
                            tank.getEnemy().setTrajectory(tank.getEnemy().UPL);
                        }
                        else if (tank.getX() > tank.getEnemy().getX() && tank.getY() < tank.getEnemy().getY()) {
                            tank.getEnemy().setX(tank.getEnemy().getX()+1);
                            tank.getEnemy().setY(tank.getEnemy().getY()-1);
                            tank.getEnemy().setTrajectory(tank.getEnemy().UPR);
                        }
                        else if (tank.getX() > tank.getEnemy().getX() && tank.getY() > tank.getEnemy().getY()) {
                            tank.getEnemy().setX(tank.getEnemy().getX()+1);
                            tank.getEnemy().setY(tank.getEnemy().getY()+1);
                            tank.getEnemy().setTrajectory(tank.getEnemy().DOWNR);
                        }
                        else if (tank.getX() < tank.getEnemy().getX() && tank.getY() > tank.getEnemy().getY()) {
                            tank.getEnemy().setX(tank.getEnemy().getX()-1);
                            tank.getEnemy().setY(tank.getEnemy().getY()+1);
                            tank.getEnemy().setTrajectory(tank.getEnemy().DOWNL);
                        }
                        else if (tank.getX() > tank.getEnemy().getX() && tank.getY() == tank.getEnemy().getY()) {
                            tank.getEnemy().setTrajectory(tank.getEnemy().RIGHT);
                        }
                        else if (tank.getX() < tank.getEnemy().getX() && tank.getY() == tank.getEnemy().getY()) {
                            tank.getEnemy().setTrajectory(tank.getEnemy().LEFT);
                        }
                        else if(tank.getX() == tank.getEnemy().getX() && tank.getY() > tank.getEnemy().getY()) {
                            tank.getEnemy().setTrajectory(tank.getEnemy().DOWN);
                        }
                        else if (tank.getX() == tank.getEnemy().getX() && tank.getY() < tank.getEnemy().getY()) {
                            tank.getEnemy().setTrajectory(tank.getEnemy().UP);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };enemyAi.start();
    }

    //endregion

}  // end class
