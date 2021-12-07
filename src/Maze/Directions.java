package Maze;

import java.util.Arrays;


/**
 *The Directions enum enumerates the 4 directions (NORTH, EAST, SOUTH, WEST).
 *This is used for calculating the valid cells from which
 * a solver can travel to another cell
 */

public enum Directions {
    North(0,-1,0x1), //bValue=0001
    East(1,0,0x2),
    South(0,1,0x4),
    West(-1,0,0x8);

    /**
     * The x, y offset of a given direction.
     * The values it can take: 0,1,-1.
     * bValue: The bit values corresponding to a height
     */
    private final int xOffset,yOffset, bValue;

    Directions(int xOffset, int yOffset, int bValue){
        this.xOffset=xOffset;
        this.yOffset=yOffset;
        this.bValue=bValue;
    }

    /**
     * {@link Directions#xOffset}
     */
    public int getXOffset(){return xOffset;}
    /**
     * {@link Directions#yOffset}
     */
    public int getYOffset(){return yOffset;}
    /**
     * {@link Directions#bValue}
     */
    public int getBValue(){return bValue;}


    /**
     * Calculates the offset direction
     * from the given coordinates relative to the current cell.
     * @param x0 The x coordinate of the first cell
     * @param y0 The y coordinate of the first cell
     * @param x1 The x coordinate of the second cell
     * @param y1 The y coordinate of the second cell
     * @return The relative direction of neighbouring cell from source.
     */
    public static Directions getOffsetDirection(int x0, int y0, int x1, int y1){
        for(Directions d : Directions.values()){
            if(x1-x0== d.getXOffset() && y1-y0==d.getYOffset())
                return d;
        }
        return null;
    }

    /**
     *Calculates a new direction which is perpendicular to the current one.
     * @param d The current direction which we face
     * @param positive Determines if we turn clockwise of counter-clockwise
     * @return a new direction perpendicular to the passed direction
     */
    public static Directions turnSideways(Directions d, boolean positive){
        int newVal= (positive) ? ((d.getBValue() << 1)%15)  : ((d.getBValue() << 3)%15);

        return Arrays.stream(Directions.values())
                .filter(x -> (newVal==x.getBValue()))
                .findFirst().get();
    }

    /**
     * Every direction has a corresponding int value.
     * These values are the powers of two, as they are supposed to represent single
     * bits in a four bit number.
     * This method returns the correct constant.
     * @param bValue int value of the direction
     * @return a direction corresponding to the passed bValue
     */
    public static Directions getDirectionFromValue(int bValue){
        switch(bValue){
            case 0x1:
                return North;
            case 0x2:
                return East;
            case 0x4:
                return South;
            case 0x8:
                return West;
            default:
                return null;
        }
    }





}
