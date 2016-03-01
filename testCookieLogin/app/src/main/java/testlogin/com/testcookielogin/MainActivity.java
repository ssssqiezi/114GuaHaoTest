package testlogin.com.testcookielogin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;


public class MainActivity extends Activity implements SMSBroadcastReceiver.MessageListener {

    public static final String HOST = "http://www.bjguahao.gov.cn";
    public static String VcodeStr = HOST + "/IdentityServlet";
    public static final String GETSMSCODE = HOST + "/v/sendcodecommon.htm";
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.user_idnumber)
    EditText userIdnumber;
    @Bind(R.id.phonenubmer)
    EditText phonenubmer;
    @Bind(R.id.vcode_site)
    EditText vcodeSite;
    @Bind(R.id.vcode_img)
    ImageView vcodeImg;
    @Bind(R.id.sms_vcode)
    EditText smsVcode;
    @Bind(R.id.getVcode)
    Button getVcode;

    private String mSmsCode;
    private SMSBroadcastReceiver mSMSBroadReciever;

    DefaultHttpClient mHttpclient = new DefaultHttpClient();

    private Handler myHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    vcodeImg.setImageBitmap((Bitmap) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSMSBroadReciever = new SMSBroadcastReceiver();
        mSMSBroadReciever.setOnReceivedMessageListener(this);

        RefreshGetSiteCode();

        findViewById(R.id.getVcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendGetSmsCodeRequest();
                //sendGetSmsCodeRequest();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getSMSCodePost(GETSMSCODE,MainActivity.this);
                    }
                }).start();

            }
        });


    }

    private void RefreshGetSiteCode() {
        long timestamp = System.currentTimeMillis();

        // http://www.bjguahao.gov.cn/IdentityServlet?ts=1456734936065&channel=yzdl	200	GET	www.bjguahao.gov.cn	/IdentityServlet?ts=1456734936065&channel=yzdl	Mon Feb 29 16:35:36 CST 2016	23	2161	Complete	94x28

/*        ArrayList<OkHttpManager.BaiscNameValuePair> params = new ArrayList<OkHttpManager.BaiscNameValuePair>();
        params.add(new OkHttpManager.BaiscNameValuePair().setName("ts").setValue(timestamp + ""));
        params.add(new OkHttpManager.BaiscNameValuePair().setName("channel").setValue("yzdl"));
        String getStr = OkHttpManager.getInstance().attachHttpGetParam(VcodeStr, params);
        OkHttpManager.getInstance().displayImage((ImageView) findViewById(R.id.vcode_img), getStr);*/
        new Thread(new Runnable() {
            @Override
            public void run() {
/*                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getVodeImage(VcodeStr);
                    }
                });*/
                getVodeImage(VcodeStr);
            }
        }).start();

/*        params.clear();
        timestamp = System.currentTimeMillis();
        params.add(new OkHttpManager.BaiscNameValuePair().setName("ts").setValue(timestamp + ""));
        params.add(new OkHttpManager.BaiscNameValuePair().setName("channel").setValue("yzdl"));
        getStr = OkHttpManager.getInstance().attachHttpGetParam(VcodeStr, params);

        OkHttpManager.getInstance().displayImage((ImageView) findViewById(R.id.vcode_img), getStr);*/
    }

    @OnClick(R.id.vcode_img)
    public void RefreshSiteVode(){
        RefreshGetSiteCode();
    }


    public void sendGetSmsCodeRequest() {

        String userName = ((EditText) findViewById(R.id.username)).getText().toString();
        String encodeStr = "";
        try {
            encodeStr = URLEncoder.encode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //userName=%E4%BB%BB%E6%96%87%E6%9D%B0&isshefn=142702198404164512&sms=13466397091&code=2344&channel=yzdl&smsCode=&smsType=2&loginLocation=1

        String smsCode = null;
        RequestBody requestBody = new FormBody.Builder()
                .add("userName", encodeStr)
                .add("isshefn", userIdnumber.getText().toString())
                .add("sms", phonenubmer.getText().toString())
                .add("code", vcodeSite.getText().toString())
                .add("channel", "yzdl").add("smsCode", "").add("smsType", "2").add("loginLocation", "1").build();
        OkHttpManager.getInstance().PostRequest(GETSMSCODE, requestBody);
    }

    public void getVodeImage(String url){

        long timestamp = System.currentTimeMillis();
        ArrayList<OkHttpManager.BaiscNameValuePair> params = new ArrayList<OkHttpManager.BaiscNameValuePair>();
        params.add(new OkHttpManager.BaiscNameValuePair().setName("ts").setValue(timestamp + ""));
        params.add(new OkHttpManager.BaiscNameValuePair().setName("channel").setValue("yzdl"));
        String getStr = OkHttpManager.getInstance().attachHttpGetParam(VcodeStr, params);

        HttpGet httpRequest = new HttpGet(getStr);
        // 取得默认的HttpClient
        //DefaultHttpClient httpclient = new DefaultHttpClient();
        String strResult = null;
        // NameValuePair实现请求参数的封装

/*        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ts", timestamp + ""));
        params.add(new BasicNameValuePair("channel", "yzdl"))*/;



        //httpRequest.setParams();
        httpRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
        httpRequest.addHeader("Accept", "image/png,image/*;q=0.8,*/*;q=0.5");
        httpRequest.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        httpRequest.addHeader("Accept-Encoding", "gzip, deflate");
        httpRequest.addHeader("Referer", "http://www.bjguahao.gov.cn/index.htm");

        try {
            // 添加请求参数到请求对象
            //httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            // 获得响应对象
            HttpResponse httpResponse = mHttpclient.execute(httpRequest);
            // 判断是否请求成功
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 获得响应返回Json格式数据
               // vcodeImg.setImageBitmap(BitmapFactory.decodeStream(httpResponse.getEntity().getContent()));
                Message message =  new Message();
                message.what=1;
                message.obj = BitmapFactory.decodeStream(httpResponse.getEntity().getContent());
                myHander.sendMessage(message);

            } else {
                strResult = "错误响应:" + httpResponse.getStatusLine().toString();
            }
        } catch (ClientProtocolException e) {
            strResult = "错误响应:" + e.getMessage().toString();
            e.printStackTrace();
        } catch (IOException e) {
            strResult = "错误响应:" + e.getMessage().toString();
            e.printStackTrace();
        } catch (Exception e) {
            strResult = "错误响应:" + e.getMessage().toString();
            e.printStackTrace();
        }
    }

    public String getSMSCodePost(String url, MainActivity mainActivity) {
        // 根据url获得HttpPost对象
        HttpPost httpRequest = new HttpPost(url);
        // 取得默认的HttpClient
        //DefaultHttpClient httpclient = new DefaultHttpClient();
        String strResult = null;
        // NameValuePair实现请求参数的封装
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userName", mainActivity.username.getText().toString()));
        params.add(new BasicNameValuePair("isshefn", mainActivity.userIdnumber.getText().toString()));
        params.add(new BasicNameValuePair("sms", mainActivity.phonenubmer.getText().toString()));
        params.add(new BasicNameValuePair("code", mainActivity.vcodeSite.getText().toString()));
        params.add(new BasicNameValuePair("channel", "yzdl"));
        params.add(new BasicNameValuePair("smsCode", ""));
        params.add(new BasicNameValuePair("smsType", "2"));
        params.add(new BasicNameValuePair("loginLocation", "1"));

        httpRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
        httpRequest.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        httpRequest.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        httpRequest.addHeader("Accept-Encoding", "gzip, deflate");
        httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpRequest.addHeader("Referer", "http://www.bjguahao.gov.cn/index.htm");
        httpRequest.addHeader("X-Requested-With", "XMLHttpRequest");

        try {
            // 添加请求参数到请求对象
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            // 获得响应对象
            HttpResponse httpResponse = mHttpclient.execute(httpRequest);
            // 判断是否请求成功
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 获得响应返回Json格式数据
                strResult = EntityUtils.toString(httpResponse.getEntity());
                return strResult;
            } else {
                strResult = "错误响应:" + httpResponse.getStatusLine().toString();
            }
        } catch (ClientProtocolException e) {
            strResult = "错误响应:" + e.getMessage().toString();
            e.printStackTrace();
            return strResult;
        } catch (IOException e) {
            strResult = "错误响应:" + e.getMessage().toString();
            e.printStackTrace();
            return strResult;
        } catch (Exception e) {
            strResult = "错误响应:" + e.getMessage().toString();
            e.printStackTrace();
            return strResult;
        }
        return strResult;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReceived(String message) {
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(message);
        mSmsCode = m.replaceAll("").trim();
        smsVcode.setText(mSmsCode);
    }
}
