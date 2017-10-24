package config;

public interface VarConfig {

    String noNet = "noNet";
    int PWD_FIRST = 0;//第一次设置密码
    int PWD_AGAIN = 1;//二次密码验证
    int PWD_ORIGIN = 2;//第二次设置密码
    int PWD_NEW = 3;//设置新密码
    String pwdFirstTip = "设置六位提现密码";//第一次设置密码提示
    String pwdAgainTip = "请在输入一次";//二次密码验证提示
    String pwdOriginTip = "输入原有提现密码";//第二次设置密码提示
    String pwdNewTip = "设置新的提现密码";//设置新密码
    String pwdSuccessTip = "设置密码成功";//设置密码成功提示
    String pwdDifferTip = "两次密码不一致";//两次密码不一致提示
    String pwdErrorTip = "原密码不正确";//原密码不正确
    int PWD_COUNT_DOWN = 60;//重新设置密码手机验证码倒计时(单位：秒)
    int GET_LOGIN_CODE_DOWN = 5;//获取登录验证码倒计时时间(单位：秒)
    String pwdResendTip = "重新发送";//重新发送提示
    String phoneErrorTip = "手机号格式不正确";//验证手机号格式
    String getPwdTip = "获取动态密码";//获取动态密码提示
}
