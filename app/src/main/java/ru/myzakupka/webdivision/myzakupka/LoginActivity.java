package ru.myzakupka.webdivision.myzakupka;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.util.VKUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private boolean isResumed = false;
    private HTTP http = new HTTP();

    private static final String[] vkScope = new String[]{
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.DOCS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                if (isResumed) {
                    switch (res) {
                        case LoggedIn:
                            showListOrders();
                            break;
                        case Pending:
                            break;
                        case Unknown:
                            break;
                    }
                }
            }

            @Override
            public void onError(VKError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        if (VKSdk.isLoggedIn()) {
            showListOrders();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;
        super.onPause();
    }

    private void showListOrders() {
        Intent intent = new Intent(this, ListOrdersActivity.class);
        startActivity(intent);
    }

    public void loginVK(View view) {
        VKSdk.login(this, vkScope);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final String devicIMEI = telephonyManager.getDeviceId();
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(final VKAccessToken res) {
                new AsyncTask<Void, String, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        String s = "";
                        try {
                            String sig = LoginActivity.md5("expire=" + res.expiresIn + "&mid=" + res.userId + "&secret=" + res.secret + "&sid=" + res.accessToken);
                            http.get("https://api.vmeste.market/android/notifications?device_token=" + devicIMEI + "&expire=" + res.expiresIn + "&mid=" + res.userId + "&secret=" + res.secret + "&sid=" + res.accessToken + "&sig=" + sig);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return s;
                    }

                    @Override
                    protected void onPostExecute(final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(result);
                            }
                        });
                    }
                }.execute();
            }

            @Override
            public void onError(VKError error) {

            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
