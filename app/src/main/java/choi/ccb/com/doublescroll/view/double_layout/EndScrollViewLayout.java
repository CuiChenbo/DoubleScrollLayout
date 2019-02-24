package choi.ccb.com.doublescroll.view.double_layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * ChenboCui 上下层双View联合滚动 最后一个是滚动布局
 */
public class EndScrollViewLayout extends ViewGroup {
    public static String TAG = DoubleScrollViewLayout.class.getName();

    private ViewGroup TopViewGroup;
    TopBottomMonitorScrollView bottomScrollView;
    VelocityTracker velocityTracker = VelocityTracker.obtain();
    Scroller scroller = new Scroller(getContext());

    int currPosition = 0;
    int position1Y;
    int lastY,lastX;
    public int scaledTouchSlop;//最小滑动距离
    private final int speed = 200;  //触发自动滚动的滑动速度
    private final int TriggerDistance = 211;  //触发自动滚动的距离；
    boolean isIntercept;

    private boolean bottomScrollVIewIsInTop = false; //第二层是否滚动到了顶部
    private boolean topScrollVIewIsInBottom = true;  //第一层是都滚动到了底部


    public EndScrollViewLayout(Context context) {
        super(context);
        init();
    }

    public EndScrollViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EndScrollViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        post(new Runnable() {
            @Override
            public void run() {
                // 因为爱车的Top布局是整屏的，所以现在直接获取第一个控件；
//                if (getChildAt(0) instanceof RelativeLayout || getChildAt(0) instanceof LinearLayout) {
                TopViewGroup = (ViewGroup) getChildAt(0);
//                }else {
//                    ViewGroup viewGroup  = (ViewGroup)getChildAt(0);
//                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
//                        if (viewGroup.getChildAt(i) instanceof RelativeLayout || viewGroup.getChildAt(i) instanceof LinearLayout) {
//                            TopViewGroup = (ViewGroup) getChildAt(i);
//                            break;
//                        }
//                    }
//                }

                //第二层布局必须是TopBottomMonitorScrollView，它可以监听滑动到顶部；
                if (getChildAt(1) instanceof TopBottomMonitorScrollView) {
                    bottomScrollView = (TopBottomMonitorScrollView) getChildAt(1);
                }
//                else if (getChildAt(1) instanceof SwipeRefreshLayout){
//                    SwipeRefreshLayout swipeRefreshLayout  = (SwipeRefreshLayout)getChildAt(1);
//                    for (int i = 0; i < swipeRefreshLayout.getChildCount(); i++) {
//                        if (swipeRefreshLayout.getChildAt(i) instanceof TopBottomMonitorScrollView){
//                            bottomScrollView = (TopBottomMonitorScrollView) getChildAt(i);
//                            break;
//                        }
//                    }
//                }
                else{
                    ViewGroup vg  = (ViewGroup)getChildAt(1);
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        if (vg.getChildAt(i) instanceof TopBottomMonitorScrollView){
                            bottomScrollView = (TopBottomMonitorScrollView) vg.getChildAt(i);
                            break;
                        }
                    }
                }
                bottomScrollView.setScrollListener(new TopBottomMonitorScrollView.ScrollListener() {
                    @Override
                    public void onScroll(int scrollY) {
                        if (scrollY <= 1){
                            bottomScrollVIewIsInTop = true;
                        }else {
                            bottomScrollVIewIsInTop = false;
                        }
                    }

                    @Override
                    public void onScrollToBottom() {

                    }

                    @Override
                    public void onScrollToTop() {

                    }

                    @Override
                    public void notBottom() {

                    }
                });

                position1Y = TopViewGroup.getBottom();

                scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!getIsTopVis()){
            return super.dispatchTouchEvent(ev);
        }
        //防止子View禁止父view拦截事件
        if (currPosition == 1){this.requestDisallowInterceptTouchEvent(false);}
//        this.requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!getIsTopVis()){
            return super.onInterceptTouchEvent(ev);
        }
        int y = (int) ev.getY();
        int x = (int) ev.getX();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                lastX = x;
                break;
            case MotionEvent.ACTION_MOVE:

                if (topScrollVIewIsInBottom) {
                    //判断是否已经滚动到了底部
                    int dy1 = lastY - y;
                    int dx1 = lastX - x;
                    //判断是否是向上滑动&&并且上下滑动的值大于左右滑动的值&&和是否在第一屏
                    if (dy1 > 0 && Math.abs(dy1) > Math.abs(dx1) && currPosition == 0) {
                        if (dy1 >= scaledTouchSlop) {
                            isIntercept = true;//拦截事件
                            lastY = y;
                        }
                    }
                }

//                Log.i("Ccb", "bottomScrollVIewIsInTop: "+bottomScrollVIewIsInTop+"-----");
                if (bottomScrollVIewIsInTop) {
                    int dy = lastY - y;
                    int dx = lastX - x;
                    //判断是否是向下滑动&&并且上下滑动的值大于左右滑动的值&&是否在第二屏
                    if (dy < 0 && Math.abs(dy) > Math.abs(dx) && currPosition == 1) {
                        if (Math.abs(dy) >= scaledTouchSlop) {
                            isIntercept = true;
                            lastY = y; //拦截事件后，重新给lastY赋值Y的位置
                        }
                    }
                }
                break;
        }
        return isIntercept;
    }


    private boolean isRecordDown = false; //是否去记录按下事件，false是记录；
    private int downY; //按下的Y位置
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!getIsTopVis()){
            return super.onTouchEvent(event);
        }
        int y = (int) event.getY();
        velocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!isRecordDown){
                    downY = (int) event.getY();
                    isRecordDown = true;
                }

                int dy = lastY - y;
                if (getScrollY() + dy < 0) {
                    dy = getScrollY() + dy + Math.abs(getScrollY() + dy);
                }
                /*
                 * bottomScrollView.getBottom() 是获取控件的高度，但是如果在scrollview外层在包裹一个布局，获取到的则是外层父控件的高度
                 * 如果在scrollview外层还有父控件时，ScrollView组件只允许一个子View，可以利用这一个特性，获取子View的高度即所要的ScrollView的整体高度
                 * 如果ScrollView的高度刚好一屏，那就让他的高度*2
                 */
//                if (getScrollY() + dy + getHeight() > bottomScrollView.getBottom()) {
//                    dy = dy - (getScrollY() + dy - (bottomScrollView.getBottom() - getHeight()));
//                }
                int fatherHeight = bottomScrollView.getBottom() == getHeight() ? (bottomScrollView.getBottom()*2) : bottomScrollView.getBottom();
//                if (bottomScrollView.getBottom() == getHeight()){
//                    fatherHeight = (bottomScrollView.getBottom())*2;
//                }else{
//                    fatherHeight = bottomScrollView.getBottom();
//                }
                if (getScrollY() + dy + getHeight() > fatherHeight) {
                    dy = dy - (getScrollY() + dy - (fatherHeight - getHeight()));
                }
                scrollBy(0, dy);
//                L.cc(dy+"位移");
                break;
            case MotionEvent.ACTION_UP:
                isRecordDown = false;
                int differY = downY - y;

                isIntercept = false;

                velocityTracker.computeCurrentVelocity(1000);
                float yVelocity = velocityTracker.getYVelocity();

                //滑动速度或滑动距离达到触发点，就进行翻页；
                if (currPosition == 0) {
                    if ((yVelocity < 0 && yVelocity < -speed) || differY > TriggerDistance) {
                        smoothScroll(position1Y);
                        currPosition = 1;
                        if (currPositionChangeListener != null) currPositionChangeListener.currPosition(currPosition);
                    } else {
                        smoothScroll(0);
                    }
                } else {
                    if ((yVelocity > 0 && yVelocity > speed) || differY < -TriggerDistance) {
                        smoothScroll(0);
                        currPosition = 0;
                        if (currPositionChangeListener != null) currPositionChangeListener.currPosition(currPosition);
                    } else {
                        smoothScroll(position1Y);
                    }
                }
                Log.i(TAG, "currPosition: "+currPosition);
                break;
        }
        lastY = y;
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int childTop = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(l, childTop, r, childTop + child.getMeasuredHeight());
            childTop += child.getMeasuredHeight();
        }
    }

    //通过Scroller实现弹性滑动
    private void smoothScroll(int tartY) {
        int dy = tartY - getScrollY();
        scroller.startScroll(getScrollX(), getScrollY(), 0, dy,500);
        invalidate();
    }


    /**
     * 滚动到第二页
     */
    public void scrollTo1Y(){
        smoothScroll(position1Y);
        currPosition = 1;
        bottomScrollView.smoothScrollTo(0,0);
        if (currPositionChangeListener != null) currPositionChangeListener.currPosition(currPosition);
    }
    /**
     * 滚动到第一页
     */
    public void scrollTo0Y(){
        smoothScroll(0);
        currPosition = 0;
        if (currPositionChangeListener != null) currPositionChangeListener.currPosition(currPosition);
    }

    public boolean getIsTopVis(){
        if (TopViewGroup.getVisibility() == GONE){
            return false;
        }else{
            return true;
        }
    }


    public int getCurrPosition(){
        return currPosition;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    public interface onCurrPositionChangeListener{
        /**
         *
         * @param positon 0是第一层，1是第二层
         */
        void currPosition(int positon);
    }

    private onCurrPositionChangeListener currPositionChangeListener;
    public void setOnCurrPositionChangeListener(onCurrPositionChangeListener currPositionChangeListener){
        this.currPositionChangeListener = currPositionChangeListener;
    }

}