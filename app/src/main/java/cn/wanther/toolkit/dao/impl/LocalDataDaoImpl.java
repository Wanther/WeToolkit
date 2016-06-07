package cn.wanther.toolkit.dao.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.wanther.toolkit.component.DataBaseHelper;
import cn.wanther.toolkit.dao.BasicDaoSupport;
import cn.wanther.toolkit.dao.LocalDataDao;
import cn.wanther.toolkit.exception.StorageException;

public class LocalDataDaoImpl extends BasicDaoSupport implements LocalDataDao {
    @Override
    public void save(final int category, final String key, final String content) throws StorageException{
        execute(new DoInSQLite<Void>() {
            @Override
            public Void doInSQLite(SQLiteDatabase db) throws Exception {

                ContentValues cv = new ContentValues();
                cv.put("CATEGORY", category);
                cv.put("KEY", key);
                cv.put("VALUE", content);

                Cursor c = null;

                try {
                    c = db.query("LOCAl_DATA",
                            new String[]{"KEY"},
                            "CATEGORY=? AND KEY=?", new String[]{category + "", key},
                            null, null, null);
                    if(c.moveToNext()){
                        db.update("LOCAl_DATA", cv, "CATEGORY=? AND KEY=?", new String[]{category + "", key});
                    }else{
                        db.insert("LOCAl_DATA", null, cv);
                    }

                } finally {
                    DataBaseHelper.close(c);
                }

                return null;
            }
        });
    }

    @Override
    public void save(String key, String content) throws StorageException{
        save(CATE_SCOPE_APP, key, content);
    }

    @Override
    public String get(final int category, final String key) throws StorageException{
        return executeNoTransactionReadOnly(new DoInSQLite<String>() {

            @Override
            public String doInSQLite(SQLiteDatabase db) throws Exception {

                Cursor c = null;

                try {
                    c = db.query("LOCAl_DATA", new String[]{"VALUE"},
                            "CATEGORY=? AND KEY=?", new String[]{category + "", key},
                            null, null, null);

                    if(c.moveToNext()){
                        return c.getString(c.getColumnIndex("VALUE"));
                    }

                } finally {
                    DataBaseHelper.close(c);
                }

                return null;
            }
        });
    }

    @Override
    public String get(String key) throws StorageException{
        return get(CATE_SCOPE_APP, key);
    }

    @Override
    public void delete(final int category) throws StorageException{
        executeNoTransaction(new DoInSQLite<Void>() {
            @Override
            public Void doInSQLite(SQLiteDatabase db) throws Exception {
                db.delete("LOCAl_DATA", "CATEGORY=?", new String[]{category + ""});
                return null;
            }
        });
    }

    @Override
    public void delete(final int category, final String key) throws StorageException{
        executeNoTransaction(new DoInSQLite<Void>() {
            @Override
            public Void doInSQLite(SQLiteDatabase db) throws Exception {
                db.delete("LOCAl_DATA", "CATEGORY=? AND KEY=?", new String[]{category + "", key});
                return null;
            }
        });
    }
}
