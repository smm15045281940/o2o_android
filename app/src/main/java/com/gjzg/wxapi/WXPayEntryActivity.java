package com.gjzg.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.gjzg.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import config.AppConfig;
import utils.Utils;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private TextView resultTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_pay_result);
        resultTv = (TextView) findViewById(R.id.tv_result);
        api = WXAPIFactory.createWXAPI(this, AppConfig.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Utils.log(WXPayEntryActivity.this, "onPayFinish, errCode\n" + resp.errCode);
        Utils.log(WXPayEntryActivity.this, "onPayFinish,errStr\n" + resp.errStr);
        switch (resp.errCode) {
            case 0:
                Utils.toast(WXPayEntryActivity.this, "成功");
                resultTv.setText("成功");
                break;
            case -1:
                Utils.toast(WXPayEntryActivity.this, "错误");
                resultTv.setText("错误");
                break;
            case -2:
                Utils.toast(WXPayEntryActivity.this, "用户取消");
                resultTv.setText("用户取消");
                break;
        }
//		Log.e(T, "onPayFinish, errCode = " + resp.errCode);
//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
//		}
    }
}