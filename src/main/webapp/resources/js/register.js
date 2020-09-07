
  
$(document).ready(function(){

  $(".enterprise-register-btn").bind("click",function(){
    $(".personal-register-btn").removeClass("register-active");
    $(".enterprise-register-btn").addClass("register-active");
    $("#personal-register").fadeOut();
    $("#enterprise-register").fadeIn();
  });


  $(".personal-register-btn").bind("click",function(){
    $(".enterprise-register-btn").removeClass("register-active");
    $(".personal-register-btn").addClass("register-active");
    $("#personal-register").fadeIn();
    $("#enterprise-register").fadeOut();
  });


  //企业资料填写 设置登录按钮状态函数
  function enterpriseButtonStatus() {

    //追踪鼠标状态判断
    $(".etp-organization,.etp-organization-address,.etp-organization-tel,.etp-organization-legal,.etp-username,.etp-username,.etp-phonenumber,.etp-captcha,.etp-password,.etp-password-confirm").bind("mouseleave",function(){
      //获取输入框自字段的值
      var input1val = $("#enterprise-register .etp-organization").val();
      var input2val = $("#enterprise-register .etp-organization-address").val();
      var input3val = $("#enterprise-register .etp-organization-tel").val();
      var input4val = $("#enterprise-register .etp-organization-legal").val();
      var input5val = $("#enterprise-register .etp-username").val();
      var input6val = $("#enterprise-register .etp-phonenumber").val();
      var input7val = $("#enterprise-register .etp-captcha").val();
      var input8val = $("#enterprise-register .etp-password").val();
      var input9val = $("#enterprise-register .etp-password-confirm").val();
      //如果以上字段都填写了，登录可点，否则不可点
      if(input1val&&input2val&&input3val&&input4val&&input5val&&input6val&&input7val&&input8val&&input9val) {
        $(".etp-register-msg-next").removeClass("am-disabled");
      }
      else {
        $(".etp-register-msg-next").addClass("am-disabled");
      }
    });

    //追踪键盘状态判断
    $(".etp-organization,.etp-organization-address,.etp-organization-tel,.etp-organization-legal,.etp-username,.etp-phonenumber,.etp-captcha,.etp-password,.etp-password-confirm").bind("keyup",function(){
      //获取输入框自字段的值
      var input1val = $("#enterprise-register .etp-organization").val();
      var input2val = $("#enterprise-register .etp-organization-address").val();
      var input3val = $("#enterprise-register .etp-organization-tel").val();
      var input4val = $("#enterprise-register .etp-organization-legal").val();
      var input5val = $("#enterprise-register .etp-username").val();
      var input6val = $("#enterprise-register .etp-phonenumber").val();
      var input7val = $("#enterprise-register .etp-captcha").val();
      var input8val = $("#enterprise-register .etp-password").val();
      var input9val = $("#enterprise-register .etp-password-confirm").val();
      //如果以上字段都填写了，登录可点，否则不可点
      if(input1val&&input2val&&input3val&&input4val&&input5val&&input6val&&input7val&&input8val&&input9val) {
        $(".etp-register-msg-next").removeClass("am-disabled");
      }
      else {
        $(".etp-register-msg-next").addClass("am-disabled");
      }
    });

  }

  //设置登录按钮状态函数
  function personalButtonStatus() {

    //追踪鼠标状态判断
    $(".psn-invite-code, .psn-username, .psn-address, .psn-phonenumber, .psn-captcha, .psn-password-confirm").bind("mouseleave",function(){
      //获取输入框自字段的值
      var input1val = $("#personal-register .psn-invite-code").val();
      var input2val = $("#personal-register .psn-username").val();
      var input3val = $("#personal-register .psn-address").val();
      var input4val = $("#personal-register .psn-phonenumber").val();
      var input5val = $("#personal-register .psn-captcha").val();
      var input6val = $("#personal-register .psn-password-confirm").val();
      //如果以上字段都填写了，登录可点，否则不可点
      if(input1val&&input2val&&input3val&&input4val&&input5val&&input6val) {
        $(".psn-register-msg-next").removeClass("am-disabled");
      }
      else {
        $(".psn-register-msg-next").addClass("am-disabled");
      }
    });

    //追踪键盘状态判断
    $(".psn-invite-code, .psn-username, .psn-address, .psn-phonenumber, .psn-captcha, .psn-password-confirm").bind("keyup",function(){
      //获取输入框自字段的值
      var input1val = $("#personal-register .psn-invite-code").val();
      var input2val = $("#personal-register .psn-username").val();
      var input3val = $("#personal-register .psn-address").val();
      var input4val = $("#personal-register .psn-phonenumber").val();
      var input5val = $("#personal-register .psn-captcha").val();
      var input6val = $("#personal-register .psn-password-confirm").val();
      //如果以上字段都填写了，登录可点，否则不可点
      if(input1val&&input2val&&input3val&&input4val&&input5val&&input6val) {
        $(".psn-register-msg-next").removeClass("am-disabled");
      }
      else {
        $(".psn-register-msg-next").addClass("am-disabled");
      }
    });
    

  }

personalButtonStatus();
enterpriseButtonStatus();

});
