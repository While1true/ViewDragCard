
基于ViewDragHelper实现的循环卡片

## 1.效果
## [github地址](https://github.com/While1true/ViewDragCard)
![2017-11-29-10-16-12.gif](https://raw.githubusercontent.com/While1true/ViewDragCard/master/app/src/main/java/coms/kxjsj/myapplication/2017-12-03-12-58-50.gif
)
## 2.使用

```
> 1.数据设置

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
        
>  2.回掉监听
       viewDrag.setSettingViewCallBack(new ViewDrag.SettingViewCallBack() {
            //达到移除刚放手
            @Override
            void startSetting(View view, int position) {
                super.startSetting(view, position);
            }

            //移除动画动画结束
            @Override
            void endSetting(View view, int position) {
                super.endSetting(view, position);
            }

            //抓住了View
            @Override
            void captureView(View view, int position) {
                super.captureView(view, position);
            }
        });
```

## 3.实现
####  一. 基于ViewDragHelper实现拖动

---
ViewDragHelper的使用
1. 在构造函授实例化一个 viewDragHelper = ViewDragHelper.create(this, 1, callback);
2. 将触摸事件委托给viewDragHelper

```
  @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }
```
3. 重写ViewDragHelper.Callback实现View的滑动逻辑

---

####  二. 封装一个BaseAdapter用于设置数据
cachedSize是屏幕最多显示的View数量
```
public static abstract class DragCardAdapter<T> extends BaseAdapter{
        List<T> list;
        int cachedSize=7;
       
        public DragCardAdapter(List<T> list,int cachedSize){
            this.list=list;
            this.cachedSize=cachedSize;
        }

        public int getCachedSize() {
            return cachedSize;
        }
        @Override
        public int getCount() {
            return list==null?0:list.size();
        }

        @Override
        public  View getView(int i, View conventView, ViewGroup viewGroup){
            return getView(i,list.get(i),conventView,viewGroup);
        }

        public abstract View getView(int i,T t, View conventView, ViewGroup viewGroup);
    }
```
####  3.设置缩放平移，达到视差效果
最大的缩放限制为1-0.3
```
for (int i = 0; i < attchedView.size(); i++) {
     attchedView.get(i).setTranslationY(-(attchedView.size()-i-1)*dp2px(10));
     getChildAt(i).setScaleX(1f-0.3f*(getChildCount()-i)/getChildCount());
     getChildAt(i).setScaleY(1f-0.3f*(getChildCount()-i)/getChildCount());
}
```

---
####  4. 循环的实现
用一个变量记录当前最前面卡片的adapter位置，当最外层的被移动到后面时，变量++，并把这个view和adapter位置传给Adapter的getview()重新设置数据。保证最多cachedSize的view在屏幕循环使用

```
//当前最前面的adapter位置
 currentFront++;
attchedView.remove(frontview);
//移除再重新添加到index为0的位置，让层级放到最后面
removeView(frontview)
//重新调用getview设置view的数据显示，并添加进来
View view = adapter.getView((currentFront+attchedView.size()) % adapter.getCount(), frontview, this);
attchedView.add(0,view);
addView(view,0);
```
##  4.总结

---
- 基于利用ViewDragHepler实现View的拖拽，根据释放的位置坐标判断是移到后面，还是归原位。
- 将数据委托给自定义Adapter来获取View，并在需要重新设置数据时，再次调用getView。
- 如有兴趣，请查阅源码，查看细节[github地址](https://github.com/While1true/ViewDragCard)





