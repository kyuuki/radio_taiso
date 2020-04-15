package jp.kyuuki.radio_taiso.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

/**
 * ラジオ体操実施記録 SQLiteOpenHelper
 */
public class RecordOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "radio_taiso";

    public static final String TABLE_NAME_RECORD = "record";

    public static final String KEY_ID  = "id";
    public static final String KEY_DATE  = "date";
    public static final String KEY_STATE = "state";

    public static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME_RECORD + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_DATE + " STRING NOT NULL UNIQUE, " +
                    KEY_STATE + " INTEGER NOT NULL);";

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public RecordOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RECORD);
        onCreate(db);
    }
}
