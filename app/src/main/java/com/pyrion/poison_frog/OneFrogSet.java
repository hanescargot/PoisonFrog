package com.pyrion.poison_frog;

public class OneFrogSet {

    int frogSrc = 0;
    String creatorName = "Anonymous";
    String frogName = "NullFrogName";
    String frogID = creatorName + frogName;
    int frogProperty = Frog.SPECIES_BASIC; //9가지 더 있음
    int frogSize = 80;
    int frogPower = 1;
    int frogState = Frog.ALIVE;

    public OneFrogSet(
            int frogSrc,
            String userName,
            String frogName,
            int frogSTate,
            int frogProperty,
            int frogSize,
            int frogPower){
        this.frogSrc = frogSrc;
        this.creatorName = userName;
        this.frogName = frogName;
        this.frogState = frogSTate;
        this.frogID = userName + frogName;
        this.frogProperty = frogProperty;
        this.frogSize = frogSize;
        this.frogPower = frogPower;
    }

}
