package com.owncloud.android.ui.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
public class TencentLoginActivity extends Activity {
    private static Tencent mTencent = null;
    private static final String api = "get_simple_userinfo,list_album,upload_pic,add_album,list_photo";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTencent = Tencent.createInstance("100430223", this.getApplicationContext());
        mTencent.login(this, api, new TencentLoginListener());
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
        finish();
    }
    public static Tencent getTencent(){
        return mTencent;
    }
    private Activity getActivity(){
        return this;
    }
    private class TencentLoginListener implements IUiListener{
        public void onCancel() {
            mTencent = null;
            Toast.makeText(getActivity(), "取消登陆!", Toast.LENGTH_LONG).show();
        }
        public void onComplete(JSONObject json) {
            try {
                mTencent.setOpenId(json.getString("openid"));                
                Long expires_in = System.currentTimeMillis() + Long.parseLong(json.getString("expires_in")) * 1000;
                mTencent.setAccessToken(json.getString("access_token"), expires_in.toString());
                Toast.makeText(getActivity(), "登陆成功!", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Log.e(TencentLoginActivity.class.getName(), "json data error", e);
            }
        }

        public void onError(UiError arg0) {
            Toast.makeText(getActivity(), "登陆异常!", Toast.LENGTH_LONG).show();
        }
        
    }
}
