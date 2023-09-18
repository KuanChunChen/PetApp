package com.example.myapplication.db.dao;

import com.example.myapplication.db.entity.Pet;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PetDao {
    @Insert
    long insert(Pet pet);

    @Update
    void update(Pet pet);

    @Query("UPDATE pets SET species = :species, gender = :gender, birth = :birth, variety = :variety, bodyShape = :bodyShape, weight = :weight,imageBytes=:imageBytes WHERE name = :name")
    void updateByName(String name, String species,
                      String gender,
                      String birth,
                      String variety,
                      String bodyShape,
                      String weight,
                      byte[] imageBytes);

    @Query("SELECT id FROM pets WHERE name = :name")
    long getPetIdByName(String name);

    @Delete
    void delete(Pet pet);

    @Query("SELECT * FROM pets")
    List<Pet> getAllPets();

    @Query("SELECT * FROM pets WHERE id = :id")
    Pet getPetById(long id);

    @Query("SELECT * FROM pets WHERE id = (SELECT MAX(id) FROM pets)")
    Pet getLatestPet();

    @Query("SELECT EXISTS(SELECT 1 FROM pets WHERE name = :name)")
    boolean checkIfNameExist(String name);

}
