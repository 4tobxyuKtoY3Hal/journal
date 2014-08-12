package com.example.journal.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import com.example.journal.App;

import java.io.*;
import android.net.Uri;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

/**
 * Created by Artem on 08.07.14.
 */
public class ImageManager {

    private static final String LOG_CAT = "ImageManager";
    private ImageManager() {
    }

    public static void saveImg(final String strUrl) {
        File file = new File(App.getContext().getExternalCacheDir(), md5(strUrl) + ".cache");
        try {
//            if (file.exists()) {
//                file.delete();
//                file.createNewFile();
//                fileSave(new URL(strUrl).openStream(), new FileOutputStream(file));
//            } else {
            file.createNewFile();
            fileSave(new URL(strUrl).openStream(), new FileOutputStream(file));
//            }
//            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fetchImage(final String strUrl, final ImageView imgView) {
        if (strUrl == null || imgView == null)
            return;
//Log.d("===>","fetchImage -- "+strUrl);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                final Bitmap bitmap = (Bitmap) message.obj;
                imgView.setImageBitmap(bitmap);
            }
        };

        final Thread thread = new Thread() {
            @Override
            public void run() {
                final Bitmap image = openImage(strUrl);
             //   Log.v("===>", "image: " + image);
                if (image != null) {
                    Log.v("===>", "Got image by URL: " + strUrl);
                    final Message message = handler.obtainMessage(1, image);
                    handler.sendMessage(message);
                }
            }
        };
        thread.setPriority(3);
        thread.start();
    }

    public static Uri openImageURI(String strUrl){
       // Uri ur = Uri.parse("content://" + App.getContext().getCacheDir() +"/" + md5(strUrl) + ".cache");
        File file = new File(App.getContext().getCacheDir(), md5(strUrl) + ".cache");

        try {
            if (file.exists()) {
                Log.d("===>","openImageURI===EST "+file.length());
            } else {
                Log.d("===>","openImageURI===NO "+file.length());
                file.createNewFile();
                fileSave(new URL(strUrl).openStream(), new FileOutputStream(file));
            }

        } catch (Exception e) {
            Log.d("===>","openImageURI===ERROR "+file.length());
            e.printStackTrace();
        }

        Uri ur = Uri.fromFile(file);

        return  ur;
    }

    public static Bitmap openImage(String strUrl) {
        Bitmap bitmap = null;
        File file = new File(App.getContext().getCacheDir(), md5(strUrl) + ".cache");
        //Log.d("===>","file==="+file);
        try {
            if (file.exists()) {
             //   file.delete();
             //   file.createNewFile();
             //   fileSave(new URL(strUrl).openStream(), new FileOutputStream(file));
            } else {
                file.createNewFile();
                fileSave(new URL(strUrl).openStream(), new FileOutputStream(file));
            }
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap == null) {
            file.delete();
        }

        return bitmap;
    }

    /*
        public static void fetchImage(final String strUrl, final ImageView imgView) {
            if (strUrl == null || imgView == null)
                return;

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    final Bitmap bitmap = (Bitmap) message.obj;
                    imgView.setImageBitmap(bitmap);
                }
            };

            final Thread thread = new Thread() {
                @Override
                public void run() {
                    final Bitmap image = downloadImage(strUrl);
                    if (image != null) {
                        Log.v(LOG_CAT, "Got image by URL: " + strUrl);
                        final Message message = handler.obtainMessage(1, image);
                        handler.sendMessage(message);
                    }
                }
            };
            thread.setPriority(3);
            thread.start();
        }

        public static Bitmap downloadImage(String iUrl) {
            Bitmap bitmap = null;
            HttpURLConnection conn = null;
            BufferedInputStream buf_stream = null;
            try {
                Log.v(LOG_CAT, "Starting loading image by URL: " + iUrl);
                conn = (HttpURLConnection) new URL(iUrl).openConnection();
                conn.setDoInput(true);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.connect();
                buf_stream = new BufferedInputStream(conn.getInputStream(), 8192);
                bitmap = BitmapFactory.decodeStream(buf_stream);
                buf_stream.close();
                conn.disconnect();
                buf_stream = null;
                conn = null;
            } catch (MalformedURLException ex) {
                Log.e(LOG_CAT, "Url parsing was failed: " + iUrl);
            } catch (IOException ex) {
                Log.d(LOG_CAT, iUrl + " does not exists");
            } catch (OutOfMemoryError e) {
                Log.w(LOG_CAT, "Out of memory!!!");
                return null;
            } finally {
                if (buf_stream != null)
                    try {
                        buf_stream.close();
                    } catch (IOException ex) {
                    }
                if (conn != null)
                    conn.disconnect();
            }
            return bitmap;
        }

        private Bitmap downloadImage(Context context, int cacheTime, String iUrl, ImageView iView) {
            Bitmap bitmap = null;
            if (cacheTime != 0) {
                File file = new File(context.getExternalCacheDir(), md5(iUrl) + ".cache");
    //            long time = new Date().getTime() / 1000;
    //            long timeLastModifed = file.lastModified() / 1000;
                try {
                    if (file.exists()) {
                        if (timeLastModifed + cacheTime < time) {
                            file.delete();
                            file.createNewFile();
                            fileSave(new URL(iUrl).openStream(),
                                    new FileOutputStream(file));
                        }
                    } else {
                        file.createNewFile();
                        fileSave(new URL(iUrl).openStream(), new FileOutputStream(
                                file));
                    }
                    bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bitmap == null) {
                    file.delete();
                }
            } else {
                try {
                    bitmap = BitmapFactory.decodeStream(new URL(iUrl).openStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (iView != null) {
                downloaded.remove(iView);
            }
            return bitmap;
        }

        public void fetchImage(final Context context, final int cacheTime, final String url, final ImageView iView) {
            if (iView != null) {
                if (findObject(iView)) {
                    return;
                }
                downloaded.add(iView);
            }
            new AsyncTask<String, Void, Bitmap>() {
                protected Bitmap doInBackground(String... iUrl) {
                    return downloadImage(context, cacheTime, iUrl[0], iView);
                }

                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    if (iView != null) {
                        iView.setImageBitmap(result);
                    }
                }
            }.execute(new String[]{url});
        }
    */
    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void fileSave(InputStream is, FileOutputStream outputStream) {
        int i;
        try {
            while ((i = is.read()) != -1) {
                outputStream.write(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    public boolean findObject(ImageView object) {
        for (int i = 0; i < downloaded.size(); i++) {
            if (downloaded.elementAt(i).equals(object)) {
                return true;
            }
        }
        return false;
    }
*/
}

/*
[
    {"id":5,
    "name":"\u041f\u043e\u0434 \u0440\u0443\u043b\u0435\u043c",
    "description":"\u0422\u0435\u0441\u0442\u043e\u0432\u044b\u0439 \u0436\u0443\u0440\u043d\u0430\u043b, \u0441\u043e\u0434\u0435\u0440\u0436\u0438\u0442 3 \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u044b",
    "uri_img":{"mdpi":"null","hdpi":"null","xhdpi":"http://isdev.ru/art/assets/xhdpi/root_xh.png","xxhdpi":"null"},
    "title":[{"name":"\u0413\u043b\u0430\u0432\u043d\u0430\u044f","page":1},
            {"name":"\u0412\u0442\u043e\u0440\u0430\u044f","page":2},
            {"name":"\u0421\u0440\u0435\u0434\u043d\u044f\u044f","page":2},
            {"name":"\u041f\u043e\u0441\u043b\u0435\u0434\u043d\u044f\u044f","page":3}],"
    pages":[
        {
            "uri_img_list":"http://isdev.ru/art/assets/01.png",
            "uri_img_tumb":{"mdpi":"null","hdpi":"null","xhdpi":"http://isdev.ru/art/assets/xhdpi/txh_01.png","xxhdpi":"xnull"},
            "num":"1"
        },{
                    "uri_img_list":"http://isdev.ru/art/assets/02.png",
                    "uri_img_tumb":{"mdpi":"null","hdpi":"null","xhdpi":"http://isdev.ru/art/assets/xhdpi/txh_01.png","xxhdpi":"xnull"},
                    "num":"2"
                },{
                            "uri_img_list":"http://isdev.ru/art/assets/03.png",
                            "uri_img_tumb":{"mdpi":"null","hdpi":"null","xhdpi":"http://isdev.ru/art/assets/xhdpi/txh_01.png","xxhdpi":"xnull"},
                            "num":"3"
                        }
        ]
     }
        ]
 */
