/*
Copyright 2016-2017 Pieter Pareit

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */
package be.ppareit.swiftp.locale;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractAppCompatPluginActivity;


import be.ppareit.swiftp.FsSettings;
import be.ppareit.swiftp.R;

import static be.ppareit.swiftp.locale.SettingsBundleHelper.BUNDLE_BOOLEAN_RUNNING;
import static be.ppareit.swiftp.locale.SettingsBundleHelper.generateBundle;
import static be.ppareit.swiftp.locale.SettingsBundleHelper.getBundleRunningState;

/**
 * Created by ppareit on 29/04/16.
 */
public class EditActivity extends AbstractAppCompatPluginActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(FsSettings.getTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.locale_edit_layout);

        CharSequence callingApplicationLabel = null;
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(getCallingPackage(), 0);
            callingApplicationLabel = pm.getApplicationLabel(ai);
        } catch (final PackageManager.NameNotFoundException e) {
            Log.e("swiftp","Calling package couldn't be found%s", e); //$NON-NLS-1$
        }
        if (null != callingApplicationLabel) {
            setTitle(callingApplicationLabel);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(R.string.swiftp_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isBundleValid(@NonNull Bundle bundle) {
        return SettingsBundleHelper.isBundleValid(bundle);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull Bundle previousBundle,
                                               @NonNull String previousBlurb) {
        if (!isBundleValid(previousBundle)) {
            Log.e("swiftp","Invalid bundle received, repairing to default");
            previousBundle = generateBundle(this, false);
        }
        boolean running = previousBundle.getBoolean(BUNDLE_BOOLEAN_RUNNING);
        RadioButton radioButton =
                (RadioButton) findViewById(running ? R.id.radio_server_running :
                                R.id.radio_server_stopped);
        radioButton.setChecked(true);
    }

    @Nullable
    @Override
    public Bundle getResultBundle() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_server_state_group);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        boolean running = (checkedId == R.id.radio_server_running);

        return generateBundle(this, running);
    }

    @NonNull
    @Override
    public String getResultBlurb(@NonNull Bundle bundle) {
        boolean running = getBundleRunningState(bundle);
        return running ? "Running" : "Stopped";
    }
}
