package com.jxd.wanttospend.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author renji
 * @date 2018/1/3
 */

public class Bitmap2Base64 {

    public static String bitmapToBase64NONseal(Bitmap bitmap) {
        //转换结束以后的Base64编码
        String result = null;
        //读取bitmap以后通过字节数组来盛装
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                //通过Bitmap的方法把Bitmap数据放到字节数组当中，用于之后的转换使用
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                baos.flush();
                baos.close();
                //把字节数组转换
                byte[] bitmapBytes = baos.toByteArray();
                //进行编码转换
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.replace(" ", "");
    }

    //byte[]转base64
    public static String byte2Base64StringFun(byte[] b){
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap stringToBitmap(String string) {
            //将字符串转换成Bitmap类型
            Bitmap bitmap = null;
            try {
                byte[] bitmapArray;
                bitmapArray = Base64.decode(string, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
    }
}
