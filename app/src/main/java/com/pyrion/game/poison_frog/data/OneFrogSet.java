package com.pyrion.game.poison_frog.data;

import com.pyrion.poison_frog.R;

public class OneFrogSet {
    private int frogKey = 1;
    private int cursor;
    private int houseType;
    private String creatorName;
    private String frogName;
    private int frogSpecies;
    private int frogSize;
    private int frogPower;
    private int getFrogState;

    // constructor

    public OneFrogSet(){
        //default is buy new house
        this.houseType = Frog.HOUSE_TYPE_BUY_NEW;
        this.creatorName = "Anonymous";
        this.frogName = "NullFrogName";
        this.getFrogState = Frog.STATE_ALIVE;
        this.frogSpecies = Frog.SPECIES_BASIC;
        this.frogSize = Frog.SIZE_DEFAULT;
        this.frogPower = Frog.POWER_DEFAULT;
    }

    public OneFrogSet(
            int frogKey,
            int houseType,
            String creatorName,
            String frogName,
            int frogSTate,
            int frogSpecies,
            int frogSize,
            int frogPower){
        this.frogKey = frogKey;
        this.houseType = houseType;
        this.creatorName = creatorName;
        this.frogName = frogName;
        this.getFrogState = frogSTate;
        this.frogSpecies = frogSpecies;
        this.frogSize = frogSize;
        this.frogPower = frogPower;
    }

    //getter

    public int getFrogKey() {
        return frogKey;
    }

    public int getCursor() {
        return cursor;
    }

    public int getHouseType() {
        return houseType;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getFrogName() {
        return frogName;
    }

    public int getFrogSpecies() {
        return frogSpecies;
    }

    public int getFrogSize() {
        return frogSize;
    }

    public int getFrogPower() {
        return frogPower;
    }

    public int getFrogState() {
        return getFrogState;
    }



    public int getFrogSrc(){
        int frogState = this.getFrogState;
        switch (frogState){
            case (Frog.STATE_ALIVE):
            case (Frog.STATE_EXERCISE):
                return this.getFrogSpecies();
            case (Frog.STATE_DEATH):
                return R.drawable.main_dead_frog;
            case (Frog.STATE_SOLD):
                return R.drawable.main_gift;
        }
        // Null Image
        return (R.drawable.normal);
    }


    //setter

    public void setFrogKey(int frogKey) {
        this.frogKey = frogKey;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public void setHouseType(int houseType) {
        this.houseType = houseType;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public void setFrogName(String frogName) {
        this.frogName = frogName;
    }

    public void setFrogSpecies(int frogSpecies) {
        this.frogSpecies = frogSpecies;
    }

    public void setFrogSize(int frogSize) {
        this.frogSize = frogSize;
    }

    public void setFrogPower(int frogPower) {
        this.frogPower = frogPower;
    }


    public void setFrogState(int frogState) {
        this.getFrogState = frogState;
    }


}
