package activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.gjzg.R;

import java.util.ArrayList;
import java.util.List;

import intface.OnRefreshListener;
import view.CListView;
import view.CProgressDialog;

public class MainActivity extends AppCompatActivity implements OnRefreshListener {

    private View rootView;
    private CProgressDialog cProgressDialog;
    private CListView cListView;
    private List<String> dataList;
    private ArrayAdapter<String> adapter;

    private final int FIRST = 0, REFRESH = 1, LOAD = 2;
    private int STATE_LOAD;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case 1:
                        switch (STATE_LOAD){
                            case FIRST:
                                cProgressDialog.cDismiss();
                                break;
                            case REFRESH:
                                
                                break;
                            case LOAD:
                                break;
                        }
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    };

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
        loadData();
    }

    private void initView() {
        initRootView();
        initDialogView();
    }

    private void initRootView() {
        cListView = (CListView) rootView.findViewById(R.id.clv);
    }

    private void initDialogView() {
        cProgressDialog = new CProgressDialog(this, R.style.dialog_cprogress);
    }

    private void initData() {
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        STATE_LOAD = FIRST;
    }

    private void setData() {
        cListView.setAdapter(adapter);
    }

    private void setListener() {
        cListView.setOnRefreshListener(this);
    }

    private void loadData() {
        cProgressDialog.cShow();
        for (int i = 0; i < 20; i++) {
            dataList.add(i + "");
        }
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    @Override
    public void onDownPullRefresh() {
        STATE_LOAD = REFRESH;
        loadData();
    }

    @Override
    public void onLoadingMore() {
        STATE_LOAD = LOAD;
        loadData();
    }
}
