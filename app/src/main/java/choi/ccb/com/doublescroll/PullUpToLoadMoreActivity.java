package choi.ccb.com.doublescroll;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import choi.ccb.com.doublescroll.view.DoubleScrollViewLayout;
import choi.ccb.com.doublescroll.view.EndScrollViewLayout;

public class PullUpToLoadMoreActivity extends AppCompatActivity {

    private DoubleScrollViewLayout doubleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_up_to_load_more);
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.sw);
        doubleLayout = findViewById(R.id.doubleLayout);
        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doubleLayout.scrollToTop();
            }
        });
        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doubleLayout.scrollTo1Y();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1800);
            }
        });

        doubleLayout.setOnScrollTopListener(new DoubleScrollViewLayout.onScrollTopListener() {
            @Override
            public void isTop(boolean top) {
                if (doubleLayout.getCurrPosition() == 0) {
                    swipeRefreshLayout.setEnabled(top);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

    }
}
