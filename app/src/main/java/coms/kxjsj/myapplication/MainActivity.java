package coms.kxjsj.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewDrag viewDrag = (ViewDrag) findViewById(R.id.viewDrag);
        viewDrag.setSettingViewCallBack(new ViewDrag.SettingViewCallBack() {
            @Override
            void startSetting(View view) {
                Toast.makeText(view.getContext(),"动画开始",Toast.LENGTH_SHORT).show();
            }

            @Override
            void endSetting(View view) {
                Toast.makeText(view.getContext(),"动画结束",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
