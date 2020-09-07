 
package cn.membership.web.constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebRequestConstant {

    public static final String STATUS_MODEL_MAP_KEY = "status";

    public static final int SUCCESS_STATUS = 0;

    public static final int EXCEPTION_STATUS = -2;

    public static final String MESSAGE_MODEL_MAP_KEY = "message";

    public static final String SERVER_TIME_MODEL_MAP_KEY = "serverTime";

    public static final String SUCCESS_MESSAGE = "成功！";

    public static final String EXCEPTION_MESSAGE = "异常！";

    public static final String REQUEST_BASE_URL_MODEL_MAP_KEY = "requestBaseUrl";
    
    public static final String REQUEST_URL_MODEL_MAP_KEY = "requestUrl";
    
    public static final String REQUEST_QUERY_STRING_MODEL_MAP_KEY = "requestQueryString";

    public static final String REQUEST_PARAMS_MODEL_MAP_KEY = "requestParams";

    public static final String RANDOM_REQUEST_PARAM_KEY = "random";
    
    public static final String DEBUG_REQUEST_PARAM_KEY = "debug";

    public static final boolean DEFAULT_RANDOM = false;
    
    public static final String OFFSET_REQUEST_PARAM_KEY = "offset";

    public static final int DEFAULT_OFFSET = 0;

    public static final String PAGE_SIZE_REQUEST_PARAM_KEY = "pageSize";

    public static final int DEFAULT_PAGE_SIZE = 10;
    
    public static final String RESULT_MODEL_MAP_KEY = "result";
    
    public static final String TOTAL_RESULT_MAP_KEY = "total";

    public static final String FROM_RESULT_MAP_KEY = "from";

    public static final String TO_RESULT_MAP_KEY = "to";
    
    public static final String TO_BE_CONTINUED_RESULT_MAP_KEY = "toBeContinued";
    
    public static final String AFFECTED_ROWS_COUNT_RESULT_MAP_KEY = "affectedRowsCount";

    public static final int DEFAULT_AFFECTED_ROWS_COUNT = 0;

    public static final String VALUE_FIELD_LIST_REQUEST_PARAM_KEY = "valueFieldList";

    public static final String WHERE_FIELD_LIST_REQUEST_PARAM_KEY = "whereFieldList";

    public static final String LIKE_FIELD_LIST_REQUEST_PARAM_KEY = "likeFieldList";

    public static final String RANGE_FIELD_LIST_REQUEST_PARAM_KEY = "rangeFieldList";

    public static final String GROUP_FIELD_LIST_REQUEST_PARAM_KEY = "groupFieldList";

    public static final String SORT_FIELD_LIST_REQUEST_PARAM_KEY = "sortFieldList";

    public static final String ALL_ARG_MAP_REQUEST_ATTR_KEY = "allArgMap";

    public static final String VALUE_ARG_MAP_REQUEST_ATTR_KEY = "valueArgMap";

    public static final String WHERE_ARG_MAP_REQUEST_ATTR_KEY = "whereArgMap";

    public static final String LIKE_ARG_MAP_REQUEST_ATTR_KEY = "likeArgMap";

    public static final String RANGE_ARG_MAP_REQUEST_ATTR_KEY = "rangeArgMap";

    public static final String GROUP_ARG_LIST_REQUEST_ATTR_KEY = "groupArgList";

    public static final String SORT_ARG_MAP_REQUEST_ATTR_KEY = "sortArgMap";
    
    public static final String FILE_ARG_MAP_REQUEST_ATTR_KEY = "fileArgMap";
    
    public static final String REQUEST_ARG_MAP_REQUEST_ATTR_KEY = "requestArgMap";
    
    public static final List<String> ARG_REQUEST_ATTR_KEY_LIST = new ArrayList<String>();

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static final SimpleDateFormat DEFAULT_DATETIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    
    public static final Map<String, String> HOST = new HashMap<String, String>();    
    
    static {
        ARG_REQUEST_ATTR_KEY_LIST.add(ALL_ARG_MAP_REQUEST_ATTR_KEY);
        ARG_REQUEST_ATTR_KEY_LIST.add(VALUE_ARG_MAP_REQUEST_ATTR_KEY);
        ARG_REQUEST_ATTR_KEY_LIST.add(WHERE_ARG_MAP_REQUEST_ATTR_KEY);
        ARG_REQUEST_ATTR_KEY_LIST.add(LIKE_ARG_MAP_REQUEST_ATTR_KEY);
        ARG_REQUEST_ATTR_KEY_LIST.add(RANGE_ARG_MAP_REQUEST_ATTR_KEY);
        ARG_REQUEST_ATTR_KEY_LIST.add(GROUP_ARG_LIST_REQUEST_ATTR_KEY);
        ARG_REQUEST_ATTR_KEY_LIST.add(SORT_ARG_MAP_REQUEST_ATTR_KEY);
        ARG_REQUEST_ATTR_KEY_LIST.add(FILE_ARG_MAP_REQUEST_ATTR_KEY);
    }
}
