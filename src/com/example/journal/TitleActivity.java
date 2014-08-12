package com.example.journal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.journal.manager.BaseManager;

import java.util.ArrayList;

/**
 * Created by Artem on 02.07.14.
 */
public class TitleActivity extends Activity implements AdapterView.OnItemClickListener {

    ListView mTitleList;
    ArrayList<ItemTitle> mArrItemTitle;
    Adapter mAdapter;

    public static final String ID = "id";
    private long mIdJournal = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        mArrItemTitle = new ArrayList<ItemTitle>();

        mIdJournal = getIntent().getIntExtra(ID, -1);

        BaseManager bm = BaseManager.getInstance();
        bm.open();
        Cursor c = bm.getTitle(mIdJournal);

        if (c != null) {
            while (c.moveToNext()) {
                mArrItemTitle.add(new ItemTitle(
                        c.getString(c.getColumnIndex(BaseManager.TITLE.NAME)),
                        c.getInt(c.getColumnIndex(BaseManager.TITLE.NUM))
                ));
            }
            c.close();
        }

        bm.close();

        mAdapter = new Adapter(mArrItemTitle);

        mTitleList = (ListView) findViewById(R.id.activityTitle_ListView_title);
        mTitleList.setAdapter(mAdapter);
        mTitleList.setOnItemClickListener(this);
        ImageView iv = null;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PagesActivity.class);
        intent.putExtra(PagesActivity.PAGE, ""+mArrItemTitle.get(position).page);
        intent.putExtra(PagesActivity.ID, ""+mIdJournal);
        startActivity(intent);
    }

    private class ItemTitle {
        public String nam = null;
        public int page = -1;

        public ItemTitle(String nam, int page) {
            this.nam = nam;
            this.page = page;
        }
    }

    private class Adapter extends BaseAdapter {
        ArrayList<ItemTitle> mArrItemTitle;

        public Adapter(ArrayList<ItemTitle> arrItemTitle) {
            mArrItemTitle = arrItemTitle;
        }

        @Override
        public int getCount() {
            return mArrItemTitle.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrItemTitle.get(position);
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
                        .inflate(R.layout.item_title, parent, false);
            }

            ItemTitle item = (ItemTitle) getItem(position);

            ((TextView) v.findViewById(R.id.itemTitle_TextView_title)).setText(item.nam);
            ((TextView) v.findViewById(R.id.itemTitle_TextView_page)).setText("" + item.page);

            return v;
        }
    }

}
