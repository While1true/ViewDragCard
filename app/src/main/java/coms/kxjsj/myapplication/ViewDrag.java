package coms.kxjsj.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vange on 2017/12/1.
 */

public class ViewDrag extends FrameLayout{

    private ViewDragHelper viewDragHelper;
    private ViewDragHelper.Callback callback;


    List<View>allViews=new ArrayList<>();

    /**
     * 标记settleCapturedViewAt开始
     */
    private boolean startSetting;

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

    private void init() {
        callback = new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child==allViews.get(allViews.size()-1);
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                if(settingViewCallBack!=null){
                    settingViewCallBack.captureView(capturedChild);
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int left = releasedChild.getLeft();
                int top = releasedChild.getTop();
                int measuredWidth = getMeasuredWidth();
                int measuredHeight = getMeasuredHeight();
                if(left*measuredHeight<=top*measuredWidth) {
                    if (left > measuredWidth / 2 - releasedChild.getMeasuredWidth() / 2) {
                        left = measuredWidth - getPaddingRight() - getPaddingRight();
                    } else {
                        left = -releasedChild.getMeasuredWidth();
                    }
                }else{
                    if (top > measuredHeight / 2 - releasedChild.getMeasuredHeight() / 2) {
                        top = measuredHeight - getPaddingTop() - getPaddingBottom();
                    } else {
                        top = -releasedChild.getMeasuredHeight();
                    }
                }
                viewDragHelper.settleCapturedViewAt(left,top);
                startSetting=true;
                if(settingViewCallBack!=null){
                    settingViewCallBack.startSetting(releasedChild);
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
                return child.getMeasuredHeight();
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
//                System.out.println("left"+left+"--"+dx);
                return clampBoundary(child,left,0);
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
//                System.out.println("top"+top+"--"+dy);
                return clampBoundary(child,top,1);
            }
        };
        viewDragHelper = ViewDragHelper.create(this, 1, callback);
        Class clazz=ViewDragHelper.class;
        try {
            Field mMaxVelocity = clazz.getDeclaredField("mMaxVelocity");
            mMaxVelocity.setAccessible(true);
            mMaxVelocity.setFloat(viewDragHelper,100);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
                View frontview = allViews.get(allViews.size() - 1);
                if(settingViewCallBack!=null){
                    settingViewCallBack.endSetting(frontview);
                }

                allViews.remove(frontview);
                allViews.add(0,frontview);
                removeView(frontview);
                addView(frontview,0);
                for (int i = 0; i < allViews.size(); i++) {
                    allViews.get(i).setTranslationY(-(allViews.size()-i-1)*10);
//                    allViews.get(i).setScaleX(1f/(allViews.size()-i-1));
                }
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
        void startSetting(View view){}

        /**
         * 结束退回
         * @param view
         */
        void endSetting(View view){}

        /**
         * view捕获了
         * @param view
         */
        void captureView(View view){}
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        allViews.clear();
        for (int i = 0; i < getChildCount(); i++) {
            allViews.add(getChildAt(i));
            getChildAt(i).setTranslationY(-(getChildCount()-i-1)*10);
//            getChildAt(i).setScaleX(1f/(getChildCount()-i-1));
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        ((LayoutParams)params).gravity= Gravity.CENTER;
        super.addView(child, index, params);
    }
}
