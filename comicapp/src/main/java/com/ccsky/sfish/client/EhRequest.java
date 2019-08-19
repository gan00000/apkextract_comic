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

import com.ccsky.sfish.Settings;

public class EhRequest {

    private int mMethod;
    private Object[] mArgs;
    private SkyClient.Callback mCallback;
    private SkyConfig mSkyConfig;

    SkyClient.Task task;

    private boolean mCancel = false;

    public EhRequest setMethod(int method) {
        mMethod = method;
        return this;
    }

    public EhRequest setArgs(Object... args) {
        mArgs = args;
        return this;
    }

    public EhRequest setCallback(SkyClient.Callback callback) {
        mCallback = callback;
        return this;
    }

    public EhRequest setEhConfig(SkyConfig skyConfig) {
        mSkyConfig = skyConfig;
        return this;
    }

    public int getMethod() {
        return mMethod;
    }

    public Object[] getArgs() {
        return mArgs;
    }

    public SkyClient.Callback getCallback() {
        return mCallback;
    }

    public SkyConfig getEhConfig() {
        return mSkyConfig != null ? mSkyConfig : Settings.getEhConfig();
    }

    public void cancel() {
        if (!mCancel) {
            mCancel = true;
            if (task != null) {
                task.stop();
                task = null;
            }
        }
    }

    public boolean isCancelled() {
        return mCancel;
    }
}
