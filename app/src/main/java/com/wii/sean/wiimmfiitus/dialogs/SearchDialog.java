package com.wii.sean.wiimmfiitus.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.customViews.NintendoTextview;

public class SearchDialog extends DialogFragment {

    public static String MIISEARCH = "mii";
    public static String FRIENDCODESEARCH = "fcode";
    private Button cancel;
    private Button search;
    private NintendoTextview searchTypeTextView;
    private EditText friendCodeEditText;
    private SwitchCompat toggleSwitch;

    public interface DialogListener {
        public void valuesReturned(String value, String searchType);
    }

    public SearchDialog() {

    }

    public static SearchDialog newInstance() {
        Bundle args = new Bundle();
        SearchDialog fragment = new SearchDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_code_dialog, container);
        super.onCreateView(inflater, container, savedInstanceState);
        cancel = (Button) view.findViewById(R.id.button_cancel);
        search = (Button) view.findViewById(R.id.button_search);
        searchTypeTextView = (NintendoTextview) view.findViewById(R.id.dialog_search_title);
        friendCodeEditText = (EditText) view.findViewById(R.id.friend_code_edittext);
        toggleSwitch = (SwitchCompat) view.findViewById(R.id.alertdialog_toggle_switch);
        addEditTextListener();
        addSearchListener();
        return view;
    }

    private TextWatcher getTextWatcher() {
        //format friendcode textfield with this Textwatcher
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // for friendcode entries
                if(!toggleSwitch.isChecked()) {
                    // allows credit card style formatting
                    if(s.length() == 4 || s.length() == 9) {
                        s.append('-');
                    }
                    if (friendCodeEditText.getText().toString().length() >= 14) {
                        search.setEnabled(true);
                    } else
                        search.setEnabled(false);
                }
                // for mii text entry
                if(toggleSwitch.isChecked()) {
                    if (friendCodeEditText.getText().toString().length() >= 1) {
                        search.setEnabled(true);
                    } else
                        search.setEnabled(false);
                }
            }
        };
        return textWatcher;
    }

    private void addEditTextListener() {
        friendCodeEditText.addTextChangedListener(getTextWatcher());
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if(isChecked) {
                    //change search title and keyboard type
                    searchTypeTextView.setText(R.string.dialog_body_mii);
                    friendCodeEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    friendCodeEditText.getText().clear();
                }
                else {
                    searchTypeTextView.setText(R.string.dialog_body_fcode);
                    friendCodeEditText.getText().clear();
                    friendCodeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                }
            }
        });
    }

    private void addSearchListener() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = friendCodeEditText.getText().toString();
                String searchType = toggleSwitch.isChecked() == true ? MIISEARCH : FRIENDCODESEARCH;
                //todo hacky need to not do this. Fuckups will be had
                DialogListener myListener = (DialogListener) getFragmentManager().getFragments().get(0);
                myListener.valuesReturned(value, searchType);
                dismiss();
            }
        });
    }
}
