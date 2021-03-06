package alex.aleksandr.ru.musicguitar;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MusicDb extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "musiclistdb.db";
    private static final int DATABASE_VERSION = 1;
    private static final String AUTHOR_TABLE_NAME = "authorsong";
    private static final String ID_AUTHOR = "_id";
    private static final String AUTHOR_NAME = "authorname";
    private static final String SONG_TABLE_NAME = "songlist";
    private static final String ID_SONG = "_id";
    private static final String SONG_NAME = "songname";
    private static final String SONG_TEXT = "songtext";
    private static final String SONG_AUTHOR = "songauthor";
    private static MusicDb musicDb = null;

    public static MusicDb getInstance(Context context) {

        if (musicDb == null) {
            musicDb = new MusicDb(context);
        }
        return musicDb;
    }

    private MusicDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            String tableOne = "CREATE TABLE " + AUTHOR_TABLE_NAME + " (" +
                    ID_AUTHOR + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AUTHOR_NAME + " TEXT UNIQUE);";

            String tableTwo = "CREATE TABLE " + SONG_TABLE_NAME + " (" +
                    ID_SONG + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SONG_NAME + " TEXT UNIQUE, " +
                    SONG_TEXT + " TEXT, " +
                    SONG_AUTHOR + " TEXT, " +
                    "FOREIGN KEY (" + SONG_AUTHOR + ") REFERENCES " +
                    AUTHOR_TABLE_NAME + "(" + AUTHOR_NAME + "));";

            sqLiteDatabase.execSQL(tableOne);
            sqLiteDatabase.execSQL(tableTwo);
            sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            Log.e("Error created databases", e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public Cursor queryAuthorByName() {
        Cursor c = querySelectAuthor(null, null);
        return c;
    }

    public Cursor queryAuthorByNameFilter(String filter) {
        Cursor c = querySelectAuthor(AUTHOR_NAME + " LIKE ?", new String[]{"%" + filter + "%"});
        return c;
    }


    public Cursor querySongById(long id) {
        Cursor c = querySelectSong("_id= ?", new String[]{String.valueOf(id)});
        return c;
    }

    public Cursor querySongByAuthorName(String name) {
        Cursor c = querySelectSong(SONG_AUTHOR + "= ?", new String[]{name});
        return c;
    }

    public Cursor querySongByAuthorNameFilter(String name, String filter) {
        Cursor c = querySelectSong(SONG_AUTHOR + "= ? AND " + SONG_NAME + " LIKE ?",
                new String[]{name, "%" + filter + "%"});
        return c;
    }


    public Cursor querySelectAuthor(String args, String[] argInit) {
        Cursor cursor = getWritableDatabase().query(
                AUTHOR_TABLE_NAME,
                null,
                args,
                argInit,
                AUTHOR_NAME, null, null
        );
        return cursor;
    }

    public Cursor querySelectSong(String args, String[] argsInit) {
        Cursor cursor = getWritableDatabase().query(
                SONG_TABLE_NAME,
                null,
                args,
                argsInit,
                SONG_NAME, null, null
        );
        return cursor;
    }

    public static String getAuthorName() {
        return AUTHOR_NAME;
    }

    public static String getSongName() {
        return SONG_NAME;
    }

    public static String getSongText() {
        return SONG_TEXT;
    }

    public static String getSongAuthor() {
        return SONG_AUTHOR;
    }

    public boolean addListSong(String author, String nameSong, String textSong) {
        try {
            String sqlInSongList = "INSERT INTO " + SONG_TABLE_NAME +
                    " (" + SONG_NAME + ", " +
                    SONG_TEXT + ", " + SONG_AUTHOR +
                    ") VALUES (\"" + nameSong + "\", \"" + textSong +
                    "\", \"" + author + "\");";
            getWritableDatabase().execSQL(sqlInSongList);
            return true;
        } catch (SQLException e) {
            Log.e("Error added song", e.toString());
            return false;
        }
    }

    public void addListAuthor(String author) {
        Cursor cursor = querySelectAuthor(AUTHOR_NAME + "= ?", new String[]{author});
        if (cursor.getCount() == 0) {
            try {
                String sqlInAuthor = "INSERT INTO " + AUTHOR_TABLE_NAME +
                        " (" + AUTHOR_NAME +
                        ") VALUES (\"" + author + "\");";
                getWritableDatabase().execSQL(sqlInAuthor);
            } catch (SQLException e) {
                Log.e("Error added author", e.toString());
                cursor.close();
            }
        }
        cursor.close();
    }

    public void deleteAuthor(String author) {
        try {
            String sql = "DELETE FROM " + AUTHOR_TABLE_NAME +
                    " WHERE " + AUTHOR_NAME + "=\"" + author + "\";";
            getWritableDatabase().execSQL(sql);
        } catch (SQLException e) {
            Log.e("Error deleteing author", e.toString());
        }
    }

    public void deleteSong(long id) {
        try {
            String sql = "DELETE FROM " + SONG_TABLE_NAME +
                    " WHERE _id=" + id + ";";
            getWritableDatabase().execSQL(sql);
        } catch (SQLException e) {
            Log.e("Error deleteing song", e.toString());
        }
    }

    public boolean updateListSong(String a, String sn, String stxt, long id, String oldAuthor) {
        Cursor cursor = querySelectSong(SONG_AUTHOR + "= ?", new String[]{oldAuthor});
        try {
            String sql = "UPDATE " + SONG_TABLE_NAME +
                    " SET " + SONG_NAME + "= ?, " +
                    SONG_TEXT + "= ?, " +
                    SONG_AUTHOR + "= ? WHERE _id= ?;";
            Object[] args = new Object[]{sn, stxt, a, id};
            getWritableDatabase().execSQL(sql, args);
            addListAuthor(a);
            if (cursor.getCount() == 0) {
                deleteAuthor(oldAuthor);
            }
            cursor.close();
            return true;
        } catch (SQLException e) {
            Log.e("Error update song", e.toString());
            cursor.close();
            return false;
        }
    }
}
