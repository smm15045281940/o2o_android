package usermanage.presenter;


import android.content.Context;
import android.net.Uri;

import bean.UserInfoBean;

public interface IUserManagePresenter {

    void info(String url);

    void up(Context context, String id, Uri uri);

    void destroy();
}
