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

package com.ccsky.sfish.client;

import android.content.Context;
import android.os.AsyncTask;

import com.ccsky.sfish.client.exception.CancelledException;
import com.ccsky.sfish.SkyApplication;
import com.ccsky.util.ExceptionUtils;
import com.ccsky.util.IoThreadPoolExecutor;
import com.hippo.yorozuya.SimpleHandler;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.Call;
import okhttp3.OkHttpClient;

public class SkyClient {

    public static final String TAG = SkyClient.class.getSimpleName();

    public static final int METHOD_SIGN_IN = 0;
    public static final int METHOD_GET_GALLERY_LIST = 1;
    public static final int METHOD_GET_GALLERY_DETAIL = 3;
    public static final int METHOD_GET_PREVIEW_SET = 4;
    public static final int METHOD_GET_RATE_GALLERY = 5;
    public static final int METHOD_GET_COMMENT_GALLERY = 6;
    public static final int METHOD_GET_GALLERY_TOKEN = 7;
    public static final int METHOD_GET_FAVORITES = 8;
    public static final int METHOD_ADD_FAVORITES = 9;
    public static final int METHOD_ADD_FAVORITES_RANGE = 10;
    public static final int METHOD_MODIFY_FAVORITES = 11;
    public static final int METHOD_GET_TORRENT_LIST = 12;
    public static final int METHOD_GET_PROFILE = 14;
    public static final int METHOD_VOTE_COMMENT = 15;
    public static final int METHOD_IMAGE_SEARCH = 16;
    public static final int METHOD_ARCHIVE_LIST = 17;
    public static final int METHOD_DOWNLOAD_ARCHIVE = 18;

    private final ThreadPoolExecutor mRequestThreadPool;
    private final OkHttpClient mOkHttpClient;

    public SkyClient(Context context) {
        mRequestThreadPool = IoThreadPoolExecutor.getInstance();
        mOkHttpClient = SkyApplication.getOkHttpClient(context);
    }

    public void execute(EhRequest request) {
        if (!request.isCancelled()) {
            Task task = new Task(request.getMethod(), request.getCallback(), request.getEhConfig());
            task.executeOnExecutor(mRequestThreadPool, request.getArgs());
            request.task = task;
        } else {
            request.getCallback().onCancel();
        }
    }

    public class Task extends AsyncTask<Object, Void, Object> {

        private final int mMethod;
        private Callback mCallback;
        private SkyConfig mSkyConfig;

        private final AtomicReference<Call> mCall = new AtomicReference<>();
        private final AtomicBoolean mStop = new AtomicBoolean();

        public Task(int method, Callback callback, SkyConfig skyConfig) {
            mMethod = method;
            mCallback = callback;
            mSkyConfig = skyConfig;
        }

        // Called in Job thread
        public void setCall(Call call) throws CancelledException {
            if (mStop.get()) {
                // Stopped Job thread
                throw new CancelledException();
            } else {
                mCall.lazySet(call);
            }
        }

        public SkyConfig getEhConfig() {
            return mSkyConfig;
        }

        public void stop() {
            if (!mStop.get()) {
                mStop.lazySet(true);

                if (mCallback != null) {
                    // TODO Avoid new runnable
                    final Callback finalCallback = mCallback;
                    SimpleHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            finalCallback.onCancel();
                        }
                    });
                }

                Status status = getStatus();
                if (status == Status.PENDING) {
                    cancel(false);
                } else if (status == Status.RUNNING) {
                    // It is running, cancel call if it is created
                    Call call = mCall.get();
                    if (call != null) {
                        call.cancel();
                    }
                }

                // Clear
                mCallback = null;
                mSkyConfig = null;
                mCall.lazySet(null);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Object doInBackground(Object... params) {
            try {
                switch (mMethod) {
                    case METHOD_SIGN_IN:
                        return SkyEngine.signIn(this, mOkHttpClient, (String) params[0], (String) params[1], (String) params[2], (String) params[3]);
                    case METHOD_GET_GALLERY_LIST:
                        return SkyEngine.getGalleryList(this, mOkHttpClient, (String) params[0]);
                    case METHOD_GET_GALLERY_DETAIL:
                        return SkyEngine.getGalleryDetail(this, mOkHttpClient, (String) params[0]);
                    case METHOD_GET_PREVIEW_SET:
                        return SkyEngine.getPreviewSet(this, mOkHttpClient, (String) params[0]);
                    case METHOD_GET_RATE_GALLERY:
                        return SkyEngine.rateGallery(this, mOkHttpClient, (Long) params[0], (String) params[1], (Long) params[2], (String) params[3], (Float) params[4]);
                    case METHOD_GET_COMMENT_GALLERY:
                        return SkyEngine.commentGallery(this, mOkHttpClient, (String) params[0], (String) params[1]);
                    case METHOD_GET_GALLERY_TOKEN:
                        return SkyEngine.getGalleryToken(this, mOkHttpClient, (Long) params[0], (String) params[1], (Integer) params[2]);
                    case METHOD_GET_FAVORITES:
                        return SkyEngine.getFavorites(this, mOkHttpClient, (String) params[0], (Boolean) params[1]);
                    case METHOD_ADD_FAVORITES:
                        return SkyEngine.addFavorites(this, mOkHttpClient, (Long) params[0], (String) params[1], (Integer) params[2], (String) params[3]);
                    case METHOD_ADD_FAVORITES_RANGE:
                        return SkyEngine.addFavoritesRange(this, mOkHttpClient, (long[]) params[0], (String[]) params[1], (Integer) params[2]);
                    case METHOD_MODIFY_FAVORITES:
                        return SkyEngine.modifyFavorites(this, mOkHttpClient, (String) params[0], (long[]) params[1], (Integer) params[2], (Boolean) params[3]);
                    case METHOD_GET_TORRENT_LIST:
                        return SkyEngine.getTorrentList(this, mOkHttpClient, (String) params[0], (Long) params[1], (String) params[2]);
                    case METHOD_GET_PROFILE:
                        return SkyEngine.getProfile(this, mOkHttpClient);
                    case METHOD_VOTE_COMMENT:
                        return SkyEngine.voteComment(this, mOkHttpClient, (Long) params[0], (String) params[1], (Long) params[2], (String) params[3], (Long) params[4], (Integer) params[5]);
                    case METHOD_IMAGE_SEARCH:
                        return SkyEngine.imageSearch(this, mOkHttpClient, (File) params[0], (Boolean) params[1], (Boolean) params[2], (Boolean) params[3]);
                    case METHOD_ARCHIVE_LIST:
                        return SkyEngine.getArchiveList(this, mOkHttpClient, (String) params[0], (Long) params[1], (String) params[2]);
                    case METHOD_DOWNLOAD_ARCHIVE:
                        return SkyEngine.downloadArchive(this, mOkHttpClient, (Long) params[0], (String) params[1], (String) params[2], (String) params[3]);
                    default:
                        return new IllegalStateException("Can't detect method " + mMethod);
                }
            } catch (Throwable e) {
                ExceptionUtils.throwIfFatal(e);
                return e;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Object result) {
            if (mCallback != null) {
                //noinspection StatementWithEmptyBody
                if (!(result instanceof CancelledException)) {
                    if (result instanceof Exception) {
                        mCallback.onFailure((Exception) result);
                    } else {
                        mCallback.onSuccess(result);
                    }
                } else {
                    // onCancel is called in stop
                }
            }

            // Clear
            mCallback = null;
            mSkyConfig = null;
            mCall.lazySet(null);
        }
    }

    public interface Callback<E> {

        void onSuccess(E result);

        void onFailure(Exception e);

        void onCancel();
    }
}
