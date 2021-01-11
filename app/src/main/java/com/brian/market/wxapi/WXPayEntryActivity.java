package com.brian.market.wxapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.brian.market.App;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private IWXAPI iwxAPI;
    private String code = "";
    private BaseResp resp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = new App();
        iwxAPI = app.iwxAPI;

    }

    private void observeError() {
    }

    private void observeSuccess() {

    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp != null) {
            resp = baseResp;
            SendAuth.Resp temp = (SendAuth.Resp)baseResp;
            code = temp.code;
        }
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                break;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        setIntent(intent);
        iwxAPI.handleIntent(intent, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        iwxAPI.handleIntent(getIntent(), this);

        observeSuccess();
        observeError();
    }
}
