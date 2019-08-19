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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextUtils;
import android.widget.Toast;

import com.ccsky.sfish.client.EhUrlOpener;
import com.ccsky.sfish.ui.SkyMainActivity;
import com.ccsky.sfish.R;
import com.ccsky.scene.Announcer;
import com.ccsky.scene.StageActivity;
import com.ccsky.util.ExceptionUtils;

import androidx.annotation.NonNull;

public final class UrlOpener {

    private UrlOpener() {
    }

    public static void openUrl(@NonNull Context context, String url, boolean ehUrl) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        Intent intent;
        Uri uri = Uri.parse(url);

        if (ehUrl) {
            Announcer announcer = EhUrlOpener.parseUrl(url);
            if (null != announcer) {
                intent = new Intent(context, SkyMainActivity.class);
                intent.setAction(StageActivity.ACTION_START_SCENE);
                intent.putExtra(StageActivity.KEY_SCENE_NAME, announcer.getClazz().getName());
                intent.putExtra(StageActivity.KEY_SCENE_ARGS, announcer.getArgs());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        }

        // Intent.ACTION_VIEW
        intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (Throwable e) {
            ExceptionUtils.throwIfFatal(e);
            Toast.makeText(context, R.string.error_cant_find_activity, Toast.LENGTH_SHORT).show();
        }
    }
}
