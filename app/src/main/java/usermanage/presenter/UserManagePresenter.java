package usermanage.presenter;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;

import listener.JsonListener;
import usermanage.listener.UpLoadingIconListener;
import usermanage.module.IUserManageModule;
import usermanage.module.UserManageModule;
import usermanage.view.IUserManageActivity;

public class UserManagePresenter implements IUserManagePresenter {

    private IUserManageActivity userManageActivity;
    private IUserManageModule userManageModule;
    private Handler handler;

    public UserManagePresenter(IUserManageActivity userManageActivity) {
        this.userManageActivity = userManageActivity;
        userManageModule = new UserManageModule();
        handler = new Handler();
    }

    @Override
    public void info(String url) {
        userManageModule.info(url, new JsonListener() {
            @Override
            public void success(final String json) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userManageActivity.infoSuccess(json);
                    }
                });
            }

            @Override
            public void failure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                userManageActivity.infoFailure(failure);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void up(Context context, String id, Uri uri) {
        userManageModule.up(context, id, uri, new UpLoadingIconListener() {
            @Override
            public void upLoadingIconFailure(final String failure) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userManageActivity.upLoadIconFailure(failure);
                    }
                });
            }

            @Override
            public void upLoadingIconSuccess(final String success, final Bitmap bitmap) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userManageActivity.upLoadIconSuccess(success, bitmap);
                    }
                });
            }
        });
    }

    @Override
    public void destroy() {
        if (userManageModule != null) {
            userManageModule.cancelTask();
            userManageModule = null;
        }
        if (userManageActivity != null) {
            userManageActivity = null;
        }
        if (handler != null) {
            handler = null;
        }
    }
}
