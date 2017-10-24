package publishjob.view;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import adapter.ScnDiaAdapter;
import listener.ListItemClickHelp;
import publishjob.adapter.PublishKindAdapter;
import publishjob.bean.PublishJobBean;
import publishjob.bean.PublishKindBean;
import selectaddress.bean.SelectAddressBean;
import selectaddress.view.SelectAddressActivity;
import taskconfirm.view.TaskConfirmActivity;
import utils.Utils;

public class PublishJobActivity extends AppCompatActivity implements View.OnClickListener, ListItemClickHelp {

    private View rootView, headView;
    private RelativeLayout returnRl, addRl, submitRl;
    private EditText titleEt, descriptionEt, addressEt;
    private TextView typeTv, areaTv;
    private ListView lv;
    private PublishKindAdapter adapter;
    private List<PublishKindBean> publishKindBeanList;

    private String areaId;

    private PublishJobBean publishJobBean;

    private View typePopView;
    private PopupWindow typePop;
    private TextView typeTitleTv;
    private ImageView typeCloseIv;
    private ListView typeLv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_send_job, null);
        setContentView(rootView);
        initView();
        initData();
        setData();
        setListener();
    }

    private void initView() {
        initRootView();
        initHeadView();
        initPopView();
    }

    private void initData() {
        publishKindBeanList = new ArrayList<>();
        PublishKindBean publishKindBean = new PublishKindBean();
        publishKindBean.setKind("0");
        publishKindBean.setAmount("0");
        publishKindBean.setSalary("0");
        publishKindBean.setStartTime("0");
        publishKindBean.setEndTime("0");
        publishKindBeanList.add(publishKindBean);
        adapter = new PublishKindAdapter(PublishJobActivity.this, publishKindBeanList, this);
        publishJobBean = new PublishJobBean();
    }

    private void setData() {
        lv.setAdapter(adapter);
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_publish_job_return);
        addRl = (RelativeLayout) rootView.findViewById(R.id.rl_publish_job_add);
        submitRl = (RelativeLayout) rootView.findViewById(R.id.rl_publish_job_submit);
        lv = (ListView) rootView.findViewById(R.id.lv_publish_job);
    }

    private void initHeadView() {
        headView = LayoutInflater.from(PublishJobActivity.this).inflate(R.layout.head_publish_job, null);
        titleEt = (EditText) headView.findViewById(R.id.et_head_publish_job_title);
        descriptionEt = (EditText) headView.findViewById(R.id.et_head_publish_job_description);
        addressEt = (EditText) headView.findViewById(R.id.et_head_publish_job_address);
        typeTv = (TextView) headView.findViewById(R.id.tv_head_publish_job_type);
        areaTv = (TextView) headView.findViewById(R.id.tv_head_publish_job_area);
        lv.addHeaderView(headView);
    }

    private void initPopView() {
        typePopView = LayoutInflater.from(PublishJobActivity.this).inflate(R.layout.dialog_scn, null);
        typeTitleTv = (TextView) typePopView.findViewById(R.id.tv_dialog_scn_title);
        typeTitleTv.setText("项目类型");
        typeCloseIv = (ImageView) typePopView.findViewById(R.id.iv_dialog_scn_close);
        typeCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typePop.dismiss();
            }
        });
        typeLv = (ListView) typePopView.findViewById(R.id.lv_dialog_scn);
        final List<String> typeList = new ArrayList<>();
        typeList.add("小型工地");
        typeList.add("个人家装");
        typeList.add("大型建筑项目");
        ScnDiaAdapter scnDiaAdapter = new ScnDiaAdapter(PublishJobActivity.this, typeList);
        typeLv.setAdapter(scnDiaAdapter);
        typeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                publishJobBean.setType(typeList.get(position));
                typeTv.setText(publishJobBean.getType());
                typePop.dismiss();
            }
        });
        typePop = new PopupWindow(typePopView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        typePop.setFocusable(true);
        typePop.setTouchable(true);
        typePop.setOutsideTouchable(true);
        typePop.setBackgroundDrawable(new BitmapDrawable());
        typePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        addRl.setOnClickListener(this);
        submitRl.setOnClickListener(this);
        titleEt.addTextChangedListener(titleTw);
        descriptionEt.addTextChangedListener(descriptionTw);
        addressEt.addTextChangedListener(addressTw);
        typeTv.setOnClickListener(this);
        areaTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_publish_job_return:
                finish();
                break;
            case R.id.rl_publish_job_add:
                Utils.log(PublishJobActivity.this, "增加工种");
                break;
            case R.id.rl_publish_job_submit:
                Utils.log(PublishJobActivity.this, "PublishJobBean=" + publishJobBean.toString());
                submit();
                break;
            case R.id.tv_head_publish_job_type:
                backgroundAlpha(0.8f);
                typePop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                break;
            case R.id.tv_head_publish_job_area:
                startActivityForResult(new Intent(PublishJobActivity.this, SelectAddressActivity.class), 1);
                break;
        }
    }

    private void submit() {
        startActivity(new Intent(PublishJobActivity.this, TaskConfirmActivity.class));
        if (TextUtils.isEmpty(publishJobBean.getTitle())) {
            Utils.toast(PublishJobActivity.this, "标题不能为空！");
        } else if (TextUtils.isEmpty(publishJobBean.getDescription())) {
            Utils.toast(PublishJobActivity.this, "描述不能为空！");
        } else if (TextUtils.isEmpty(publishJobBean.getType())) {
            Utils.toast(PublishJobActivity.this, "工程类型不能为空！");
        } else if (TextUtils.isEmpty(publishJobBean.getArea())) {
            Utils.toast(PublishJobActivity.this, "工作区域不能为空！");
        } else if (TextUtils.isEmpty(publishJobBean.getAddress())) {
            Utils.toast(PublishJobActivity.this, "详细地址不能为空！");
        }
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View item, View widget, int position, int which, boolean isChecked) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 & data != null) {
            SelectAddressBean selectAddressBean = (SelectAddressBean) data.getSerializableExtra("sa");
            if (selectAddressBean != null) {
                areaId = selectAddressBean.getId();
                publishJobBean.setArea(selectAddressBean.getName());
                areaTv.setText(publishJobBean.getArea());
            }
        }
    }

    TextWatcher titleTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            publishJobBean.setTitle(s.toString());
        }
    };

    TextWatcher descriptionTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            publishJobBean.setDescription(s.toString());
        }
    };

    TextWatcher addressTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            publishJobBean.setAddress(s.toString());
        }
    };
}
