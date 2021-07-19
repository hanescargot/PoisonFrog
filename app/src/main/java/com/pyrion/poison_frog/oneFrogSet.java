package com.pyrion.poison_frog;

public class oneFrogSet {

    int frogSrc = 0;
    String userName = "Anonymous";
    String frogName = "NullFrogName";
    String frogID = userName + frogName;
    String frogProperty = "normalFrog"; //9가지 더 있음
    int frogSize = 80;
    int frogPower = 1;

    public oneFrogSet(
            int frogSrc,
            String userName,
            String frogName,
            String frogProperty,
            int frogSize,
            int frogPower){
        this.frogSrc = frogSrc;
        this.userName = userName;
        this.frogName = frogName;
        this.frogID = userName + frogName;
        this.frogProperty = frogProperty;
        this.frogSize = frogSize;
        this.frogPower = frogPower;
    }

}
