package com.wii.sean.wiimmfiitus.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.customViews.NintendoTextview;
import com.wii.sean.wiimmfiitus.helpers.PreferencesManager;
import com.wii.sean.wiimmfiitus.helpers.SetTouchDelegate;

public class SearchDialog extends DialogFragment {

    public static String MIISEARCH = "mii";
    public static String FRIENDCODESEARCH = "fcode";
    private Button cancel;
    private Button search;
    private Button delete;
    private NintendoTextview searchTypeTextView;
    private EditText friendCodeEditText;
    private SwitchCompat toggleSwitch;
    private String[] searchTypeArray;

    // Dialog callback interface
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
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
        super.onCreateView(inflater, container, savedInstanceState);
        cancel = (Button) view.findViewById(R.id.button_cancel);
        search = (Button) view.findViewById(R.id.button_search);
        searchTypeArray = getResources().getStringArray(R.array.dialog_search_type_label);
        searchTypeTextView = (NintendoTextview) view.findViewById(R.id.dialog_search_title);
        searchTypeTextView.setText(sharedPreferences.getBoolean(PreferencesManager.DIALOGSEARCHPREFERENCE, false) ? searchTypeArray[1] :  searchTypeArray[0]);
        friendCodeEditText = (EditText) view.findViewById(R.id.friend_code_edittext);
        toggleSwitch = (SwitchCompat) view.findViewById(R.id.alertdialog_toggle_switch);
        toggleSwitch.setChecked(sharedPreferences.getBoolean(PreferencesManager.DIALOGSEARCHPREFERENCE, false));
        delete = (Button) view.findViewById(R.id.delete_friend_code);
        delete.setVisibility(View.INVISIBLE);
        SetTouchDelegate.expandTouchArea(friendCodeEditText, delete, 5);
        addEditTextListener();
        addSearchListener();
        addOtherListeners();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
                //hide show delete button
                if(s.length() > 0)
                    delete.setVisibility(View.VISIBLE);
                else
                    delete.setVisibility(View.INVISIBLE);
                // for friendcode entries
                if(!toggleSwitch.isChecked()) {
                    // allows credit card style formatting
                    if(s.length() == 4 || s.length() == 9) {
                        s.append('-');
                    }
                    if (friendCodeEditText.getText().toString().length() >= 14) {
                        search.setVisibility(View.VISIBLE);
                    } else {
                        search.setVisibility(View.INVISIBLE);

                    }
                }
                // for mii text entry
                if(toggleSwitch.isChecked()) {
                    if (friendCodeEditText.getText().toString().length() >= 1)
                        search.setVisibility(View.VISIBLE);
                    else
                        search.setVisibility(View.INVISIBLE);
                }
            }
        };
        return textWatcher;
    }

    private void addEditTextListener() {
        boolean searchType = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE).getBoolean(PreferencesManager.DIALOGSEARCHPREFERENCE, false);
        if(searchType == false)
            friendCodeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
        else
            friendCodeEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        friendCodeEditText.addTextChangedListener(getTextWatcher());
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if(isChecked) {
                    //change search title and keyboard type
                    searchTypeTextView.setText(searchTypeArray[1]);
                    friendCodeEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    friendCodeEditText.getText().clear();
                    getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(PreferencesManager.DIALOGSEARCHPREFERENCE, true)
                            .apply();
                }
                else {
                    searchTypeTextView.setText(searchTypeArray[0]);
                    friendCodeEditText.getText().clear();
                    friendCodeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
                    getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean(PreferencesManager.DIALOGSEARCHPREFERENCE, false)
                            .apply();
                }
            }
        });
    }

    private void addSearchListener() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = friendCodeEditText.getText().toString();
                String searchType = toggleSwitch.isChecked() ? MIISEARCH : FRIENDCODESEARCH;
                //todo hacky need to not do this. Fuckups will be had
                DialogListener myListener = (DialogListener) getFragmentManager().getFragments().get(0);
                myListener.valuesReturned(value, searchType);
                dismiss();
            }
        });
    }

    private void addOtherListeners() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendCodeEditText.setText("");
            }
        });
    }
}
