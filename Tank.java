package com.example.thomas.tankwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;


public class Tank {

    //region Variables

    //region Drawing

    Rect rect;                      //the hitbox of the tank
    private int defImages[];        //the default image array of the tank
    private Bitmap bitmapUp;        //the bitmap for upward facing movement
    private Bitmap bitmapLeft;      //the bitmap for left facing movement
    private Bitmap bitmapRight;     //the bitmap for right facing movement
    private Bitmap bitmapDown;      //the bitmap for downward facing movement
    private Bitmap bitmapUpR;       //the bitmap for upward to the right facing movement
    private Bitmap bitmapUpL;       //the bitmap for upward to the left facing movement
    private Bitmap bitmapDownR;     //the bitmap for downward to the right facing movement
    private Bitmap bitmapDownL;     //the bitmap for downward to the left facing movement
    private Bitmap currentBitmap;   //the currently active bitmap
    private int length;             //the length of the tank
    private int height;             //the height of the tank
    private int x;                  //the x co-ordinate of the tank
    private int y;                  //the y co-ordinate of the tank

    //endregion

    //region Movement

    //these are used for directional pointers
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;
    public final int UPL = 5;
    public final int UPR = 6;
    public final int DOWNR = 7;
    public final int DOWNL = 8;

    private int trajectory;             //the direction the tank is moving in
    private boolean moving = false;     //if the tank is moving or not
    private int tankSpeed;              //the speed of the tank

    //endregion

    private Context context;
    private boolean isEnemy = false;    //if the tank is an enemy, not the player
    private Tank enemy;                 //the tank's enemy (can be the player if this is an enemies object)

    //endregion

    //region Constructor

    /**
     * base constructor for tank object, sets the size of the tank, its place on the screen and it's direction
     * @param context
     * @param screenX the ending of the screen x co-ordinate
     * @param screenY the ending of the screen y co-ordinate
     * @param images  the array holding any changes to the default images from the customisation window
     */
    public Tank(Context context, int screenX, int screenY, int images[]){
        rect = new Rect();

        this.context = context;

        trajectory = 3;

        length = screenX/15;
        height = screenY/15;

        x = screenX/2;
        y = screenY/2;

        defImages = images;

        reset();
        currentBitmap = bitmapUp;
        setRect();
    }

    /**
     * resets the tanks images to default and the speed
     */
    public void reset(){
        setBitmaps(defImages);
        setSpeed(350);
    }

    //endregion

    //region Public Accessors

    /**
     * allows setting of a tank class to be the enemy
     */
    public void isEnemy(){

        trajectory = 4;

        defImages = new int[]{
                R.drawable.enemy1,
                R.drawable.enemy2,
                R.drawable.enemy3,
                R.drawable.enemy4,
                R.drawable.enemy5,
                R.drawable.enemy6,
                R.drawable.enemy7,
                R.drawable.enemy8,
        };

        setBitmaps(defImages);
        currentBitmap = bitmapDown;

        isEnemy = true;

        x = RandomX();
        y = RandomY();
        setRect();
    }

    /**
     * RNG for X respawn point
     * @return random x coordinate
     */
    private int RandomX(){
        int rx = 0;
        int min = -200;
        int max = 900;
        Random rand = new Random();
        rx = (int)Math.floor(Math.random()*(max-min+1)+min);
        return rx;
    }

    /**
     * RNG for X respawn point
     * @return random y coordinate
     */
    private int RandomY(){
        int ry = 0;
        int min = 900;
        int max = 1200;
        Random rand = new Random();
        ry = (int)Math.floor(Math.random()*(max-min+1)+min);
        return ry;
    }

    /**
     * public accessor for the tanks enemy
     * @return the enemy tank, relative to this tank
     */
    public Tank getEnemy(){
        return enemy;
    }

    /**
     * public accessor to set the tanks enemy
     * @param tank the tank to become an enemy
     */
    public void setEnemy(Tank tank){
        enemy = tank;
    }

    /**
     * public accessor for the hitbox rectangle
     * @return the rectangle
     */
    public Rect getRect(){
        return rect;
    }

    /**
     * public accessor to allow setting of the rectangles position and size
     */
    public void setRect() {
        rect.top = y;
        rect.bottom = y + currentBitmap.getHeight();
        rect.left = x;
        rect.right = x + currentBitmap.getWidth();
    }

    /**
     * public accessor to retrieve the current image
     * @return the current tank image
     */
    public Bitmap getBitmap(){
        return currentBitmap;
    }

    /**
     * public accessor to allow setting of bitmap array onto the bitmap variables
     * @param bitmaps the array holding R.drawable pointers
     */
    public void setBitmaps(int[] bitmaps){
        bitmapLeft = BitmapFactory.decodeResource(context.getResources(), bitmaps[0]);
        bitmapLeft = Bitmap.createScaledBitmap(bitmapLeft, height, length, false);

        bitmapRight = BitmapFactory.decodeResource(context.getResources(), bitmaps[1]);
        bitmapRight = Bitmap.createScaledBitmap(bitmapRight, height, length, false);

        bitmapUp = BitmapFactory.decodeResource(context.getResources(), bitmaps[2]);
        bitmapUp = Bitmap.createScaledBitmap(bitmapUp, length, height, false);

        bitmapDown = BitmapFactory.decodeResource(context.getResources(), bitmaps[3]);
        bitmapDown = Bitmap.createScaledBitmap(bitmapDown, length, height, false);

        bitmapUpL = BitmapFactory.decodeResource(context.getResources(), bitmaps[4]);
        bitmapUpL = Bitmap.createScaledBitmap(bitmapUpL, (int)(length*1.5), height, false);

        bitmapUpR = BitmapFactory.decodeResource(context.getResources(), bitmaps[5]);
        bitmapUpR = Bitmap.createScaledBitmap(bitmapUpR, (int)(length*1.5), height, false);

        bitmapDownR = BitmapFactory.decodeResource(context.getResources(), bitmaps[6]);
        bitmapDownR = Bitmap.createScaledBitmap(bitmapDownR, (int)(length*1.5), height, false);

        bitmapDownL = BitmapFactory.decodeResource(context.getResources(), bitmaps[7]);
        bitmapDownL = Bitmap.createScaledBitmap(bitmapDownL, (int)(length*1.5), height, false);
    }

    /**
     * public accessor for the tanks speed
     * @return tank speed
     */
    public int getSpeed(){
        return tankSpeed;
    }

    /**
     * public accessor to alter the speed i.e. jet mode
     * @param speed the value to change the speed to
     */
    public void setSpeed(int speed) {
        tankSpeed = speed;
    }

    /**
     * public accessor to retrieve current tank x co-ordinate
     * @return tank x co-ordinate
     */
    public int getX(){
        return x;
    }

    /**
     * public accessor to set the X co-ordinate
     * @param newX new co-ordinate
     */
    public void setX(int newX) { x = newX; }

    /**
     * public accessor to retrieve current tank y co-ordinate
     * @return tank y co-ordinate
     */
    public int getY(){
        return y;
    }

    /**
     * public accessor to set the Y co-ordinate
     * @param newY new co-ordinate
     */
    public void setY(int newY) { y = newY; }

    /**
     * public accessor for length of tank
     * @return the tanks length
     */
    public int getLength(){
        return currentBitmap.getWidth();
    }

    /**
     * public accessor for the tanks height
     * @return the tanks height
     */
    public int getHeight(){
        return currentBitmap.getHeight();
    }

    /**
     * public accessor the tanks current trajectory
     * @return which direction the tank is facing
     */
    public int getTrajectory(){
        return trajectory;
    }

    /**
     * public accessor to set the tanks trajectory
     * @param state the new trajectory
     */
    public void setTrajectory(int state){
        trajectory = state;
        moving = true;
    }

    //endregion

    /**
     * stops the tank from moving
     */
    public void stopTank(){
        moving = false;
    }

    /**
     * causes the tank to move if the moving variable is true
     * @param fps the current frames per second, creating the speed
     */
    public void update(int fps){

        if(moving)
        {
            switch(trajectory)
            {
                case LEFT:
                    x = x - tankSpeed / fps;
                    currentBitmap = bitmapLeft;
                    break;
                case RIGHT:
                    x = x + tankSpeed / fps;
                    currentBitmap = bitmapRight;
                    break;
                case UP:
                    y = y - tankSpeed / fps;
                    currentBitmap = bitmapUp;
                    break;
                case DOWN:
                    y = y + tankSpeed / fps;
                    currentBitmap = bitmapDown;
                    break;
                case UPL:
                    x = x - tankSpeed / fps;
                    y = y - tankSpeed/ fps;
                    currentBitmap = bitmapUpL;
                    break;
                case UPR:
                    x = x + tankSpeed / fps;
                    y = y - tankSpeed/ fps;
                    currentBitmap = bitmapUpR;
                    break;
                case DOWNL:
                    x = x - tankSpeed / fps;
                    y = y + tankSpeed/fps;
                    currentBitmap = bitmapDownL;
                    break;
                case DOWNR:
                    x = x + tankSpeed / fps;
                    y = y + tankSpeed/ fps;
                    currentBitmap = bitmapDownR;
                    break;
                default:
                    break;
            }

            setRect();
        }
    }

}