package complain.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapter.ComplainImageAdapter;
import adapter.ComplainIssueAdapter;
import bean.ComplainIssueBean;
import bean.ToComplainBean;
import complain.presenter.ComplainPresenter;
import complain.presenter.IComplainPresenter;
import config.IntentConfig;
import config.NetConfig;
import config.PathConfig;
import config.PermissionConfig;
import pic.view.PicActivity;
import bean.UserInfoBean;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;
import view.CImageView;
import view.CProgressDialog;

public class ComplainActivity extends AppCompatActivity implements IComplainActivity, View.OnClickListener, AdapterView.OnItemClickListener {

    private View rootView;
    private RelativeLayout returnRl;
    private RelativeLayout addImageRl;
    private TextView submitTv;
    private View popView;
    private PopupWindow pop;
    private TextView cameraTv, mapTv, cancelTv;
    private CProgressDialog cpd;
    private GridView gv;
    private List<Bitmap> list;
    private ComplainImageAdapter adapter;
    private String picPath;
    private List<String> upLoadImageList;
    private IComplainPresenter complainPresenter;
    private TextView cplIsTv;
    private View cplIsPopView;
    private PopupWindow cplIsPop;
    private ListView cplIsLv;
    private List<ComplainIssueBean> complainIssueBeanList = new ArrayList<>();
    private ComplainIssueAdapter complainIssueAdapter;

    private ToComplainBean toComplainBean;
    private UserInfoBean userInfoBean;

    private final int USER_INFO_SUCCESS = 1;
    private final int USER_INFO_FAILURE = 2;
    private final int USER_SKILL_SUCCESS = 3;
    private final int USER_SKILL_FAILURE = 4;
    private final int USER_ISSUE_SUCCESS = 5;
    private final int USER_ISSUE_FAILURE = 6;
    private final int SUBMIT_SUCCESS = 7;
    private final int SUBMIT_FAILURE = 8;
    private String skillName;

    private CImageView iconIv;
    private ImageView sexIv;
    private TextView nameTv, skillNameTv, evaluateTv;
    private EditText contentEt;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case USER_INFO_SUCCESS:
                        complainPresenter.userSkill(NetConfig.skillsUrl);
                        break;
                    case USER_INFO_FAILURE:
                        break;
                    case USER_SKILL_SUCCESS:
                        complainPresenter.userIssue(NetConfig.complainTypeUrl + "?ct_type=" + toComplainBean.getCtType());
                        break;
                    case USER_SKILL_FAILURE:
                        break;
                    case USER_ISSUE_SUCCESS:
                        notifyData();
                        break;
                    case USER_ISSUE_FAILURE:
                        break;
                    case SUBMIT_SUCCESS:
                        cpd.dismiss();
                        Utils.toast(ComplainActivity.this, "投诉成功");
                        finish();
                        break;
                    case SUBMIT_FAILURE:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_complain, null);
        setContentView(rootView);
        initView();
        initData();
        setData();
        setListener();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (complainPresenter != null) {
            complainPresenter.destory();
            complainPresenter = null;
        }
        if (handler != null) {
            handler.removeMessages(USER_INFO_SUCCESS);
            handler.removeMessages(USER_INFO_FAILURE);
            handler.removeMessages(USER_SKILL_SUCCESS);
            handler.removeMessages(USER_SKILL_FAILURE);
            handler.removeMessages(USER_ISSUE_SUCCESS);
            handler.removeMessages(USER_ISSUE_FAILURE);
            handler.removeMessages(SUBMIT_SUCCESS);
            handler.removeMessages(SUBMIT_FAILURE);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
        initCplIsPopView();
        initPopView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_complain_return);
        addImageRl = (RelativeLayout) rootView.findViewById(R.id.rl_complain_add_image);
        submitTv = (TextView) rootView.findViewById(R.id.tv_complain_sumit);
        gv = (GridView) rootView.findViewById(R.id.gv_complain_image);
        cpd = Utils.initProgressDialog(this, cpd);

        iconIv = (CImageView) rootView.findViewById(R.id.iv_complain_icon);
        sexIv = (ImageView) rootView.findViewById(R.id.iv_complain_sex);
        nameTv = (TextView) rootView.findViewById(R.id.tv_complain_name);
        skillNameTv = (TextView) rootView.findViewById(R.id.tv_complain_skill_name);
        evaluateTv = (TextView) rootView.findViewById(R.id.tv_complain_evaluate);
        contentEt = (EditText) rootView.findViewById(R.id.et_complain_content);
    }

    private void initCplIsPopView() {
        cplIsTv = (TextView) rootView.findViewById(R.id.tv_complain_issue);
        cplIsPopView = LayoutInflater.from(ComplainActivity.this).inflate(R.layout.pop_lv, null);
        cplIsLv = (ListView) cplIsPopView.findViewById(R.id.lv_pop_lv);
        cplIsPop = new PopupWindow(cplIsPopView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        cplIsPop.setFocusable(true);
        cplIsPop.setTouchable(true);
        cplIsPop.setOutsideTouchable(true);
        complainIssueAdapter = new ComplainIssueAdapter(ComplainActivity.this, complainIssueBeanList);
        cplIsLv.setAdapter(complainIssueAdapter);
    }

    private void initPopView() {
        popView = LayoutInflater.from(this).inflate(R.layout.pop_add_image, null);
        pop = new PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        pop.setTouchable(true);
        pop.setOutsideTouchable(true);
        cameraTv = (TextView) popView.findViewById(R.id.tv_pop_add_image_camera);
        mapTv = (TextView) popView.findViewById(R.id.tv_pop_add_image_map);
        cancelTv = (TextView) popView.findViewById(R.id.tv_pop_add_image_cancel);
        cameraTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                if (Utils.hasSdcard()) {
                    requestPhoto();
                } else {
                    Utils.toast(ComplainActivity.this, "Sd卡不可用");
                }
            }
        });
        mapTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                startActivityForResult(new Intent(ComplainActivity.this, PicActivity.class), IntentConfig.PIC_REQUEST);
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });
    }

    private void initData() {
        toComplainBean = (ToComplainBean) getIntent().getSerializableExtra(IntentConfig.toComplain);
        toComplainBean.setAuthorId(UserUtils.readUserData(ComplainActivity.this).getId());
        list = new ArrayList<>();
        adapter = new ComplainImageAdapter(this, list);
        upLoadImageList = new ArrayList<>();
        complainPresenter = new ComplainPresenter(this);
    }

    private void setData() {
        gv.setAdapter(adapter);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        cplIsTv.setOnClickListener(this);
        addImageRl.setOnClickListener(this);
        submitTv.setOnClickListener(this);
        cplIsLv.setOnItemClickListener(this);
        gv.setOnItemClickListener(this);
        contentEt.addTextChangedListener(contentTw);
    }

    private void loadData() {
        cpd.show();
        complainPresenter.userInfo(NetConfig.userInfoUrl + toComplainBean.getAgainstId());
    }

    @Override
    public void userInfoSuccess(String json) {
        userInfoBean = DataUtils.getUserInfoBean(json);
        handler.sendEmptyMessage(USER_INFO_SUCCESS);
    }

    @Override
    public void userInfoFailure(String failure) {

    }

    @Override
    public void userSkillSuccess(String json) {
        String[] arr = userInfoBean.getU_skills().split(",");
        List<String> skillIdList = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            skillIdList.add(arr[i]);
        }
        List<String> skillNameList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < skillNameList.size(); i++) {
            sb.append(skillNameList.get(i));
            if (i != skillNameList.size() - 1) {
                sb.append("、");
            }
        }
        skillName = sb.toString();
        handler.sendEmptyMessage(USER_SKILL_SUCCESS);
    }

    @Override
    public void userSkillFailure(String failure) {

    }

    @Override
    public void userIssueSuccess(String json) {
        complainIssueBeanList.addAll(DataUtils.getComplainIssueBeanList(json));
        handler.sendEmptyMessage(USER_ISSUE_SUCCESS);
    }

    @Override
    public void userIssueFailure(String failure) {

    }

    @Override
    public void submitSuccess(String json) {
        handler.sendEmptyMessage(SUBMIT_SUCCESS);
    }

    @Override
    public void submitFailure(String failure) {

    }

    private void notifyData() {
        cpd.dismiss();
        Picasso.with(ComplainActivity.this).load(userInfoBean.getU_img()).placeholder(R.mipmap.person_face_default).error(R.mipmap.person_face_default).into(iconIv);
        String sex = userInfoBean.getU_sex();
        if (sex.equals("0")) {
            sexIv.setImageResource(R.mipmap.female);
        } else if (sex.equals("1")) {
            sexIv.setImageResource(R.mipmap.male);
        }
        nameTv.setText(userInfoBean.getU_true_name());
        skillNameTv.setText(skillName);
        evaluateTv.setText("好评" + userInfoBean.getU_high_opinions() + "次");
        complainIssueAdapter.notifyDataSetChanged();
    }

    private void submitData() {
        if (TextUtils.isEmpty(toComplainBean.getCtId())) {
            Utils.toast(ComplainActivity.this, "请选择投诉问题");
        } else {
            cpd.show();
            complainPresenter.submit(NetConfig.complainSubmitUrl, toComplainBean);
        }
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    private void requestPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int p = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (p != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PermissionConfig.CAMERA);
            } else {
                takePhoto();
            }
        } else {
            takePhoto();
        }
    }

    private void takePhoto() {
        Intent cI = new Intent();
        cI.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String dateStr = sdf.format(new Date());
        picPath = PathConfig.cameraPath + "IMG_" + dateStr + ".jpg";
        File picFile = new File(picPath);
        Uri picUri = Uri.fromFile(picFile);
        cI.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        startActivityForResult(cI, PermissionConfig.CAMERA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_complain_return:
                finish();
                break;
            case R.id.tv_complain_issue:
                cplIsPop.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                backgroundAlpha(0.5f);
                cplIsPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(1.0f);
                    }
                });
                break;
            case R.id.rl_complain_add_image:
                if (list.size() < 5) {
                    if (pop.isShowing()) {
                        pop.dismiss();
                    } else {
                        pop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                        backgroundAlpha(0.5f);
                        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                backgroundAlpha(1.0f);
                            }
                        });
                    }
                } else {
                    Utils.toast(ComplainActivity.this, "最多上传五张!");
                }
                break;
            case R.id.tv_complain_sumit:
                Utils.log(ComplainActivity.this, toComplainBean.toString());
                submitData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionConfig.CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Utils.toast(ComplainActivity.this, "请在系统设置里打开相机功能");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionConfig.CAMERA && resultCode == RESULT_OK) {
            Utils.log(ComplainActivity.this, "camera=" + picPath);
            upLoadImageList.add(picPath);
            Utils.log(ComplainActivity.this, "upLoadImageList=" + upLoadImageList.toString());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            options.inTempStorage = new byte[1024];
            Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
            list.add(bitmap);
            adapter.notifyDataSetChanged();
        }
        if (requestCode == IntentConfig.PIC_REQUEST && resultCode == IntentConfig.PIC_RESULT && data != null) {
            List<String> l = new ArrayList<>();
            l.addAll(data.getStringArrayListExtra(IntentConfig.PIC));
            upLoadImageList.addAll(l);
            Utils.log(ComplainActivity.this, "upLoadImageList=" + upLoadImageList.toString());
            Utils.log(ComplainActivity.this, "l=" + l.toString());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            options.inTempStorage = new byte[1024];
            for (int i = 0; i < l.size(); i++) {
                Bitmap bitmap = BitmapFactory.decodeFile(l.get(i), options);
                list.add(bitmap);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_pop_lv:
                cplIsTv.setText(complainIssueBeanList.get(position).getName());
                toComplainBean.setCtId(complainIssueBeanList.get(position).getId());
                cplIsPop.dismiss();
                break;
            case R.id.gv_complain_image:
                list.remove(position);
                upLoadImageList.remove(position);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    TextWatcher contentTw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            toComplainBean.setContent(s.toString());
        }
    };
}
