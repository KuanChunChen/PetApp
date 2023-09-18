package com.example.myapplication.db;


import com.example.myapplication.db.dao.NutritionFactsDao;
import com.example.myapplication.db.dao.PetDao;
import com.example.myapplication.db.entity.Pet;
import com.example.myapplication.model.NutritionFacts;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Pet.class,
        NutritionFacts.class}, version = 2)

public abstract class AppDatabase extends RoomDatabase {
    public abstract PetDao petDao();
    public abstract NutritionFactsDao nutritionFactsDao();
}