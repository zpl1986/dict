(function($) {
	
    $.runAjaxRequest = function(path, params, contextObject) {
        if (!contextObject) {
            contextObject = document.body;
        } // if (!contextObject)
        return $.ajax({
            type : "post",
            async:true,
            url : path,
            data : params,
            //dataType : "jsonp",
            //jsonp: "callback",
            //传递给请求处理程序或页面的，用以获得jsonp回调函数名的参数名(默认为:callback)
            // jsonpCallback:"chutong_" + Math.floor(Math.random() * 6),
            //自定义的jsonp回调函数名称，默认为jQuery自动生成的随机函数名
            context: contextObject,
            success : function(response){
            	//alert("."+response.message+".");
                // if ($.isFunction(callback)) {
                    // callback(response);
                // }                
            },
            error:function(response){
            	//alert("."+response.message+".");
                // if ($.isFunction(callback)) {
                    // callback(response);
                // }                
            }
        });  
        
    };

    $.getParamsObject = function (paramsString) {
        var paramsObject = new Object();
    
        if (paramsString.length > 0) {
            var paramArray = paramsString.split(/&|=/);
            
            if (paramArray.length >= 2) {
                for (var i = 0; i + 1 < paramArray.length; i = i + 2) {
                    paramsObject[paramArray[i]] = decodeURI(paramArray[i+1]);
                } // for (var i = 0; i + 1 < paramArray.length; i = i + 2)               
            } // if (paramArray.length >= 2)            
        }
        
        return paramsObject;
    };

    $.getRequestParamsObject = function(extraRequestParams) {
        var requestParams;

        if (window.location.search.length > 1) {
            requestParams = $.getParamsObject(window.location.search.substr(1));
        } // if (window.location.search.length > 1) 

        if (!requestParams) {
            requestParams = new Object();
        } // if (!requestParams)  

        if (extraRequestParams) {
            for (var key in extraRequestParams) {
                requestParams[key] = extraRequestParams[key];
            } // for (var key in extraRequestParams)               
        } // if (extraRequestParams)        
        
        return requestParams;
    };
    
    $.goBack = function(targetURL) {
        // if (!targetURL) {
            // targetURL = '';
        // }
        
        // window.location.href = document.referrer || targetURL;
        if (targetURL) {
            window.location.href = targetURL;
        } else {
            history.go(-1);
        }
    };       
    
})(jQuery);