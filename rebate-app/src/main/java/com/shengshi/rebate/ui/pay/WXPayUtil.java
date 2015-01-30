package com.shengshi.rebate.ui.pay;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.MD5;
import com.shengshi.base.tools.ToastUtils;
import com.shengshi.base.widget.CustomProgressDialog;
import com.shengshi.rebate.bean.OrderResultEntity;
import com.shengshi.rebate.config.RebateConstants;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * <p>Title:       微信支付工具类
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-14
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class WXPayUtil {

    private static WXPayUtil instance;
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private static Object lock = new Object();
    private Activity mActivity;

    private boolean debug = false;

    private WXPayUtil(Activity activity) {
        this.mActivity = activity;
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(activity, RebateConstants.WX_APP_ID, false);
    }

    public static WXPayUtil getInstance(Activity activity) {
        synchronized (lock) {
            if (instance == null) {
                instance = new WXPayUtil(activity);
            }
            return instance;
        }
    }

    // 将该app注册到微信
    public void registerApp() {
        api.registerApp(RebateConstants.WX_APP_ID);
    }

    /**
     * 发起支付动作
     *
     * @param result
     */
    public void startPay(OrderResultEntity result) {
        if (!api.isWXAppInstalled()) {
            ToastUtils.showToast(mActivity, "请先安装微信才能支付", Toast.LENGTH_SHORT);
            return;
        }
        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (!isPaySupported) {
            ToastUtils.showToast(mActivity, "请先升级您的微信版本", Toast.LENGTH_SHORT);
            return;
        }
        if (debug) {
            new GetAccessTokenTask().execute();
        } else {
            sendPayReq(result);
        }
    }

    /**
     * 调用微信客户端支付
     *
     * @param result
     */
    private void sendPayReq(OrderResultEntity result) {
        if (result == null || result.data == null || TextUtils.isEmpty(result.data.orderSn)) {
            Log.i("订单支付实体OrderResultEntity 无效");
            return;
        }
        PayReq req = new PayReq();
        req.appId = result.data.wx_appid;
        req.partnerId = result.data.wx_partnerid;
        req.prepayId = result.data.wx_prepayid;
        req.nonceStr = result.data.wx_noncestr;
        req.timeStamp = result.data.wx_timestamp;
        req.packageValue = result.data.wx_package;
        req.sign = result.data.wx_sign;
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        api.sendReq(req);
    }

    //********************以下是客户端自己模拟生成订单号等调用起微信客户端参数****************************//

    private String orderSn = "";//订单号
    private String productName = "";//商品名称
    private int productPrices;//商品总支付价格，单位为分

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductPrices(int productPrices) {
        this.productPrices = productPrices;
    }

    /**
     * 获取access token
     */
    public class GetAccessTokenTask extends AsyncTask<Void, Void, GetAccessTokenResult> {

        CustomProgressDialog dialog;
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

        @Override
        protected void onPreExecute() {
            dialog = CustomProgressDialog.getDialog(mActivity);
            dialog.setMessage("正在获取access token...");
            dialog.show();
        }

        @Override
        protected GetAccessTokenResult doInBackground(Void... params) {
            GetAccessTokenResult result = new GetAccessTokenResult();
            String formatUrl = String.format(url, RebateConstants.WX_APP_ID,
                    RebateConstants.WX_APP_SECRET);
            Log.i("get access token, url = " + formatUrl);

            byte[] buf = WXUtil.httpGet(formatUrl);
            if (buf == null || buf.length == 0) {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }
            String content = new String(buf);
            result.parseFrom(content);
            return result;
        }

        @Override
        protected void onPostExecute(GetAccessTokenResult result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (result.localRetCode == LocalRetCode.ERR_OK) {
                Toast.makeText(mActivity, "获取access token成功", Toast.LENGTH_SHORT).show();
                Log.i("accessToken = " + result.accessToken);
                GetPrepayIdTask getPrepayId = new GetPrepayIdTask(result.accessToken);
                getPrepayId.execute();
            } else {
                Toast.makeText(mActivity,
                        String.format("获取access token失败，原因%s", result.localRetCode.name()),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, GetPrepayIdResult> {

        CustomProgressDialog dialog;
        private String accessToken;
        private String url;

        public GetPrepayIdTask(String accessToken) {
            this.accessToken = accessToken;
            this.url = "https://api.weixin.qq.com/pay/genprepay?access_token=%s";
        }

        @Override
        protected void onPreExecute() {
            dialog = CustomProgressDialog.getDialog(mActivity);
            dialog.setMessage("正在获取预支付订单..");
            dialog.show();
        }

        @Override
        protected GetPrepayIdResult doInBackground(Void... params) {
            String formatUrl = String.format(url, accessToken);
            String entity = genProductArgs();
            Log.d("url = " + formatUrl);
            Log.d("entity = " + entity);
            GetPrepayIdResult result = new GetPrepayIdResult();
            byte[] buf = WXUtil.httpPost(formatUrl, entity);
            if (buf == null || buf.length == 0) {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }
            String content = new String(buf);
            Log.i("content = " + content);
            result.parseFrom(content);
            return result;
        }

        @Override
        protected void onPostExecute(GetPrepayIdResult result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (result.localRetCode == LocalRetCode.ERR_OK) {
                Toast.makeText(mActivity, "获取prepayid成功", Toast.LENGTH_SHORT).show();
                sendPayReq(result);
            } else {
                Toast.makeText(mActivity,
                        String.format("获取prepayid失败，原因%s", result.localRetCode.name()),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private long timeStamp;
    /**
     * 32位内的随机串，防重发
     */
    private String nonceStr;
    /**
     * 订单详情
     */
    private String packageValue;

    private String genProductArgs() {
        JSONObject json = new JSONObject();
        try {
            json.put("appid", RebateConstants.WX_APP_ID);
            String traceId = getTraceId(); // traceId 由开发者自定义，非必须，可用于订单的查询与跟踪，建议根据支付用户信息生成此id
            json.put("traceid", traceId);
            nonceStr = genNonceStr();
            json.put("noncestr", nonceStr);

            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("bank_type", "WX"));
            packageParams.add(new BasicNameValuePair("body", productName));//商品名称信息
            packageParams.add(new BasicNameValuePair("fee_type", "1"));//费用类型，这里1为默认的人民币
            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
            packageParams.add(new BasicNameValuePair("notify_url", "http://weixin.qq.com"));//支付成功后将通知该地址
            //FIXME 测试写死值
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));//订单号，商户需要保证该字段对于本商户的唯一性
            packageParams.add(new BasicNameValuePair("partner", RebateConstants.WX_PARTNER_ID));//商户号
            //FIXME 测试写死值
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));//用户浏览器的ip，这个需要在前端获取。这里使用127.0.0.1测试值
            //FIXME 值是否正确？
            packageParams.add(new BasicNameValuePair("total_fee", String.valueOf(productPrices)));//支付金额，单位为分，必须整型
            packageValue = genPackage(packageParams);
            json.put("package", packageValue);

            timeStamp = genTimeStamp();
            json.put("timestamp", timeStamp);

            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", RebateConstants.WX_APP_ID));
            signParams.add(new BasicNameValuePair("appkey", RebateConstants.WX_APP_KEY));
            signParams.add(new BasicNameValuePair("noncestr", nonceStr));
            signParams.add(new BasicNameValuePair("package", packageValue));
            signParams.add(new BasicNameValuePair("timestamp", String.valueOf(timeStamp)));
            signParams.add(new BasicNameValuePair("traceid", traceId));
            json.put("app_signature", genSign(signParams));
            json.put("sign_method", "sha1");//加密方式，默认为 sha1
        } catch (Exception e) {
            Log.e("genProductArgs fail, ex = " + e.getMessage());
            return null;
        }

        return json.toString();
    }

    /**
     * 建议 traceid 字段包含用户信息及订单信息，方便后续对订单状态的查询和跟踪
     */
    private String getTraceId() {
        return AppHelper.getPackageName(mActivity) + "_" + orderSn + "crestxu_" + genTimeStamp();
    }

    /**
     * 注意：商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一
     */
    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * 生成订单详情 <br/>
     * package生成方法：
     * A)对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）后，使用URL 键值对的格
     * 式（即 key1=value1&key2=value2…）拼接成字符串 string1；
     * <p/>
     * B) 在string1 最后拼接上 key=partnerKey 得到stringSignTemp 字符串， 并对 stringSignTemp进行 md5 运算，
     * 再将得到的字符串所有字符转换为大写，得到 sign值signValue。
     * <p/>
     * C)对string1 中的所有键值对中的 value 进行 urlencode 转码，按照 a 步骤重新拼接成字符串，得到string2。
     * 对于js 前端程序，一定要使用函数 encodeURIComponent 进行urlencode编码（注意！进行urlencode 时要将空格转化为%20而不是+）。
     * <p/>
     * D)将sign=signValue 拼接到string1 后面得到最终的 package 字符串。
     *
     * @param params
     * @return
     */
    private String genPackage(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(RebateConstants.WX_PARTNER_KEY); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成
        // 进行md5摘要前，params内容为原始内容，未经过url encode处理
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return URLEncodedUtils.format(params, "utf-8") + "&sign=" + packageSign;
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private String genSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (; i < params.size() - 1; i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append(params.get(i).getName());
        sb.append('=');
        sb.append(params.get(i).getValue());

        String sha1 = WXUtil.sha1(sb.toString());
        Log.i("genSign, sha1 = " + sha1);
        return sha1;
    }

    /**
     * 调用微信客户端支付
     *
     * @param result
     */
    private void sendPayReq(GetPrepayIdResult result) {
        PayReq req = new PayReq();
        req.appId = RebateConstants.WX_APP_ID;
        req.partnerId = RebateConstants.WX_PARTNER_ID;
        req.prepayId = result.prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = String.valueOf(timeStamp);
        req.packageValue = "Sign=" + packageValue;

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("appkey", RebateConstants.WX_APP_KEY));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genSign(signParams);

        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        api.sendReq(req);
    }

    /**
     * **********************************支付用到的实体********************************************
     */

    private static enum LocalRetCode {
        ERR_OK, ERR_HTTP, ERR_JSON, ERR_OTHER
    }

    @SuppressWarnings("unused")
    private static class GetAccessTokenResult {

        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String accessToken;
        public int expiresIn;
        public int errCode;
        public String errMsg;

        public void parseFrom(String content) {
            if (content == null || content.length() <= 0) {
                Log.e("parseFrom fail, content is null");
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }
            try {
                JSONObject json = new JSONObject(content);
                if (json.has("access_token")) { // success case
                    accessToken = json.getString("access_token");
                    expiresIn = json.getInt("expires_in");
                    localRetCode = LocalRetCode.ERR_OK;
                } else {
                    errCode = json.getInt("errcode");
                    errMsg = json.getString("errmsg");
                    localRetCode = LocalRetCode.ERR_JSON;
                }
            } catch (Exception e) {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    @SuppressWarnings("unused")
    private static class GetPrepayIdResult {
        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String prepayId;
        public int errCode;
        public String errMsg;

        public void parseFrom(String content) {
            if (content == null || content.length() <= 0) {
                Log.e("parseFrom fail, content is null");
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }
            try {
                JSONObject json = new JSONObject(content);
                if (json.has("prepayid")) { // success case
                    prepayId = json.getString("prepayid");
                    localRetCode = LocalRetCode.ERR_OK;
                } else {
                    localRetCode = LocalRetCode.ERR_JSON;
                }
                errCode = json.getInt("errcode");
                errMsg = json.getString("errmsg");
            } catch (Exception e) {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

}
