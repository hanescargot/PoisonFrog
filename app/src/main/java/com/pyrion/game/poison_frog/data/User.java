package com.pyrion.game.poison_frog.data;

public class User {

    String name;
    int selectedFrogKey;
    int money;

    public User(String name, int selectedFrogKey, int money) {
        this.name = name;
        this.selectedFrogKey = selectedFrogKey;
        this.money = money;
    }

    public User() {
        this.name = "Anonymous";
        this.selectedFrogKey = 0;
        this.money = 0;
    }


}
