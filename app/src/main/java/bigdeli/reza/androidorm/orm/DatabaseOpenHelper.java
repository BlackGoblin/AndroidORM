package bigdeli.reza.androidorm.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import bigdeli.reza.androidorm.model.Category;
import bigdeli.reza.androidorm.model.Step;
import bigdeli.reza.androidorm.model.Tutorial;
import bigdeli.reza.androidorm.model.User;

/**
 * This class is a utility class for initializing database.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    // constant objects
    private static final String DATABASE_NAME = "database";
    private static final int VERSION = 1;

    // database objects
    private static DatabaseOpenHelper instance;
    private Context context;


    private DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * get an instance to use database adapter's functionality
     *
     * @param context the context to be used
     * @return an static instance of the database adapter
     */
    public static DatabaseOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseOpenHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create models tables
        createTableCategory(sqLiteDatabase);
        createTableTutorial(sqLiteDatabase);
        createTableStep(sqLiteDatabase);
        createTableUser(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // update models tables
    }

    public void createTableCategory(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + Category.class.getSimpleName() + " (" +
                "Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "Title TEXT NOT NULL," +
                "ImageUrl TEXT," +
                "Description TEXT" +
                ");");
    }

    public void createTableTutorial(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + Tutorial.class.getSimpleName() + " (" +
                "Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "Title TEXT NOT NULL," +
                "Description TEXT," +
                "CoverImageUrl TEXT," +
                "CoverVideoUrl TEXT," +
                "Rate REAL," +
                "CategoryId INTEGER," +
                "Tags TEXT," +
                "UserId INTEGER" +
                ");");
    }

    public void createTableStep(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + Step.class.getSimpleName() + " (" +
                "Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "OrderNumber INTEGER NOT NULL," +
                "ImageUrl TEXT," +
                "Instruction TEXT, " +
                "TutorialId INTEGER NOT NULL" +
                ");");
    }

    public void createTableUser(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + User.class.getSimpleName() + " (" +
                "Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "Name TEXT," +
                "FamilyName TEXT," +
                "Email NOT NULL, " +
                "UserName TEXT," +
                "PhoneNumber TEXT," +
                "Verified TEXT" +
                ");");
    }

}