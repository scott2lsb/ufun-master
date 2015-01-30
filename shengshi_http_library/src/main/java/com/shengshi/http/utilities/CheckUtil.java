package com.shengshi.http.utilities;

import java.util.List;
import java.util.Map;

/**
 * <p>Title:     CheckUtil
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-9-30
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class CheckUtil {

    public static boolean isValidate(String content) {
        if (content != null && !"".equals(content.trim())) {
            return true;
        }
        return false;
    }

    public static boolean isValidate(List<?> list) {
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    public static boolean isValidate(Map<?, ?> map) {
        if (map != null && map.size() > 0) {
            return true;
        }
        return false;
    }
}
