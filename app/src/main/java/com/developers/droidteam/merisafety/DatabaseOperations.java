package com.developers.droidteam.merisafety;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by siddharth on 6/21/2017.
 */

public class DatabaseOperations extends SQLiteOpenHelper {

    public static final int database_version = 1;
    public String CREATE_QUERY = "CREATE TABLE " + TableData.TableInfo.TABLE_NAME + "(" + TableData.TableInfo.USER_NAME + " TEXT, " + TableData.TableInfo.USER_PASS + " TEXT);";
    public String CREATE_QUERY_GAUR = "CREATE TABLE " + TableData.TableInfo.TABLE_NAME_GAUR + "("+TableData.TableInfo.GAUR_NAME+" TEXT, "+ TableData.TableInfo.GAUR_NUM+" TEXT, "+ TableData.TableInfo.GAUR_EMAIL+" TEXT);";
    public DatabaseOperations(Context context) {
        super(context, TableData.TableInfo.DATABASE_NAME, null, database_version);
        Log.d("Database Operations", "Database Created Successfully");
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_QUERY);
        sdb.execSQL(CREATE_QUERY_GAUR);

        Log.d("Database Operations", "Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Put information into the database
     */

    public long putInformation(DatabaseOperations dop, String name, String pass) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.TableInfo.USER_NAME, name);
        cv.put(TableData.TableInfo.USER_PASS, pass);
        long k = SQ.insert(TableData.TableInfo.TABLE_NAME, null, cv);
        Log.d("Database Operations", "One raw data inserted");
      return k;
    }

    public void putInformationGaur(DatabaseOperations dop, String name, String num,String email) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.TableInfo.GAUR_NAME, name);
        cv.put(TableData.TableInfo.GAUR_NUM, num);
        cv.put(TableData.TableInfo.GAUR_EMAIL, email);
        long k = SQ.insert(TableData.TableInfo.TABLE_NAME_GAUR, null, cv);
        Log.d("Database Operations", "One raw data inserted");
    }

    /**
     * Retreiving data from the database
     */
    public Cursor getInformation(DatabaseOperations dop) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] coloums = {TableData.TableInfo.USER_NAME, TableData.TableInfo.USER_PASS};
        Cursor CR = SQ.query(TableData.TableInfo.TABLE_NAME, coloums, null, null, null, null, null);
        return CR;
    }

    public Cursor getInformationGaur(DatabaseOperations dop) {
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] coloums = {TableData.TableInfo.GAUR_NAME, TableData.TableInfo.GAUR_NUM,TableData.TableInfo.GAUR_EMAIL};
        Cursor CR = SQ.query(TableData.TableInfo.TABLE_NAME_GAUR, coloums, null, null, null, null, null);
        return CR;
    }

    // get user password
    public Cursor getUserPass(DatabaseOperations DOP, String user) {
        SQLiteDatabase SQ = DOP.getReadableDatabase();
        String selection = TableData.TableInfo.USER_NAME + " LIKE ?";
        String coloumns[] = {TableData.TableInfo.USER_PASS};
        String args[] = {user};
        Cursor CR = SQ.query(TableData.TableInfo.TABLE_NAME, coloumns, selection, args, null, null, null);
        return CR;
    }

}