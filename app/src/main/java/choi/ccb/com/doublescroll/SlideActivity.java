package choi.ccb.com.doublescroll;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import choi.ccb.com.doublescroll.view.slide_layout.SlideDetailsLayout;

public class SlideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        SlideDetailsLayout slideDetailsLayout = findViewById(R.id.slide);
        slideDetailsLayout.setOnSlideDetailsListener(new SlideDetailsLayout.OnSlideDetailsListener() {
            @Override
            public void onStatucChanged(SlideDetailsLayout.Status status) {
                Log.i("ccb", "onStatucChanged: "+status.name());
            }
        });
    }
}
