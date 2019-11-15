/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jxd.wanttospend;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cenxiaozhong
 * @date 2017/5/13
 */
public class DefaultDownloadImpl implements android.webkit.DownloadListener {
	/**
	 * Application Context
	 */
	protected Context mContext;
	/**
	 * Activity
	 */
	protected WeakReference<Activity> mActivityWeakReference = null;
	/**
	 * TAG 用于打印，标识
	 */
	private static final String TAG = DefaultDownloadImpl.class.getSimpleName();
	/**
	 * 权限拦截
	 */
	protected PermissionInterceptor mPermissionListener = null;
	/**
	 * AbsAgentWebUIController
	 */
	protected WeakReference<AbsAgentWebUIController> mAgentWebUIController;

	private static Handler mHandler = new Handler(Looper.getMainLooper());


	protected DefaultDownloadImpl(Activity activity, WebView webView, PermissionInterceptor permissionInterceptor) {
		this.mContext = activity.getApplicationContext();
		this.mActivityWeakReference = new WeakReference<Activity>(activity);
		this.mPermissionListener = permissionInterceptor;
		this.mAgentWebUIController = new WeakReference<AbsAgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(webView));
	}


	@Override
	public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				onDownloadStartInternal(url, userAgent, contentDisposition, mimetype, contentLength);
			}
		});
	}

	protected void onDownloadStartInternal(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
		if (null == mActivityWeakReference.get() || mActivityWeakReference.get().isFinishing()) {
			return;
		}
		if (null != this.mPermissionListener) {
			if (this.mPermissionListener.intercept(url, AgentWebPermissions.STORAGE, "download")) {
				return;
			}
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			List<String> mList = null;
			if ((mList = checkNeedPermission()).isEmpty()) {
			} else {
				Action mAction = Action.createPermissionsAction(mList.toArray(new String[]{}));
				ActionActivity.setPermissionListener(getPermissionListener(url));
				ActionActivity.start(mActivityWeakReference.get(), mAction);
			}
		} else {
		}
	}

	protected ActionActivity.PermissionListener getPermissionListener(final String url) {
		return new ActionActivity.PermissionListener() {
			@Override
			public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
				if (checkNeedPermission().isEmpty()) {
				} else {
					if (null != mAgentWebUIController.get()) {
						mAgentWebUIController
								.get()
								.onPermissionsDeny(
										checkNeedPermission().
												toArray(new String[]{}),
										AgentWebPermissions.ACTION_STORAGE, "Download");
					}
					LogUtils.e(TAG, "储存权限获取失败~");
				}

			}
		};
	}

	protected List<String> checkNeedPermission() {
		List<String> deniedPermissions = new ArrayList<>();
		if (!AgentWebUtils.hasPermission(mActivityWeakReference.get(), AgentWebPermissions.STORAGE)) {
			deniedPermissions.addAll(Arrays.asList(AgentWebPermissions.STORAGE));
		}
		return deniedPermissions;
	}

	protected void showDialog(final String url) {
		Activity mActivity;
		if (null == (mActivity = mActivityWeakReference.get()) || mActivity.isFinishing()) {
			return;
		}
		AbsAgentWebUIController mAgentWebUIController;
		if (null != (mAgentWebUIController = this.mAgentWebUIController.get())) {
			mAgentWebUIController.onForceDownloadAlert(url, createCallback(url));
		}
	}

	protected Handler.Callback createCallback(final String url) {
		return new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				return true;
			}
		};
	}

	protected void performDownload(String url) {

	}


	public static DefaultDownloadImpl create(@NonNull Activity activity,
	                                         @NonNull WebView webView,
	                                         @Nullable PermissionInterceptor permissionInterceptor) {

		return new DefaultDownloadImpl(activity, webView, permissionInterceptor);
	}
}
