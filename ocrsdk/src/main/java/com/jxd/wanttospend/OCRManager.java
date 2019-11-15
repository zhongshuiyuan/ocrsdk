package com.jxd.wanttospend;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.idcard.TRECAPI;
import com.idcard.TRECAPIImpl;
import com.jxd.wanttospend.utils.ReturnBean;

/**
 * 全局app
 * Created by WZG on 2016/12/12.
 */

public class OCRManager {
    private  static boolean debug;
    private static com.jxd.wanttospend.OCRManager OCRManager;
    private StyleParams styleParams;
    private static Application application;
    private OCRCallback ocrCallback;
    private TRECAPI engine;
    private OCRStatus ocrStatus;
    private Activity activity;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ReturnBean body= (ReturnBean) msg.obj;

        }
    };

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
    public void startOcrType(final Activity activity, OCRCallback ocrCallback) {
        this.ocrCallback=ocrCallback;
        this.activity=activity;
        /*if (ocrStatus.getIdType() == OCRStatus.OCR_EMBLEM) {
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
        }*/
        if (ocrStatus.getIdType() == OCRStatus.OCR_EMBLEM) {
            Intent intent=new Intent(activity, IDCardActivity.class);
            intent.putExtra(IDCardActivity.IDCard, OCRStatus.OCR_EMBLEM);
            intent.putExtra("side",0 );
            activity.startActivity(intent);
        } else if (ocrStatus.getIdType() == OCRStatus.OCR_Positive) {
            Intent intent=new Intent(activity, IDCardActivity.class);
            intent.putExtra(IDCardActivity.IDCard, OCRStatus.OCR_Positive);
            intent.putExtra("side",1 );
            activity.startActivity(intent);
        }else{
            Intent intent=new Intent(activity, IDCardActivity.class);
            intent.putExtra(IDCardActivity.IDCard, OCRStatus.OCR_NONE);
            intent.putExtra("side",1 );
            activity.startActivity(intent);
        }
       /* if (TextUtils.isEmpty(OCRStatus.institutionCode)||TextUtils.isEmpty(OCRStatus.password))
            return;
        RequestBean requestBean=new RequestBean();
        requestBean.setInstitutionCode(OCRStatus.institutionCode);
        requestBean.setPassword(OCRStatus.password);
        requestBean.setType(OCRStatus.type);
        OkHttpClient client=new OkHttpClient();
        MediaType MEDIA_TYPE_JSON= MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody=RequestBody.create(MEDIA_TYPE_JSON,new Gson().toJson(requestBean));
        Request request = new Request.Builder()
                .url("http:// 192.168.1.116:19610/sdkValid/checkAuthority")
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
        });*/
    }

    public OCRCallback getOCRCallback(){
        return ocrCallback;
    }

    public static com.jxd.wanttospend.OCRManager getInstance() {
        if (OCRManager==null){
            synchronized (com.jxd.wanttospend.OCRManager.class){
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
