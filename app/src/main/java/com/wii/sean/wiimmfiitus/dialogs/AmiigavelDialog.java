package com.wii.sean.wiimmfiitus.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wii.sean.wiimmfiitus.R;

public class AmiigavelDialog extends DialogFragment {

    private Button friend;
    private Button cheater;

    public static int FRIEND = 0;
    public static int CHEATER = 1;

    public interface LobbyDialogListener {
        public void decide(int decision);
    }

    public AmiigavelDialog() {

    }

    public static AmiigavelDialog newInstance() {
        Bundle args = new Bundle();
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
        final LobbyDialogListener lobbyDialogListener;
        //todo research a better option here instead of 2 buttons
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lobbyDialogListener.decide(AmiigavelDialog.FRIEND);
                dismiss();
            }
        });
        cheater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lobbyDialogListener.decide(AmiigavelDialog.CHEATER);
                dismiss();
            }
        });
    }


}
