package activity;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import adapter.ScnDiaAdapter;
import adapter.ScnJobCdtAdapter;
import bean.ScnJobBean;
import listener.ListItemClickHelp;
import utils.Utils;

public class JobScnActivity extends CommonActivity implements View.OnClickListener, ListItemClickHelp, AdapterView.OnItemClickListener {

    private View rootView, scnDialogView;
    private AlertDialog scnDialog;
    private ListView scnDialogLv;
    private RelativeLayout returnRl, searchRl;
    private ListView jobScnCdtLv;

    private ScnJobBean scnJobBeanCdt;
    private ScnJobCdtAdapter scnJobCdtAdapter;

    private ScnDiaAdapter scnDiaAdapter;
    private List<String> scnDialogList, disList, durationList, moneyList, startTimeList, kindList, typeList;
    private int scnDiaPos;

    @Override
    protected View getRootView() {
        return rootView = LayoutInflater.from(this).inflate(R.layout.activity_scn_job,null);
    }

    @Override
    protected void initView() {
        initRootView();
        initDialogView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_screen_job_return);
        searchRl = (RelativeLayout) rootView.findViewById(R.id.rl_screen_job_search);
        jobScnCdtLv = (ListView) rootView.findViewById(R.id.lv_scn_job_cdt);
    }

    private void initDialogView() {
        scnDialogView = View.inflate(this, R.layout.dialog_listview, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(scnDialogView);
        scnDialog = builder.create();
        scnDialogLv = (ListView) scnDialogView.findViewById(R.id.lv_dialog_listview);
    }

    @Override
    protected void initData() {
        scnJobBeanCdt = new ScnJobBean();
        scnJobBeanCdt.setName("");
        scnJobBeanCdt.setNameHint("输入职位");
        scnJobBeanCdt.setDisTitle("搜索范围：");
        scnJobBeanCdt.setDisContent("请选择搜索范围");
        scnJobBeanCdt.setDurationTitle("项目工期：");
        scnJobBeanCdt.setDurationContent("请选择项目工期");
        scnJobBeanCdt.setMoneyTitle("工资金额：");
        scnJobBeanCdt.setMoneyContent("请选择工资金额");
        scnJobBeanCdt.setStartTimeTitle("开始时间：");
        scnJobBeanCdt.setStartTimeContent("请选择项目开始时间");
        scnJobBeanCdt.setKindTitle("招聘工种：");
        scnJobBeanCdt.setKindContent("请选择招聘工种");
        scnJobBeanCdt.setTypeTitle("项目类型：");
        scnJobBeanCdt.setTypeContent("请选择项目类型");
        scnJobCdtAdapter = new ScnJobCdtAdapter(this, scnJobBeanCdt, this);

        scnDialogList = new ArrayList<>();
        scnDiaAdapter = new ScnDiaAdapter(this, scnDialogList);
        disList = new ArrayList<>();
        durationList = new ArrayList<>();
        moneyList = new ArrayList<>();
        startTimeList = new ArrayList<>();
        kindList = new ArrayList<>();
        typeList = new ArrayList<>();

        disList.add("2公里以内");
        disList.add("5公里以内");
        disList.add("10公里以内");
        disList.add("10公里以外");

        durationList.add("2日以内");
        durationList.add("5日以内");
        durationList.add("10日以内");
        durationList.add("1月以内");
        durationList.add("1月以外");

        moneyList.add("500元以内");
        moneyList.add("1000元以内");
        moneyList.add("2000元以内");
        moneyList.add("2000元以上");

        startTimeList.add("1天以内");
        startTimeList.add("3天以内");
        startTimeList.add("1周以内");
        startTimeList.add("2周以内");
        startTimeList.add("2周以外");

        kindList.add("水泥工");
        kindList.add("瓦工");
        kindList.add("力工");
        kindList.add("搬运工");
        kindList.add("焊接工");
        kindList.add("其他工种");

        typeList.add("小型工地");
        typeList.add("个人家装");
        typeList.add("大型建筑项目");
    }

    @Override
    protected void setData() {
        jobScnCdtLv.setAdapter(scnJobCdtAdapter);
        Utils.setListViewHeight(jobScnCdtLv);
        scnDialogLv.setAdapter(scnDiaAdapter);
    }

    @Override
    protected void setListener() {
        returnRl.setOnClickListener(this);
        searchRl.setOnClickListener(this);
        scnDialogLv.setOnItemClickListener(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_screen_job_return:
                finish();
                break;
            case R.id.rl_screen_job_search:
                Utils.toast(this, scnJobBeanCdt.toString());
                break;
        }
    }

    @Override
    public void onClick(View item, View widget, int position, int which, boolean isChecked) {
        switch (which) {
            case R.id.ll_item_scn_job_cdt_click:
                scnDialogList.clear();
                switch (position) {
                    case 1:
                        scnDiaPos = 1;
                        scnDialogList.addAll(disList);
                        break;
                    case 2:
                        scnDiaPos = 2;
                        scnDialogList.addAll(durationList);
                        break;
                    case 3:
                        scnDiaPos = 3;
                        scnDialogList.addAll(moneyList);
                        break;
                    case 4:
                        scnDiaPos = 4;
                        scnDialogList.addAll(startTimeList);
                        break;
                    case 5:
                        scnDiaPos = 5;
                        scnDialogList.addAll(kindList);
                        break;
                    case 6:
                        scnDiaPos = 6;
                        scnDialogList.addAll(typeList);
                        break;
                }
                scnDiaAdapter.notifyDataSetChanged();
                scnDialog.show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String diaRes = scnDialogList.get(position);
        switch (scnDiaPos) {
            case 1:
                scnJobBeanCdt.setDisContent(diaRes);
                break;
            case 2:
                scnJobBeanCdt.setDurationContent(diaRes);
                break;
            case 3:
                scnJobBeanCdt.setMoneyContent(diaRes);
                break;
            case 4:
                scnJobBeanCdt.setStartTimeContent(diaRes);
                break;
            case 5:
                scnJobBeanCdt.setKindContent(diaRes);
                break;
            case 6:
                scnJobBeanCdt.setTypeContent(diaRes);
                break;
        }
        scnDialog.dismiss();
        scnJobCdtAdapter.notifyDataSetChanged();
    }
}
