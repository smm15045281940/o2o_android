package activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import fragment.DiscountInfoFragment;
import fragment.FirstPageFragment;
import fragment.MineFragment;
import fragment.WorkManageFragment;
import utils.Utils;
import view.CFragment;

/**
 * 创建日期：2017/7/28 on 13:50
 * 作者:孙明明
 * 描述:主页
 */

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private View rootView;
    private RadioGroup mainRg;

    private List<CFragment> cFragmentList;
    private FragmentManager fragmentManager;
    private int curIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_main, null);
        setContentView(rootView);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mainRg = (RadioGroup) rootView.findViewById(R.id.rg_main);
        ((RadioButton) mainRg.getChildAt(curIndex)).setChecked(true);
    }

    private void initData() {
        FirstPageFragment firstPageFragment = new FirstPageFragment();
        WorkManageFragment workManageFragment = new WorkManageFragment();
        DiscountInfoFragment discountInfoFragment = new DiscountInfoFragment();
        MineFragment mineFragment = new MineFragment();
        cFragmentList = new ArrayList<>();
        cFragmentList.add(firstPageFragment);
        cFragmentList.add(workManageFragment);
        cFragmentList.add(discountInfoFragment);
        cFragmentList.add(mineFragment);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.ll_main_set, cFragmentList.get(curIndex));
        fragmentTransaction.commit();
    }

    private void setListener() {
        mainRg.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int b = checkedId;
        if (b == 2) {
            request();
        } else {
            changeFragment(checkedId - 1);
        }
    }

    private void changeFragment(int tarIndex) {
        if (tarIndex != curIndex) {
            Fragment curFragment = cFragmentList.get(curIndex);
            Fragment tarFragment = cFragmentList.get(tarIndex);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(curFragment);
            if (tarFragment.isAdded()) {
                transaction.show(tarFragment);
            } else {
                transaction.add(R.id.ll_main_set, tarFragment);
            }
            curIndex = tarIndex;
            transaction.commit();
        }
    }

    private void request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int p = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (p != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                changeFragment(1);
            }
        } else {
            changeFragment(1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    changeFragment(1);
                } else {
                    Utils.toast(this, "没有定位权限");
                }
                break;
        }
    }
}