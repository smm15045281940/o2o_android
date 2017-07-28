package fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gjzg.R;

import utils.Utils;
import view.CFragment;

/**
 * 创建日期：2017/7/28 on 13:53
 * 作者:孙明明
 * 描述:优惠信息
 */

public class DiscountInfoFragment extends CFragment {

    private View rootView;
    private TextView tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discount_info, null);
        initView();
        setListener();
        return rootView;
    }

    private void initView() {
        tv = (TextView) rootView.findViewById(R.id.tv_discount_info);
    }

    private void setListener() {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                photo();
            }
        } else {
            photo();
        }
    }

    private void photo() {
//        Intent intent = new Intent();
//        intent.setAction("android.media.action.IMAGE_CAPTURE");
//        startActivity(intent);
        Utils.toast(getActivity(), "call");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    photo();
                } else {
                    Utils.toast(getActivity(), "你关闭了该权限，请去设置里打开权限");
                }
                break;
        }
    }
}
