package fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import activity.JobActivity;
import utils.Utils;
import view.CFragment;

/**
 * 创建日期：2017/7/28 on 13:52
 * 作者:孙明明
 * 描述:首页
 */

public class FirstPageFragment extends CFragment {

    private View rootView;
    private LinearLayout linearLayout;

    private List<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_first_page, null);
        initView();
        return rootView;
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        loadData();
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout = (LinearLayout) rootView.findViewById(R.id.ll_first_page);
    }

    private void loadData() {
        for (int i = 0; i < 3; i++) {
            list.add("imageUrl:" + i);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp.weight = 1.0f;
        lp.setMargins(10, 10, 10, 10);
        for (int i = 0; i < list.size(); i++) {
            ImageView iv = new ImageView(getActivity());
            iv.setLayoutParams(lp);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(R.mipmap.ic_launcher);
            iv.setBackgroundColor(Color.GREEN);
            linearLayout.addView(iv);
            final int finalI = i;
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalI){
                        case 0:
                            Intent intent = new Intent(getActivity(), JobActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            Utils.log(getActivity(),"找工作");
                            break;
                        case 2:
                            Utils.log(getActivity(),"发布工作");
                            break;
                    }
                }
            });
        }
    }
}
