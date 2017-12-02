package coms.kxjsj.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ViewDrag viewDrag = (ViewDrag) findViewById(R.id.viewDrag);
        viewDrag.setSettingViewCallBack(new ViewDrag.SettingViewCallBack() {
            @Override
            void startSetting(View view) {
                /**
                 * 刚放手
                 */
            }

            @Override
            void endSetting(View view) {
                /**
                 * 放手后动画结束
                 */
                if(toast==null)
                toast = Toast.makeText(view.getContext(), "移动到最后了", Toast.LENGTH_LONG);
                else {
                    toast.setText("移动到最后了");
                }
                toast.show();
            }
        });
    }
}
