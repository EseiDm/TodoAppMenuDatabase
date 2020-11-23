package org.uvigo.esei.dm.todoapp;

import android.app.Application;

import org.uvigo.esei.dm.todoapp.database.DBManager;

public class TodoApplication extends Application {

    private DBManager dbManager;

    @Override
    public void onCreate() {
        super.onCreate();
        this.dbManager = new DBManager(this);
    }

    public DBManager getDbManager() {
        return dbManager;
    }
}
