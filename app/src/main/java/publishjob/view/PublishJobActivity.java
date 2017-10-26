package publishjob.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.ScnDiaAdapter;
import config.NetConfig;
import listener.ListItemClickHelp;
import publishjob.adapter.PublishKindAdapter;
import publishjob.adapter.SelectSkillAdapter;
import publishjob.bean.PublishJobBean;
import publishjob.bean.PublishKindBean;
import publishjob.listener.PublishJobClickHelp;
import publishjob.presenter.IPublishJobPresenter;
import publishjob.presenter.PublishJobPresenter;
import selectaddress.bean.SelectAddressBean;
import selectaddress.view.SelectAddressActivity;
import skills.bean.SkillsBean;
import taskconfirm.view.TaskConfirmActivity;
import utils.DataUtils;
import utils.Utils;
import view.CProgressDialog;

public class PublishJobActivity extends AppCompatActivity implements IPublishJobActivity, View.OnClickListener, PublishJobClickHelp {

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
    private ListView typeLv;

    private IPublishJobPresenter publishJobPresenter;
    private List<String> taskTypeList;
    private CProgressDialog cpd;
    private ScnDiaAdapter scnDiaAdapter;

    private View skillPopView;
    private PopupWindow skillPop;
    private ListView skillLv;
    private SelectSkillAdapter selectSkillAdapter;
    private List<SkillsBean> skillsBeanList;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private int year, month, day;
    private final int START_TIME = 1;
    private final int END_TIME = 2;
    private int pickState;
    private int pickPosition;
    private final int TASK_TYPE_SUCCESS = 1;
    private final int TASK_TYPE_FAILURE = 2;
    private final int SKILL_SUCCESS = 3;
    private final int SKILL_FAILURE = 4;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case TASK_TYPE_SUCCESS:
                        scnDiaAdapter.notifyDataSetChanged();
                        break;
                    case TASK_TYPE_FAILURE:
                        break;
                    case SKILL_SUCCESS:
                        selectSkillAdapter.notifyDataSetChanged();
                        break;
                    case SKILL_FAILURE:
                        break;
                }
            }
        }
    };

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
        loadData();
    }

    private void initView() {
        initRootView();
        initHeadView();
        initPopView();
    }

    private void initData() {
        publishJobBean = new PublishJobBean();
        publishJobPresenter = new PublishJobPresenter(this);
        publishKindBeanList = new ArrayList<>();
        PublishKindBean publishKindBean = new PublishKindBean("", "", "", "", "", "");
        publishKindBeanList.add(publishKindBean);
        publishJobBean.setPublishKindBeanList(publishKindBeanList);
        adapter = new PublishKindAdapter(PublishJobActivity.this, publishKindBeanList, this);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(PublishJobActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String timeStr = year + "-" + (month + 1) + "-" + dayOfMonth;
                switch (pickState) {
                    case START_TIME:
                        publishKindBeanList.get(pickPosition).setStartTime(timeStr);
                        adapter.notifyDataSetChanged();
                        break;
                    case END_TIME:
                        publishKindBeanList.get(pickPosition).setEndTime(timeStr);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }, year, month, day);
    }

    private void setData() {
        lv.setAdapter(adapter);
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_publish_job_return);
        addRl = (RelativeLayout) rootView.findViewById(R.id.rl_publish_job_add);
        submitRl = (RelativeLayout) rootView.findViewById(R.id.rl_publish_job_submit);
        lv = (ListView) rootView.findViewById(R.id.lv_publish_job);
        cpd = Utils.initProgressDialog(PublishJobActivity.this, cpd);
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
        ((TextView) typePopView.findViewById(R.id.tv_dialog_scn_title)).setText("项目类型");
        (typePopView.findViewById(R.id.iv_dialog_scn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typePop.isShowing()) {
                    typePop.dismiss();
                }
            }
        });
        typeLv = (ListView) typePopView.findViewById(R.id.lv_dialog_scn);
        taskTypeList = new ArrayList<>();
        scnDiaAdapter = new ScnDiaAdapter(PublishJobActivity.this, taskTypeList);
        typeLv.setAdapter(scnDiaAdapter);
        typeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                publishJobBean.setType(taskTypeList.get(position));
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

        skillPopView = LayoutInflater.from(PublishJobActivity.this).inflate(R.layout.dialog_scn, null);
        ((TextView) skillPopView.findViewById(R.id.tv_dialog_scn_title)).setText("招聘工种");
        (skillPopView.findViewById(R.id.iv_dialog_scn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skillPop.isShowing()) {
                    skillPop.dismiss();
                }
            }
        });
        skillLv = (ListView) skillPopView.findViewById(R.id.lv_dialog_scn);
        skillLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                publishKindBeanList.get(pickPosition).setId(skillsBeanList.get(position).getS_id());
                publishKindBeanList.get(pickPosition).setKind(skillsBeanList.get(position).getS_name());
                adapter.notifyDataSetChanged();
                skillPop.dismiss();
            }
        });
        skillsBeanList = new ArrayList<>();
        selectSkillAdapter = new SelectSkillAdapter(PublishJobActivity.this, skillsBeanList);
        skillLv.setAdapter(selectSkillAdapter);
        skillPop = new PopupWindow(skillPopView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        skillPop.setFocusable(true);
        skillPop.setTouchable(true);
        skillPop.setOutsideTouchable(true);
        skillPop.setBackgroundDrawable(new BitmapDrawable());
        skillPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
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

    private void loadData() {
        publishJobPresenter.getTaskType("http://api.gangjianwang.com/Tools/taskType");
        publishJobPresenter.getSkill(NetConfig.skillBaseUrl);
    }

    private void submit() {
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
        } else {
            int idCount = 0;
            for (int i = 0; i < publishKindBeanList.size(); i++) {
                if (!TextUtils.isEmpty(publishKindBeanList.get(i).getId())) {
                    idCount++;
                }
            }
            if (idCount != publishKindBeanList.size()) {
                Utils.toast(PublishJobActivity.this, "招聘工种不能为空！");
            } else {
                int amountCount = 0;
                for (int i = 0; i < publishKindBeanList.size(); i++) {
                    if (!TextUtils.isEmpty(publishKindBeanList.get(i).getAmount())) {
                        amountCount++;
                    }
                }
                if (amountCount != publishKindBeanList.size()) {
                    Utils.toast(PublishJobActivity.this, "工人数量不能为空！");
                } else {
                    int salaryCount = 0;
                    for (int i = 0; i < publishKindBeanList.size(); i++) {
                        if (!TextUtils.isEmpty(publishKindBeanList.get(i).getSalary())) {
                            salaryCount++;
                        }
                    }
                    if (salaryCount != publishKindBeanList.size()) {
                        Utils.toast(PublishJobActivity.this, "工人工资不能为空！");
                    } else {
                        int startTimeCount = 0;
                        for (int i = 0; i < publishKindBeanList.size(); i++) {
                            if (!TextUtils.isEmpty(publishKindBeanList.get(i).getStartTime())) {
                                startTimeCount++;
                            }
                        }
                        if (startTimeCount != publishKindBeanList.size()) {
                            Utils.toast(PublishJobActivity.this, "开始日期不能为空！");
                        } else {
                            int endTimeCount = 0;
                            for (int i = 0; i < publishKindBeanList.size(); i++) {
                                if (!TextUtils.isEmpty(publishKindBeanList.get(i).getEndTime())) {
                                    endTimeCount++;
                                }
                            }
                            if (endTimeCount != publishKindBeanList.size()) {
                                Utils.toast(PublishJobActivity.this, "结束日期不能为空！");
                            } else {
                                int timeCount = 0;
                                for (int i = 0; i < publishKindBeanList.size(); i++) {
                                    if (judgeTime(publishKindBeanList.get(i).getStartTime(), publishKindBeanList.get(i).getEndTime())) {
                                        timeCount++;
                                    }
                                }
                                if (timeCount != publishKindBeanList.size()) {
                                    Utils.toast(PublishJobActivity.this, "结束日期不能早于开始日期！");
                                } else {
                                    Intent i = new Intent(PublishJobActivity.this, TaskConfirmActivity.class);
                                    i.putExtra("publishJobBean", publishJobBean);
                                    startActivity(i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean judgeTime(String startTime, String endTime) {
        String[] startArr = startTime.split("-");
        String[] endArr = endTime.split("-");
        int startYear = Integer.parseInt(startArr[0]);
        int startMonth = Integer.parseInt(startArr[1]);
        int startDay = Integer.parseInt(startArr[2]);
        int endYear = Integer.parseInt(endArr[0]);
        int endMonth = Integer.parseInt(endArr[1]);
        int endDay = Integer.parseInt(endArr[2]);
        if (endYear < startYear) {
            return false;
        } else if (endYear == startYear) {
            if (endMonth < startMonth) {
                return false;
            } else if (endMonth == startMonth) {
                if (endDay < startDay) {
                    return false;
                } else if (endDay == startDay) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_publish_job_return:
                finish();
                break;
            case R.id.rl_publish_job_add:
                PublishKindBean publishKindBean = new PublishKindBean("", "", "", "", "", "");
                publishKindBeanList.add(publishKindBean);
                adapter.notifyDataSetChanged();
                lv.setSelection(publishKindBeanList.size());
                break;
            case R.id.rl_publish_job_submit:
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

    @Override
    public void taskTypeSuccess(List<String> list) {
        taskTypeList.addAll(list);
        handler.sendEmptyMessage(TASK_TYPE_SUCCESS);
    }

    @Override
    public void taskTypeFailure(String failure) {
        handler.sendEmptyMessage(TASK_TYPE_FAILURE);
    }

    @Override
    public void skillSuccess(String json) {
        skillsBeanList.addAll(DataUtils.getSkillBeanList(json));
        handler.sendEmptyMessage(SKILL_SUCCESS);
    }

    @Override
    public void skillFailure(String failure) {
        handler.sendEmptyMessage(SKILL_FAILURE);
    }

    @Override
    public void onClick(int viewId, int viewPosition) {
        pickPosition = viewPosition;
        switch (viewId) {
            case R.id.tv_item_publish_job_kind:
                if (!skillPop.isShowing()) {
                    backgroundAlpha(0.5f);
                    skillPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.tv_item_publish_job_start_time:
                pickState = START_TIME;
                datePickerDialog.show();
                break;
            case R.id.tv_item_publish_job_end_time:
                pickState = END_TIME;
                datePickerDialog.show();
                break;
            case R.id.rl_item_publish_job_delete:
                publishKindBeanList.remove(viewPosition);
                adapter.notifyDataSetChanged();
                break;
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
