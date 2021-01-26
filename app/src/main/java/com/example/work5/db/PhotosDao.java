package com.example.work5.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.work5.model.Photo;

import java.util.List;

@Dao
public interface PhotosDao {
    // Выбрать все в бд
    @Query("SELECT * FROM Photos")
    public List<Photo> LoadAll();
    // Вставка url фото в бд
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertPhoto(Photo photo);
    // удаление из бд
    @Delete
    public void deletePhoto(Photo photo);
}
