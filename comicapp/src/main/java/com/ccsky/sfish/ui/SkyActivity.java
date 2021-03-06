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

package com.ccsky.sfish.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;

import com.ccsky.content.ContextLocalWrapper;
import com.ccsky.sfish.Analytics;
import com.ccsky.sfish.R;
import com.ccsky.sfish.Settings;
import com.ccsky.sfish.SkyApplication;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hippo.android.resource.AttrResources;

import java.util.Locale;

public abstract class SkyActivity extends AppCompatActivity {

    @StyleRes
    protected abstract int getThemeResId(int theme);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(getThemeResId(Settings.getTheme()));

        super.onCreate(savedInstanceState);

        ((SkyApplication) getApplication()).registerActivity(this);

        if (Analytics.isEnabled()) {
            FirebaseAnalytics.getInstance(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Settings.getApplyNavBarThemeColor()) {
            getWindow().setNavigationBarColor(AttrResources.getAttrColor(this, R.attr.colorPrimaryDark));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ((SkyApplication) getApplication()).unregisterActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(Settings.getEnabledSecurity()){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale locale = null;
        String language = Settings.getAppLanguage();
        if (language != null && !language.equals("system")) {
            String[] split = language.split("-");
            if (split.length == 1) {
                locale = new Locale(split[0]);
            } else if (split.length == 2) {
                locale = new Locale(split[0], split[1]);
            } else if (split.length == 3) {
                locale = new Locale(split[0], split[1], split[2]);
            }
        }

        if (locale != null) {
            newBase = ContextLocalWrapper.wrap(newBase, locale);
        }

        super.attachBaseContext(newBase);
    }
}
