package choi.ccb.com.doublescroll;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MyScrollActivity extends AppCompatActivity {

    private NestedScrollView scrollView;
    private int TopViewHeight;
    private boolean is2Y = false; //是否在第一层
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_scroll);
        scrollView = findViewById(R.id.scrollView);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                TopViewHeight = ((ViewGroup)scrollView.getChildAt(0)).getChildAt(0).getMeasuredHeight();
            }
        }, 50);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(handler.obtainMessage(touchEventId, view), 100);
                }
                return false;
            }
        });

    }

    private int lastY = 0;
    private int touchEventId = -9983761;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NestedScrollView scroller = (NestedScrollView) msg.obj;
            if (msg.what == touchEventId) {
                if (lastY == scroller.getScrollY()) {
                    AutoScroll();
                    Log.i("ccb", "handleMessage: 滑动结束");
                    handler.removeMessages(touchEventId);
                } else {
                    Log.i("ccb", "handleMessage: 滑动中ing````````");
                    lastY = scroller.getScrollY();
                    handler.sendMessageDelayed(handler.obtainMessage(touchEventId,scroller), 100);
                }
            }
        }
    };

    //滑动停止后 自动滚动第一屏
    private void AutoScroll() {
        Log.i("ccb", "AutoScroll: "+is2Y +"````"+lastY+"````"+TopViewHeight+"```"+scrollView.getScrollY());

        if (lastY > 10 && TopViewHeight > lastY && !is2Y){  //当在第一层，滑动距离超过一定距离并且没有超过一层的高度时
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0,TopViewHeight);
                    is2Y = true;
                }
            }, 100);
        }else if (is2Y && lastY < TopViewHeight){  //当在第二层，滑动距离停留在一层的高度时
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0,0);
                    is2Y = false;
                }
            }, 100);
        }

        //如果用户直接滑动距离超过第一屏，不需要自动滚动时，记录一下当前的位置；
        if (lastY >= TopViewHeight){
            is2Y = true;
        }else {
            is2Y = false;
        }
    }

    public void click(View view) {
        scrollView.smoothScrollTo(0,TopViewHeight);
        is2Y = true;
    }
}
