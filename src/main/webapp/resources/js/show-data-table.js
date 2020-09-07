var validUrlRegExp = new RegExp(/^http/);
var validLikeQueryExp = new RegExp(/(^(\*))|((\*)$)/);

function urlencode (str) {  
    str = (str + '').toString();   

    return encodeURIComponent(str).replace(/!/g, '%21').replace(/'/g, '%27').replace(/\(/g, '%28').replace(/\)/g, '%29').replace(/\*/g, '%2A').replace(/%20/g, '+');  
} 

function showLoadingProgressBar() {
    $('#loading-progress-bar').css('left', ($('#loading-progress-bar').parent().width() - 39) / 2).css('top', ($(window).height() - 39) / 2).show();
}

function hideLoadingProgressBar() {
    $('#loading-progress-bar').hide();
}


$(document).ready(function() {
	
	$('.record-detail-trigger').button('reset').removeAttr("disabled");
	$('.record-detail-trigger').button('reset').removeAttr("disabled");
	$('.record-enable-trigger').button('reset').removeAttr("disabled");
	$('.record-freeze-trigger').button('reset').removeAttr("disabled");
	$('.record-disable-trigger').button('reset').removeAttr("disabled");
	
    $('#back-btn').click(function(event) {
        event.preventDefault();
        $(this).button('loading');
        $.goBack();
    });	
	
	$('#checkbox-all').on('change', function() {
        var isAllChecked = $('#checkbox-all').is(':checked');
		$('[name=table-item-selecter]').prop('checked', isAllChecked);            
        if (isAllChecked) {
            $('#remove-btn').show();
        } else {
            $('#remove-btn').hide();
        }
	});  

	$('[name=table-item-selecter]').on('change', function() {
        if ($(this).is(':checked')) {
            $('#remove-btn').show();
        } else {
            if (!$('[name=table-item-selecter]').is(':checked')) {
                $('#remove-btn').hide();
            }
            $('#checkbox-all').prop('checked', false); 
        }
	}); 	
    
    $('#first-page-trigger, #previous-page-trigger, #next-page-trigger, #last-page-trigger').click(function(event) {
        event.preventDefault();
        var pageBaseUrl = $.trim($(this).attr('href'));
        if (pageBaseUrl && pageBaseUrl.length > 0 && validUrlRegExp.test(pageBaseUrl)) {
            var requestParamsObject = $.getRequestParamsObject();
            if (requestParamsObject && requestParamsObject.logic_mode) {
                window.location.href = pageBaseUrl;
            } else {
                window.location.href = pageBaseUrl;
            }
            
        } // if (pageBaseUrl && pageBaseUrl.length > 0 && validUrlRegExp.test(pageBaseUrl))
    });
    
    $('#current-page-trigger').click(function(event) {
        event.preventDefault();
        window.location.reload();
    });
    
    $('.record-detail-trigger').click(function (event) {
        event.preventDefault();
        var detailReqUrl= $(this).attr('data-detail-req-url');
        $(this).button('loading');
        if (detailReqUrl && detailReqUrl.length > 0) {
            window.location.href = detailReqUrl;
            $(this).button('reset');
        }
        
    });
    
    $('.record-enable-trigger').click(function (event) {
        event.preventDefault();
        var enableReqUrl= $(this).attr('data-enable-req-url');
        var recordEnableTrigger = $(this);
        recordEnableTrigger.button('loading');
        showLoadingProgressBar();
        if (enableReqUrl && enableReqUrl.length > 0 && validUrlRegExp.test(enableReqUrl)) {
            $.runAjaxRequest(enableReqUrl)
            .done(function(response) {

                if (response)
                {
                    if (0 == response.status) {
                        window.location.reload();
                    } else {
                    	recordEnableTrigger.button('reset'); 
                    }
                } // if (response)

            })
            .fail(function(response) {   
            	recordEnableTrigger.button('reset'); 
            });              
        }
        
    });    
    
    $('.record-freeze-trigger').click(function (event) {
        event.preventDefault();
        var freezeReqUrl= $(this).attr('data-freeze-req-url');
        var recordFreezeTrigger = $(this);
        recordFreezeTrigger.button('loading');
        showLoadingProgressBar();
        if (freezeReqUrl && freezeReqUrl.length > 0 && validUrlRegExp.test(freezeReqUrl)) {
            $.runAjaxRequest(freezeReqUrl)
            .done(function(response) {

                if (response)
                {
                    if (0 == response.status) {
                        window.location.reload();
                    } else {
                    	recordFreezeTrigger.button('reset'); 
                    }
                } // if (response)

            })
            .fail(function(response) {   
            	recordFreezeTrigger.button('reset'); 
            });              
        }
        
    });
    
    $('.record-disable-trigger').click(function (event) {
        event.preventDefault();
        var disableReqUrl= $(this).attr('data-disable-req-url');
        var recordDisableTrigger = $(this);
        recordDisableTrigger.button('loading');
        showLoadingProgressBar();
        if (disableReqUrl && disableReqUrl.length > 0 && validUrlRegExp.test(disableReqUrl)) {
            $.runAjaxRequest(disableReqUrl)
            .done(function(response) {

                if (response)
                {
                    if (0 == response.status) {
                        window.location.reload();
                    } else {
                    	recordDisableTrigger.button('reset'); 
                    }
                } // if (response)

            })
            .fail(function(response) {   
            	recordDisableTrigger.button('reset'); 
            });              
        }
        
    });    
    
    $('#remove-btn').click(function (event) {
        event.preventDefault();
        var removeReqUrl = $.trim($(this).attr('data-remove-req-url'));
        var removeBtn = $(this);
        removeBtn.button('loading');
        if (removeReqUrl && removeReqUrl.length > 0 && validUrlRegExp.test(removeReqUrl)) {
            var removeRecordIds = new Array();
            $('[name=table-item-selecter]:checked').each(function() {
                var recordId = $.trim($(this).attr('data-record-id'));
                if (recordId && recordId.length > 0) {
                    removeRecordIds.push(recordId);
                } // if (recordId && recordId.length > 0)
            });  
            
            if (removeRecordIds.length > 0) {
                var requestParams = new Object();
                requestParams.id = removeRecordIds;
                $.runAjaxRequest(removeReqUrl, requestParams)
                .done(function(response) {

                    if (response)
                    {
                        if (0 == response.status) {
                            window.location.reload();
                        } else {
                            removeBtn.button('reset'); 
                        }
                    } // if (response)

                })
                .fail(function(response) {   
                    removeBtn.button('reset'); 
                });                 
            } // if (removeRecordIds.length > 0)
        } // if (removeReqUrl && removeReqUrl.length > 0 && validUrlRegExp.test(removeReqUrl))       
        
    });

    $('.record-remove-trigger').click(function (event) {
        event.preventDefault();
        var removeReqUrl= $.trim($(this).attr('data-remove-req-url'));
        var recordRemoveTrigger = $(this);
        recordRemoveTrigger.button('loading');
        if (removeReqUrl && removeReqUrl.length > 0 && validUrlRegExp.test(removeReqUrl)) {
            $.runAjaxRequest(removeReqUrl)
            .done(function(response) {

                if (response)
                {
                    if (0 == response.status) {
                        window.location.reload();
                    } else {
                        recordRemoveTrigger.button('reset'); 
                    }
                } // if (response)

            })
            .fail(function(response) {   
                recordRemoveTrigger.button('reset'); 
            });              
        }
        
    });
        
});