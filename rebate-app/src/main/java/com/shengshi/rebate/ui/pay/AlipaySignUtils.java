package com.shengshi.rebate.ui.pay;

import com.shengshi.base.tools.Log;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class AlipaySignUtils {

    private static final String ALGORITHM = "RSA";

    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String sign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(AlipayBase64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(ALGORITHM, "BC");//多加一个"BC"参数，适配4.1.0之后手机
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));

            byte[] signed = signature.sign();

            return AlipayBase64.encode(signed);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }

        return null;
    }

}
