package com.pyrion.poison_frog.data;

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

    //frog state
    public static final int SPECIES_BASIC = R.drawable.main_frog_jelly;

    public static final int SPECIES_BLACK = R.drawable.ic_egg;

    public static final int SPECIES_A = R.drawable.red_egg;
    public static final int SPECIES_B = R.drawable.red_egg;
    public static final int SPECIES_C = R.drawable.red_egg;

    public static final int SPECIES_D = R.drawable.blue_egg;
    public static final int SPECIES_E = R.drawable.blue_egg;
    public static final int SPECIES_F = R.drawable.blue_egg;

    public static final int SPECIES_G = R.drawable.gold_egg;
    public static final int SPECIES_H = R.drawable.gold_egg;
    public static final int SPECIES_I = R.drawable.gold_egg;

    //house type
    public static final int HOUSE_TYPE_BUY_NEW = 0;
    public static final int HOUSE_TYPE_LENT = 1;
    public static final int HOUSE_TYPE_BOUGHT = 2;


    public static int getFrogSpecies(int number) {
        switch (number){
            case 0:
                return Frog.SPECIES_BASIC;
            case 1:
                return Frog.SPECIES_BLACK;
            case 2:
                return Frog.SPECIES_A;
            case 3:
                return Frog.SPECIES_B;
            case 4:
                return Frog.SPECIES_C;
            case 5:
                return Frog.SPECIES_D;
            case 6:
                return Frog.SPECIES_E;
            case 7:
                return Frog.SPECIES_F;
            case 8:
                return Frog.SPECIES_G;
            case 9:
                return Frog.SPECIES_H;
            case 10:
                return Frog.SPECIES_I;

        }
        return Frog.SPECIES_BASIC;
    }


    public static String getStringSpecies(int species) {
        switch (species){
            case Frog.SPECIES_BASIC:
                return "기본";
            case Frog.SPECIES_BLACK:
                return "Frog.SPECIES_BLACK";
            case Frog.SPECIES_A:
                return "Frog.SPECIES_A";
//            case Frog.SPECIES_B:
//                return "Frog.SPECIES_B";
//            case Frog.SPECIES_C:
//                return "Frog.SPECIES_C";
            case Frog.SPECIES_D:
                return "Frog.SPECIES_D";
//            case Frog.SPECIES_E:
//                return "Frog.SPECIES_E";
//            case Frog.SPECIES_F:
//                return "Frog.SPECIES_F";
            case Frog.SPECIES_G:
                return "Frog.SPECIES_G";
//            case Frog.SPECIES_H:
//                return "Frog.SPECIES_H";
//            case Frog.SPECIES_I:
//                return "Frog.SPECIES_I";

        }
        return "Frog.SPECIES_BASIC";
    }


}
