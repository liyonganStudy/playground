package com.netease.cloudmusic.module.walle;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ChannelUtil {
	private static String mChannel;

	/**
	 * 返回市场。 如果获取失败返回""
	 *
	 * @param context
	 * @return
	 */
	public static String getChannel(Context context) {
		return getChannel(context, "");
	}

	/**
	 * 返回市场。 如果获取失败返回defaultChannel
	 *
	 * @param context
	 * @param defaultChannel
	 * @return
	 */
	public static String getChannel(Context context, String defaultChannel) {
		// 内存中获取
		if (!TextUtils.isEmpty(mChannel)) {
			return mChannel;
		}
		// 从apk中获取
		mChannel = getChannelFromApk(context);
		if (!TextUtils.isEmpty(mChannel)) {
			return mChannel;
		}
		// 全部获取失败
		return defaultChannel;
	}

	/**
	 * 从apk中获取版本信息
	 *
	 * @param context
	 * @return
	 */
	private static String getChannelFromApk(Context context) {
		// 从apk包中获取
		final String apkPath = getApkPath(context);
		if (TextUtils.isEmpty(apkPath)) {
			return null;
		}
		ChannelInfo channel = ChannelReader.get(new File(apkPath));
		if (channel == null) {
			return null;
		}
		return channel.getChannel();
	}

	@Nullable
	private static String getApkPath(@NonNull final Context context) {
		String apkPath = null;
		try {
			final ApplicationInfo applicationInfo = context.getApplicationInfo();
			if (applicationInfo == null) {
				return null;
			}
			apkPath = applicationInfo.sourceDir;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return apkPath;
	}
}
