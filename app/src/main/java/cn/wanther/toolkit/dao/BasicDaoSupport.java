package cn.wanther.toolkit.dao;

import android.database.sqlite.SQLiteDatabase;

import cn.wanther.toolkit.component.DataBaseHelper;
import cn.wanther.toolkit.exception.StorageException;

public class BasicDaoSupport {
    private static final String TAG = "BasicDaoSupport";

    public interface DoInSQLite<Result>{
        Result doInSQLite(SQLiteDatabase db) throws Exception;
    }

    private DataBaseHelper mDbHelper;

    public BasicDaoSupport(){}

    public void setDBHelper(DataBaseHelper helper){
        mDbHelper = helper;
    }

    protected DataBaseHelper getDBHelper(){
        return mDbHelper;
    }

    protected <Result> Result execute(DoInSQLite<Result> opr) throws StorageException {
        synchronized (mDbHelper) {
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                Result result = opr.doInSQLite(db);

                db.setTransactionSuccessful();

                return result;

            } catch(Exception e){
                throw new StorageException("SQLite execute inTransaction failed", e);
            }finally {
                db.endTransaction();
                DataBaseHelper.close(db);
            }
        }
    }

    protected <Result> Result executeReadOnly(DoInSQLite<Result> opr) throws StorageException{
        synchronized (mDbHelper) {
            SQLiteDatabase db = getDBHelper().getReadableDatabase();
            db.beginTransaction();
            try {
                Result result = opr.doInSQLite(db);

                db.setTransactionSuccessful();

                return result;

            } catch(Exception e){
                throw new StorageException("SQLite execute inTransaction failed", e);
            }finally {
                db.endTransaction();
                DataBaseHelper.close(db);
            }
        }
    }

    protected <Result> Result executeNoTransaction(DoInSQLite<Result> opr) throws StorageException{
        synchronized (mDbHelper) {
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            try {
                Result result = opr.doInSQLite(db);

                return result;

            } catch(Exception e){
                throw new StorageException("SQLite execute inTransaction failed", e);
            }finally {
                DataBaseHelper.close(db);
            }
        }
    }

    protected <Result> Result executeNoTransactionReadOnly(DoInSQLite<Result> opr) throws StorageException{
        synchronized (mDbHelper) {
            SQLiteDatabase db = getDBHelper().getReadableDatabase();
            try {
                Result result = opr.doInSQLite(db);

                return result;

            } catch(Exception e){
                throw new StorageException("SQLite execute inTransaction failed", e);
            }finally {
                DataBaseHelper.close(db);
            }
        }
    }
}
