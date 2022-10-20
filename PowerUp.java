package com.example.thomas.tankwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class PowerUp {

    //region Markers

    private int current;        //the current power up active: 0 jet mode, 1 invincibility, 2 triple shot
    private boolean active;    //if the powerup is active or not
    private boolean spawn;            //the marker that handles the spawning of the powerup
    private boolean shooting;         //the marker that handles the triple shot activity

    //endregion

    //region Drawing

    private final int iconH = 78;   //image icon height
    private final int iconW = 78;   //image icon width
    private Bitmap jetIcon;         //the bitmap to hold the jet mode icon
    private Bitmap invincibleIcon;  //bitmap to store the invincibility icon
    private Bitmap tripleIcon;      //bitmap to store the tripleshot icon
    private Bitmap currentBitmap;   //the currently active bitmap
    private Rect rect;              //the hitbox of the icon
    private int x;                  //the x co-ordinate of the icon
    private int y;                  //the y co-ordinate of the icon
    private int originX;            //the x co-ordinate of the beginning of the game screen
    private int originY;            //the y co-ordinate of the beginning of the game screen
    private int screenX;            //the x co-ordinate of the end of the game screen
    private int screenY;            //the y co-ordinate of the end of the game screen

    //endregion

    //region Triple Shot

    public boolean shoot1 = false;    //the state of the first extra shot, if it is active or not
    public boolean shoot2 = false;    //the state of the first extra shot, if it is active or not

    //endregion

    //region References

    private Tank player;            //the tank who uses the powerups     //the game world
    private int invincibleImages[];     //array to hold id's of invinicbility sprites
    private int jetImages[];            //array to hold id's of jet sprites

    //endregion

    //region Constructor

    /**
     *  base constructor for PowerUp, sets up base attributes, retrieves game screen size, sets icons
     * @param context
     * @param x denotes the beginning of the active game space, below the touch inputs
     * @param y denotes the beginning of the active game space, below the touch inputs
     * @param endX not the full screen x co-ordinate, but a reduced value denoting the active game space
     * @param endY not the full screen y co-ordinate, but a reduced value denoting the active game space
     */
    public PowerUp(Context context, int x, int y, int endX, int endY) {
        active = false;
        shooting = false;

        originX = x;
        originY = y;
        screenX = endX;
        screenY = endY;

        rect = new Rect();
        jetIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.jetmode);
        jetIcon = Bitmap.createScaledBitmap(jetIcon, iconW, iconH, false);

        invincibleIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.invincible);
        invincibleIcon = Bitmap.createScaledBitmap(invincibleIcon, iconW, iconH, false);

        tripleIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.tripleshot);
        tripleIcon = Bitmap.createScaledBitmap(tripleIcon, iconW, iconH, false);

        jetImages = new int[]{
                R.drawable.jet1,
                R.drawable.jet2,
                R.drawable.jet3,
                R.drawable.jet4,
                R.drawable.jet5,
                R.drawable.jet6,
                R.drawable.jet7,
                R.drawable.jet8
        };

        invincibleImages = new int[]{
                R.drawable.gold1,
                R.drawable.gold2,
                R.drawable.gold3,
                R.drawable.gold4,
                R.drawable.gold5,
                R.drawable.gold6,
                R.drawable.gold7,
                R.drawable.gold8,
        };

        //moving icons and shells offscreen
        initialisePowerup();
    }

    //endregion

    //region public get methods

    /**
     *  public accessor for current powerup
     * @return integer based on which powerup has been chosen
     */
    public int getCurrent()
    {
        return current;
    }

    /**
     *  public accessor for variable active
     * @return true if powerup ability is active, false if not
     */
    public boolean getActive() {
        return active;
    }

    /**
     *  public accessor for state of triple shot
     * @return true if active, false if not
     */
    public boolean getTriple() {
        return shooting;
    }

    /**
     *  public accessor for X co-ordinate of icon
     * @return X co-ordinate
     */
    public int getX(){
        return x;
    }

    /**
     *  public accessor for Y co-ordinate of icon
     * @return Y co-ordinate
     */
    public int getY(){
        return y;
    }

    /**
     *  public accessor for the spawn state of the powerup
     * @return true if spawned, false if not
     */
    public boolean getSpawn() {
        return spawn;
    }

    /**
     *  public accessor for the current image displayed
     * @return the current image
     */
    public Bitmap getBitmap() {
        return currentBitmap;
    }

    /**
     *  public accessor for rectangle variable
     * @return the rectangle
     */
    public Rect getRect(){
        return rect;
    }

    //endregion

    //region public set methods

    /**
     *  sets the playable tank to the potential user of the powerup
     * @param tank the players tank object
     */
    public void setPlayer(Tank tank)
    {
        player = tank;
    }

    //endregion

    //region generating powerups

    /**
     *  when the powerup is spawned it randomly selects from the created abilities
     */
    public void generatePower() {
        boolean safeSpawn;
        Random rnd = new Random();

        //random number used to spawn whichever powerup corresponds
        current = rnd.nextInt(3);

        current = 0;
        switch (current)
        {
            case 0:
                setJet();
                break;
            case 1:
                setInvincible();
                break;
            case 2:
                setTriple();
                break;
            default:
                break;
        }

        //loop continually runs if powerup is spawned off screen or inside a tank until it is not
        do{
            x = rnd.nextInt(screenX);
            y = rnd.nextInt(screenY);
            rect.top = y;
            rect.bottom = y + iconH;
            rect.left = x;
            rect.right = x + iconW;
            safeSpawn = spawnCollisions(rect);
        }while(!safeSpawn);

        spawn = true;
    }

    /**
     *  sets the icon to jet mode
     */
    private void setJet()
    {
        currentBitmap = jetIcon;
    }

    /**
     *  sets the icon to invincibility
     */
    private void setInvincible() {
        currentBitmap = invincibleIcon;
    }

    /**
     *  sets the icon to triple shot
     */
    private void setTriple() {
        currentBitmap = tripleIcon;
    }

    //endregion

    //region using powerups

    /**
     *  method handling when a powerup is picked up by the player
     */
    public void activatePower() {

        active = true;
        initialisePowerup();

        if(current == 0) {
            player.setBitmaps(jetImages);
            player.setSpeed(player.getSpeed()*2);
        }
        else if (current == 1)
            player.setBitmaps(invincibleImages);
        else if (current == 2){
            shooting = true;
        }
    }

    /**
     *  turns off the powerups power
     */
    public void deactivate(){
        active = false;

        if(current == 0 || current == 1)
            player.reset();
        else
            shooting = false;
    }

    /**
     *de spawns the powerup, hides it off screen
     */
    public void initialisePowerup() {
        rect.top = -100;
        rect.bottom = rect.top + iconH;
        rect.left = -100;
        rect.right = rect.left + iconW;
        spawn = false;
    }

    //endregion

    //region TripleShot

    /**
     * repeatable method to test collision for spawning a powerup
     * @param rect the icon rectangle used to check for collisions
     */
    private boolean spawnCollisions(Rect rect) {

        //the shells position is checked against the border of the game
        if ((rect.left < originX) || (rect.right > screenX) || (rect.bottom > screenY) || (rect.top < originY))
            return false;
        //the shells position is checked against the picturebox border of the tank
        else if (Rect.intersects(rect,player.getRect()) || Rect.intersects(rect,player.getEnemy().getRect()))
            return false;

        return true;
    }

    //endregion
}
