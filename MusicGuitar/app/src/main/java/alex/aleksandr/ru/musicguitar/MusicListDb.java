package alex.aleksandr.ru.musicguitar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MusicListDb extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "musicdb.db";
    private static final int DATABASE_VERSION = 1;

    private static final String AUTHOR_TABLE_NAME = "authorsong";
    private static final String SONG_TABLE_NAME = "songlist";

    private static final String ID_AUTHOR = "_id";
    private static final String AUTHOR_NAME = "authorname";

    private static final String ID_SONG = "_id";
    private static final String SONG_NAME = "songname";
    private static final String SONG_TEXT = "songtext";
    private static final String SONG_AUTHOR = "songauthor";


    public MusicListDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String tableOne = "CREATE TABLE " + AUTHOR_TABLE_NAME + " (" +
                ID_AUTHOR + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AUTHOR_NAME + " TEXT UNIQUE);";

        String tableTwo = "CREATE TABLE " + SONG_TABLE_NAME + " (" +
                ID_SONG + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SONG_NAME + " TEXT, " +
                SONG_TEXT + " TEXT, " +
                SONG_AUTHOR + " TEXT, " +
                "FOREIGN KEY (" + SONG_AUTHOR + ") REFERENCES " +
                AUTHOR_TABLE_NAME + "(" + AUTHOR_NAME + "));";

        sqLiteDatabase.execSQL(tableOne);
        sqLiteDatabase.execSQL(tableTwo);
        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addAuthor(String author) {
        String sql = "INSERT INTO " + AUTHOR_TABLE_NAME +
                " (" + AUTHOR_NAME +
                ") VALUES (\"" + author + "\");";
        getWritableDatabase().execSQL(sql);
    }

    public Cursor querySel() {
        Cursor cursor = getWritableDatabase().query(
                AUTHOR_TABLE_NAME,
                null,
                "_id = ?",
                new String[]{"1"},
                null, null, null
        );

        return cursor;
    }

}