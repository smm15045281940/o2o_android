package config;

//网络配置
public interface NetConfig {
    //城市
    String baseCityUrl = "http://api.gangjianwang.com/Regions/index";
    String hotCityUrl = "?action=hot";
    String letterCityUrl = "?action=letter";
    //工种
    String kindUrl = "http://api.gangjianwang.com/Skills/index";
    //获取验证码
    String codeUrl = "http://api.gangjianwang.com/Users/sendVerifyCode?phone_number=";
    //登录
    String loginUrl = "http://api.gangjianwang.com/Users/login";
    //支付方式
    String payWayUrl = "http://api.gangjianwang.com/Payments/index";

    //测试用（城市列表）
    public static final String testUrl = "http://www.gangjianwang.com/shop/index.php?act=index&op=getWapAreaCities";
    //服务条款
    public static final String sevClsUrl = "http://zy.persistence.net.cn/";
}
