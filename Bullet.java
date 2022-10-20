package com.example.thomas.tankwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Bullet {

    //region Variables

    //region Drawing

    private int x;                      //the bullets x co-ordinate
    private int y;                      //the bullets y co-ordinate
    private int width;                  //the bullets width
    private int height;                 //the bullets height
    private Rect rect;                  //the bullets hitbox
    private Bitmap currentBitmap;       //the image of the bullet

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

    int heading = 0;                 //where the shell is firing
    int speed = 650;               //shell speed

    //endregion

    private boolean isActive;           //if the bullet has been fired

    //endregion

    //region Constructor

    /**
     * base constructor for bullet object, creates the size of the bullet and assigns the image to the bitmap with a hitbox
     * @param context
     * @param screenX   the x end of the game screen
     */
    public Bullet(Context context, int screenX) {
        isActive = false;

        width = screenX/100;
        height = screenX/100;
        this.rect = new Rect();

        currentBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.shell);
        currentBitmap = Bitmap.createScaledBitmap(currentBitmap, width, height, false);
    }

    //endregion

    //region Public Accessors

    /**
     * public accessor for the shell image
     * @return the active image
     */
    public Bitmap getBitmap() {
        return currentBitmap;
    }

    /**
     * public accessor for shells x co-ordinate
     * @return x co-ordinate
     */
    public int getX(){
        return x;
    }

    /**
     * public accessor for shells y co-ordinate
     * @return y co-ordinate
     */
    public int getY(){
        return y;
    }

    /**
     * public accessor for rectangle (hitbox)
     * @return rectangle
     */
    public Rect getRect() {
        return rect;
    }

    /**
     * public accessor to set the rectangles position and size
     * @param x new x position of rectangle
     * @param y new y position of rectangle
     */
    public void setRect(int x, int y){
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + width;
    }

    /**
     * public accessor for bullets status
     * @return true if shell is live, false if not
     */
    public boolean getStatus() {
        return isActive;
    }

    /**
     * public accessor to set the shell to inactive
     */
    public void setInactive() {
        isActive = false;
    }

    /**
     * retrieves the y co-ordinate impact point of the shell
     * @return y co-ordinate
     */
    public float getImpactPointY() {
        if(heading == DOWN || heading == DOWNR || heading == DOWNL)
            return y + height;
        else
            return y;
    }

    /**
     * retrieves the x co-ordinate impact point of the shell
     * @return x co-ordinate
     */
    public float getImpactPointX() {
        if(heading == RIGHT || heading == UPR || heading == DOWNR)
            return x + width;
        else
            return x;
    }

    //endregion

    /**
     * public accessor for the bullet to fire, using the firing tanks position to spawn it
     * @param tank the tank who fired
     * @param direction the direction the tank is facing
     * @return
     */
    public boolean shoot(Tank tank, int direction) {
        if(!isActive) {

            heading = direction;
            isActive = true;

            switch(heading) {
                case LEFT:
                    x = tank.getX();
                    y = tank.getY() + (tank.getHeight()/2);
                    break;
                case RIGHT:
                    x = tank.getX() + tank.getLength();
                    y = tank.getY() + (tank.getHeight()/2);
                    break;
                case UP:
                    x = tank.getX() + (tank.getLength()/2);
                    y = tank.getY();
                    break;
                case DOWN:
                    x = tank.getX() + (tank.getLength()/2);
                    y = tank.getY() + tank.getHeight();
                    break;
                case UPL:
                    x = tank.getX();
                    y = tank.getY();
                    break;
                case UPR:
                    x = tank.getX() + tank.getLength();
                    y = tank.getY();
                    break;
                case DOWNR:
                    x = tank.getX() + tank.getLength();
                    y = tank.getY() + tank.getHeight();
                    break;
                case DOWNL:
                    x = tank.getX();
                    y = tank.getY() + tank.getHeight();
                    break;
            }


            return true;
        }

        //Bullet already active
        return false;
    }

    /**
     * method for the triple shots first extra shell firing
     * @param tank the tank who fired the shell
     * @param direction the direction the tank is facing
     * @return true if it has been fired, false if not
     */
    public boolean tripleShot1(Tank tank, int direction) {
        if (!isActive) {
            heading = direction;
            isActive = true;

            switch (heading) {
                case LEFT:
                case UP:
                    x = tank.getX();
                    y = tank.getY();
                    break;
                case RIGHT:
                    x = tank.getX() + tank.getLength();
                    y = tank.getY();
                    break;
                case DOWN:
                    x = tank.getX();
                    y = tank.getY() + tank.getHeight();
                    break;
                case UPL:
                    x = tank.getX() + (tank.getLength()/4);
                    y = tank.getY();
                    break;
                case UPR:
                    x = (tank.getX() + tank.getLength()) - (tank.getLength()/4);
                    y = tank.getY();
                    break;
                case DOWNR:
                    x = tank.getX() + tank.getLength();
                    y = (tank.getY() + tank.getHeight()) - (tank.getHeight()/4);
                    break;
                case DOWNL:
                    x = tank.getX();
                    y = (tank.getY() + tank.getHeight()) - (tank.getHeight()/4);
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * method for the triple shots second extra shell firing
     * @param tank the tank who fired the shell
     * @param direction the direction the tank is facing
     * @return true if it has been fired, false if not
     */
    public boolean tripleShot2(Tank tank, int direction) {
        if (!isActive) {
            heading = direction;
            isActive = true;

            switch (heading) {
                case LEFT:
                    x = tank.getX();
                    y = tank.getY() + tank.getHeight();
                    break;
                case RIGHT:
                case DOWN:
                    x = tank.getX() + tank.getLength();
                    y = tank.getY() + tank.getHeight();
                    break;
                case UP:
                    x = tank.getX() + tank.getLength();
                    y = tank.getY();
                    break;
                case UPL:
                    x = tank.getX();
                    y = tank.getY() + (tank.getHeight()/4);
                    break;
                case UPR:
                    x = tank.getX() + tank.getLength();
                    y = tank.getY() + (tank.getHeight()/4);
                    break;
                case DOWNR:
                    x = (tank.getX() + tank.getLength()) - (tank.getHeight()/4);
                    y = tank.getY() + tank.getHeight();
                    break;
                case DOWNL:
                    x = tank.getX() + (tank.getHeight()/4);
                    y = tank.getY() + tank.getHeight();
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * method for updating the position of the first extra shell from triple shot
     * @param fps frames per second, influencing speed
     */
    public void triple1Update(int fps){

        switch(heading)
        {
            case LEFT:
            case UP:
                x -= speed / fps;
                y -= speed / fps;
                break;
            case RIGHT:
                x += speed / fps;
                y -= speed / fps;
                break;
            case DOWN:
                x -= speed /fps;
                y += speed / fps;
                break;
            case UPL:
            case UPR:
                y -= speed / fps;
                break;
            case DOWNL:
                x -= speed / fps;
                break;
            case DOWNR:
                x += speed / fps;
                break;
            default:
                break;
        }
        setRect(x,y);
    }

    /**
     * method for updating the position of the second extra shell from triple shot
     * @param fps frames per second, influencing speed
     */
    public void triple2Update(int fps){

        switch(heading)
        {
            case LEFT:
                x -= speed / fps;
                y += speed / fps;
                break;
            case RIGHT:
            case DOWN:
                x += speed / fps;
                y += speed / fps;
                break;
            case UP:
                x += speed / fps;
                y -= speed / fps;
                break;
            case UPL:
                x -= speed / fps;
                break;
            case UPR:
                x += speed / fps;
                break;
            case DOWNL:
            case DOWNR:
                y += speed / fps;
                break;
            default:
                break;
        }
        setRect(x,y);
    }

    /**
     * method used to update the shells position after being fired
     * @param fps frames per second, influencing speed
     */
    public void update(int fps) {

        switch(heading)
        {
            case LEFT:
                x = x - speed / fps;
                break;
            case RIGHT:
                x = x + speed / fps;
                break;
            case UP:
                y = y - speed / fps;
                break;
            case DOWN:
                y = y + speed / fps;
                break;
            case UPL:
                x = x - speed / fps;
                y = y - speed / fps;
                break;
            case UPR:
                x = x + speed / fps;
                y = y - speed / fps;
                break;
            case DOWNL:
                x = x - speed / fps;
                y = y + speed / fps;
                break;
            case DOWNR:
                x = x + speed / fps;
                y = y + speed / fps;
                break;
            default:
                break;
        }

        setRect(x, y);
    }
}