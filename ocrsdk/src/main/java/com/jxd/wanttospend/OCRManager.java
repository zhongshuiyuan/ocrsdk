package com.jxd.wanttospend;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.idcard.TRECAPI;
import com.idcard.TRECAPIImpl;
import com.jxd.wanttospend.utils.HttpUtils;
import com.jxd.wanttospend.utils.RequestBean;
import com.jxd.wanttospend.utils.ReturnBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 全局app
 * Created by WZG on 2016/12/12.
 */

public class OCRManager {
    private  static boolean debug;
    private static OCRManager OCRManager;
    private StyleParams styleParams;
    private static Application application;
    private OCRCallback ocrCallback;
    private TRECAPI engine;
    private OCRStatus ocrStatus;
    private Handler handler=new Handler();

    public StyleParams getStyleParams() {
        return styleParams;
    }

    public void setStyleParams(StyleParams styleParams) {
        this.styleParams = styleParams;
    }

    private OCRManager() {
        if (engine==null)
        engine = new TRECAPIImpl();
    }

    public TRECAPI getEngine(){
        return engine;
    }

    public void setOcrStatus(OCRStatus ocrStatus){
        this.ocrStatus=ocrStatus;
    }

    //正面
    public void startOcrType(final Activity activity, final OCRCallback ocrCallback) {
        this.ocrCallback=ocrCallback;
        if (TextUtils.isEmpty(OCRStatus.institutionCode)||TextUtils.isEmpty(OCRStatus.password))
            return;

        RequestBean requestBean=new RequestBean();
        requestBean.setInstitutionCode(OCRStatus.institutionCode);
        requestBean.setPassword(OCRStatus.password);
        requestBean.setType(OCRStatus.type);
        requestBean.setClassName(OCRStatus.className);
        OkHttpClient client=new OkHttpClient();
        MediaType MEDIA_TYPE_JSON= MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody=RequestBody.create(MEDIA_TYPE_JSON,new Gson().toJson(requestBean));
        Request request = new Request.Builder()
                .url(BuildConfig.BASE_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                Gson gson = new Gson();
                final ReturnBean body = gson.fromJson(string,ReturnBean.class);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (body.getCode().equals("000000")){
                            if (ocrStatus.getIdType() == OCRStatus.OCR_EMBLEM) {
                                Intent intent=new Intent(activity,IDCardActivity.class);
                                intent.putExtra(IDCardActivity.IDCard, OCRStatus.OCR_EMBLEM);
                                intent.putExtra("side",0 );
                                activity.startActivity(intent);
                            } else if (ocrStatus.getIdType() == OCRStatus.OCR_Positive) {
                                Intent intent=new Intent(activity,IDCardActivity.class);
                                intent.putExtra(IDCardActivity.IDCard, OCRStatus.OCR_Positive);
                                intent.putExtra("side",1 );
                                activity.startActivity(intent);
                            }else{
                                Intent intent=new Intent(activity,IDCardActivity.class);
                                intent.putExtra(IDCardActivity.IDCard, OCRStatus.OCR_NONE);
                                intent.putExtra("side",1 );
                                activity.startActivity(intent);
                            }
                        }
                    }
                },0);
            }
        });
    }

    public OCRCallback getOCRCallback(){
        return ocrCallback;
    }

    public static OCRManager getInstance() {
        if (OCRManager==null){
            synchronized (OCRManager.class){
                if (OCRManager==null){
                    OCRManager=new OCRManager();
                }
            }
        }
        return OCRManager;
    }

    public static  void initSDK(Application app){
        setApplication(app);
        setDebug(false);
    }

    public  static void init(Application app, boolean debug){
        setApplication(app);
        setDebug(debug);
    }

    private static void setApplication(Application application) {
        OCRManager.application= application;
    }

    public  static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        OCRManager.debug = debug;
    }

    public class StyleParams{

        //标题字体颜色
        private int titleColor;
        //标题栏背景颜色设置
        private int backColor;
        //返回按钮设置
        private int imgFinish;
        //结束当前页面按钮
        private int imgStop;

        public int getTitleColor() {
            return titleColor;
        }

        public void setTitleColor(int titleColor) {
            this.titleColor = titleColor;
        }

        public int getBackColor() {
            return backColor;
        }

        public void setBackColor(int backColor) {
            this.backColor = backColor;
        }

        public int getImgFinish() {
            return imgFinish;
        }

        public void setImgFinish(int imgFinish) {
            this.imgFinish = imgFinish;
        }

        public int getImgStop() {
            return imgStop;
        }

        public void setImgStop(int imgStop) {
            this.imgStop = imgStop;
        }
    }
}