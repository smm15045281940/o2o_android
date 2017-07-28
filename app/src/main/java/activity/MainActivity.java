package activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import adapter.MainFragPageAdapter;
import fragment.DiscountInfoFragment;
import fragment.FirstPageFragment;
import fragment.MineFragment;
import fragment.WorkManageFragment;
import view.CFragment;

/**
 * 创建日期：2017/7/28 on 13:50
 * 作者:孙明明
 * 描述:主页
 */

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private View rootView;
    private ViewPager mainVp;
    private RadioGroup mainRg;

    private List<CFragment> cFragmentList;
    private MainFragPageAdapter mainFragPageAdapter;
    private int curPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = View.inflate(this, R.layout.activity_main, null);
        setContentView(rootView);
        initView();
        initData();
        setData();
        setListener();
    }

    private void initView() {
        mainVp = (ViewPager) rootView.findViewById(R.id.vp_main);
        mainRg = (RadioGroup) rootView.findViewById(R.id.rg_main);
        ((RadioButton) mainRg.getChildAt(curPosition)).setChecked(true);
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
        mainFragPageAdapter = new MainFragPageAdapter(getSupportFragmentManager(), this, cFragmentList);
    }

    private void setData() {
        mainVp.setAdapter(mainFragPageAdapter);
    }

    private void setListener() {
        mainRg.setOnCheckedChangeListener(this);
        mainVp.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton) mainRg.getChildAt(position)).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mainVp.setCurrentItem(checkedId - 1, false);
    }
}