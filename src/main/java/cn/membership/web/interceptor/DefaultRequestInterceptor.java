 
package cn.membership.web.interceptor;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 默认请求处理拦截器
 * 
 * @author zhongpeiliang
 * @version 0.0.1
 * @since       
 */
public class DefaultRequestInterceptor extends BaseRequestInterceptor {

    @Override
    protected Map<String, Field> getRequestParamFieldMap() {
        return null;
    }

    
}
