package com.example.myapplication.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pets")
public class Pet {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "name", collate = ColumnInfo.NOCASE)
    public String name;
    public String species;
    public String gender;
    public String birth;
    public String variety;
    public String bodyShape;
    public String weight;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] imageBytes;

}
