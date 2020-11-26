package com.example.getuserinfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class MainActivity extends AppCompatActivity {

    TextView txtInfo;
    Button btnLogin, btnLogout;
    HuaweiIdAuthParams authParams;
    HuaweiIdAuthService service;
    HuaweiIdAuthResult result;

    private void init() {
        txtInfo = findViewById(R.id.txtInfo);
        btnLogin = findViewById(R.id.btnLogin);
        result = new HuaweiIdAuthResult();
        authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams();
        service = HuaweiIdAuthManager.getService(MainActivity.this, authParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.isSuccess()) {
                    txtInfo.setText(R.string.wait);
                    btnLogin.setEnabled(false);

                    service.signOut();
                    result = new HuaweiIdAuthResult();

                    txtInfo.setText(R.string.loggedout);
                    btnLogin.setText(R.string.login);
                    btnLogin.setEnabled(true);
                } else {
                    txtInfo.setText(R.string.wait);
                    btnLogin.setEnabled(false);

                    authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setId()
                            .setEmail()
                            .setAccessToken()
                            .createParams();
                    service = HuaweiIdAuthManager.getService(MainActivity.this, authParams);
                    startActivityForResult(service.getSignInIntent(), 8888);
                    btnLogin.setText(R.string.logout);
                    btnLogin.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Process the authorization result to obtain an ID token from AuthHuaweiId.
        super.onActivityResult(requestCode, resultCode, data);

        handleSignInResult(data);

        if (requestCode == 8888) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            AuthHuaweiId huaweiAccount;
            if (authHuaweiIdTask.isSuccessful()) {
                huaweiAccount = authHuaweiIdTask.getResult();
                txtInfo.setText("EMAIL\n" + huaweiAccount.getEmail());
                Log.i("TAG", "idToken:" + huaweiAccount.getIdToken());
            } else {
                txtInfo.setText("HATA\n" +((ApiException) authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }

    private void handleSignInResult(Intent data) {


        // Obtain the authorization response from the intent.
        result = HuaweiIdAuthAPIManager.HuaweiIdAuthAPIService.parseHuaweiIdFromIntent(data);
//        Log.d("TAG", "handleSignInResult status = " + result.getStatus() + ", result = " + result.isSuccess());


    }
}