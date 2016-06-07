package cn.wanther.toolkit.component;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import cn.wanther.toolkit.BuildConfig;

public class DataBaseHelper extends SQLiteOpenHelper {
    protected static final String TAG = "DataBaseHelper";

    public static final String KEY_DB_DATA_NAME = "db.uda.name";
    public static final String KEY_DB_DATA_VERSION = "db.uda.version";

    public static void close(SQLiteDatabase db) {
        if (db == null) {
            return;
        }
        try {
            db.close();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, e.getMessage(), e);
            }
        }
    }

    public static void close(Cursor c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, e.getMessage(), e);
            }
        }
    }

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        if (TextUtils.isEmpty(name)) {
            throw new RuntimeException("database name cannot be empty");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 删除中version scope的数据
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
