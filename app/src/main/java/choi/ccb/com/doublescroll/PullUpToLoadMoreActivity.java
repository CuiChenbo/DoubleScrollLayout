package choi.ccb.com.doublescroll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import choi.ccb.com.doublescroll.view.DoubleScrollViewLayout;

public class PullUpToLoadMoreActivity extends AppCompatActivity {

    private DoubleScrollViewLayout doubleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_up_to_load_more);

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

    }
}
