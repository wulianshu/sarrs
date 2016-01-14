package com.chaojishipin.sarrs.widget;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.bean.SarrsSlideMenuItem;
import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xll on 2015/10/13.
 * 滑动listview Itemview
 */
public class SarrsListItemView extends RelativeLayout {


    /**
     *  添加 滑动以及手势
     * */
    private static final int STATE_CLOSE = 0;
    private static final int STATE_OPEN = 1;
    private int mDownX;
    private int state = STATE_CLOSE;
    private GestureDetectorCompat mGestureDetector;
    private GestureDetector.SimpleOnGestureListener mGestureListener;
    private boolean isFling;
    private int MIN_FLING = Utils.dip2px(15);
    private int MAX_VELOCITYX = -Utils.dip2px(500);
    private ScrollerCompat mOpenScroller;
    private ScrollerCompat mCloseScroller;


    private int mBaseX;
    private int position;
    private Context mContext;

    // 滑动速率
    private static final int SNAP_VELOCITY = 1000;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    /**
     *  设置对应滑动的listview 的itemview 下面menu的宽度即，滑动宽度
     * */

    private static int mMenuWidth;

    public SarrsListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        /**
         *   初始化手势
         * *//*
        mGestureListener=new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                isFling = false;
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                if ((e1.getX() - e2.getX()) > MIN_FLING
                        && velocityX < MAX_VELOCITYX) {
                    isFling = true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        };
           mGestureDetector = new GestureDetectorCompat(getContext(),
                mGestureListener);*/
        /**
         *  自动滚动 初始化
         * */
            mCloseScroller = ScrollerCompat.create(getContext());
            mOpenScroller = ScrollerCompat.create(getContext());
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();


    }

    public  static  void setMenuWidth(int width){
        SarrsListItemView.mMenuWidth=width;
    }
    public SarrsListItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }




    public boolean isOpen() {
        return state == STATE_OPEN;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.e("xll", "list item touch " + event.getAction());
        return super.onTouchEvent(event);
    }

    /**
     *  开始滑动
     * */
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        postInvalidate();
    }

    /**
     *  开始滑动
     *  @param dis  滑动偏移量
     * */
    private void swipe(int dis) {
        if(dis>mMenuWidth){
            dis=mMenuWidth;
        }else if(dis<-mMenuWidth){
            dis=-mMenuWidth;
        }
        layout(-dis, getTop(),
                getWidth()-dis, getMeasuredHeight());

        postInvalidate();

        LogUtil.e("xll", " swipe over " + getLeft());
    }



    @Override
    public void computeScroll() {
        if (state == STATE_OPEN) {
            if (mOpenScroller.computeScrollOffset()) {
                swipe(mOpenScroller.getCurrX());
                postInvalidate();
            }
        } else {
            if (mCloseScroller.computeScrollOffset()) {
                swipe(mBaseX - mCloseScroller.getCurrX());
                LogUtil.e("xll","mBase X "+mBaseX);
                postInvalidate();
            }
        }
    }


    public void smoothCloseMenu() {
        state = STATE_CLOSE;
        mBaseX = -getLeft() / 2;
        mCloseScroller.startScroll(0, 0, mBaseX, 0, 350);// 滚动的final位置
      //  mCloseScroller.startScroll(getLeft(), getTop(),getLeft(), getTop(), 250);// 滚动的final位置
        postInvalidate();
    }
    // 创建open动画
    public void smoothOpenMenu() {
        state = STATE_OPEN;
       /* mOpenScroller.startScroll(getLeft(), getTop(),
                -mMenuWidth - getLeft(), getTop(), 250);*/
        mOpenScroller.startScroll(-getLeft() / 2, 0,
                getWidth(), 0, 350);
        postInvalidate();

    }
    // 创建close动画
    public void closeMenu() {
        /*if (mCloseScroller.computeScrollOffset()) {
            mCloseScroller.abortAnimation();
        }*/
        if (state == STATE_OPEN) {
            state = STATE_CLOSE;
            swipe(0);
        }
    }
    /**
     *   执行滑动 的touch事件
     *
     * */
    int openLeft=0;
    int dx=0;
   /* public boolean onSwipe(MotionEvent event) {

        LogUtil.e("xll","onMove Height "+getMeasuredHeight());
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                openLeft=getLeft();
                mDownX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                dx = (int) (mDownX - event.getX());
                if(state==STATE_CLOSE){
                    if(mDownX-event.getX()<0){
                        //eat event
                        LogUtil.e("xll ","OnMove left "+getLeft());
                        if(getLeft()<0){
                            onMoveRight(event);
                        }else{
                            //eat event
                            LogUtil.e("xll", " state Close!  right");
                        }

                        return true;
                    }else{

                        swipe(dx);

                    }
                }
                if (state == STATE_OPEN) {
                    if(mDownX-event.getX()>=0){
                        //open 向左侧滑动
                        if(getLeft()>-mMenuWidth){
                            onMoveLeft(event);
                        }else{
                            LogUtil.e("xll"," state Open!  left");
                        }
                        LogUtil.e("xll ","OnMove left "+getLeft());

                        return false;
                    }else {
                        onMoveRight(event);
                    }
                    LogUtil.e("xll", "state " + openLeft);
                }



                break;
            case MotionEvent.ACTION_UP:
                LogUtil.e("xll","open touch baseX "+mBaseX);
                LogUtil.e("xll","open touch getX "+getX());

                if(mBaseX-getX()>=mMenuWidth/2){
                        smoothOpenMenu();
                }else {
                        if(mBaseX-getX()>=0){
                            if(state==STATE_OPEN){
                                return true;
                            }else{
                                smoothCloseMenu();
                            }

                        }
                    if(getX()-mBaseX>mMenuWidth/3){
                        smoothCloseMenu();
                    }else{
                        if(state==STATE_CLOSE){
                           return true;
                        }else{
                            smoothOpenMenu();
                        }


                    }



                }
                break;
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        return super.onTouchEvent(event);
    }*/

    boolean onMoveLeft(MotionEvent event){
        boolean isEat=false;
        // 设置向右滑动阀值
        int deltX=(int)Math.abs(mDownX-event.getX());
        LogUtil.e("xll"," state Open!  righ deleta is "+deltX);
        if(deltX>0){
            openLeft=getLeft()-deltX;
            // state open 左滑出界
            if(openLeft<=-mMenuWidth){
                openLeft=-mMenuWidth;
                layout(openLeft, getTop(),
                        getWidth(), getMeasuredHeight());
                if(getLeft()==-mMenuWidth){
                    state=STATE_OPEN;
                }
                postInvalidate();
                isEat=true;
                return isEat;
            }else{
                openLeft=getLeft()-deltX;
                layout(openLeft, getTop(),
                        getWidth(), getHeight());
                if(getLeft()==-mMenuWidth){
                    state=STATE_OPEN;
                }
                postInvalidate();
            }
        }
        return isEat;
    }

    boolean onMoveRight(MotionEvent event){
        boolean isEat=false;
           // 设置向右滑动阀值
           int deltX=(int)Math.abs(mDownX-event.getX());
           LogUtil.e("xll"," state Open!  righ deleta is "+deltX);
           if(deltX>0){
               if(deltX>mMenuWidth){
                   deltX=mMenuWidth;
               }
               openLeft=getLeft()+deltX;
               LogUtil.e("xll", " openLeft" + openLeft);
               //  左侧边界大于0，向右滑动
               if(openLeft>=0){
                   openLeft=0;
                   layout(openLeft, getTop(),
                           getWidth(), getMeasuredHeight());
                   if(getLeft()==0){
                       state=STATE_CLOSE;
                   }

                   postInvalidate();
                   isEat=true;
                   return isEat;
               }else{
                   openLeft=getLeft()+deltX;
                   LogUtil.e("xll", " onMove width " + getWidth());

                   layout(openLeft, getTop(),
                           getWidth(), getHeight());
                   postInvalidate();
                   if(openLeft<=-mMenuWidth){
                       openLeft=-mMenuWidth;
                       if(getLeft()==-mMenuWidth){
                           state=STATE_OPEN;
                       }

                   }
               }
           }
  return isEat;
   }

    private static int startR, startL;
    private View view;
    private int x;
    SarrsMainMenuView goneView;
    public void setDeleteView(SarrsMainMenuView goneView) {
         this.goneView=goneView;
    }
  boolean  onSwipe(MotionEvent event){
      {
          if (event.getAction() == MotionEvent.ACTION_DOWN) {
              x = (int) event.getX();
              if (startL == 0 && startR == 0) {
                  startR = view.getRight();
                  startL = view.getLeft();
              }
          } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
              int tempRight = view.getRight()
                      - (x - (int) event.getX());
              if (x - (int) event.getX() > 0) {
                  if (tempRight < goneView.getLeft()) {
                      view.layout(startL - goneView.getWidth(),
                              view.getTop(), goneView.getLeft(),
                              view.getBottom());
                  } else {
                      view.layout(
                              (int) (view.getLeft() - (x - (int) event
                                      .getX())),
                              view.getTop(),
                              (int) (view.getRight() - (x - (int) event
                                      .getX())), view.getBottom());
                  }

              } else if ((int) event.getX() - x > 0) {
                  tempRight = view.getRight()
                          + ((int) event.getX() - x);
                  if (tempRight > goneView.getRight()) {
                      view.layout(startL, view.getTop(), startR,
                              view.getBottom());
                  } else {
                      view.layout(
                              (int) (view.getLeft() + ((int) event
                                      .getX() - x)), view.getTop(),
                              (int) (view.getRight() + ((int) event
                                      .getX() - x)), view.getBottom());
                  }
              }
          } else if (event.getAction() == MotionEvent.ACTION_UP) {
              if ((int) event.getX() < x) {
                  if (view.getRight() < (goneView.getLeft() + goneView
                          .getRight()) / 2) {
                      view.layout(startL - goneView.getWidth(),
                              view.getTop(), goneView.getLeft(),
                              view.getBottom());
                  } else {
                      view.layout(startL, view.getTop(), startR,
                              view.getBottom());
                  }
              } else {
                  if (view.getRight() > (goneView.getLeft() + goneView
                          .getRight()) / 2) {
                      view.layout(startL, view.getTop(), startR,
                              view.getBottom());

                  } else {
                      view.layout(startL - goneView.getWidth(),
                              view.getTop(), goneView.getLeft(),
                              view.getBottom());
                  }
              }
          }
          return true;
      }
    }

  /*  @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            view =this;
            this.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        x = (int) event.getX();
                        if (startL == 0 && startR == 0) {
                            startR = view.getRight();
                            startL = view.getLeft();
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        int tempRight = view.getRight()
                                - (x - (int) event.getX());
                        if (x - (int) event.getX() > 0) {
                            if (tempRight < goneView.getLeft()) {
                                view.layout(startL - goneView.getWidth(),
                                        view.getTop(), goneView.getLeft(),
                                        view.getBottom());
                            } else {
                                view.layout(
                                        (int) (view.getLeft() - (x - (int) event
                                                .getX())),
                                        view.getTop(),
                                        (int) (view.getRight() - (x - (int) event
                                                .getX())), view.getBottom());
                            }

                        } else if ((int) event.getX() - x > 0) {
                            tempRight = view.getRight()
                                    + ((int) event.getX() - x);
                            if (tempRight > goneView.getRight()) {
                                view.layout(startL, view.getTop(), startR,
                                        view.getBottom());
                            } else {
                                view.layout(
                                        (int) (view.getLeft() + ((int) event
                                                .getX() - x)), view.getTop(),
                                        (int) (view.getRight() + ((int) event
                                                .getX() - x)), view.getBottom());
                            }
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        if ((int) event.getX() < x) {
                            if (view.getRight() < (goneView.getLeft() + goneView
                                    .getRight()) / 2) {
                                view.layout(startL - goneView.getWidth(),
                                        view.getTop(), goneView.getLeft(),
                                        view.getBottom());
                            } else {
                                view.layout(startL, view.getTop(), startR,
                                        view.getBottom());
                            }
                        } else {
                            if (view.getRight() > (goneView.getLeft() + goneView
                                    .getRight()) / 2) {
                                view.layout(startL, view.getTop(), startR,
                                        view.getBottom());

                            } else {
                                view.layout(startL - goneView.getWidth(),
                                        view.getTop(), goneView.getLeft(),
                                        view.getBottom());
                            }
                        }
                    }
                    return true;
                }
            });
        }
    }*/
}




  /*  public boolean onSwipe(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        if(event.getAction()==MotionEvent.ACTION_HOVER_MOVE){
            return true;
        }
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                LogUtil.e("xll","down");
                mDownX = (int) event.getX();
                isFling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // LogUtil.e("xll","move");
                int dis = (int) (mDownX - event.getX());
                LogUtil.e("xll","move "+dis);
            *//*    private static final int STATE_CLOSE = 0;
                private static final int STATE_OPEN = 1;*//*
                LogUtil.e("xll","move "+state);
                if(state==STATE_CLOSE){
                    if(mDownX-event.getX()<0){
                        //eat event
                        LogUtil.e("xll"," state Close!  right");
                        return true;
                    }
                }
                if (state == STATE_OPEN) {
                    int openLeft=0;
                    openLeft=-mMenuWidth;
                    if(mDownX-event.getX()>=0){
                        //不做处理
                        LogUtil.e("xll"," state Open!  left");
                        return false;
                    }else {
                        // 百分比滑动速率
                        //double deltX=((Math.abs(mDownX-event.getX()))*(double)(Math.round(mMenuWidth/getWidth())/10.0));
                        // 设置向右滑动阀值
                        int deltX=(int)Math.abs(mDownX-event.getX());

                        LogUtil.e("xll"," state Open!  righ deleta is "+deltX);
                        if(deltX>0){
                            if(deltX>mMenuWidth){
                                deltX=mMenuWidth;
                            }
                            openLeft=openLeft+deltX;
                            LogUtil.e("xll", " openLeft" + openLeft);
                            if(openLeft>=0){
                                openLeft=0;
                                layout(0, getTop(),
                                        getWidth(), getHeight());
                                // state=STATE_CLOSE;
                                postInvalidate();
                                return true;
                            }else{
                                LogUtil.e("xll","listitem width"+getWidth());
                                LogUtil.e("xll","listitem right x1"+getWidth()+openLeft);
                                LogUtil.e("xll", "listitem right x2" + getWidth() + (-openLeft));


                                if(getX()-mBaseX>=mMenuWidth){
                                    LogUtil.e("xll", "listitem delta over" + (getX() - mBaseX));
                                    layout(0, getTop(),
                                            getWidth(), getMeasuredHeight());
                                    // scrollTo(0,0);
                                }else{
                                    LogUtil.e("xll", "listitem delta inner" + (getX() - mBaseX));
                                    //scrollTo(-openLeft,0);
                                    layout(openLeft, getTop(),
                                            getWidth() + openLeft, getHeight());
                                }

                                postInvalidate();

                            }


                        }

                        // 向右滑动
                    }
                    LogUtil.e("xll", "state " + openLeft);
                }else{
                    swipe(dis);
                }




                break;
            case MotionEvent.ACTION_UP:
                LogUtil.e("xll","open touch baseX "+mBaseX);
                LogUtil.e("xll","open touch getX "+getX());

                if(mBaseX-getX()>=mMenuWidth/2){
                    smoothOpenMenu();
                }else {
                    if(mBaseX-getX()>=0){
                        if(state==STATE_OPEN){
                            return true;
                        }else{
                            smoothCloseMenu();
                        }

                    }
                    if(getX()-mBaseX>mMenuWidth/2){
                        smoothCloseMenu();
                    }else{
                        if(state==STATE_CLOSE){
                            return true;
                        }else{
                            smoothOpenMenu();
                        }


                    }



                }
                break;
        }
        return super.onTouchEvent(event);
    }
*/



