package org.uvigo.esei.dm.todoapp.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBManager extends SQLiteOpenHelper {

    private static String TODO_DATABASE_NAME = "todo_db";
    private static int TODO_DATABASE_VERSION = 1;

    public static final String TASKS_TABLE_NAME = "tasks";
    public static final String TASK_COLUMN_ID = "_id";
    public static final String TASK_COLUMN_NAME = "name";
    public static final String TASK_COLUMN_DONE = "done";

    public DBManager(@Nullable Context context) {
        super(context, TODO_DATABASE_NAME, null, TODO_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TASKS_TABLE_NAME +"(" +
                    TASK_COLUMN_ID +" INTEGER PRIMARY KEY," +
                    TASK_COLUMN_NAME + " TEXT NOT NULL," +
                    TASK_COLUMN_DONE + " INTEGER NOT NULL" +
                    ")");
            db.setTransactionSuccessful();

        }catch (SQLException exception){
            Log.e(DBManager.class.getName(), "onCreate", exception);
        }finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
