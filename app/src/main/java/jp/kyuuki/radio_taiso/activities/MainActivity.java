package jp.kyuuki.radio_taiso.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.kyuuki.radio_taiso.R;
import jp.kyuuki.radio_taiso.fragments.RecordConfirmDialogFragment;
import jp.kyuuki.radio_taiso.commons.Logger;
import jp.kyuuki.radio_taiso.models.Record;
import jp.kyuuki.radio_taiso.models.RecordOpenHelper;

public class MainActivity extends BaseActivity
        implements RecordConfirmDialogFragment.RecordConfirmDialogListener {

    private static final int DIALOG_ACTION_ADD = 1;
    private static final int DIALOG_ACTION_DELETE = 2;

    // View
    private CaldroidFragment mCaldroidFragment;
    private TextView mNewsText;
    private Button mStartButton;

    static SQLiteDatabase mRecordDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // ラジオ体操を始めるボタン
        mNewsText = (TextView) findViewById(R.id.news_text);
        mNewsText.setText("ご注意：モバイル回線にて動画を視聴する際に、キャリア各社にて通信料金が発生する場合がございます。");

        mCaldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CaldroidDefault);
        mCaldroidFragment.setArguments(args);

//        mCaldroidFragment.setTextColorForDate(R.color.accent500, new Date());
        //mCaldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.right_arrow), new Date());
        mCaldroidFragment.setCaldroidListener(new CaldroidFragmentListener());

        androidx.fragment.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_container, mCaldroidFragment);
        t.commit();

        // ラジオ体操を始めるボタン
        mStartButton = (Button) findViewById(R.id.button_start);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Analytics
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "youtube");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                // http://qiita.com/gari_jp/items/bb061411e8404de597d4
                //String videoId = "DeGkiItB9d8";
                String videoId = "_YZZfaMGEOU";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("vnd.youtube:" + videoId));
                i.putExtra("VIDOE_ID", videoId);
                startActivityForResult(i, 1);
            }
        });

        /*
         * データベース関連
         */
        RecordOpenHelper helper = new RecordOpenHelper(this);
        mRecordDb = helper.getWritableDatabase();
        //mRecordDb.execSQL("DROP TABLE IF EXISTS " + RecordOpenHelper.TABLE_NAME_RECORD);
        //mRecordDb.execSQL(RecordOpenHelper.TABLE_CREATE);
        // TODO: テーブルがなくなったり、おかしくなったときに復旧できるように。

        // 記録データを全取得
        String[] cols = { RecordOpenHelper.KEY_DATE, RecordOpenHelper.KEY_STATE };
        Cursor cursor = mRecordDb.query(RecordOpenHelper.TABLE_NAME_RECORD, cols, null, null, null, null, null, null);

        boolean eol = cursor.moveToFirst();
        while (eol) {
            Logger.v(TAG, "date = " + cursor.getString(0));
            Logger.v(TAG, "state = " + cursor.getString(1));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                Date d = sdf.parse(cursor.getString(0));
                mCaldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.check), d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            eol = cursor.moveToNext();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.v(TAG, "[" + this.hashCode() + "] onActivityResult()");
        Logger.v(TAG, "requestCode: " + requestCode);
        Logger.v(TAG, "resultCode: " + resultCode);
        Logger.v(TAG, "data: " + data);

        // Analytics
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "youtube_back");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        // ボタンを押した後にある程度時間が経っていたら、ダイアログ表示
        RecordConfirmDialogFragment dialogFragment = RecordConfirmDialogFragment.newInstance("お疲れさまでした！", "結果を記録しますか？", DIALOG_ACTION_ADD, new Date());
        dialogFragment.setCancelable(false);
        // show で落ちる。
        // http://stackoverflow.com/questions/14262312/java-lang-illegalstateexception-can-not-perform-this-action-after-onsaveinstanc
        //dialogFragment.show(getSupportFragmentManager(), "record_confirm_dialog");
        androidx.fragment.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.add(dialogFragment, "record_confirm_dialog");
        t.commitAllowingStateLoss();
    }

    /*
     * RecordConfirmDialogFragment.RecordConfirmDialogListener
     */

    // 記録確認ダイアログで OK が押された
    @Override
    public void onDialogPositiveClick(RecordConfirmDialogFragment dialog, int action, Date date) {
        Logger.v(TAG, "Record");
        Logger.v(TAG, "action = " + action);
        Logger.v(TAG, "date = " + date);

        Bundle bundle = new Bundle();

        switch (action) {
            case DIALOG_ACTION_ADD:
                // Analytics
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "add");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Record record = new Record(date);
                mRecordDb.insert(RecordOpenHelper.TABLE_NAME_RECORD, null, record.toContentValues());
                mCaldroidFragment.setBackgroundDrawableForDate(getResources().getDrawable(R.drawable.check), date);
                break;
            case DIALOG_ACTION_DELETE:
                // Analytics
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "delete");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Record.deleteRecord(mRecordDb, date);
                mCaldroidFragment.clearBackgroundDrawableForDate(date);
                break;
            default:
                // TODO: Android でアサーションってどんなのがいいんだろう。
                break;
        }

        mCaldroidFragment.refreshView();
    }

    /*
     * CaldroidListener
     */
    class CaldroidFragmentListener extends CaldroidListener {
        @Override
        public void onSelectDate(Date date, View view) {
            String[] cols = { RecordOpenHelper.KEY_DATE, RecordOpenHelper.KEY_STATE };
            String[] args = { RecordOpenHelper.dateFormat.format(date) };

            Cursor cursor = mRecordDb.query(RecordOpenHelper.TABLE_NAME_RECORD, cols, RecordOpenHelper.KEY_DATE + " = ?", args, null, null, null, null);
            int state = 0;
            if (cursor.moveToFirst()) {
                state = cursor.getInt(1);
            }
            Logger.v(TAG, "state = " + state);

            RecordConfirmDialogFragment dialogFragment;
            if (state == 1) {
                dialogFragment = RecordConfirmDialogFragment.newInstance("記録削除", "記録を削除しますか？", DIALOG_ACTION_DELETE, date);
            } else {
                dialogFragment = RecordConfirmDialogFragment.newInstance("記録追加", "記録を追加しますか？", DIALOG_ACTION_ADD, date);
            }
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(), "record_confirm_dialog");
        }
    }
}
