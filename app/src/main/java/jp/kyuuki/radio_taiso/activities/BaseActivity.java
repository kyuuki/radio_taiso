package jp.kyuuki.radio_taiso.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;

import jp.kyuuki.radio_taiso.BuildConfig;
import jp.kyuuki.radio_taiso.R;
import jp.kyuuki.radio_taiso.commons.Logger;
import jp.kyuuki.radio_taiso.commons.Utils;

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = BaseActivity.class.getName();
    protected String getLogTag() {
        return TAG;
    }

    protected FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.v(getLogTag(), "[" + this.hashCode() + "] onCreate()");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.v(getLogTag(), "[" + this.hashCode() + "] onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.v(getLogTag(), "[" + this.hashCode() + "] onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.v(getLogTag(), "[" + this.hashCode() + "] onDestroy()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // デバッグ版でなければデバッグメニュー非表示
        if (BuildConfig.DEBUG == false) {
//            menu.findItem(R.id.action_debug).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;
        if (id == R.id.action_contact) {
            startActivityIntentContact();
            return true;
        } else if (id == R.id.action_about) {
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

//        switch (id) {
//            case R.id.action_contact:
//                startActivityIntentContact();
//                return true;
//            case R.id.action_about:
//                intent = new Intent(this, AboutActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.action_debug:
//                intent = new Intent(this, DebugActivity.class);
//                startActivity(intent);
//                return true;
//        }
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    protected void startActivityIntentContact() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + getString(R.string.contact_email)));
        //intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"kyuuki.japan+rensou@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.contact_text,
                Utils.getVersionName(this), Build.VERSION.RELEASE, Build.MANUFACTURER + " " + Build.MODEL));
        startActivity(intent);
    }
}
