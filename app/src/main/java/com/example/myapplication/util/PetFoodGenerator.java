package com.example.myapplication.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PetFoodGenerator {

    private static final String[] FOOD_NAMES = {
            "牛肉餐",
            "雞肉餐",
            "魚肉餐",
            "羊肉餐",
            "火雞餐",
            "雞蛋餐",
            "鮮美牛肉骨",
            "香脆雞胸條",
            "紅蘋果口味貓糧",
            "綠色蔬菜零食球",
            "嫩煮三文魚條",
            "天然羊肉骨",
            "素食狗餅乾",
            "魚油補充膠囊",
            "無穀物兔肉罐頭",
            "鹿肉骨頭棒",
            "藍莓牛奶零食",
            "綜合海鮮口味狗糧",
            "牛奶骨頭零食",
            "烤雞肉條",
            "有機蔬果脆片",
            "美味鮭魚餅乾",
            "鳳梨口味貓砂",
            "鴨肉條",
            "雞心肝軟糖",
            "野生鮭魚罐頭",
            "豆腐零食球",
            "紅藜東風螺螺旋餅",
            "濕潤海鮮口味貓糧"
    };


    public static class PetFood {
        String name;
        int grams;

        public PetFood(String name, int grams) {
            this.name = name;
            this.grams = grams;
        }

        @Override
        public String toString() {
            return name + " " + grams + "g";
        }
    }

    private static int randomValueInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static List<String> generateRandomPetFoodList(int count) {
        List<String> petFoodList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            PetFood randomPetFood = generateRandomPetFood();
            petFoodList.add(randomPetFood.toString());
        }

        return petFoodList;
    }

    public static PetFood generateRandomPetFood() {
        String name = FOOD_NAMES[randomValueInRange(0, FOOD_NAMES.length - 1)];
        int grams = randomValueInRange(50, 200);

        return new PetFood(name, grams);
    }

}
