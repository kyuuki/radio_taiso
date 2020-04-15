package jp.kyuuki.radio_taiso.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jp.kyuuki.radio_taiso.R;
import jp.kyuuki.radio_taiso.commons.Utils;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView = findViewById(R.id.text_version);
        textView.setText(Utils.getVersionName(this));

        Button button =findViewById(R.id.button_contact);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIntentContact();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニュー表示をしない
        return true;
    }
}
