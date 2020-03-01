package com.dev.a0301words.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

//    用于创建数据库实例
@Database(entities = {Word.class}, version = 5, exportSchema = false)
public abstract class WordDataBase extends RoomDatabase {
    private static WordDataBase INSTANCE;

    public static synchronized WordDataBase getWordDataBase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), WordDataBase.class, "word_db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build();
        }

        return INSTANCE;
    }

    public abstract WordDao getWordDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN foo_data   INTEGER NOT NULL DEFAULT 1");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN bar_data INTEGER NOT NULL DEFAULT 1");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE word_temp (id INTEGER PRIMARY KEY NOT NULL" +
                    ",english_word TEXT" +
                    ",chinese_meaning TEXT)");

            database.execSQL("INSERT INTO word_temp(id,english_word,chinese_meaning)" +
                    "SELECT id,english_word,chinese_meaning FROM word");

            database.execSQL("DROP TABLE word");

            database.execSQL("ALTER TABLE word_temp RENAME TO word");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE word ADD COLUMN chinese_invisible INTEGER NOT NULL DEFAULT 0");
        }
    };
}
