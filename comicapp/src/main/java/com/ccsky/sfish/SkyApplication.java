/*
 * Copyright 2016 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ccsky.sfish;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.ccsky.content.RecordingApplication;
import com.ccsky.network.StatusCodeException;
import com.ccsky.sfish.client.EhCookieStore;
import com.ccsky.sfish.client.EhDns;
import com.ccsky.sfish.client.SkyClient;
import com.ccsky.sfish.client.SkyEngine;
import com.ccsky.sfish.client.data.GalleryDetail;
import com.ccsky.sfish.download.DownloadManager;
import com.ccsky.sfish.spider.SpiderDen;
import com.ccsky.sfish.ui.CommonOperations;
import com.ccsky.text.Html;
import com.ccsky.util.BitmapUtils;
import com.ccsky.util.ExceptionUtils;
import com.ccsky.util.IoThreadPoolExecutor;
import com.ccsky.util.ReadableTime;
import com.getkeepsafe.relinker.ReLinker;
import com.hippo.a7zip.A7Zip;
import com.hippo.a7zip.A7ZipExtractLite;
import com.hippo.beerbelly.SimpleDiskCache;
import com.hippo.conaco.Conaco;
import com.hippo.image.Image;
import com.hippo.image.ImageBitmap;
import com.hippo.unifile.UniFile;
import com.hippo.yorozuya.FileUtils;
import com.hippo.yorozuya.IntIdGenerator;
import com.hippo.yorozuya.OSUtils;
import com.hippo.yorozuya.SimpleHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class SkyApplication extends RecordingApplication {

    private static final String TAG = SkyApplication.class.getSimpleName();
    private static final String KEY_GLOBAL_STUFF_NEXT_ID = "global_stuff_next_id";

    public static final boolean BETA = false;

    private static final boolean DEBUG_CONACO = false;
    private static final boolean DEBUG_PRINT_NATIVE_MEMORY = false;
    private static final boolean DEBUG_PRINT_IMAGE_COUNT = false;
    private static final long DEBUG_PRINT_INTERVAL = 3000L;

    private static SkyApplication instance;

    private final IntIdGenerator mIdGenerator = new IntIdGenerator();
    private final HashMap<Integer, Object> mGlobalStuffMap = new HashMap<>();
    private EhCookieStore mEhCookieStore;
    private SkyClient mSkyClient;
    private SkyProxySelector mSkyProxySelector;
    private OkHttpClient mOkHttpClient;
    private ImageBitmapHelper mImageBitmapHelper;
    private Conaco<ImageBitmap> mConaco;
    private LruCache<Long, GalleryDetail> mGalleryDetailCache;
    private SimpleDiskCache mSpiderInfoCache;
    private DownloadManager mDownloadManager;
    private Hosts mHosts;
    private FavouriteStatusRouter mFavouriteStatusRouter;

    private final List<Activity> mActivityList = new ArrayList<>();

    public static SkyApplication getInstance() {
        return instance;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onCreate() {
        instance = this;

//        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
//        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
//            try {
//                if (Settings.getSaveCrashLog()) {
//                    Crash.saveCrashLog(instance, e);
//                }
//            } catch (Throwable ignored) { }
//
//            if (handler != null) {
//                handler.uncaughtException(t, e);
//            }
//        });

        super.onCreate();

        GetText.initialize(this);
        StatusCodeException.initialize(this);
        Settings.initialize(this);
        ReadableTime.initialize(this);
        Html.initialize(this);
        AppConfig.initialize(this);
        SpiderDen.initialize(this);
        SkyDB.initialize(this.getApplicationContext());
        SkyEngine.initialize();
        BitmapUtils.initialize(this.getApplicationContext());
        Image.initialize(this);
        A7Zip.loadLibrary(A7ZipExtractLite.LIBRARY, libname -> ReLinker.loadLibrary(SkyApplication.this, libname));

        if (SkyDB.needMerge()) {
            SkyDB.mergeOldDB(this);
        }

        if (Settings.getEnableAnalytics()) {
            Analytics.start(this);
        }

        // Do io tasks in new thread
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Check no media file
                try {
                    UniFile downloadLocation = Settings.getDownloadLocation();
                    if (Settings.getMediaScan()) {
                        CommonOperations.removeNoMediaFile(downloadLocation);
                    } else {
                        CommonOperations.ensureNoMediaFile(downloadLocation);
                    }
                } catch (Throwable t) {
                    ExceptionUtils.throwIfFatal(t);
                }

                // Clear temp files
                try {
                    clearTempDir();
                } catch (Throwable t) {
                    ExceptionUtils.throwIfFatal(t);
                }

                return null;
            }
        }.executeOnExecutor(IoThreadPoolExecutor.getInstance());

        // Check app update
        update();

        // Update version code
        try {
            PackageInfo pi= getPackageManager().getPackageInfo(getPackageName(), 0);
            Settings.putVersionCode(pi.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            // Ignore
        }

        mIdGenerator.setNextId(Settings.getInt(KEY_GLOBAL_STUFF_NEXT_ID, 0));

        if (DEBUG_PRINT_NATIVE_MEMORY || DEBUG_PRINT_IMAGE_COUNT) {
            debugPrint();
        }
    }

    private void clearTempDir() {
        File dir = AppConfig.getTempDir();
        if (null != dir) {
            FileUtils.deleteContent(dir);
        }
        dir = AppConfig.getExternalTempDir();
        if (null != dir) {
            FileUtils.deleteContent(dir);
        }

        // Add .nomedia to external temp dir
        CommonOperations.ensureNoMediaFile(UniFile.fromFile(AppConfig.getExternalTempDir()));
    }

    private void update() {
        int version = Settings.getVersionCode();
        if (version < 52) {
            Settings.putGuideGallery(true);
        }
    }

    public void clearMemoryCache() {
        if (null != mConaco) {
            mConaco.getBeerBelly().clearMemory();
        }
        if (null != mGalleryDetailCache) {
            mGalleryDetailCache.evictAll();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            clearMemoryCache();
        }
    }

    private void debugPrint() {
        new Runnable() {
            @Override
            public void run() {
                if (DEBUG_PRINT_NATIVE_MEMORY) {
                    Log.i(TAG, "Native memory: " + FileUtils.humanReadableByteCount(
                            Debug.getNativeHeapAllocatedSize(), false));
                }
                if (DEBUG_PRINT_IMAGE_COUNT) {
                    Log.i(TAG, "Image count: " + Image.getImageCount());
                }
                SimpleHandler.getInstance().postDelayed(this, DEBUG_PRINT_INTERVAL);
            }
        }.run();
    }

    public int putGlobalStuff(@NonNull Object o) {
        int id = mIdGenerator.nextId();
        mGlobalStuffMap.put(id, o);
        Settings.putInt(KEY_GLOBAL_STUFF_NEXT_ID, mIdGenerator.nextId());
        return id;
    }

    public boolean containGlobalStuff(int id) {
        return mGlobalStuffMap.containsKey(id);
    }

    public Object getGlobalStuff(int id) {
        return mGlobalStuffMap.get(id);
    }

    public Object removeGlobalStuff(int id) {
        return mGlobalStuffMap.remove(id);
    }

    public boolean removeGlobalStuff(Object o) {
        return mGlobalStuffMap.values().removeAll(Collections.singleton(o));
    }

    public static EhCookieStore getEhCookieStore(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mEhCookieStore == null) {
            application.mEhCookieStore = new EhCookieStore(context);
        }
        return application.mEhCookieStore;
    }

    @NonNull
    public static SkyClient getEhClient(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mSkyClient == null) {
            application.mSkyClient = new SkyClient(application);
        }
        return application.mSkyClient;
    }

    @NonNull
    public static SkyProxySelector getEhProxySelector(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mSkyProxySelector == null) {
            application.mSkyProxySelector = new SkyProxySelector();
        }
        return application.mSkyProxySelector;
    }

    @NonNull
    public static OkHttpClient getOkHttpClient(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mOkHttpClient == null) {
            application.mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .cookieJar(getEhCookieStore(application))
                    .dns(new EhDns(application))
                    .proxySelector(getEhProxySelector(application))
                    .build();
        }
        return application.mOkHttpClient;
    }

    @NonNull
    public static ImageBitmapHelper getImageBitmapHelper(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mImageBitmapHelper == null) {
            application.mImageBitmapHelper = new ImageBitmapHelper();
        }
        return application.mImageBitmapHelper;
    }

    private static int getMemoryCacheMaxSize() {
        return Math.min(20 * 1024 * 1024, (int) OSUtils.getAppMaxMemory());
    }

    @NonNull
    public static Conaco<ImageBitmap> getConaco(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mConaco == null) {
            Conaco.Builder<ImageBitmap> builder = new Conaco.Builder<>();
            builder.hasMemoryCache = true;
            builder.memoryCacheMaxSize = getMemoryCacheMaxSize();
            builder.hasDiskCache = true;
            builder.diskCacheDir = new File(context.getCacheDir(), "thumb");
            builder.diskCacheMaxSize = 80 * 1024 * 1024; // 80MB
            builder.okHttpClient = getOkHttpClient(context);
            builder.objectHelper = getImageBitmapHelper(context);
            builder.debug = DEBUG_CONACO;
            application.mConaco = builder.build();
        }
        return application.mConaco;
    }

    @NonNull
    public static LruCache<Long, GalleryDetail> getGalleryDetailCache(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mGalleryDetailCache == null) {
            // Max size 25, 3 min timeout
            application.mGalleryDetailCache = new LruCache<>(25);
            getFavouriteStatusRouter().addListener((gid, slot) -> {
                GalleryDetail gd = application.mGalleryDetailCache.get(gid);
                if (gd != null) {
                    gd.favoriteSlot = slot;
                }
            });
        }
        return application.mGalleryDetailCache;
    }

    @NonNull
    public static SimpleDiskCache getSpiderInfoCache(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (null == application.mSpiderInfoCache) {
            application.mSpiderInfoCache = new SimpleDiskCache(
                    new File(context.getCacheDir(), "spider_info"), 5 * 1024 * 1024); // 5M
        }
        return application.mSpiderInfoCache;
    }

    @NonNull
    public static DownloadManager getDownloadManager() {
        return getDownloadManager(instance);
    }

    @NonNull
    public static DownloadManager getDownloadManager(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mDownloadManager == null) {
            application.mDownloadManager = new DownloadManager(application);
        }
        return application.mDownloadManager;
    }

    @NonNull
    public static Hosts getHosts(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mHosts == null) {
            application.mHosts = new Hosts(application, "hosts.db");
        }
        return application.mHosts;
    }

    @NonNull
    public static FavouriteStatusRouter getFavouriteStatusRouter() {
        return getFavouriteStatusRouter(getInstance());
    }

    @NonNull
    public static FavouriteStatusRouter getFavouriteStatusRouter(@NonNull Context context) {
        SkyApplication application = ((SkyApplication) context.getApplicationContext());
        if (application.mFavouriteStatusRouter == null) {
            application.mFavouriteStatusRouter = new FavouriteStatusRouter();
        }
        return application.mFavouriteStatusRouter;
    }

    @NonNull
    public static String getDeveloperEmail() {
        return "ehviewersu$gmail.com".replace('$', '@');
    }

    public void registerActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void unregisterActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    @Nullable
    public Activity getTopActivity() {
        if (!mActivityList.isEmpty()) {
            return mActivityList.get(mActivityList.size() - 1);
        } else {
            return null;
        }
    }

    // Avoid crash on some "energy saving" devices
    @Override
    public ComponentName startService(Intent service) {
        try {
            return super.startService(service);
        } catch (Throwable t) {
            ExceptionUtils.throwIfFatal(t);
            return null;
        }
    }

    // Avoid crash on some "energy saving" devices
    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        try {
            return super.bindService(service, conn, flags);
        } catch (Throwable t) {
            ExceptionUtils.throwIfFatal(t);
            return false;
        }
    }

    // Avoid crash on some "energy saving" devices
    @Override
    public void unbindService(ServiceConnection conn) {
        try {
            super.unbindService(conn);
        } catch (Throwable t) {
            ExceptionUtils.throwIfFatal(t);
        }
    }
}
