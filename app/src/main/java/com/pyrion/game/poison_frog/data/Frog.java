package com.pyrion.game.poison_frog.data;

import com.pyrion.poison_frog.R;

public class Frog {
    public static final String FROG_NAME_NULL = "frog_null_name";
    public static final String USER_NAME_NULL = "user_null_name";

    public static final int SIZE_DEFAULT = 80;
    public static final int POWER_DEFAULT = 1;

    //frog state
    public static final int STATE_ALIVE = 1;
    public static final int STATE_SOLD = 2;
    public static final int STATE_DEATH = 3;
    public static final int STATE_EXERCISE = 4;

    //frog state
    public static final int FROG_SPECIES_COUNT = 7;
    public static final int SPECIES_BASIC = R.drawable.normal;

    public static final int SPECIES_SPIDER = R.drawable.spider;
    public static final int SPECIES_HORN = R.drawable.horn;

    public static final int SPECIES_JELLY = R.drawable.jelly;
    public static final int SPECIES_DEATH = R.drawable.death;

    public static final int SPECIES_SNAKE = R.drawable.snake;
    public static final int SPECIES_FIRE = R.drawable.fire;

    //house type
    public static final int HOUSE_TYPE_BUY_NEW = 0;
    public static final int HOUSE_TYPE_LENT = 1;
    public static final int HOUSE_TYPE_BOUGHT = 2;

    public static int getFrogSpecies(int number) {
        switch (number){
            case 0:
                return Frog.SPECIES_BASIC;
            case 1:
                return Frog.SPECIES_SPIDER;
            case 2:
                return Frog.SPECIES_HORN;
            case 3:
                return Frog.SPECIES_JELLY;
            case 4:
                return Frog.SPECIES_DEATH;
            case 5:
                return Frog.SPECIES_SNAKE;
            case 6:
                return Frog.SPECIES_FIRE;
        }
        return Frog.SPECIES_BASIC;
    }


    public static String getStringSpecies(int species) {
        switch (species){
            case Frog.SPECIES_BASIC:
                return "노말구리";
            case Frog.SPECIES_SPIDER:
                return "거미구리";
            case Frog.SPECIES_HORN:
                return "뿔구리";
            case Frog.SPECIES_JELLY:
                return "젤리구리";
            case Frog.SPECIES_DEATH:
                return "저승구리";
            case Frog.SPECIES_SNAKE:
                return "뱀구리";
            case Frog.SPECIES_FIRE:
                return "불구리";

        }
        return "Frog.SPECIES_BASIC";
    }


}
