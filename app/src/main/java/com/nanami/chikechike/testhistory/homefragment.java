package com.nanami.chikechike.testhistory;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.nanami.chikechike.myapplication.R;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.UserStreamAdapter;

/**
 * Created by nanami on 2014/09/05.
 */
public class homefragment extends android.support.v4.app.ListFragment {

    android.os.Handler handler = new android.os.Handler();
    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){

        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.homefragment, container, false);

        Context context = getActivity();

        if (!TwitterUtils.hasAccessToken(context)) {
            Intent intent = new Intent(context, TwitterOAuthActivity.class);
            startActivity(intent);
        } else {
            mAdapter = new TweetAdapter(context);

            setListAdapter(mAdapter);

            mTwitter = TwitterUtils.getTwitterInstance(context);
            reloadTimeLine();

            TwitterStream twitterStream = TwitterUtils.getTwitterStreamInstance(context);
            {
                twitterStream.addListener(new mMyUserStreamAdapter());
                twitterStream.user();
            }
        }


        return ll;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    /*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getActivity(), "あ?", Toast.LENGTH_SHORT).show();
    } */   //　なんかしらんけど実装されなかった。


    class mMyUserStreamAdapter extends UserStreamAdapter {
        public void onStatus(final Status status) {
            super.onStatus(status);
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                refreshView(status);
                            }
                        });
                    }
                }).start();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }


    private void refreshView(Status status) {
        try {
            mAdapter.insert(status, 0);
            mAdapter.notifyDataSetChanged();
            getListView().invalidateViews();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemLongClickListener(new mOnItemLongClockListener());
        getListView().setOnItemClickListener(new mOnItemClickListener());
    }

    public class mOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?>parent, View view, int position, long id){
            showToast(((Status)parent.getItemAtPosition(position)).getUser().getName());
        }
    }

    public class mOnItemLongClockListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parebt, View view, int position,long id) {

            showToast("長押し　POS:" + String.valueOf(position) + "id:" + String.valueOf(id));

            return false;
        }
    }


    private void reloadTimeLine() {
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return mTwitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    mAdapter.clear();
                    for (twitter4j.Status status : result) {
                        mAdapter.add(status);
                    }
                    getListView().setSelection(0);
                } else {
                    showToast("タイムラインの取得に失敗しました。");
                }
            }
        };
        task.execute();
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

}
