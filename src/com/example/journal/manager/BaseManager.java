package com.example.journal.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.journal.App;
import com.example.journal.work.DBHelper;

/**
 * Created by Artem on 23.06.14.
 */
public class BaseManager {

    private static final String LOG_CAT = "BaseManager";

    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;
    private static BaseManager sBaseManager = null;

    private BaseManager() {

    }

    public static BaseManager getInstance() {
        if (sBaseManager == null) {
            sBaseManager = new BaseManager();
        }
        return sBaseManager;
    }

    public void open() {
        mDBHelper = new DBHelper(App.getContext(), DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper != null) {
            mDBHelper.close();
        }
    }

    public int getMaxIdJournal() {
        int id = 0;
        Cursor cursor = mDB.query(TABLE.JOURNAL, new String[]{BaseManager.JOURNAL.ID_JOURNAL},
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int i = cursor.getInt(cursor.getColumnIndex(BaseManager.JOURNAL.ID_JOURNAL));
                if (i > id) id = i;
            }
            cursor.close();
        }
        return id;
    }

    public Cursor getJournals() {
        return mDB.query(TABLE.JOURNAL, null, null, null, null, null, null);
    }

    public Cursor getJournal(long idJournal) {
        return mDB.query(TABLE.JOURNAL, null, BaseManager.JOURNAL.ID + " = " + idJournal, null, null, null, null);
    }

    public Cursor getPages(int idJournal) {
        return mDB.query(TABLE.PAGES, null, null, null, null, null, null);
    }

    public Cursor getLoded(long idJournal) {
        return mDB.query(TABLE.PAGES, null, BaseManager.PAGE.ID_JOURNAL + " = " + idJournal, null, null, null, BaseManager.PAGE.ID_JOURNAL);
    }

    public int updateStatus(long idJournal) {
        ContentValues cv = new ContentValues();
        cv.put(BaseManager.JOURNAL.STATUS,BaseManager.STATUS_JOURNAL.UPLOADED);
        return mDB.update(TABLE.JOURNAL, cv, BaseManager.JOURNAL.ID + " = " + idJournal, null);
    }

    public Cursor getTitle(long idJournal) {
        return mDB.query(TABLE.TITLE, null, BaseManager.TITLE.ID_JOURNAL + " = " + idJournal, null, null, null, BaseManager.TITLE.ID_JOURNAL);
    }

    public long addJournal(String name, String des, String strUri, int id, String status) {

        Log.d(LOG_CAT, ">>> addJournal <<<");
        Log.d(LOG_CAT, "// name=" + name);
        Log.d(LOG_CAT, "// des=" + des);
        Log.d(LOG_CAT, "// strUri=" + strUri);
        Log.d(LOG_CAT, "// idJournal=" + id);
        Log.d(LOG_CAT, "// status=" + status);

        ContentValues cv = new ContentValues();
        cv.put(BaseManager.JOURNAL.NAME, name);
        cv.put(JOURNAL.DES, des);
        cv.put(BaseManager.JOURNAL.URI_IMG, strUri);
        cv.put(BaseManager.JOURNAL.ID_JOURNAL, id);
        cv.put(BaseManager.JOURNAL.STATUS, status);
        return mDB.insert(TABLE.JOURNAL, null, cv);
    }

    public void addPage(String uriPage, String uriTumb, int num, long idJurnal) {

        Log.d(LOG_CAT, ">>> addPage <<<");
        Log.d(LOG_CAT, "// uriPage=" + uriPage);
        Log.d(LOG_CAT, "// uriTumb=" + uriTumb);
        Log.d(LOG_CAT, "// num=" + num);
        Log.d(LOG_CAT, "// idJournal=" + idJurnal);

        ContentValues cv = new ContentValues();
        cv.put(BaseManager.PAGE.URI_IMG_LIST, uriPage);
        cv.put(BaseManager.PAGE.URI_IMG_TUMB, uriTumb);
        cv.put(BaseManager.PAGE.NUM_LIST, num);
        cv.put(BaseManager.PAGE.ID_JOURNAL, idJurnal);
        mDB.insert(TABLE.PAGES, null, cv);
    }

    public void addTitle(String name, int num, long idJurnal) {

        Log.d(LOG_CAT, ">>> addTitle <<<");
        Log.d(LOG_CAT, "// name=" + name);
        Log.d(LOG_CAT, "// num=" + num);
        Log.d(LOG_CAT, "// idJournal=" + idJurnal);

        ContentValues cv = new ContentValues();
        cv.put(BaseManager.TITLE.NAME, name);
        cv.put(BaseManager.TITLE.NUM, num);
        cv.put(BaseManager.TITLE.ID_JOURNAL, idJurnal);
        mDB.insert(TABLE.TITLE, null, cv);
    }

    public static interface TABLE {
        public static final String JOURNAL = "journals";
        public static final String PAGES = "pages";
        public static final String TITLE = "title";
    }

    public static interface JOURNAL {
        public static final String ID = "_id";
        public static final String URI_IMG = "uri_img";
        public static final String NAME = "name";
        public static final String DES = "des";
        public static final String ID_JOURNAL = "id_journal";
        public static final String STATUS = "status";
    }

    public static interface PAGE {
        public static final String ID = "_id";
        public static final String URI_IMG_LIST = "uri_img_list";
        public static final String URI_IMG_TUMB = "uri_img_tumb";
        public static final String NUM_LIST = "num_list";
        public static final String ID_JOURNAL = "id_journal";

    }

    public static interface TITLE {
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String ID_JOURNAL = "id_journal";
        public static final String NUM = "num";
    }


    public static interface STATUS_JOURNAL {
        public static final String NOT_LOADED = "not_loaded";
        public static final String UPLOADED = "uploaded";
        public static final String PROGRESS = "progress";
        public static final String BUY = "buy";
        public static final String FREE = "free";
    }

}
