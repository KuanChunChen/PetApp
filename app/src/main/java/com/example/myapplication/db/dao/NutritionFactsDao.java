package com.example.myapplication.db.dao;

import com.example.myapplication.model.NutritionFacts;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NutritionFactsDao {
    @Insert
    long insert(NutritionFacts nutritionFacts);

    @Update
    void update(NutritionFacts nutritionFacts);

    @Query("UPDATE nutrition_facts SET carbohydrates = :carbohydrates, protein = :protein, fat = :fat, vitamins = :vitamins, minerals = :minerals, dietaryFiber = :dietaryFiber, water = :water WHERE name = :name")
    void updateNutritionFacts(String name,
                              int carbohydrates,
                              int protein,
                              int fat,
                              int vitamins,
                              int minerals,
                              int dietaryFiber,
                              int water);

    @Delete
    void delete(NutritionFacts nutritionFacts);

    @Query("SELECT * FROM nutrition_facts")
    List<NutritionFacts> getAllNutritionFacts();

    @Query("SELECT * FROM nutrition_facts WHERE name != :specifiedName")
    List<NutritionFacts> getAllNutritionFactsExcept(String specifiedName);
    @Query("SELECT name FROM nutrition_facts")
    String[] getAllNames();

    @Query("SELECT COUNT(*) FROM nutrition_facts")
    int getNutritionFactsCount();

    @Query("SELECT * FROM nutrition_facts WHERE name = :specifiedName")
    NutritionFacts getNutritionFactsByName(String specifiedName);

    @Query("SELECT EXISTS(SELECT 1 FROM nutrition_facts WHERE name = :name)")
    boolean checkIfNameExist(String name);
}