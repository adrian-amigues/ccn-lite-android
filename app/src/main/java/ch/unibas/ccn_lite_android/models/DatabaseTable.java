package ch.unibas.ccn_lite_android.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by maria on 2016-11-25.
 */

//This class is doing some manipulation on a table stored in the user's smart phone.
public class DatabaseTable {
    public final String tableName = "PicturePathTable2";
    public final String firstColumnName = "Name";
    public final String secondColumnName = "PictureAddress";
    SQLiteDatabase myDB=null;

    public DatabaseTable(SQLiteDatabase myDB){
        this.myDB = myDB;
    }

    //Creates a table with the name specified in tableName field if this table does not exist.
    public void createTable(){
        myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + tableName
                + " (Name VARCHAR, PictureAddress VARCHAR);");
    }

    //Selects all data from the table with the name specified in tableName field
    public Cursor selectData(){
        Cursor c = myDB.rawQuery("SELECT * FROM " + tableName , null);
        return c;
    }

    //Updates the PictureAddress column of the table with the name specified in tableName field
    public void updateTable(Uri uri, String areaName){
        myDB.execSQL("UPDATE "
                + tableName
                + " SET PictureAddress='" + uri + "'"
                + " WHERE Name = '" + areaName + "'");
    }

    //Inserts into the table with the name specified in tableName field
    public void insertToTable(Uri uri, String areaName){
        myDB.execSQL("INSERT INTO "
                + tableName
                + " (Name, PictureAddress)"
                + " VALUES ('" + areaName + "','" + uri + "');");
    }

    //Deletes a specific row from the table with the name specified in tableName field
    public void deleteFromTable(String areaName){
        myDB.execSQL("DELETE from "
                + tableName
                + " WHERE Name = '" + areaName + "'");
    }


}
