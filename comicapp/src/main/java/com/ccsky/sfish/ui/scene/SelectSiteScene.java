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
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ccsky.sfish.R;
import com.ccsky.sfish.Settings;
import com.ccsky.sfish.client.SkyUrl;
import com.ccsky.sfish.ui.SkyMainActivity;
import com.hippo.yorozuya.ViewUtils;

public class SelectSiteScene extends SkySolidScene implements View.OnClickListener {

    private RadioGroup mRadioGroup;
    private View mOk;

    @Override
    public boolean needShowLeftDrawer() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView2(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scene_select_site, container, false);

        mRadioGroup = (RadioGroup) ViewUtils.$$(view, R.id.radio_group);
        mOk = ViewUtils.$$(view, R.id.ok);

        mOk.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startMain();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRadioGroup = null;
        mOk = null;
    }

    @Override
    public void onClick(View v) {
        SkyMainActivity activity = getActivity2();
        if (null == activity || null == mRadioGroup) {
            return;
        }

        if (v == mOk) {
            int id = mRadioGroup.getCheckedRadioButtonId();
//            switch (id) {
//                case R.id.site_e:
//                    Settings.putSelectSite(false);
//                    Settings.putGallerySite(SkyUrl.SITE_E);
//                    startSceneForCheckStep(CHECK_STEP_SELECT_SITE, getArguments());
//                    finish();
//                    break;
//                case R.id.site_ex:
//                    Settings.putSelectSite(false);
//                    Settings.putGallerySite(SkyUrl.SITE_EX);
//                    startSceneForCheckStep(CHECK_STEP_SELECT_SITE, getArguments());
//                    finish();
//                    break;
//                default:
//                    Toast.makeText(activity, R.string.no_select, Toast.LENGTH_SHORT).show();
//                    break;
//            }

            if (id == R.id.site_e){

                Settings.putSelectSite(false);
                Settings.putGallerySite(SkyUrl.SITE_E);
                startSceneForCheckStep(CHECK_STEP_SELECT_SITE, getArguments());
                finish();

            }else if (id == R.id.site_ex){

                Settings.putSelectSite(false);
                Settings.putGallerySite(SkyUrl.SITE_EX);
                startSceneForCheckStep(CHECK_STEP_SELECT_SITE, getArguments());
                finish();

            }else {
                Toast.makeText(activity, R.string.no_select, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startMain(){
        Settings.putSelectSite(false);
        Settings.putGallerySite(SkyUrl.SITE_E);
        startSceneForCheckStep(CHECK_STEP_SELECT_SITE, getArguments());
        finish();
    }
}
