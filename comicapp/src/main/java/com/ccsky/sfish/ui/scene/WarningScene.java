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

package com.ccsky.sfish.ui.scene;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.ccsky.sfish.R;
import com.ccsky.sfish.Settings;
import com.ccsky.sfish.ui.SkyMainActivity;
import com.ccsky.sfish.ui.annotation.ViewLifeCircle;
import com.hippo.yorozuya.ViewUtils;

public final class WarningScene extends SkySolidScene implements View.OnClickListener {

    @Nullable
    private View mReject;
    @Nullable
    @ViewLifeCircle
    private View mAccept;

    @Override
    public boolean needShowLeftDrawer() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView2(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scene_warning, container, false);

        mReject = ViewUtils.$$(view, R.id.reject);
        mAccept = ViewUtils.$$(view, R.id.accept);

        mReject.setOnClickListener(this);
        mAccept.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mReject = null;
        mAccept = null;
    }

    @Override
    public void onClick(View v) {
        if (mReject == v) {
            finishStage();
        } else if (mAccept == v) {
            // Never show this warning anymore
            Settings.putShowWarning(false);

            // Start new scene and finish it self
            SkyMainActivity activity = getActivity2();
            if (null != activity) {
//                startSceneForCheckStep(CHECK_STEP_WARNING, getArguments());
                startSceneForCheckStep(CHECK_STEP_ANALYTICS, getArguments());
            }
            finish();
        }
    }
}
