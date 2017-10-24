package config;

public interface NetConfig {

    String hotCityUrl = "http://api.gangjianwang.com/Regions/index?action=hot";
    String comCityUrl = "http://api.gangjianwang.com/Regions/index?action=letter";
    String setPwdUrl = "api.gangjianwang.com/Users/setPassword";
    String editPwdUrl = "api.gangjianwang.com/Users/passwordEdit";
    String fgtPwdCodeUrl = "api.gangjianwang.com/Users/passwordEdit?u_mobile=";
    String fgtPwdUrl = "http://api.gangjianwang.com/Users/passwordEdit";
    String provePhoneUrl = "http://api.gangjianwang.com/Users/passwordEdit";
    String proveIdcardUrl = "http://api.gangjianwang.com/Users/passwordEdit";
    String proveOriPwdUrl = "api.gangjianwang.com/Users/passwordEdit";
    String userInfoEditUrl = "http://api.gangjianwang.com/Users/usersInfoEdit";
    String skillBaseUrl = "http://api.gangjianwang.com/Skills/index";
    String workerUrl = "http://api.gangjianwang.com/Users/getUsersBySkills";
    String taskBaseUrl = "http://api.gangjianwang.com/Tasks/index";
    String codeUrl = "http://api.gangjianwang.com/Users/sendVerifyCode?phone_number=";
    String loginUrl = "http://api.gangjianwang.com/Users/login";
    String payWayUrl = "http://api.gangjianwang.com/Payments/index";
    String collectWorkerUrl = "http://api.gangjianwang.com/Users/favorateUsers?u_id=";
    String collectJobUrl = "http://api.gangjianwang.com/Users/favorateTasks?u_id=";
    String userFundUrl = "http://api.gangjianwang.com/Users/usersFunds?u_id=";
    String accountDetailUrl = "http://api.gangjianwang.com/Users/getUsersFundsLog";
    String userInfoUrl = "http://api.gangjianwang.com/Users/usersInfo?u_id=";
    String iconUpdateUrl = "http://api.gangjianwang.com/Users/usersHeadEidt";
    String redPacketUrl = "http://api.gangjianwang.com/Bouns/index?action=list&bt_id=2";
    String voucherUrl = "http://api.gangjianwang.com/Bouns/index?action=list&bt_id=1";
    String applyWithdrawUrl = "http://api.gangjianwang.com/Users/applyWithdraw";
    String useCplIs = "http://api.gangjianwang.com/Users/complaintsType?ct_type=";
    String selectAddressBaseUrl = "http://api.gangjianwang.com/Regions/index?action=list&r_pid=";
    String usersFundsLogUrl = "http://api_zy.gangjianwang.com/Users/getUsersFundsLog";
}
