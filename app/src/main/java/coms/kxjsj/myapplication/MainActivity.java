package coms.kxjsj.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toast toast;
    int[]colors={0xffffff00,0xff00ff00,0xff00ffff,0xff0000ff,0xff666666,0xff234578};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ViewDrag viewDrag = (ViewDrag) findViewById(R.id.viewDrag);
        List<String>list=new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("这是第"+i+"项");
        }
        viewDrag.setAdapter(new ViewDrag.DragCardAdapter<String>(list,5) {

            @Override
            public View getView(int i, String str, View conventView, ViewGroup viewGroup) {
                if(conventView==null){
                    conventView=getLayoutInflater().inflate(R.layout.ss,viewGroup,false);
                }
                TextView tv=conventView.findViewById(R.id.tv);
                tv.setText(str);
                tv.setBackgroundColor(colors[i%colors.length]);
                return conventView;
            }
        });
//        viewDrag.setSettingViewCallBack(new ViewDrag.SettingViewCallBack() {
//            @Override
//            void startSetting(View view) {
//                /**
//                 * 刚放手
//                 */
//            }
//
//            @Override
//            void endSetting(View view) {
//                /**
//                 * 放手后动画结束
//                 */
//                if(toast==null)
//                toast = Toast.makeText(view.getContext(), "移动到最后了", Toast.LENGTH_LONG);
//                else {
//                    toast.setText("移动到最后了");
//                }
//                toast.show();
//            }
//        });
    }
}
