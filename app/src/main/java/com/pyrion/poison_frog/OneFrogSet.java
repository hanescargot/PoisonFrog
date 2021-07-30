package com.pyrion.poison_frog;

public class OneFrogSet {
    int cursor;
    int houseType;
    String creatorName;
    String frogName;
    int frogSpecies;
    int frogSize;
    int frogPower;
    int frogState;

    public OneFrogSet(){
        this.cursor = Frog.CURSOR_STORED;
        this.houseType = Frog.HOUSE_TYPE_LENT;
        this.creatorName = "Anonymous";
        this.frogName = "NullFrogName";
        this.frogState = Frog.STATE_ALIVE;
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
        this.frogState = frogSTate;
        this.frogSpecies = frogSpecies;
        this.frogSize = frogSize;
        this.frogPower = frogPower;
    }

}
