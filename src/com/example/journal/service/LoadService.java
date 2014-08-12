package com.example.journal.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.example.journal.App;
import com.example.journal.R;
import com.example.journal.manager.BaseManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Artem on 26.06.14.
 */
public class LoadService extends IntentService {

    private static final String LOG_CAT = "LoadService";
    public static final String COMAND = "comanr";
    private static final String ID = "id";

   // public static final String ACTION_UPDATE = "com.example.journal.service.UPDATE";
    public final static String BROADCAST_ACTION = "com.example.journal";

    public static interface COMANDS {
        public static final int LOAD_IMEG = 0x0;
        public static final int LOAD_JSON = 0x1;
    }

    private static interface JSON {
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DATE = "data";
        public static final String NUM_JOURNAL = "num";
        public static final String NUM_PAGE = "num";
        public static final String URI_IMG = "uri_img";
        public static final String MDPI = "mdpi";
        public static final String HDPI = "hdpi";
        public static final String XHDPI = "xhdpi";
        public static final String XXHDPI = "xxhdpi";
        public static final String TITLE = "title";
        public static final String TITLE_NAME = "name";
        public static final String TITLE_PAGE = "page";
        public static final String PAGES = "pages";
        public static final String URI_IMG_PAGE = "uri_img_list";
        public static final String URI_IMG_TUMB = "uri_img_tumb";

    }

    public LoadService() {
        super("Load service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int comand = intent.getIntExtra(COMAND, -1);
        if (comand == -1) return;
        switch (comand) {
            case COMANDS.LOAD_IMEG:
                int id = intent.getIntExtra(ID, -1);
                if (id == -1) return;


                break;
            case COMANDS.LOAD_JSON:

                JSONArray jsArr = null;
                JSONObject itmJsObj = null;

                BaseManager bm = BaseManager.getInstance();
                bm.open();

                try {
                    int i = bm.getMaxIdJournal();
                    jsArr = getJornals(i);
                    Log.d(LOG_CAT, jsArr.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (jsArr == null) {
                    bm.close();
                    return;
                }

                Intent intentUpdate = new Intent();
                intentUpdate.setAction(BROADCAST_ACTION);
                intentUpdate.putExtra("UPDATE", true);
                sendBroadcast(intentUpdate);

                for (int i = 0; i < jsArr.length(); i++) {
                    try {

                        itmJsObj = jsArr.getJSONObject(i);

                        if (itmJsObj == null) return;

                        long idJournal = bm.addJournal(
                                itmJsObj.getString("name"),
                                itmJsObj.getString("description"),
                                itmJsObj.getJSONObject("uri_img").getString("xhdpi"),
                                itmJsObj.getInt("id"),
                                BaseManager.STATUS_JOURNAL.NOT_LOADED
                        );

                        JSONArray titles = itmJsObj.getJSONArray("title");
                        for (int j = 0; j < titles.length(); j++) {
                            bm.addTitle(
                                    titles.getJSONObject(j).getString("name"),
                                    titles.getJSONObject(j).getInt("page"),
                                    idJournal
                            );
                        }

                        JSONArray pages = itmJsObj.getJSONArray("pages");
                        for (int j = 0; j < pages.length(); j++) {
                            JSONObject page = pages.getJSONObject(j);
                            bm.addPage(
                                    page.getString("uri_img_list"),
                                    page.getJSONObject("uri_img_tumb").getString("xhdpi"),
                                    page.getInt("num"),
                                    idJournal
                            );

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                bm.close();

                break;
        }

    }

    private JSONArray getJornals(int id) throws IOException, JSONException {

        String strUri = this.getString(R.string.url_server)
                + this.getString(R.string.url_json)
                + "?id=" + id;
        Log.d("===>",""+strUri);
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        url = new URL(strUri);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = rd.readLine()) != null) {
            result += line;
        }
        rd.close();
        Log.d("===>",""+result);
        return new JSONArray(result);

    }

    public static void loadImeg(int id) {
        Intent intent = new Intent(App.getContext(), LoadService.class);
        intent.putExtra(COMAND, COMANDS.LOAD_IMEG);
        intent.putExtra(ID, id);
        App.getContext().startService(intent);
    }

    public static void loadJson() {

        Intent intent = new Intent(App.getContext(), LoadService.class);
        intent.putExtra(COMAND, COMANDS.LOAD_JSON);
        App.getContext().startService(intent);
    }


}
