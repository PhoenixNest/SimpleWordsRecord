package com.dev.a0301words.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//    用于管理数据库操作
@Dao //Database Access Object
public interface WordDao {
    @Insert
    void insertWords(Word... words);

    @Update
    void updateWords(Word... words);

    @Delete
    void deleteWords(Word... words);

    @Query("DELETE FROM word")
    void deleteAllWords();

    @Query("SELECT * FROM word ORDER BY ID DESC")
    LiveData<List<Word>> getAllLiveWords();

    @Query("SELECT * FROM word WHERE english_word LIKE :inputTemp ORDER BY ID DESC")
    LiveData<List<Word>> getLiveListWithInputTemp(String inputTemp);

}
