package testlogin.com.testcookielogin;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jie on 2016/2/29.
 */
public class OkHttpManager {

    private static OkHttpManager mInstance;
    private Gson mGson;
    private Handler mainHandler;
    private static OkHttpClient mOkHttpClient;


    public static OkHttpManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpManager.class) {
                mInstance = new OkHttpManager();
            }
        }
        return mInstance;
    }

    private OkHttpManager() {
        mOkHttpClient = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public void displayImage(final ImageView imageView, final String url) {
        final Request request = new Request.Builder().url(url).build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                is = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bitmap);
            }
        });
    }




    public void PostRequest(String url, RequestBody requestBody) {
            final Request request =  new Request.Builder().url(url).post(requestBody).build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                 String testStr = response.toString();
                }
            });
    }


    public static class BaiscNameValuePair{
        String name ;
        String value;

        public String getName() {
            return name;
        }

        public BaiscNameValuePair setName(String name) {
            this.name = name;
            return this;
        }

        public String getValue() {
            return value;
        }

        public BaiscNameValuePair setValue(String value) {
            this.value = value;
            return this;
        }
    }


    public static String attachHttpGetParam(String url, List<BaiscNameValuePair> listparams) {
        String urlStr = url+"?";
        for (int i=0;i<listparams.size();i++) {
            BaiscNameValuePair baiscNameValuePair = listparams.get(i);
            urlStr+=baiscNameValuePair.getName()+"="+baiscNameValuePair.getValue();
            if(i+1<listparams.size()){
                urlStr+="&";
            }
        }
        return urlStr;
    }

}
