package ch.unibas.ccn_lite_android.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by maria on 2016-11-25.
 */

public class DatabaseTable {
    public final String tableName = "PicturePathTable2";
    public final String firstColumnName = "Name";
    public final String secondColumnName = "PictureAddress";
    SQLiteDatabase myDB=null;

    public DatabaseTable(SQLiteDatabase myDB){
        this.myDB = myDB;
    }

    public void createTable(){
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + tableName
                + " (Name VARCHAR, PictureAddress VARCHAR);");
    }

    public Cursor selectData(){
        Cursor c = myDB.rawQuery("SELECT * FROM " + tableName , null);
        return c;
    }

    public void updateTable(Uri uri, String areaName){
        myDB.execSQL("UPDATE "
                + tableName
                + " SET PictureAddress='" + uri + "'"
                + " WHERE Name = '" + areaName + "'");
    }

    public void insertToTable(Uri uri, String areaName){
        myDB.execSQL("INSERT INTO "
                + tableName
                + " (Name, PictureAddress)"
                + " VALUES ('" + areaName + "','" + uri + "');");
    }

    public void deleteFromTable(String areaName){
        myDB.execSQL("DELETE from "
                + tableName
                + " WHERE Name = '" + areaName + "'");
    }


}
