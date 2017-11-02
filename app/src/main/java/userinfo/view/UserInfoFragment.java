package userinfo.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import bean.SkillBean;
import config.NetConfig;
import userinfo.adapter.UserInfoAdapter;
import bean.UserInfoBean;
import userinfo.presenter.IUserInfoPresenter;
import userinfo.presenter.UserInfoPresenter;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;
import view.CProgressDialog;

public class UserInfoFragment extends Fragment implements IUserInfoFragment {

    private View rootView;
    private ListView lv;
    private CProgressDialog cpd;
    private IUserInfoPresenter userInfoPresenter;
    private final int INFO_SUCCESS = 1;
    private final int INFO_FAILURE = 2;
    private final int SKILL_SUCCESS = 3;
    private final int SKILL_FAILURE = 4;
    private UserInfoBean userInfoBean;
    private List<SkillBean> skillBeanList = new ArrayList<>();
    private UserInfoAdapter userInfoAdapter;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case INFO_SUCCESS:
                        userInfoPresenter.skill(NetConfig.skillUrl + "?s_id=" + userInfoBean.getU_skills());
                        break;
                    case INFO_FAILURE:
                        break;
                    case SKILL_SUCCESS:
                        notifyData();
                        break;
                    case SKILL_FAILURE:
                        break;
                    case 5:
                        loadData();
                        break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_userinfo, null);
        initView();
        initData();
        loadData();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userInfoPresenter != null) {
            userInfoPresenter.destroy();
            userInfoPresenter = null;
        }
        if (handler != null) {
            handler.removeMessages(INFO_SUCCESS);
            handler.removeMessages(INFO_FAILURE);
            handler.removeMessages(SKILL_SUCCESS);
            handler.removeMessages(SKILL_FAILURE);
            handler = null;
        }
    }

    private void initView() {
        lv = (ListView) rootView.findViewById(R.id.lv_fragment_userinfo);
        cpd = Utils.initProgressDialog(getActivity(), cpd);
    }

    private void initData() {
        userInfoPresenter = new UserInfoPresenter(this);
    }

    private void loadData() {
        cpd.show();
        userInfoPresenter.info(NetConfig.userInfoUrl + UserUtils.readUserData(getActivity()).getId());
    }

    private void notifyData() {
        cpd.dismiss();
        userInfoAdapter = new UserInfoAdapter(getActivity(), userInfoBean, skillBeanList);
        lv.setAdapter(userInfoAdapter);
    }

    @Override
    public void infoSuccess(String json) {
        userInfoBean = DataUtils.getUserInfoBean(json);
        handler.sendEmptyMessage(INFO_SUCCESS);
    }

    @Override
    public void infoFailure(String failure) {

    }

    @Override
    public void skillSuccess(String json) {
        Log.e("UserInfoFragment=", json);
        skillBeanList.clear();
        skillBeanList.addAll(DataUtils.getSkillBeanList(json));
        Log.e("UserInfoFragment=", skillBeanList.toString());
        handler.sendEmptyMessage(SKILL_SUCCESS);
    }

    @Override
    public void skillFailure(String failure) {

    }
}
