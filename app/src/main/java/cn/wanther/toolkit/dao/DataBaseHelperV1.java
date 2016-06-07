package cn.wanther.toolkit.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import cn.wanther.toolkit.component.DataBaseHelper;

public class DataBaseHelperV1 extends DataBaseHelper {

    public DataBaseHelperV1(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        String sql = "CREATE TABLE LOCAl_DATA("
                + "CATEGORY INTEGER NOT NULL DEFAULT 0,"
                + "KEY TEXT NOT NULL,"
                + "VALUE TEXT NOT NULL"
                + ")";

        db.execSQL(sql);
    }

}
