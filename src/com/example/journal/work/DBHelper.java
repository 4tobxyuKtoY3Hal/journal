package com.example.journal.work;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.journal.manager.BaseManager;

/**
 * Created by Artem on 23.06.14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String CREATE_JOURNALS =
            "create table " + BaseManager.TABLE.JOURNAL + "(" +
                    BaseManager.JOURNAL.ID + " integer primary key autoincrement, " +
                    BaseManager.JOURNAL.NAME + " text, " +
                    BaseManager.JOURNAL.DES + " text, " +
                    BaseManager.JOURNAL.URI_IMG + " text, " +
                    BaseManager.JOURNAL.ID_JOURNAL + " integer, " +
                    BaseManager.JOURNAL.STATUS + " text);";

    private static final String CREATE_PAGES =
            "create table " + BaseManager.TABLE.PAGES + "(" +
                    BaseManager.PAGE.ID + " integer primary key autoincrement, " +
                    BaseManager.PAGE.NUM_LIST + " text, " +
                    BaseManager.PAGE.URI_IMG_LIST + " text, " +
                    BaseManager.PAGE.ID_JOURNAL + " integer, " +
                    BaseManager.PAGE.URI_IMG_TUMB + " text);";

    private static final String CREATE_TITLE =
            "create table " + BaseManager.TABLE.TITLE + "(" +
                    BaseManager.TITLE.ID + " integer primary key autoincrement, " +
                    BaseManager.TITLE.NUM + " text, " +
                    BaseManager.TITLE.NAME + " text, " +
                    BaseManager.TITLE.ID_JOURNAL + " integer);";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_JOURNALS);
        db.execSQL(CREATE_PAGES);
        db.execSQL(CREATE_TITLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        switch (newVersion){
//            case 2:
//            case 3:
//            case 4:
//            case 5:
//        }
    }

}
