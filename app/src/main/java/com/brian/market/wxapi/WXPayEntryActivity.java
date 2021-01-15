package com.brian.market.wxapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.brian.market.App;
import com.brian.market.R;
import com.brian.market.payment.StripePayment;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public abstract class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private IWXAPI iwxAPI;
    private String code = "";
    private BaseResp resp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iwxAPI = WXAPIFactory.createWXAPI(WXPayEntryActivity.this, getString(R.string.wechat_app_id), true);

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
            SendAuth.Resp authResp = new SendAuth.Resp();
            PayResp payResp = new PayResp();

            if(baseResp.equals(authResp)) {
               authResp = (SendAuth.Resp)baseResp;
                code = authResp.code;
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        wechatLoginSuccess();
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        finish();
                        break;
                }

            }else if(baseResp.equals(payResp)) {
                payResp = (PayResp)baseResp;
                switch (payResp.errCode) {
                    case 0:
                        Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                        wechatPaySuccess();
                        break;
                    case -1:
                        Toast.makeText(this, "Error during payment", Toast.LENGTH_SHORT).show();
                        break;
                    case -2:
                        Toast.makeText(this, "Unknown result", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

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

    public abstract void wechatLoginSuccess();
    public abstract void wechatPaySuccess();
}
