package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gjzg.R;

import view.CFragment;

/**
 * 创建日期：2017/7/28 on 13:52
 * 作者:孙明明
 * 描述:工作管理
 */

public class WorkManageFragment extends CFragment {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_work_manage, null);
        return rootView;
    }
}