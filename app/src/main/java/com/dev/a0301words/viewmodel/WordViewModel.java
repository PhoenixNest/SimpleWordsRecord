package com.dev.a0301words.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dev.a0301words.db.Word;
import com.dev.a0301words.repository.WordRepository;

import java.util.List;

//    管理UI数据
public class WordViewModel extends AndroidViewModel {

    private WordRepository wordRepository;

    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
    }

    public LiveData<List<Word>> getLiveWordsList() {
        return wordRepository.getLiveWordList();
    }

    //    查
    public LiveData<List<Word>> findWords(String inputWords) {
        return wordRepository.findWords(inputWords);
    }

    //    增
    public void insertWords(Word... words) {
        wordRepository.insertWords(words);
    }

    //    改
    public void updateWords(Word... words) {
        wordRepository.updateWords(words);
    }

    //    删(单个)
    public void deleteWords(Word... words) {
        wordRepository.deleteWords(words);
    }

    //    清空
    public void deleteALlWords() {
        wordRepository.deleteALlWords();
    }
}
