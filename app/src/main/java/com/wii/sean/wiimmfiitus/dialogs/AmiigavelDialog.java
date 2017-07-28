package com.wii.sean.wiimmfiitus.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wii.sean.wiimmfiitus.R;
import com.wii.sean.wiimmfiitus.helpers.CheaterFriendSearch;
import com.wii.sean.wiimmfiitus.helpers.PreferencesManager;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.io.Serializable;

public class AmiigavelDialog extends DialogFragment {

    private TextView friend;
    private TextView cheater;
    private String[] friendAction;

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
        friendAction = getResources().getStringArray(R.array.dialog_friend_choice);
        friend = (TextView) view.findViewById(R.id.friend);
        friend.setText(((MiiCharacter) getArguments().getSerializable("mii")).isFriend() ? friendAction[1] : friendAction[0]);
        cheater = (TextView) view.findViewById(R.id.cheater);
        addListeners();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void addListeners() {
        //todo research a better option here instead of 2 buttons
        final MiiCharacter miiCharacter = (MiiCharacter) getArguments().getSerializable("mii");
        //todo. create a preferencesmanager singleton, remove multiple instances throughout codebase
        final PreferencesManager p = new PreferencesManager(getContext());
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: add event code to remoce friend
                miiCharacter.setFriend(true);
                miiCharacter.setType(MiiCharacter.DEFAULT_VIEW);
                p.addToPreference(PreferencesManager.FAVOURITESPREFERENCES, miiCharacter);
                dismiss();
                Toast.makeText(getContext(), "done", Toast.LENGTH_SHORT).show();
            }
        });
        cheater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAcheater = CheaterFriendSearch.isACheater(getContext(), miiCharacter);
                if(!isAcheater) {
                    miiCharacter.setIsCheater(true);
                    p.addToPreference(PreferencesManager.CHEATERPREFERENCES, miiCharacter);
                    dismiss();
                }
                else {
                    miiCharacter.setIsCheater(false);
                    p.removeFromPreference(PreferencesManager.CHEATERPREFERENCES, miiCharacter);
                    dismiss();
                }
                Toast.makeText(getContext(), "done", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
