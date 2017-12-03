package coms.kxjsj.myapplication;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DateSorter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vange on 2017/12/1.
 */

public class ViewDrag extends FrameLayout{

    private ViewDragHelper viewDragHelper;
    private ViewDragHelper.Callback callback;

    private List<View> attchedView=new ArrayList<>();

    private DragCardAdapter adapter;
    private final DataSetObserver observer=new DataSetObserver() {
        @Override
        public void onChanged() {
            handleViews();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };
    /**
     * 标记settleCapturedViewAt开始
     */
    private boolean startSetting;
    private boolean startSettingBack;
    int currentFront;

    public ViewDrag(@NonNull Context context) {
        this(context,null);
    }

    public ViewDrag(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ViewDrag(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAdapter(DragCardAdapter adapter){
        this.adapter=adapter;
        adapter.registerDataSetObserver(observer);
        adapter.notifyDataSetChanged();
    }

    private void handleViews(){
        if(adapter==null||adapter.getCount()==0){
            return;
        }
        removeAllViews();
        attchedView.clear();
        int cachedSize = adapter.getCachedSize();
        for (int i = currentFront+cachedSize-1; i >=0; i--) {
            View view = adapter.getView(i%adapter.getCount(),null, this);
            attchedView.add(view);
            addView(view);
        }
        for (int i = 0; i < attchedView.size(); i++) {
            View view=attchedView.get(i);
            view.setTranslationY(-(getChildCount()-i-1)*dp2px(10));
            view.setScaleX(1f-0.3f*(getChildCount()-i)/getChildCount());
            view.setScaleY(1f-0.3f*(getChildCount()-i)/getChildCount());
        }
    }
    private void init() {
        callback = new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child==attchedView.get(attchedView.size()-1)||(attchedView.size()>1&&startSetting&&child==attchedView.get(attchedView.size()-2));
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);

                if(startSettingBack){
                    return;
                }
                    float rotationX = changedView.getRotation();
                    int measuredWidth = getMeasuredWidth();
                    int measuredWidth1 = changedView.getMeasuredWidth();

                    if(left>measuredWidth/2-measuredWidth1/2&&rotationX<=0) {
                        changedView.setRotation(10);
                    }else if(left<measuredWidth/2-measuredWidth1/2&&rotationX>=0){
                        changedView.setRotation(-10);
                    }
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                if(settingViewCallBack!=null){
                    settingViewCallBack.captureView(capturedChild,currentFront);
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int left = releasedChild.getLeft();
                int right = releasedChild.getRight();
                int measuredWidth = getMeasuredWidth();
                int measuredHeight = getMeasuredHeight();
                if(left<0||right>measuredWidth-getPaddingLeft()) {
                    if (right > measuredWidth-getPaddingLeft()) {
                        left = measuredWidth - getPaddingRight() - getPaddingRight();
                    } else {
                        left = -releasedChild.getMeasuredWidth();
                    }
                    viewDragHelper.settleCapturedViewAt(left, measuredHeight/2- releasedChild.getMeasuredHeight()/2);
                    startSetting = true;
                    if (settingViewCallBack != null) {
                        settingViewCallBack.startSetting(releasedChild,currentFront);
                    }
                }else{
                    releasedChild.setRotation(0);
                    viewDragHelper.settleCapturedViewAt((measuredWidth- releasedChild.getMeasuredWidth())/2, measuredHeight/2- releasedChild.getMeasuredHeight()/2);
                    startSettingBack=true;
                }
                invalidate();
                System.out.println("xvel"+xvel+"yvel"+yvel);

            }

            @Override
            public void onEdgeTouched(int edgeFlags, int pointerId) {
                super.onEdgeTouched(edgeFlags, pointerId);
            }

            @Override
            public boolean onEdgeLock(int edgeFlags) {
                return super.onEdgeLock(edgeFlags);
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                super.onEdgeDragStarted(edgeFlags, pointerId);
            }

            @Override
            public int getOrderedChildIndex(int index) {
                return super.getOrderedChildIndex(index);
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return 0;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
//                System.out.println("left"+left+"--"+dx);
                return clampBoundary(child,left,0);
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
//                System.out.println("top"+top+"--"+dy);
//                int centre = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2 - child.getMeasuredHeight() / 2;
//                if(top<centre-dp2px(80)){
//                    top=centre-dp2px(80);
//                }else if(top>centre+dp2px(80)){
//                    top=centre+dp2px(80);
//                }
                return clampBoundary(child,top,1);
            }
        };
        viewDragHelper = ViewDragHelper.create(this, 1, callback);
//        Class clazz=ViewDragHelper.class;
//        try {
//            Field mMaxVelocity = clazz.getDeclaredField("mMaxVelocity");
//            mMaxVelocity.setAccessible(true);
//            mMaxVelocity.setFloat(viewDragHelper,100);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

    }

    private int clampBoundary(View view,int current,int orentation){
        if(current<-view.getMeasuredWidth()){
            current=-view.getMeasuredWidth();
        }
        if(orentation== 0) {
            int boundsWidth = getMeasuredWidth() - getPaddingLeft()-getPaddingRight();
            if (current > boundsWidth) {
                current = boundsWidth;
            }
        }else{
            int boundsHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
            if (current > boundsHeight) {
                current = boundsHeight;
            }
        }

        return current;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }
    @Override
    public void computeScroll(){
        handleSettingViewCallBack();
    }

    private void handleSettingViewCallBack(){
        if(startSetting) {
            if (viewDragHelper.continueSettling(true)) {
                invalidate();
            } else {
                startSetting=false;
                View frontview = attchedView.get(attchedView.size() - 1);
                frontview.setRotation(0);
                if(settingViewCallBack!=null){
                    settingViewCallBack.endSetting(frontview,currentFront);
                }
                currentFront++;
                attchedView.remove(frontview);
                removeView(frontview);
                View view = adapter.getView((currentFront+attchedView.size()) % adapter.getCount(), frontview, this);
                attchedView.add(0,view);
                addView(view,0);
                for (int i = 0; i < attchedView.size(); i++) {
                    attchedView.get(i).setTranslationY(-(attchedView.size()-i-1)*dp2px(10));
                    getChildAt(i).setScaleX(1f-0.3f*(getChildCount()-i)/getChildCount());
                    getChildAt(i).setScaleY(1f-0.3f*(getChildCount()-i)/getChildCount());
                }
            }
        }
        if(startSettingBack){
            if (viewDragHelper.continueSettling(true)) {
                invalidate();
            }else{
                startSettingBack=false;
//                View frontview = attchedView.get(attchedView.size() - 1);
//                frontview.setRotation(0);
            }
        }
    }

    SettingViewCallBack settingViewCallBack;

    public void setSettingViewCallBack(SettingViewCallBack settingViewCallBack){
        this.settingViewCallBack=settingViewCallBack;
    }

    public static abstract class SettingViewCallBack{
        /**
         * 开始退回
         * @param view
         */
        void startSetting(View view,int position){}

        /**
         * 结束退回
         * @param view
         */
        void endSetting(View view,int position){}

        /**
         * view捕获了
         * @param view
         */
        void captureView(View view,int position){}
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
}

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        ((LayoutParams)params).gravity= Gravity.CENTER;
        super.addView(child, index, params);
    }
    
    private int dp2px(float dp){
       return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getResources().getDisplayMetrics());
    }
    
    public static abstract class DragCardAdapter<T> extends BaseAdapter{
        List<T> list;
        int cachedSize=7;
       
        public DragCardAdapter(List<T> list,int cachedSize){
            this.list=list;
            this.cachedSize=cachedSize;
        }

        public List<T> getList() {
            return list;
        }

        public int getCachedSize() {
            return cachedSize;
        }

        public void stList(List<T> list){
            this.list=list;
        }
        @Override
        public int getCount() {
            return list==null?0:list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public  View getView(int i, View conventView, ViewGroup viewGroup){
            return getView(i,list.get(i),conventView,viewGroup);
        }

        public abstract View getView(int i,T t, View conventView, ViewGroup viewGroup);
    }
            
}
