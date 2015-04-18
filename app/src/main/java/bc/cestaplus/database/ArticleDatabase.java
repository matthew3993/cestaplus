package bc.cestaplus.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import bc.cestaplus.objects.ArticleObj;

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
            String sqlString = "INSERT INTO " + DatabaseHelper.VSETKO_TABLE_NAME + " VALUES (?,?,?,?,?,?,?,?);";
            //complice the statment
            SQLiteStatement statement = db.compileStatement(sqlString);

        // begin a transaction
        db.beginTransaction();

        //delete apropiate number of old articles
            try {

                db.execSQL("DELETE FROM " + DatabaseHelper.VSETKO_TABLE_NAME + " WHERE " + DatabaseHelper.COLUMN_UID + " IN " +
                             "(SELECT " + DatabaseHelper.COLUMN_UID + " FROM " + DatabaseHelper.VSETKO_TABLE_NAME + " " +
                                "ORDER BY " + DatabaseHelper.COLUMN_PUB_DATE + " ASC " +
                                "LIMIT " + listArticles.size() + ");"
                );
            } catch (SQLiteException exception) {
                Log.e("database", exception + "");
            }

        //insert new ones // using bulk insert
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
    } //end updateArticles


    public void insertArticlesAll(ArrayList<ArticleObj> listArticles, boolean clearPrevious){
        if (clearPrevious){
            deleteArticlesAll();
        }
        Log.i("database", "inserting articles");

    //bulk insert
        //create a sql prepared statement?
        String sqlString = "INSERT INTO " + DatabaseHelper.VSETKO_TABLE_NAME + " VALUES (?,?,?,?,?,?,?,?);";

        //complice the statment
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

            //Toast.makeText(CustomApplication.getCustomAppContext(), "inserting entry " + i, Toast.LENGTH_SHORT).show();
            statement.execute();
        }
        //set the transaction as sucesfull
        db.setTransactionSuccessful();
        //end the transaction
        db.endTransaction();

    }//end insertArticlesAll

    private void deleteArticlesAll() {
        db.delete(DatabaseHelper.VSETKO_TABLE_NAME, null, null);
    }

    public ArrayList<ArticleObj> getAllArticles(){
        Log.i("database", "reading data");

        ArrayList<ArticleObj> retArticles = new ArrayList<>();

        //list of columns to be retrieved
        String [] columns = {
                DatabaseHelper.COLUMN_UID,
                DatabaseHelper.COLUMN_TITLE,
                DatabaseHelper.COLUMN_SHORT_TEXT,
                DatabaseHelper.COLUMN_IMAGE_URL,
                DatabaseHelper.COLUMN_PUB_DATE,
                DatabaseHelper.COLUMN_SECTION,
                DatabaseHelper.COLUMN_ARTICLE_ID,
                DatabaseHelper.COLUMN_LOCKED
        };

        Cursor cursor = db.query(DatabaseHelper.VSETKO_TABLE_NAME, columns, null, null, null, null, DatabaseHelper.COLUMN_PUB_DATE + " DESC", null);

        if(cursor != null && cursor.moveToFirst()){
            do {
                retArticles.add(new ArticleObj(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getLong(4) == -1 ? null : new Date(cursor.getLong(4)),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getLong(7) == 1 ? true : false
                ));
                //Toast.makeText(CustomApplication.getCustomAppContext(), "getting article object", Toast.LENGTH_SHORT).show();

            } while (cursor.moveToNext());
        } // end if

        return retArticles;


    }//end getAllArticles


    /**
     * INNER CLASS DATABASE HELPER
     */
    private static class DatabaseHelper
    extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "database.db";
        private static final int DATABASE_VERSION = 1;

    //tables
        private static final String VSETKO_TABLE_NAME = "VSETKO";
        private static final String TEMA_TABLE_NAME = "TEMA_MESIACA";

    //columns
        private static final String COLUMN_UID = "_id";            //id of row in database
        private static final String COLUMN_TITLE = "title";
        private static final String COLUMN_SHORT_TEXT = "short_text";
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
            try {
                db.execSQL("CREATE TABLE " + VSETKO_TABLE_NAME + " (" +
                            COLUMN_UID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , " +
                            COLUMN_TITLE + " VARCHAR, " +
                            COLUMN_SHORT_TEXT + " VARCHAR, " +
                            COLUMN_IMAGE_URL + " VARCHAR, " +
                            COLUMN_PUB_DATE + " INTEGER, " +
                            COLUMN_SECTION + " VARCHAR, " +
                            COLUMN_ARTICLE_ID + " VARCHAR, " +
                            COLUMN_LOCKED + " INTEGER);");
                Log.i("database", "Database onCreate was called");

            } catch (SQLiteException exception) {
                Log.e("database", exception+"");
            }
            //Toast.makeText(CustomApplication.getCustomAppContext(), "Database onCreate", Toast.LENGTH_LONG).show();
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

    } //end DatabaseHelper
} //end ArticleDatabase
