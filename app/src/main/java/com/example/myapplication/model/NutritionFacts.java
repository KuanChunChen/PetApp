package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NutritionFacts {

    public String name;
    public int carbohydrates;
    public int protein;
    public int fat;
    public int vitamins;
    public int minerals;
    public int dietaryFiber;
    public int water;

    public NutritionFacts(String name, int carbohydrates, int protein, int fat, int vitamins, int minerals, int dietaryFiber, int water) {
        this.name = name;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.vitamins = vitamins;
        this.minerals = minerals;
        this.dietaryFiber = dietaryFiber;
        this.water = water;
    }

    public List<String> toStringList() {
        List<String> stringList = new ArrayList<>();
        if (carbohydrates != 0) {
            stringList.add("碳水化合物 " + carbohydrates + " g");
        }
        if (protein != 0) {
            stringList.add("蛋白質 " + protein + " g");
        }
        if (fat != 0) {
            stringList.add("脂肪 " + fat + " g");
        }
        if (vitamins != 0) {
            stringList.add("維生素 " + vitamins + " g");
        }
        if (minerals != 0) {
            stringList.add("礦物質 " + minerals + " g");
        }
        if (dietaryFiber != 0) {
            stringList.add("膳食纖維 " + dietaryFiber + " g");
        }
        if (water != 0) {
            stringList.add("水分 " + water + " g");
        }
        return stringList;
    }

    private static int randomValueInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static NutritionFacts generateFacts(String name) {

        int carbohydrates = randomValueInRange(10, 50);
        int protein = randomValueInRange(10, 50);
        int fat = randomValueInRange(5, 20);
        int vitamins = randomValueInRange(1, 10);
        int minerals = randomValueInRange(1, 10);
        int dietaryFiber = randomValueInRange(1, 10);
        int water = randomValueInRange(5, 20);

        return new NutritionFacts(name, carbohydrates, protein, fat, vitamins, minerals, dietaryFiber, water);
    }

}