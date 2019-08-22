/* Copyright (c) 2012 Google Inc.
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ccsky.util.SPUtil;
import com.gp.pay.util.IabHelper;
import com.gp.pay.util.IabResult;
import com.gp.pay.util.Inventory;
import com.gp.pay.util.Purchase;

import java.util.List;


/**
 * Example game using in-app billing version 3.
 *
 * Before attempting to run this sample, please read the README file. It
 * contains important information on how to set up this project.
 *
 * All the game-specific logic is implemented here in SkyPayActivity, while the
 * general-purpose boilerplate that can be reused in any app is provided in the
 * classes in the util/ subdirectory. When implementing your own application,
 * you can copy over util/*.java to make use of those utility classes.
 *
 * This game is a simple "driving" game where the player can buy gas
 * and drive. The car has a tank which stores gas. When the player purchases
 * gas, the tank fills up (1/4 tank at a time). When the player drives, the gas
 * in the tank diminishes (also 1/4 tank at a time).
 *
 * The user can also purchase a "premium upgrade" that gives them a red car
 * instead of the standard blue com.ssract.one (exciting!).
 *
 * The user can also purchase a subscription ("infinite gas") that allows them
 * to drive without using up any gas while that subscription is active.
 *
 * It's important to note the consumption mechanics for each item.
 *
 * PREMIUM: the item is purchased and NEVER consumed. So, after the original
 * purchase, the player will always own that item. The application knows to
 * display the red car instead of the blue com.ssract.one because it queries whether
 * the premium "item" is owned or not.
 *
 * INFINITE GAS: this is a subscription, and subscriptions can't be consumed.
 *
 * GAS: when gas is purchased, the "gas" item is then owned. We consume it
 * when we apply that item's effects to our app's world, which to us means
 * filling up 1/4 of the tank. This happens immediately after purchase!
 * It's at this point (and not when the user drives) that the "gas"
 * item is CONSUMED. Consumption should always happen when your game
 * world was safely updated to apply the effect of the purchase. So,
 * in an example scenario:
 *
 * BEFORE:      tank at 1/2
 * ON PURCHASE: tank at 1/2, "gas" item is owned
 * IMMEDIATELY: "gas" is consumed, tank goes to 3/4
 * AFTER:       tank at 3/4, "gas" item NOT owned any more
 *
 * Another important point to notice is that it may so happen that
 * the application crashed (or anything else happened) after the user
 * purchased the "gas" item, but before it was consumed. That's why,
 * on startup, we check if we own the "gas" item, and, if so,
 * we have to apply its effects to our world and consume it. This
 * is also very important!
 *
 * @author Bruno Oliveira (Google)
 */
public class SkyPayActivity extends AppCompatActivity {
    // Debug tag, for logging
    static final String TAG = "sky";

    public static final String oneDay = "com.sfinsh.1day";
    public static final String sevenDay = "com.sfinsh.7day";
    public static final String fifteenDay = "com.sfinsh.15day";
    public static final String monthDay = "com.sfinsh.30day";


    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtIUewonIsWJJF5IMQWmccAqOIKqn4cixqw77QQjTj7/m36gsm+UbvyGW7seLmyahQg1pR8p4zJ294/v6PQwdZLU+zKxCACo4rmUnfMh5u8UoEPfSDAd2Vmm+kzeD+uIOezGavn7iXR4J+ElWavtglublkkoIXWmdjtIO97oTtRG5JzBm6VkNb3MiaIk14IiMGSQ1fJbCUdqLXhgJAMApLbCJi63VCmLYY5SiF228y8mGo/A5DsDKzuCYv1NkfsvnKng5SZ5aTp9lEkGaJXSf5wAawalu8E9cw7OsH8MqUVxjlVBb8VLJpTAGFC84tc0qu+MHQFBo9a1B2Ps95fxe9QIDAQAB";


    // The helper object
    private IabHelper mHelper;

    private String currentSku;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with com.ssract.one
         * of their own and then fake messages from the server.
         */

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");

        showProgress();
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    dismissProgress();
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        findViewById(R.id.oneDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPay(oneDay);
            }
        });

        findViewById(R.id.sevenDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPay(sevenDay);
            }
        });

        findViewById(R.id.fifteenDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPay(fifteenDay);
            }
        });

        findViewById(R.id.amonthDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPay(monthDay);
            }
        });
    }


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                dismissProgress();
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

//            // Do we have the premium upgrade?
//            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
//            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
//            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
//
//            // Do we have the infinite gas plan?
//            Purchase infiniteGasPurchase = inventory.getPurchase(SKU_INFINITE_GAS);
//            mSubscribedToInfiniteGas = (infiniteGasPurchase != null &&
//                    verifyDeveloperPayload(infiniteGasPurchase));
//            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
//                        + " infinite gas subscription.");
//            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            List<Purchase> allPurchases = inventory.getAllPurchases();
            if (allPurchases != null && !allPurchases.isEmpty()){
                mHelper.consumeAsync(allPurchases, onConsumeMultiFinishedListener);
            }else {
                dismissProgress();
            }

        }
    };

    private String payload;

    // User clicked the "Buy Gas" button
    public void startPay(String sku) {

        // launch the purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener
        startLaunchPurchaseFlow();
        Log.d(TAG, "Launching purchase flow.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        payload = System.currentTimeMillis() + "";
        currentSku = sku;
        mHelper.launchPurchaseFlow(this, sku, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same com.ssract.one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on com.ssract.one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that com.ssract.one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    com.ssract.one who initiated the purchase flow (so that items purchased by the user on
         *    com.ssract.one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                payFinish(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                payFinish(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");
            SPUtil.saveSimpleInfo(SkyPayActivity.this,SkyConstant.SP_FILE_NAME, SkyConstant.SP_PAY_SKU_KEY,purchase.getSku());
            SPUtil.saveSimpleInfo(SkyPayActivity.this,SkyConstant.SP_FILE_NAME, SkyConstant.SP_PAY_DATE_KEY,System.currentTimeMillis() + "");

            if (purchase.getSku().equals(currentSku)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
//            else if (purchase.getSku().equals(SKU_PREMIUM)) {
//                // bought the premium upgrade!
//                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
//                alert("Thank you for upgrading to premium!");
//                mIsPremium = true;
//                updateUi();
//                payFinish(false);
//            }
//            else if (purchase.getSku().equals(SKU_INFINITE_GAS)) {
//                // bought the infinite gas subscription
//                Log.d(TAG, "Infinite gas subscription purchased.");
//                alert("Thank you for subscribing to infinite gas!");
//                mSubscribedToInfiniteGas = true;
//                mTank = TANK_MAX;
//                updateUi();
//                payFinish(false);
//            }
        }
    };


    IabHelper.OnConsumeMultiFinishedListener onConsumeMultiFinishedListener = new IabHelper.OnConsumeMultiFinishedListener() {
        @Override
        public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
            Log.d(TAG, "onConsumeMultiFinishedListener Consumption finished");
            dismissProgress();
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            payFinish(true);
            // We know this is the "gas" sku because it's the only com.ssract.one we consume,
            // so we don't check which sku was consumed. If you have more than com.ssract.one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");

            }
        }
    };

    private void startLaunchPurchaseFlow() {
        showProgress();
    }

    private void payFinish(boolean isSuccess) {
        currentSku = "";
        dismissProgress();
        if (isSuccess){
            AlertDialog alertDialog = new AlertDialog.Builder(SkyPayActivity.this).
                    setMessage("success").
                    setPositiveButton("ok",null).
                    create();
            alertDialog.show();
        }
    }


    private ProgressDialog progressDialog;
    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }

        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);

        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }


    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }



    void complain(String message) {
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

}
