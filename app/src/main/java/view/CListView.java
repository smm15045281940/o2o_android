package view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gjzg.R;

import intface.OnRefreshListener;

/**
 * Created by Administrator on 2017/7/27.
 */

public class CListView extends ListView implements AbsListView.OnScrollListener {

    private View headView, footView;//头、尾布局
    private ImageView headIv, footIv;//头、尾布局图片
    private TextView headTv;//头布局文字
    private int headViewHeight, footViewHeight;//头、尾布局高度
    private ObjectAnimator headAnim, footAnim;//头、尾布局图片动画

    private int firstVisibleItemPosition, downY;//显示的第一个item的索引、按下时Y轴的偏移量
    private final int DOWN_PULL_REFRESH = 0;
    private final int RELEASE_REFRESH = 1;
    private final int REFRESHING = 2;
    private int currentState;
    private boolean isScrolltoBottom,isLoadingMore;

    private OnRefreshListener mOnRefreshListener;

    public CListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        initHeadView();
        initFootView();
        initAnim();
    }

    private void initHeadView() {
        headView = View.inflate(getContext(), R.layout.head_clistview, null);
        headIv = (ImageView) headView.findViewById(R.id.iv_head_clistview);
        headTv = (TextView) headView.findViewById(R.id.tv_head_clistview);
        headView.measure(0, 0);
        headViewHeight = headView.getMeasuredHeight();
        headView.setPadding(0, -headViewHeight, 0, 0);
        this.addHeaderView(headView);
    }

    private void initFootView() {
        footView = View.inflate(getContext(), R.layout.foot_clistview, null);
        footIv = (ImageView) footView.findViewById(R.id.iv_foot_clistview);
        footView.measure(0, 0);
        footViewHeight = footView.getMeasuredHeight();
        footView.setPadding(0, -footViewHeight, 0, 0);
        this.addFooterView(footView);
    }

    private void initAnim() {
        headAnim = ObjectAnimator.ofFloat(headIv, "rotation", 0.0f, 359.0f);
        headAnim.setDuration(500);
        headAnim.setRepeatCount(-1);
        headAnim.setInterpolator(new LinearInterpolator());
        footAnim = ObjectAnimator.ofFloat(footIv, "rotation", 0.0f, 359.0f);
        footAnim.setDuration(500);
        footAnim.setRepeatCount(-1);
        footAnim.setInterpolator(new LinearInterpolator());
    }

    public void hideHeadView(){
        headAnim.cancel();
        headView.setPadding(0,-headViewHeight,0,0);
        headTv.setText("下拉可以刷新");
        headIv.setImageResource(R.mipmap.ic_launcher);
        currentState = DOWN_PULL_REFRESH;
    }

    public void hideFootView(){
        footAnim.cancel();
        footView.setPadding(0,-footViewHeight,0,0);
        footIv.setImageResource(R.mipmap.ic_launcher);
        isLoadingMore = false;
    }

    private void initData() {
        currentState = DOWN_PULL_REFRESH;
    }

    private void setListener() {
        this.setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                int diff = (moveY - downY) / 2;//间距
                int paddingTop = -headViewHeight + diff;
                if (firstVisibleItemPosition == 0 && -headViewHeight < paddingTop) {
                    if (paddingTop > 0 && currentState == DOWN_PULL_REFRESH) {
                        currentState = RELEASE_REFRESH;
                        refreshHeaderView();
                    } else if (paddingTop < 0 && currentState == RELEASE_REFRESH) {
                        currentState = DOWN_PULL_REFRESH;
                        refreshHeaderView();
                    }
                    headView.setPadding(0, paddingTop, 0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentState == RELEASE_REFRESH) {
                    headView.setPadding(0, 0, 0, 0);
                    currentState = REFRESHING;
                    refreshHeaderView();
                    if(mOnRefreshListener !=null){
                        mOnRefreshListener.onDownPullRefresh();
                    }
                } else if (currentState == DOWN_PULL_REFRESH) {
                    headView.setPadding(0, -headViewHeight, 0, 0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refreshHeaderView() {
        switch (currentState) {
            case DOWN_PULL_REFRESH:
                headTv.setText("下拉可以刷新");
                break;
            case RELEASE_REFRESH:
                headTv.setText("松开即可刷新");
                break;
            case REFRESHING:
                headTv.setText("正在刷新");
                headAnim.start();
                break;
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener){
        mOnRefreshListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == OnScrollListener.SCROLL_STATE_IDLE || scrollState == OnScrollListener.SCROLL_STATE_FLING){
            if(isScrolltoBottom && !isLoadingMore){
                isLoadingMore = true;
                footView.setPadding(0,0,0,0);
                this.setSelection(this.getCount());
                if(mOnRefreshListener != null){
                    footAnim.start();
                    mOnRefreshListener.onLoadingMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstVisibleItemPosition = firstVisibleItem;
        if(getLastVisiblePosition() == (totalItemCount - 1)){
            isScrolltoBottom = true;
        }else{
            isScrolltoBottom = false;
        }
    }
}
