package com.shengshi.rebate.config;

import com.shengshi.base.config.Key;

public class RebateKey extends Key {

    // ===========================================
    // 以下是Intent key
    // ===========================================
    public static final String KEY_INTENT_FROM_PARENT = "key_intent_from_parent";
    public static final String KEY_INTENT_REBATE_ITEM_ID = "key_intent_rebate_item_id";
    public static final String KEY_INTENT_HOME_ENTITY = "key_intent_home_entity";
    public static final String KEY_INTENT_DETAIL_ENTITY = "key_intent_detail_entity";
    public static final String KEY_INTENT_REFRESH_ORDER_FRAGMENT_FLAG = "key_intent_refresh_order_fragment_flag";
    public static final String KEY_INTENT_COMMENT_ENTITY = "key_intent_comment_entity";
    public static final String KEY_INTENT_ORDER_ENTITY = "key_intent_order_entity";
    public static final String KEY_INTENT_ORDER_ID = "key_intent_order_id";
    public static final String KEY_INTENT_IS_NEW_ORDER = "key_intent_is_new_order";//是否是新订单

    // ===========================================
    // 以下是SharedPreferences key
    // ===========================================
    public static final String KEY_CITY_CODE = "k_city_code";
    public static final String KEY_CITY_NAME = "k_city_name";
    public static final String KEY_IS_SUB_FLAG = "key_is_sub_flag";
    public static final String KEY_UFUN_USER_ID = "key_ufun_user_id";

    // ===========================================
    // 以下是Fragment tag
    // ===========================================
    public static final String TAG_REBATE_PAY_WAITTING_FRAGMENT = "tag_rebate_pay_waitting_fragment";
    public static final String TAG_REBATE_PAY_ORDER_FRAGMENT = "tag_rebate_pay_order_fragment";
    public static final String TAG_HEADER_REBATECARD_FRAGMENT = "tag_header_rebatecard_fragment";

    // ===========================================
    // 以下是StartActivity REQUEST_CODE
    // ===========================================
    public static final int REQUEST_CODE_REFRESH_USER = 100;


}
