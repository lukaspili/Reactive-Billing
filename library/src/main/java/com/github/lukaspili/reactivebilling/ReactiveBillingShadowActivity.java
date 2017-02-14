/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.lukaspili.reactivebilling;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

public class ReactiveBillingShadowActivity extends Activity {

    private static final int REQUEST_CODE = 1337;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReactiveBilling.log(null, "Shadow activity - on create");

        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onDestroy() {
        ReactiveBilling.log(null, "Shadow activity - on destroy");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        ReactiveBilling.log(null, "Shadow activity - handle intent");

        extras = intent.getBundleExtra("BUY_EXTRAS");
        PendingIntent buyIntent = intent.getParcelableExtra("BUY_INTENT");

        try {
            startIntentSenderForResult(buyIntent.getIntentSender(), REQUEST_CODE, new Intent(), 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            ReactiveBilling.log(e, "Shadow activity - cannot start buy intent");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (extras != null) {
            outState.putBundle("BUY_EXTRAS", extras);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        extras = savedInstanceState.getBundle("BUY_EXTRAS");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            return; // can it happen?
        }

        ReactiveBilling.log(null, "Shadow activity - on activity result");

        ReactiveBilling.getInstance(this).getPurchaseFlowService().onActivityResult(resultCode, data, extras);
        finish();
    }
}
