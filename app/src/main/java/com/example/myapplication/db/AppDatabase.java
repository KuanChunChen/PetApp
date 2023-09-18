package com.example.myapplication.db;


import com.example.myapplication.db.dao.PetDao;
import com.example.myapplication.db.entity.Pet;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Pet.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PetDao petDao();
}