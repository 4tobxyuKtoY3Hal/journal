package com.example.journal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.journal.manager.BaseManager;
import com.example.journal.manager.ImageManager;
import com.example.journal.service.LoadService;
import com.example.journal.work.Profile;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    /**
     * Called when the activity is first created.
     */
    private ListView mListJournal;
    private Adapter mAdapter = null;
    private Profile mProfile;
    private ArrayList<ItemJournal> mArrItemJournal;
    private BroadcastReceiver mReceiver = null;
    private ProgressDialog mPd = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        <ImageView
                    android:src="@drawable/header"
                    android:layout_width="match_parent"
                    android:layout_height="150dp"
                    android:background="#fefbe8"/>
                    */

        mArrItemJournal = new ArrayList<ItemJournal>();

        BaseManager bm = BaseManager.getInstance();
        bm.open();
        Cursor c = bm.getJournals();
        if (c != null) {
            while (c.moveToNext()) {
                mArrItemJournal.add(new ItemJournal(
                        c.getString(c.getColumnIndex(BaseManager.JOURNAL.URI_IMG)),
                        c.getString(c.getColumnIndex(BaseManager.JOURNAL.NAME)),
                        c.getString(c.getColumnIndex(BaseManager.JOURNAL.DES)),
                        c.getString(c.getColumnIndex(BaseManager.JOURNAL.STATUS)),
                        c.getInt(c.getColumnIndex(BaseManager.JOURNAL.ID))
                ));
            }
            c.close();
        }

        //    if (mArrItemJournal.size() > 0) {
        //        findViewById(R.id.mainActivity_View_progress).setVisibility(View.GONE);
        //    }

        mAdapter = new Adapter(mArrItemJournal);
        mListJournal = (ListView) findViewById(R.id.mainActivity_ListView_journal);

        View header = getLayoutInflater().inflate(R.layout.header, null);
//                header = View.inflate(getApplicationContext(), R.layout.header, null);
//                header = getLayoutInflater().inflate(R.layout.header, mListJournal, false);
                mListJournal.addHeaderView(header);

        mListJournal.setAdapter(mAdapter);
        mListJournal.setOnItemClickListener(this);
        mProfile = Profile.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        mProfile.isUpdataTime();
        if (mProfile.isUpdataTime()) {
            LoadService.loadJson();
        }


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                findViewById(R.id.mainActivity_View_progress).setVisibility(View.GONE);
                while (mArrItemJournal.size() > 0) {
                    mArrItemJournal.remove(0);
                }

                BaseManager bm = BaseManager.getInstance();
                bm.open();
                Cursor c = bm.getJournals();
                if (c != null) {
                    while (c.moveToNext()) {
                        mArrItemJournal.add(new ItemJournal(
                                c.getString(c.getColumnIndex(BaseManager.JOURNAL.URI_IMG)),
                                c.getString(c.getColumnIndex(BaseManager.JOURNAL.NAME)),
                                c.getString(c.getColumnIndex(BaseManager.JOURNAL.DES)),
                                c.getString(c.getColumnIndex(BaseManager.JOURNAL.STATUS)),
                                c.getInt(c.getColumnIndex(BaseManager.JOURNAL.ID))
                        ));
                    }
                    c.close();
                }


                mAdapter = new Adapter(mArrItemJournal);
                mListJournal = (ListView) findViewById(R.id.mainActivity_ListView_journal);
                mListJournal.setAdapter(mAdapter);
                mListJournal.setOnItemClickListener(MainActivity.this);

                mProfile = Profile.getInstance();
            }
        };

        registerReceiver(mReceiver, new IntentFilter(LoadService.BROADCAST_ACTION));

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

        final BaseManager bm = BaseManager.getInstance();
        bm.open();
        Cursor c = bm.getJournal(mArrItemJournal.get(position).id);
        String status = "";

        while (c.moveToNext()) {
            status = c.getString(c.getColumnIndex(BaseManager.JOURNAL.STATUS));
        }

        if (status.equals(BaseManager.STATUS_JOURNAL.NOT_LOADED) == true) {
            final Cursor cat = bm.getLoded(mArrItemJournal.get(position).id);

            final List<String> arrUriList = new ArrayList<String>();
            final List<String> arrUriTumb = new ArrayList<String>();

            while (cat.moveToNext()) {
                arrUriTumb.add(cat.getString(cat.getColumnIndex(BaseManager.PAGE.URI_IMG_TUMB)));
                arrUriList.add(cat.getString(cat.getColumnIndex(BaseManager.PAGE.URI_IMG_LIST)));
            }

            if (cat != null) {

                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        //   Log.v("===>", "Сообщение в хендлер "+message.obj);
                        mPd.dismiss();

                        if (message.obj == "zopa") {
                            Toast.makeText(App.getContext(), "Error loading", Toast.LENGTH_SHORT).show();
                        }

                        //  Intent intent = new Intent(MainActivity.this, TitleActivity.class);
                        //  intent.putExtra(TitleActivity.ID, ((ItemJournal) mAdapter.getItem(position)).id);
                        //  startActivity(intent);

                    }
                };

                final Bitmap image = null;

                final Thread thread = new Thread() {
                    @Override
                    public void run() {

                        Bitmap image = null;
                        Bitmap imaget = null;
                        boolean fl = false;

                          /*  while (cat.moveToNext()) {
                                final String strUrlThumb = cat.getString(cat.getColumnIndex(BaseManager.PAGE.URI_IMG_TUMB));
                                imaget = ImageManager.openImage(strUrlThumb);
                            }
                            cat.moveToFirst();
                            while (cat.moveToNext()) {
                                final String strUrlList= cat.getString(cat.getColumnIndex(BaseManager.PAGE.URI_IMG_LIST));
                                image = ImageManager.openImage(strUrlList);

                                if(image != null)
                                    fl = true;
                            }*/

                        for (String strUri : arrUriList) {
                            image = ImageManager.openImage(strUri);

                            Log.e("===>", "Итерация --- " + strUri);

                            if (image != null)
                                fl = true;
                        }

                        for (String strUri : arrUriTumb) {
                            imaget = ImageManager.openImage(strUri);
                        }

                        if (fl == true) {
                            final Message message = handler.obtainMessage(1, imaget);
                            handler.sendMessage(message);

                            bm.updateStatus(mArrItemJournal.get(position).id);
                        } else {
                            final Message message = handler.obtainMessage(1, "zopa");
                            handler.sendMessage(message);
                        }
                    }
                };

                thread.setPriority(1);
                thread.start();

                mPd = new ProgressDialog(this);
                mPd.setMessage("Loading...");
                mPd.show();

            }
            //     cat.close();
        } else if (status.equals(BaseManager.STATUS_JOURNAL.UPLOADED) == true) {
//            Intent intent = new Intent(this, TitleActivity.class);
            Intent intent = new Intent(this, PagesActivity.class);
            //intent.putExtra(TitleActivity.ID, ((ItemJournal) mAdapter.getItem(position)).id);
            startActivity(intent);
        }

    }

    private class Adapter extends BaseAdapter {

        private ArrayList<ItemJournal> mArrItem;

        public Adapter(ArrayList<ItemJournal> arrItem) {
            mArrItem = arrItem;
        }

        @Override
        public int getCount() {
            return mArrItem.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.item_assortiment, parent, false);
            }

            ItemJournal item = (ItemJournal) getItem(position);

            //  Log.d("===>","8888888+++"+item.uri);

            ImageManager.fetchImage(
                    item.uri,
                    (ImageView) v.findViewById(R.id.itemAssortiment_ImageView_img)
            );

            ((TextView) v.findViewById(R.id.itemAssortiment_TextView_name)).setText(item.nam);
            ((TextView) v.findViewById(R.id.itemAssortiment_TextView_des)).setText(item.des);
            TextView txtStatus = (TextView) v.findViewById(R.id.itemAssortiment_TextView_btnStatus);

            if (txtStatus.getVisibility() != View.GONE) txtStatus.setVisibility(View.GONE);
            if (item.status.equals(BaseManager.STATUS_JOURNAL.NOT_LOADED)) {
                txtStatus.setText("СКАЧАТЬ");
                txtStatus.setBackgroundResource(R.drawable.bt_download);
                txtStatus.setTextColor(getResources().getColor(R.color.button_download));
                if (txtStatus.getVisibility() != View.VISIBLE) txtStatus.setVisibility(View.VISIBLE);


            } else if (item.status.equals(BaseManager.STATUS_JOURNAL.UPLOADED)) {
                txtStatus.setText("ЧИТАТЬ");
                txtStatus.setBackgroundResource(0);
                txtStatus.setTextColor(getResources().getColor(R.color.button_read));
                if (txtStatus.getVisibility() != View.VISIBLE) txtStatus.setVisibility(View.VISIBLE);
            } else {
                if (txtStatus.getVisibility() != View.VISIBLE) txtStatus.setVisibility(View.VISIBLE);
            }


            return v;
        }
    }

    private class ItemJournal {
        public String uri = null;
        public String nam = null;
        public String des = null;
        public String status = null;
        public int id = -1;

        public ItemJournal(String uri, String nam, String des, String status, int id) {
            this.uri = uri;
            this.nam = nam;
            this.des = des;
            this.status = status;
            this.id = id;
        }

    }


}
