package com.wii.sean.wiimmfiitus.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.helpers.PreferencesManager;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.io.Serializable;

public class AmiigavelDialog extends DialogFragment {

    private Button friend;
    private Button cheater;

    public AmiigavelDialog() {

    }

    public static AmiigavelDialog newInstance(Serializable serializable) {
        Bundle args = new Bundle();
        args.putSerializable("mii", serializable);
        AmiigavelDialog fragment = new AmiigavelDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.amiigavel_dialog, container);
        friend = (Button) view.findViewById(R.id.friend);
        cheater = (Button) view.findViewById(R.id.cheater);
        addListeners();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void addListeners() {
        //todo research a better option here instead of 2 buttons
        final MiiCharacter miiCharacter = (MiiCharacter) getArguments().getSerializable("mii");
        //todo. create a preferencesmanager singleton, remove multiple instances throughout codebase
        PreferencesManager p = new PreferencesManager(getContext());
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miiCharacter.setFriend(true);
                p.addToPreference(PreferencesManager.FAVOURITESPREFERENCES, miiCharacter);
                dismiss();
            }
        });
        cheater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miiCharacter.setIsCheater(true);
                dismiss();
            }
        });
    }


}
