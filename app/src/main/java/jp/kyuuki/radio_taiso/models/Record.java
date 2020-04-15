package jp.kyuuki.radio_taiso.models;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * ラジオ体操実施記録。
 */
public class Record {

    Date date;  // 時刻は無視
    String dateString;
    int state;

    // 今日の記録
    public Record() {
        date = new Date();
        state = 1;
    }

    public Record(int year, int month, int day) {
        date = (new GregorianCalendar(year, month, day)).getTime();
        state = 1;
    }

    public Record(Date d) {
        date = d;
        state = 1;
    }

    // 記録データ削除
    // TODO; SQLiteDatabase に依存のコードはこのモデルから排除したい。
    public static boolean deleteRecord(SQLiteDatabase db, Date d) {
        String selection = RecordOpenHelper.KEY_DATE + " = ?";
        String[] selectionArgs = { RecordOpenHelper.dateFormat.format(d) };

        if (db.delete(RecordOpenHelper.TABLE_NAME_RECORD, selection, selectionArgs) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(RecordOpenHelper.KEY_DATE, RecordOpenHelper.dateFormat.format(date));
        cv.put(RecordOpenHelper.KEY_STATE, 1);

        return cv;
    }
}
