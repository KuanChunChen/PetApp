package com.example.myapplication;

import androidx.room.Room;
import android.app.Application;
import android.content.Context;

import com.example.myapplication.db.AppDatabase;

public class PetApplication extends Application {

    private static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "pet_database")
                .build();
    }

    public static AppDatabase getDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, "my-database")
                    .build();
        }
        return appDatabase;
    }
}