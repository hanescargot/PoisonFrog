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
    public static final int SPECIES_BLACK = 1;
    public static final int SPECIES_A = 2;
    public static final int SPECIES_B = 3;
    public static final int SPECIES_C = 4;
    public static final int SPECIES_D = 5;
    public static final int SPECIES_E = 6;
    public static final int SPECIES_F = 7;
    public static final int SPECIES_G = 8;
    public static final int SPECIES_H = 9;
    public static final int SPECIES_I = 10;

    //house type
    public static final int HOUSE_TYPE_BUY_NEW = 0;
    public static final int HOUSE_TYPE_LENT = 1;
    public static final int HOUSE_TYPE_BOUGHT = 2;


    public String FrogSpeciesStirng(int frogSpecies) {
        switch (frogSpecies){
            case Frog.SPECIES_BASIC:
                return "Normal Frog";


        }
        return "null_species";
    }

}
