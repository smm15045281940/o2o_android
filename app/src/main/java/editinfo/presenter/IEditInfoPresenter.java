package editinfo.presenter;


import bean.UserInfoBean;

public interface IEditInfoPresenter {

    void skill(String url);

    void load(String url);

    void submit(UserInfoBean userInfoBean);

    void destroy();
}
