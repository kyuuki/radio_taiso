package jp.kyuuki.radio_taiso.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import java.util.Date;

/**
 * 警告ダイアログ。
 *
 * - 標準的なガイドラインに沿って作って見よう。
 *   - http://developer.android.com/intl/ja/guide/topics/ui/dialogs.html
 *   - Activity から呼び出される前提だね。
 *   - コールバックのパラメータも微妙。
 */
public class RecordConfirmDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_ACTION = "action";
    private static final String ARG_DATE = "date";

    private RecordConfirmDialogListener mListener;

    public static RecordConfirmDialogFragment newInstance(String title, String message, int action, Date date) {
        RecordConfirmDialogFragment fragment = new RecordConfirmDialogFragment();
        Bundle args = new Bundle();
        if (title != null) {
            args.putString(ARG_TITLE, title);
        }
        args.putString(ARG_MESSAGE, message);
        args.putInt(ARG_ACTION, action);
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    public RecordConfirmDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (RecordConfirmDialogListener) activity;
        } catch (ClassCastException e) {
            mListener = null;
            //throw new ClassCastException(activity.toString() + "must implement OnAlertDialogListener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        final int action = getArguments().getInt(ARG_ACTION);
        final Date date = (Date) getArguments().getSerializable(ARG_DATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onDialogPositiveClick(RecordConfirmDialogFragment.this, action, date);
                        }
                    }
                })
                .setNegativeButton("キャンセル", null);

        return builder.create();
    }

    public static interface RecordConfirmDialogListener {
        public void onDialogPositiveClick(RecordConfirmDialogFragment dialog, int action, Date date);
    }
}
