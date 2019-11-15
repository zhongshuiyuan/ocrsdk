package com.jxd.wanttospend;

import android.graphics.Bitmap;

public class OCRStatus {

    //身份证原图像
    private Bitmap bitmap;

    //初始化码
    private static int idType;

    //成功
    public static final int OCR_Success=1;

    //超时
    public static final int OCR_TimeOut=2;

    //初始化失败
    public static final int OCR_Butld_ERR=3;

    //包名不一致
    public static final int OCR_BUTLD_ERR=4;

    //服务密码不正确
    public static final int OCR_PSW_ERR=5;

    //反面
    public static int OCR_EMBLEM=-1;
    //正面
    public static int OCR_Positive=1;
    //无图片字样
    public static int OCR_NONE=0;

    //机构编号
    public static String institutionCode;

    //服务密码
    public static String password;

    //type 1活体  2 身份证 4有源 5无源
    public static int type;

    //包名
    public static String className;

    public static String getClassName() {
        return className;
    }

    public static void setClassName(String className) {
        OCRStatus.className = className;
    }

    public static int getType() {
        return type;
    }

    public static void setType(int type) {
        OCRStatus.type = type;
    }
    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        OCRStatus.password = password;
    }

    public static String getInstitutionCode() {
        return institutionCode;
    }

    public static void setInstitutionCode(String institutionCode) {
        OCRStatus.institutionCode = institutionCode;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

}
