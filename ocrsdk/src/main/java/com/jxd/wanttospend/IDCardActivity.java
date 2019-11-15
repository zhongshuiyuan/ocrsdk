package com.jxd.wanttospend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.idcard.TParam;
import com.idcard.TRECAPI;
import com.idcard.TStatus;
import com.idcard.TengineID;
import com.turui.android.activity.WCameraActivity;
import com.turui.android.cameraview.R;
import com.turui.engine.EngineConfig;
import com.turui.engine.InfoCollection;

public class IDCardActivity extends AppCompatActivity {

    int mSide = 0;
    public static final int ZhengMian=0;
    public static final int FanMian=1;
    public static final String IDCard="IDCard";
    private int i;
    public  Bitmap copyTakeBitmap;
    public Bitmap copyTakeBitmap2;
    private TRECAPI engine;
    private OCRCallback ocrCallback;
    private OCRStatus ocrStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        engine = OCRManager.getInstance().getEngine();
        ocrCallback = OCRManager.getInstance().getOCRCallback();
        ocrStatus = new OCRStatus();
        //初始化引擎
        //默认不进行引擎初始化的判断，请根据初始化结果自行处理，进入识别界面则认为这里初始化为成功
        TStatus initStatus = this.engine.TR_StartUP(this, this.engine.TR_GetEngineTimeKey());  // 测试，有限制请勿在生产上使用
        if (initStatus == TStatus.TR_TIME_OUT) {
            // 版本号 >= 7.4.0 时 TR_StartUP(context, 时间Key不再起作用可以任意传，具体配置改到了 assets 下的 license.dat 中)
            ocrStatus.setIdType(2);
        } else if (initStatus == TStatus.TR_FAIL) {
            ocrStatus.setIdType(3);
        } else if (initStatus == TStatus.TR_OK) {
            ocrStatus.setIdType(1);
        }
        //使用按次服务需要配置以下信息
        this.engine.TR_SetParam(TParam.T_SET_PER_CALL_TIMEOUT, 10);//超时时间
        this.engine.TR_SetParam(TParam.T_SET_ROTATE_180, 1);
        this.engine.TR_SetParamString(TParam.T_SET_PER_CALL_ACCOUNT, "longjuekeji");//账号
        this.engine.TR_SetParamString(TParam.T_SET_PER_CALL_PASSWORD, "9253ce8151d74ee7960ba14d154dfc01");//key

        Intent callback = getIntent();
        i = callback.getIntExtra(IDCard, OCRStatus.OCR_EMBLEM);
        int side = callback.getIntExtra("side", 0);
        requestCameraPerm(side);
    }

    private void requestCameraPerm(int side) {
        /** 请认真检查初始化方法 engine.TR_StartUP(var1, var2) 的返回值是否成功否则进入识别界面将无法正常运行！！！
         * TengineID.TIDCARD2  身份证
         * TengineID.TIDBANK 银行卡
         * TengineID.TIDLPR 车牌
         * TengineID.TIDJSZCARD 驾驶证
         * TengineID.TIDXSZCARD 行驶证
         * TengineID.TIDBIZLIC 营业执照
         *TengineID.TIDTICKET 火车票
         * TengineID.TIDSSCCARD 社保卡
         * TengineID.TIDPASSPORT 护照
         * TengineID.TIDEEPHK 港澳通行证
         * */
        Intent intent = new Intent(IDCardActivity.this, WCameraActivity.class);
        EngineConfig config = new EngineConfig(engine, TengineID.TIDCARD2);
        config.setEngingModeType(EngineConfig.EngingModeType.SCAN);//扫描模式
        config.setShowModeChange(false);//是否显示模式切换按钮
        config.setbMattingOfIdcard(false);//正常下不用，引擎内部裁切外部无法调整
        config.setCheckCopyOfIdcard(false);//正常下不用，翻拍检测，在结果中获取InfoCollection类中的 .getImageProperty()
        config.setOpenSmallPicture(true);//开启小图（身份证头像与银行卡卡号其它证件没有）
        config.setOpenImageRotateCheck(true);//返回身份证旋转方向信息
        if (i== OCRStatus.OCR_Positive){
            config.setTipBitmapType(EngineConfig.TipBitmapType.IDCARD_PORTRAIT);//默认提示图片(目前只有身份证，其它证件可以自定义)：IDCARD_PORTRAIT:头像面，IDCARD_EMBLEM:国徽面，NONE:不显示
        }else if (i== OCRStatus.OCR_EMBLEM){
            config.setTipBitmapType(EngineConfig.TipBitmapType.IDCARD_EMBLEM);//默认提示图片(目前只有身份证，其它证件可以自定义)：IDCARD_PORTRAIT:头像面，IDCARD_EMBLEM:国徽面，NONE:不显示
        }else{
            config.setTipBitmapType(EngineConfig.TipBitmapType.NONE);//默认提示图片(目前只有身份证，其它证件可以自定义)：IDCARD_PORTRAIT:头像面，IDCARD_EMBLEM:国徽面，NONE:不显示
        }
        config.setDecodeInRectOfTakeMode(true);//UI层裁切，无法非常准确。只有拍照模式支持，如果是身份证 setMattingOfIdcard 为 false 时才有效
        config.setSaveToData(false);//保存到私有目录
        config.setResultCode(RESULT_OK);
        intent.putExtra(EngineConfig.class.getSimpleName(), config);//必须有
        startActivityForResult(intent, side);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            //身份证正面
            Bundle bundle = data.getExtras();
            InfoCollection info = (InfoCollection) bundle.getSerializable("info");
            ocrCallback.ocrSuccess(info);
            //演示显示全部信息，开发过程可以获取单个信息
            //图片内部没有进行回收，获取图片后请自行保存，以免出现问题
            if (null != info) {
                if (info.getCode() == 200) {
                    //获取图片
                    Bitmap.Config config = Bitmap.Config.RGB_565;
                    //全图
                    if (WCameraActivity.takeBitmap != null && !WCameraActivity.takeBitmap.isRecycled()) {
                        if (info.getImageRotate() == 0) {
                            copyTakeBitmap = WCameraActivity.takeBitmap.copy(config, false);
                            ocrCallback.ocrBitmap(copyTakeBitmap);
                        } else if (info.getImageRotate() == 1) {
                            //原图像输入前，被90度翻转
                            copyTakeBitmap = WCameraActivity.takeBitmap.copy(config, false);
                            Bitmap bitmap = rotaingImageView(270, copyTakeBitmap);
                            ocrCallback.ocrBitmap(bitmap);
                        } else if (info.getImageRotate() == 2) {
                            //原图像输入前，被180度翻转
                            copyTakeBitmap = WCameraActivity.takeBitmap.copy(config, false);
                            Bitmap bitmap = rotaingImageView(180, copyTakeBitmap);
                            ocrCallback.ocrBitmap(bitmap);
                        } else if (info.getImageRotate() == 3) {
                            //原图像输入前，被270度翻转
                            copyTakeBitmap = WCameraActivity.takeBitmap.copy(config, false);
                            Bitmap bitmap = rotaingImageView(90, copyTakeBitmap);
                            ocrCallback.ocrBitmap(bitmap);
                        } else {
                            Toast.makeText(this, "未知:" + info.getImageRotate(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                Log.e("OCR", "code:" + info.getCode() + " desc:" + info.getDesc());
            }
    } else if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            InfoCollection info = (InfoCollection) bundle.getSerializable("info");
            //演示显示全部信息，开发过程可以获取单个信息
            //图片内部没有进行回收，获取图片后请自行保存，以免出现问题
            if (null != info) {
                if (info.getCode() == 200) {

                    //身份证反面
                    Bitmap.Config config = Bitmap.Config.RGB_565;
                    if (WCameraActivity.takeBitmap != null && !WCameraActivity.takeBitmap.isRecycled()) {
                        copyTakeBitmap = WCameraActivity.takeBitmap.copy(config, false);
                        //    结束当前这个Activity对象的生命
                        if (info.getImageRotate() == 0) {
                            copyTakeBitmap = WCameraActivity.takeBitmap.copy(config, false);
                            ocrCallback.ocrBitmap(copyTakeBitmap);
                        } else if (info.getImageRotate() == 1) {
                            //原图像输入前，被90度翻转
                            copyTakeBitmap = WCameraActivity.takeBitmap.copy(config, false);
                            Bitmap bitmap = rotaingImageView(270, copyTakeBitmap);
                            ocrCallback.ocrBitmap(bitmap);
                        } else if (info.getImageRotate() == 2) {
                            //原图像输入前，被180度翻转
                            copyTakeBitmap = WCameraActivity.takeBitmap.copy(config, false);
                            Bitmap bitmap = rotaingImageView(180, copyTakeBitmap);
                            ocrCallback.ocrBitmap(bitmap);
                        } else if (info.getImageRotate() == 3) {
                            //原图像输入前，被270度翻转
                            copyTakeBitmap = WCameraActivity.takeBitmap.copy(config, false);
                            Bitmap bitmap = rotaingImageView(90, copyTakeBitmap);
                            ocrCallback.ocrBitmap(bitmap);
                        } else {
                            Toast.makeText(this, "未知:" + info.getImageRotate(), Toast.LENGTH_SHORT).show();
                        }
                        finish();
                        ocrCallback.ocrSuccess(info);
                    }
                }
            }

    }
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

        return resizedBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        engine.TR_ClearUP();
    }
}
