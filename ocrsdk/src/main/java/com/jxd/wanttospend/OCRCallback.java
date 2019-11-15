package com.jxd.wanttospend;

import android.graphics.Bitmap;

import com.turui.engine.InfoCollection;

public interface OCRCallback {
    void ocrSuccess(InfoCollection info);
    void ocrBitmap(Bitmap bitmap);
}
