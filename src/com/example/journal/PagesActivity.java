package com.example.journal;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.journal.manager.BaseManager;
import com.example.journal.manager.ImageManager;
import com.example.journal.view.CustomViewPager;
import com.example.journal.view.ScaleImageView;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Artem on 24.06.14.
 */


public class PagesActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private Gallery mGallery;
    private Adapter mAdapter;
    private ScrollView mScrollView;
    public static final String PAGE = "page";
    private CustomViewPager mPager;
    public static final String ID = "id";
    public static int mPage_j;

    private SamplePagerAdapter mPagerAdapter;
    private List<View> arrView = new ArrayList<View>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);

        LayoutInflater inflater = LayoutInflater.from(this);

       // Integer[] resPages = new Integer[]{R.drawable.a1, R.drawable.a2, R.drawable.a3};


        int id_j = Integer.parseInt(getIntent().getStringExtra(ID));
        mPage_j = Integer.parseInt(getIntent().getStringExtra(PAGE))-1;


        Log.d("===>","page_j>"+mPage_j);

        BaseManager bm = BaseManager.getInstance();
        bm.open();
        Cursor c = bm.getLoded(id_j);

        String[] urlPages = new String[c.getCount()];
        int i = 0;

        while (c.moveToNext()) {
            String strUrl = c.getString(c.getColumnIndex(BaseManager.PAGE.URI_IMG_LIST));// Log.d("===>","strUrl>"+strUrl);
            Bitmap img = ImageManager.openImage(strUrl);  //Log.d("===>", "strUrl>" + img);
            urlPages[i] = c.getString(c.getColumnIndex(BaseManager.PAGE.URI_IMG_TUMB));

            if(img != null){
                View page = inflater.inflate(R.layout.item_page_big, null);
                ScaleImageView iv = (ScaleImageView) page.findViewById(R.id.imagePagesBig_ImageView_page);
                iv.setImageBitmap(img);
                arrView.add(page);
            }

            i++;
        }

        c.close();
        bm.close();

  /*      for (int i = 0; i < resPages.length; i++) {
            View page = inflater.inflate(R.layout.item_page_big, null);
            ScaleImageView iv = (ScaleImageView) page.findViewById(R.id.imagePagesBig_ImageView_page);
            iv.setImageResource(resPages[i]);
            arrView.add(page);
        }*/

        mPager = (CustomViewPager) findViewById(R.id.activityPages_ViewPager_pages);
        mPagerAdapter = new SamplePagerAdapter(arrView);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i2) {
           //     Log.d("===>","onPageScrolled");
            }

            @Override
            public void onPageSelected(int i) {
            //    Log.d("===>","onPageSelected");
                gotoGalery(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            //    Log.d("===>","onPageScrollStateChanged");
            }

        });


//        imageViewer = new ImageViewer(this);
//        imageViewer.setImageResource(R.drawable.a1);
//        FrameLayout fl = (FrameLayout) findViewById(R.id.activityPages_FrameLayout_contener);
//        fl.addView(imageViewer, new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.MATCH_PARENT));


        //setContentView(imageViewer);

        mAdapter = new Adapter(urlPages);

        mGallery = (Gallery) findViewById(R.id.activityPages_Gallery_tumb);
        mGallery.setAdapter(mAdapter);
        mGallery.setPadding(0, 0, 0, 0);
        mGallery.setOnItemSelectedListener(this);
        gotoPage(mPage_j);
        gotoGaleryPage(mPage_j);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gotoPage(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void gotoPage(int page) {
        if(mPager.getCurrentItem() != page){
            mPager.setCurrentItem(page);
        }
    }

    private void gotoGalery(int page){
        int current = mGallery.getSelectedItemPosition();

        if(current<page)
          mGallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);

        else if(current>page)
            mGallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);

    }

    private void gotoGaleryPage(int page){
        mGallery.setSelection(page);

    }



    public class Adapter extends BaseAdapter {

        private int mGalleryItemBackground;
        private String[] mImage;

        public Adapter(String[] image) {
            mImage = image;
        }

        @Override
        public int getCount() {
            return mImage.length;
        }

        @Override
        public Object getItem(int position) {
            return mImage[position];
        }

        @Override
        public long getItemId(int position) {
            return position; //mImage[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.item_page, parent, false);
            }
            Bitmap bm = ImageManager.openImage(mImage[position]);

            if(bm!=null)
                ((ImageView) v.findViewById(R.id.itemPage_ImageView_img)).setImageBitmap(bm);

            return v;
        }
    }

    public class SamplePagerAdapter extends PagerAdapter {

        List<View> mArrItem = null;

        public SamplePagerAdapter(List<View> pages) {
            this.mArrItem = pages;
        }

        @Override
        public Object instantiateItem(View collection, int position) {
            View v = mArrItem.get(position);
            ((CustomViewPager) collection).addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((CustomViewPager) collection).removeView((View) view);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mArrItem.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

}
