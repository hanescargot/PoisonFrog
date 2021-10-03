package com.pyrion.game.poison_frog.data;

import com.pyrion.game.poison_frog.R;

public class OneItemSet {

    private String itemName = "";
    private String itemExplain = "";
    private int itemPrice;
    private int currentLevel;
    private int maxLevel;
    private double upgradePriceTimes;
    private String itemCase = "";

    public OneItemSet(String itemName, String itemExplain , int itemPrice, int currentLevel, int maxLevel, double upgradePriceTimes, String itemCase) {
        this.itemName = itemName;
        this.itemExplain = itemExplain;
        this.itemPrice = itemPrice;
        this.currentLevel = currentLevel;
        this.maxLevel = maxLevel;
        this.upgradePriceTimes = upgradePriceTimes;
        this.itemCase = itemCase;
    }

    public OneItemSet(){
    }

    public int getItemSrc(String itemName){
        switch(itemName){
            case "1":
                return R.drawable.free_dice;
            case "2":
                return R.drawable.free_dice;
            case "3":
                return R.drawable.free_dice;
        }
        return R.drawable.free_dice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemExplain() {
        return itemExplain;
    }

    public void setItemExplain(String itemExplain) {
        this.itemExplain = itemExplain;
    }


    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int currentItemPrice) {
        itemPrice = currentItemPrice;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public double getUpgradePriceTimes() {
        return upgradePriceTimes;
    }

    public void setUpgradePriceTimes(double upgradePriceTimes) {
        this.upgradePriceTimes = upgradePriceTimes;
    }

    public String getItemCase() {
        return itemCase;
    }

    public void setItemCase(String itemCase) {
        this.itemCase = itemCase;
    }

}
