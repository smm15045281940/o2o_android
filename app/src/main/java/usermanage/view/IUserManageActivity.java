package usermanage.view;

import android.graphics.Bitmap;

import bean.UserInfoBean;

public interface IUserManageActivity {

    void infoSuccess(String json);

    void infoFailure(String failure);

    void upLoadIconFailure(String upLoadIconFailure);

    void upLoadIconSuccess(String upLoadIconSuccess, Bitmap bitmap);

}
