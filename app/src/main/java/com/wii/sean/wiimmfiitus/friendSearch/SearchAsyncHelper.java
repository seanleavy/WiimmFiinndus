package com.wii.sean.wiimmfiitus.friendSearch;

import android.content.Context;
import android.os.AsyncTask;

import com.wii.sean.wiimmfiitus.interfaces.AsyncTaskCompleteListener;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.List;

public class SearchAsyncHelper extends AsyncTask<Object, Void, List> {

    private AsyncTaskCompleteListener asyncTaskCompleteListener;
    private Context context;

    public SearchAsyncHelper(Context c, AsyncTaskCompleteListener a) {
        this.context = c;
        this.asyncTaskCompleteListener = a;
    }

    @Override
    protected List doInBackground(Object... params) {
        return new MkFriendSearch().searchFriendList(params[0]);
    }

    @Override
    protected void onPostExecute(List list) {
        asyncTaskCompleteListener.onTaskComplete(list);
    }
}
