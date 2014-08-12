package com.example.journal.work;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.journal.App;
import com.example.journal.R;

import java.util.Date;

/**
 * Created by Artem on 30.06.14.
 */
public class Profile {

    private static Profile sProfile = null;
    private SharedPreferences mPref;
    private String mUserId = null;

    /**
     * Промежуток через который обновляем журналы
     */
    private static final long INTERVAL_UPDATE_TIME =
            App.getContext().getResources().getInteger(R.integer.interval_updata_tame) * 1000;
    private Profile() {
        Context context = App.getContext();
        mPref = context.getSharedPreferences("journal", context.MODE_PRIVATE);
    }

    public static Profile getInstance() {
        if (sProfile == null) {
            sProfile = new Profile();
        }
        return sProfile;
    }

    /**
     * Показывает первый запуск или нет
     */
    public boolean isOpenFirst() {
        synchronized (KEY.OPEN_FIRST) {
            boolean isOpenFist = mPref.getBoolean(KEY.OPEN_FIRST, true);
            if (isOpenFist) {
                SharedPreferences.Editor ed = mPref.edit();
                ed.putBoolean(KEY.OPEN_FIRST, false);
                ed.commit();
            }
            return isOpenFist;
        }

    }

    /**
     * Возвращает пришло ли время обновления списка журналов
     */
    public boolean isUpdataTime() {
        return (new Date().getTime() - getUpdataTime()) > INTERVAL_UPDATE_TIME;
    }

    /**
     * Возвращает время последнего обновления списков журналов
     */
    private long getUpdataTime() {
        synchronized (KEY.UPDATA_TIME) {
            return mPref.getLong(KEY.UPDATA_TIME, 0);
        }
    }

    /**
     * Перезаписовыет время обновления списков журналов
     */
    public void setUpdataTime() {
        synchronized (KEY.UPDATA_TIME) {
            SharedPreferences.Editor ed = mPref.edit();
            ed.putLong(KEY.UPDATA_TIME, new Date().getTime());
            ed.commit();
        }
    }

    private interface KEY {
        static public String OPEN_FIRST = "open_first";
        static public String UPDATA_TIME = "updata_time";
    }

}
