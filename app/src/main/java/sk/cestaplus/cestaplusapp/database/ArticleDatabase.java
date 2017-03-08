package sk.cestaplus.cestaplusapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.DateFormats;

/**
 * Created by Matej on 9. 4. 2015.
 */
public class ArticleDatabase{

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    public ArticleDatabase(Context context) {
        databaseHelper = new DatabaseHelper(context);
        db = databaseHelper.getWritableDatabase();
    }

    public void updateArticles(ArrayList<ArticleObj> listArticles){
        //create a sql prepared statement
        String sqlString = "INSERT INTO " + DatabaseHelper.VSETKO_TABLE_NAME + " VALUES (?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = db.compileStatement(sqlString); //compile the statement

        db.beginTransaction(); // begin a transaction

        //delete apropiate number of old articles
        try {
            db.execSQL("DELETE FROM " + DatabaseHelper.VSETKO_TABLE_NAME + " WHERE " + DatabaseHelper.COLUMN_UID + " IN " +
                         "(SELECT " + DatabaseHelper.COLUMN_UID + " FROM " + DatabaseHelper.VSETKO_TABLE_NAME + " " +
                            //"ORDER BY " + DatabaseHelper.COLUMN_PUB_DATE + " ASC " +
                            "ORDER BY " + DatabaseHelper.COLUMN_UID + " ASC " +
                            "LIMIT " + listArticles.size() + ");"
            );
        } catch (SQLiteException exception) {
            Log.e("database", exception + "");
        }

        //insert new ones // using bulk insert
        for(int i = listArticles.size() - 1; i >= 0; i--){
        //for(int i = listArticles.size()-1; i >= 0; i--){
            ArticleObj actArticle = listArticles.get(i);
            statement.clearBindings();

            //for given column index, bind the data to be put inside that index
            statement.bindString(2, actArticle.getTitle());
            statement.bindString(3, actArticle.getShort_text());
            statement.bindString(4, actArticle.getAuthor());
            statement.bindString(5, actArticle.getImageUrl());
            statement.bindLong(6, actArticle.getPubDate() == null ? -1 : actArticle.getPubDate().getTime()); //datum ulozeny v milisekondach
            statement.bindString(7, actArticle.getSection());
            statement.bindString(8, actArticle.getID());
            statement.bindLong(9, actArticle.isLocked() ? 1 : 0); // ak je locked ulozi 1 inak ulozi 0

            statement.execute();
        }

        db.setTransactionSuccessful(); //set the transaction as successful
        db.endTransaction(); //end the transaction
    } //end updateArticles

    public void insertArticlesAll(ArrayList<ArticleObj> listArticles, boolean clearPrevious){
        if (clearPrevious){
            deleteArticlesAll();
        }
        Log.i("database", "inserting articles");

    //bulk insert
        //create a sql prepared statement?
        String sqlString = "INSERT INTO " + DatabaseHelper.VSETKO_TABLE_NAME + " VALUES (?,?,?,?,?,?,?,?);";

        //compile the statement
        SQLiteStatement statement = db.compileStatement(sqlString);

        // begin a transaction
        db.beginTransaction();

        for(int i = 0; i < listArticles.size(); i++){
            ArticleObj actArticle = listArticles.get(i);
            statement.clearBindings();

            //for given column index, bind the data to be put inside that index
            statement.bindString(2, actArticle.getTitle());
            statement.bindString(3, actArticle.getShort_text());
            statement.bindString(4, actArticle.getImageUrl());
            statement.bindLong(5, actArticle.getPubDate() == null ? -1 : actArticle.getPubDate().getTime()); //datum ulozeny v milisekondach
            statement.bindString(6, actArticle.getSection());
            statement.bindString(7, actArticle.getID());
            statement.bindLong(8, actArticle.isLocked() ? 1 : 0); // ak je locked ulozi 1 inak ulozi 0

            statement.execute();
        }
        //set the transaction as sucesfull
        db.setTransactionSuccessful();
        //end the transaction
        db.endTransaction();

    }//end insertArticlesAll

    /**
     * Table dropping is needed because, we also need to reset AUTOINCREMENT value.
     * There is no TRUNCATE method in SQLITE - https://www.tutorialspoint.com/sqlite/sqlite_truncate_table.htm
     * and UPDATE sequence was not tried:
     *      http://sqlite.1065341.n5.nabble.com/Reset-auto-increment-truncate-td35528.html
     *      http://stackoverflow.com/questions/9759502/resetting-autoincrement-in-android-sqlite
     */
    public void deleteArticlesAll() {
        try {
            String sql = "DROP TABLE " + DatabaseHelper.VSETKO_TABLE_NAME + ";";
            db.execSQL(sql);
        } catch (SQLiteException exception) {
            Log.e("database", exception + "");
        }

        databaseHelper.createAllArticlesTable(db);
    }

    public ArrayList<ArticleObj> getAllArticles(){
        Log.i("database", "reading data");

        ArrayList<ArticleObj> retArticles = new ArrayList<>();

        //list of columns to be retrieved
        String [] columns = {
                DatabaseHelper.COLUMN_UID,
                DatabaseHelper.COLUMN_TITLE,
                DatabaseHelper.COLUMN_SHORT_TEXT,
                DatabaseHelper.COLUMN_AUTHOR,
                DatabaseHelper.COLUMN_IMAGE_URL,
                DatabaseHelper.COLUMN_PUB_DATE,
                DatabaseHelper.COLUMN_SECTION,
                DatabaseHelper.COLUMN_ARTICLE_ID,
                DatabaseHelper.COLUMN_LOCKED
        };

        //Cursor cursor = db.query(DatabaseHelper.VSETKO_TABLE_NAME, columns, null, null, null, null, DatabaseHelper.COLUMN_PUB_DATE + " DESC", null);
        Cursor cursor = db.query(DatabaseHelper.VSETKO_TABLE_NAME, columns, null, null, null, null,
                /*ORDER BY*/ DatabaseHelper.COLUMN_UID + " DESC", null);

        if(cursor != null && cursor.moveToFirst()){
            do {
                retArticles.add(new ArticleObj(
                        cursor.getString(1), // title
                        cursor.getString(2), // short_text
                        cursor.getString(3), // author
                        cursor.getString(4),
                        cursor.getLong(5) == -1 ? null : new Date(cursor.getLong(5)),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getLong(8) == 1 ? true : false
                ));

            } while (cursor.moveToNext());

            cursor.close();
        } // end if

        return retArticles;


    }//end getAllArticles


    public Date getFirstArticleDate(){
        String sql = "SELECT " + DatabaseHelper.COLUMN_PUB_DATE + " from " + DatabaseHelper.VSETKO_TABLE_NAME + " " +
                "where " + DatabaseHelper.COLUMN_UID + " = (SELECT MAX(" + DatabaseHelper.COLUMN_UID + ") from " + DatabaseHelper.VSETKO_TABLE_NAME + ")";

        Cursor cursor = db.rawQuery(sql, null);

        long pub_date = 0;

        if(cursor != null && cursor.moveToFirst()){
            //do {
                pub_date = cursor.getLong(0);
            //} while (cursor.moveToNext());
        } // end if

        Date firstArticleDate = new Date(pub_date);
        return firstArticleDate;
    }

    /**
     * INNER CLASS DATABASE HELPER
     */
    public static class DatabaseHelper
    extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "database.db";
        private static final int DATABASE_VERSION = 2;

    //tables
        private static final String VSETKO_TABLE_NAME = "VSETKO";
        private static final String TEMA_TABLE_NAME = "TEMA_MESIACA";

    //columns
        private static final String COLUMN_UID = "_id";            //id of row in database
        private static final String COLUMN_TITLE = "title";
        private static final String COLUMN_SHORT_TEXT = "short_text";
        private static final String COLUMN_AUTHOR = "author";
        private static final String COLUMN_IMAGE_URL = "image_url";
        private static final String COLUMN_PUB_DATE = "pub_date";
        private static final String COLUMN_SECTION = "section";
        private static final String COLUMN_ARTICLE_ID = "article_id"; // id of article received and sent to the server
        private static final String COLUMN_LOCKED = "locked";

        /**
         * CONSTRUCTOR
         *
         * @param context
         */
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            //"/mnt/sdcard/database_name.db"
            //super(context, "/sdcard/test_db.db", null, DATABASE_VERSION);

            Log.i("database", "Database CONSTRUCTOR was called");
            //getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createAllArticlesTable(db);
        }

        private void createAllArticlesTable(SQLiteDatabase db) {
            try {
                db.execSQL("CREATE TABLE " + VSETKO_TABLE_NAME + " (" +
                            COLUMN_UID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , " +
                            COLUMN_TITLE + " VARCHAR, " +
                            COLUMN_SHORT_TEXT + " VARCHAR, " +
                            COLUMN_AUTHOR + " VARCHAR, " +
                            COLUMN_IMAGE_URL + " VARCHAR, " +
                            COLUMN_PUB_DATE + " INTEGER, " +
                            COLUMN_SECTION + " VARCHAR, " +
                            COLUMN_ARTICLE_ID + " VARCHAR, " +
                            COLUMN_LOCKED + " INTEGER);");
                Log.i("database", "Database onCreate was called");

            } catch (SQLiteException exception) {
                Log.e("database", exception+"");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Log.i("database", "Database onUPDATE was called");
                db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.VSETKO_TABLE_NAME + " ;");

            } catch (SQLiteException exception) {
                Log.e("database", exception+"");
            }
            //Toast.makeText(CustomApplication.getCustomAppContext(), "Database onUPDATE", Toast.LENGTH_LONG).show();

            onCreate(db);
        }

        /**
         * HELPER METHOD FOR AndroidDatabaseManager
         */
        /*
        public ArrayList<Cursor> getData(String Query){
            //get writable database
            SQLiteDatabase sqlDB = this.getWritableDatabase();
            String[] columns = new String[] { "mesage" };
            //an array list of cursor to save two cursors one has results from the query
            //other cursor stores error message if any errors are triggered
            ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
            MatrixCursor Cursor2= new MatrixCursor(columns);
            alc.add(null);
            alc.add(null);


            try{
                String maxQuery = Query ;
                //execute the query results will be save in Cursor c
                Cursor c = sqlDB.rawQuery(maxQuery, null);


                //add value to cursor2
                Cursor2.addRow(new Object[] { "Success" });

                alc.set(1,Cursor2);
                if (null != c && c.getCount() > 0) {


                    alc.set(0,c);
                    c.moveToFirst();

                    return alc ;
                }
                return alc;
            } catch(SQLException sqlEx){
                Log.d("printing exception", sqlEx.getMessage());
                //if any exceptions are triggered save the error message to cursor an return the arraylist
                Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
                alc.set(1,Cursor2);
                return alc;
            } catch(Exception ex){

                Log.d("printing exception", ex.getMessage());

                //if any exceptions are triggered save the error message to cursor an return the arraylist
                Cursor2.addRow(new Object[] { ""+ex.getMessage() });
                alc.set(1,Cursor2);
                return alc;
            }


        }
        */
    } //end DatabaseHelper
} //end ArticleDatabase
