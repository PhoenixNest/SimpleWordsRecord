package com.dev.a0301words.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.dev.a0301words.db.Word;
import com.dev.a0301words.db.WordDao;
import com.dev.a0301words.db.WordDataBase;

import java.util.List;

//    仓库类，用于获取数据(本地，云端...)
public class WordRepository {
    private WordDao wordDao;
    private LiveData<List<Word>> liveWordList;

    public WordRepository(Context context) {

        WordDataBase wordDataBase = WordDataBase.getWordDataBase(context.getApplicationContext());
        wordDao = wordDataBase.getWordDao();
        liveWordList = wordDao.getAllLiveWords();
    }

    public LiveData<List<Word>> getLiveWordList() {
        return liveWordList;
    }

    public LiveData<List<Word>> findWords(String inputWords) {
        return wordDao.getLiveListWithInputTemp("%" + inputWords + "%");
    }

    public void insertWords(Word... words) {
        new InsertAsyncTask(wordDao).execute(words);
    }

    public void updateWords(Word... words) {
        new UpdateAsyncTask(wordDao).execute(words);
    }

    public void deleteWords(Word... words) {
        new DeleteAsyncTask(wordDao).execute(words);
    }

    public void deleteALlWords() {
        new DeleteAllAsyncTask(wordDao).execute();
    }

    //增
    public static class InsertAsyncTask extends AsyncTask<Word, Void, Void> {

        private WordDao wordDao;

        InsertAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insertWords(words);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//        任务结束后将结果带回主线程
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
//            进度发生更新时调用，可用于进度条滚动
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
//            后台任务执行之前呼叫
            super.onPreExecute();
        }
    }

    //改
    public static class UpdateAsyncTask extends AsyncTask<Word, Void, Void> {

        private WordDao wordDao;

        UpdateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }
    }

    //根据Words删除
    public static class DeleteAsyncTask extends AsyncTask<Word, Void, Void> {

        private WordDao wordDao;

        DeleteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    //删除全部
    public static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private WordDao wordDao;

        DeleteAllAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }
}
