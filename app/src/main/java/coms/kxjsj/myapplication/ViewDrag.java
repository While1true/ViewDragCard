package coms.kxjsj.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by vange on 2017/12/1.
 */

public class ViewDrag extends FrameLayout{

    private ViewDragHelper viewDragHelper;
    private ViewDragHelper.Callback callback;

    private View dragedView;

    private View settingView;

    /**
     * 标记settleCapturedViewAt开始
     */
    private boolean startSetting;

    /**
     * 视图外是否拦截事件
     */
    private boolean canOutTouch=false;

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
                return true;
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
                dragedView=capturedChild;
                if(settingViewCallBack!=null){
                    settingViewCallBack.captureView(capturedChild);
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                dragedView=null;
                settingView=releasedChild;
                int left = releasedChild.getLeft();
                int top = releasedChild.getTop();
                int measuredWidth = getMeasuredWidth();
                if(left> measuredWidth /2-releasedChild.getMeasuredWidth()/2){
                    left= measuredWidth-getPaddingRight()-releasedChild.getMeasuredWidth();
                }else {
                    left=0;
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
                   if(dragedView==null)
                       return 0;
                return clampBoundary(left,0);
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
//                System.out.println("top"+top+"--"+dy);
                if(dragedView==null)
                    return 0;
                return clampBoundary(top,1);
            }
        };
        viewDragHelper = ViewDragHelper.create(this, 1, callback);
    }

    private int clampBoundary(int current,int orentation){
        if(current<0){
            current=0;
        }
        if(orentation== 0) {
            int boundsWidth = getMeasuredWidth() - getPaddingLeft() - dragedView.getMeasuredWidth();
            if (current > boundsWidth) {
                current = boundsWidth;
            }
        }else{
            int boundsHeight = getMeasuredHeight() - getPaddingTop() - dragedView.getMeasuredHeight();
            if (current > boundsHeight) {
                current = boundsHeight;
            }
        }

        return current;
    }

    View topChildUnder;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        topChildUnder = viewDragHelper.findTopChildUnder((int) ev.getX(), (int) ev.getY());
        System.out.println(topChildUnder==null);
        if(topChildUnder==null){
           return super.onInterceptTouchEvent(ev);
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return topChildUnder!=null;
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
                if(settingViewCallBack!=null){
                    settingViewCallBack.endSetting(settingView);
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


    public boolean isCanOutTouch() {
        return canOutTouch;
    }

    public void setCanOutTouch(boolean canOutTouch) {
        this.canOutTouch = canOutTouch;
    }
}
