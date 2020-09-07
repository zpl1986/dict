var validUrlRegExp = new RegExp(/^http/);

$(document).ready(function() {

    $('input[type="text"], textarea').change(function(event) {
        $(this).siblings('.field-value-container').children('span').text($(this).val());
        $(this).parent('.input-group').siblings('.field-value-container').children('span').text($(this).val());
    });  
    
    $('input[type="checkbox"]').change(function(event) {
    	
    	var checkboxFieldValueText = '';
    	
    	$('input[type="checkbox"][name="' + $(this).attr('name') +'"]').filter(':checked').each(function() {
            var labelText = $(this).attr('data-label-text');
            
            if (0 != checkboxFieldValueText.length) {
            	checkboxFieldValueText += '<br/>';
            } // if (0 != checkboxFieldValueText.length)
            
            checkboxFieldValueText += labelText;
        });
    	
    	$(this).parent().parent().siblings('.field-value-container').children('span').html(checkboxFieldValueText);
    });    
    
    $('select').change(function(event) {
        $(this).siblings('.field-value-container').children('span').text($(this).find("option:selected").text());
    });        
    
    $('#back-btn').click(function(event) {
        event.preventDefault();
        $(this).button('loading');
        $.goBack();
    });
    
    
    $('#cancel-btn').click(function(event) {
        event.preventDefault();
        $(this).button('loading');
        $.goBack();
    });
    
    $('#save-btn').click(function(event) {
        event.preventDefault();
        
        $('#save-btn').button('loading');
        $('#create-data-form').submit();
    });    
    
    $('#create-data-form').submit(function(event) {
        event.preventDefault();
        // var formValues = $(this).serialize();
        var formValues = new Object();
        
        $('input.form-control, select.form-control, textarea.form-control').each(function() {
            var fieldValue = $(this).val();
            var fieldName = $(this).attr('name'); 

            if (fieldValue && $.trim(fieldValue).length > 0 && fieldName && $.trim(fieldName).length > 0) {
                if ($(this).is('.field-type-percent'))
                {
                    fieldValue /= 100;
                }
                fieldName = $.trim(fieldName);
                
                formValues[fieldName] = fieldValue;
                
            } // if (fieldValue && $.trim(fieldValue).length > 0 && fieldName && $.trim(fieldName).length > 0)

        });  
        
        $('input[type="checkbox"]:checked').each(function() {
            var fieldValue = $(this).val();
            var fieldName = $(this).attr('name'); 
            
            if (!(formValues[fieldName] && formValues[fieldName] instanceof Array)) {
            	formValues[fieldName] = new Array();
            } // if (!(formValues[fieldName] && formValues[fieldName] instanceof Array))
            
            formValues[fieldName].push(fieldValue);
        });        
        
        $('#exception-message-container').hide();
        
        $.runAjaxRequest($(this).prop('action'), formValues, $('#create-data-form'))
        .done(function(response) {

            if (response)
            {
                if (0 == response.status) {
                    $('#cancel-btn').hide();
                    $('#save-btn').hide();
                    // $('input[type="text"], input[type="password"], textarea').prop("readonly", true); 
                    // $('select').prop("disabled", true); 
                    // $('input[id="id"]').prop("readonly", true);
                    // $('input[id="created_at"]').prop("readonly", true); 
                    // $('.form_date').hide();
                    // $('.date-display').val($('.form_date > input').val());
                    // $('.date-display').show();
                    
                    // $('.form_datetime').hide();
                    // $('.datetime-display').val($('.form_datetime > input').val());
                    // $('.datetime-display').show();
                    
                    
                    // $('.form_time').hide();
                    // $('.time-display').val($('.form_time > input').val());
                    // $('.time-display').show(); 
                    
                    $('.input-group').hide();
                    $('.form_date').hide();
                    $('.form_datetime').hide();
                    $('.form_time').hide();
                    $('.file-upload-container').hide();
                    $('.checkbox-container').hide();  
                    $('input, textarea, select').hide(); 
                    $('.field-value-container').show();                      
                } else {
                    if (response.message) {
                        $('#exception-message-container').text(response.message);        
                    } else if (gPageExceptionMessageDefault) {
                        $('#exception-message-container').text(gPageExceptionMessageDefault);   
                    }
                    $('#exception-message-container').show(); 
                }
            } // if (response)

        })
        .fail(function(response) {
            if (response.message) {
                $('#exception-message-container').text(response.message);        
            } else if (gPageExceptionMessageServiceUnavailable) {
                $('#exception-message-container').text(gPageExceptionMessageServiceUnavailable); 
            } else if (gPageExceptionMessageDefault) {
                $('#exception-message-container').text(gPageExceptionMessageDefault);   
            }
            $('#exception-message-container').show();               
        })
        .always(function(response) {
            $('#save-btn').button('reset');                 
        });            
        
    });
    
    $('.input-group').show();
    $('.checkbox-container').show(); 
    $('input, textarea, select').show();
    $('.field-value-container').hide();     

});