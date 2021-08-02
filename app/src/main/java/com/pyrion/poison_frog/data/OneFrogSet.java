package com.pyrion.poison_frog.data;

public class OneFrogSet {
    private int key = 0;
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
        this.cursor = Frog.CURSOR_STORED;
        this.houseType = Frog.HOUSE_TYPE_LENT;
        this.creatorName = "Anonymous";
        this.frogName = "NullFrogName";
        this.getFrogState = Frog.STATE_ALIVE;
        this.frogSpecies = Frog.SPECIES_BASIC;
        this.frogSize = Frog.SIZE_DEFAULT;
        this.frogPower = Frog.POWER_DEFAULT;
    }

    public OneFrogSet(
            int houseType,
            String creatorName,
            String frogName,
            int frogSTate,
            int frogSpecies,
            int frogSize,
            int frogPower){
        this.houseType = houseType;
        this.creatorName = creatorName;
        this.frogName = frogName;
        this.getFrogState = frogSTate;
        this.frogSpecies = frogSpecies;
        this.frogSize = frogSize;
        this.frogPower = frogPower;
    }

    //getter

    public int getKey() {
        return key;
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


    //setter

    public void setKey(int key) {
        this.key = key;
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
        this.frogPower += frogPower;
    }


    public void setFrogState(int frogState) {
        this.getFrogState = frogState;
    }

    //change countable number

    public void changeFrogSize(int frogSize) {
        this.frogSize += frogSize;
    }

    public void changeFrogPower(int frogPower) {
        this.frogPower += frogPower;
    }

}
