package com.chaojishipin.sarrs.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ibest.thirdparty.share.model.Constants;
import com.ibest.thirdparty.share.presenter.WXListener;
import com.ibest.thirdparty.share.presenter.WXListenerManager;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 该类只能放到应用包名下面，不能其他模块里面，否则不会被调用到。微信分享返回至app会创建该类
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

	// IWXAPI ?????app?????????openapi???
    private IWXAPI api;

	public static WXListener getListener() {
		return WXListenerManager.getListener();
	}

	public static void setListener(WXListener mListener) {
		WXListenerManager.setListener(mListener);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.entry);
        
        // ???WXAPIFactory?????????IWXAPI?????
    	api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, true);
		// 将该app注册到微信
		api.registerApp(Constants.WX_APP_ID);
		api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// ???????????????????????????????
	@Override
	public void onReq(BaseReq req) {

	}

	// ?????????????????????????????????????????
	@Override
	public void onResp(BaseResp resp) {
		WXListener mListener = WXListenerManager.getListener();

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			if (mListener != null)
			{
				mListener.onWXComplete(resp);
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			if (mListener != null)
			{
				mListener.onWXCancel(resp);
			}
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			if (mListener != null)
			{
				mListener.onWXFailed(resp);
			}
			break;
		default:

			break;
		}
		/**
		 * 销毁该类，否则该类一直存在没能显示分享前的页面
		 */
		finish();
	}

}