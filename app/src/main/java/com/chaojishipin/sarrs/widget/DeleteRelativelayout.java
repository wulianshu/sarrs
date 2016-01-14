package com.chaojishipin.sarrs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.utils.LogUtil;

/**
 * @author xll
 * @date 2015 10 18
 * @des 侧滑Menu自定义布局
 */
@SuppressLint("InlinedApi")
public class DeleteRelativelayout extends RelativeLayout {
	private static final int STATE_CLOSE = 0;
	private static final int STATE_OPEN = 1;
	private int state = STATE_CLOSE;
	private int x;
	private View view;
	private int mHeight;
	private int mWidth;
	private int mSwipeViewHeigh;
	private int mSwipeViewWidth;
	private PullToRefreshSwipeMenuListView.SwipeMenuStatusListener swipeMenuStatusListener;

	public SarrsMainMenuView getGoneView() {
		return goneView;
	}

	private SarrsMainMenuView goneView;
	private static int startR, startL;

	@SuppressLint("ResourceAsColor")
	public DeleteRelativelayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		goneView = new SarrsMainMenuView(context,attrs);
		//goneView.setText(context.getString(R.string.delete_up));
		//goneView.setTextColor(Color.WHITE);
		LayoutParams layoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		//84
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		goneView.setLayoutParams(layoutParams);
		addView(goneView,layoutParams);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (goneView != null) {
			setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
			// 测算滑动view高度
			RelativeLayout chileHolder=(RelativeLayout)getChildAt(1);
			measureChild(chileHolder, widthMeasureSpec, heightMeasureSpec);
			mSwipeViewWidth = chileHolder.getMeasuredWidth();
			mSwipeViewHeigh = chileHolder.getMeasuredHeight();
			LogUtil.e("xll", "onMeasure swipeView w h " + mSwipeViewWidth + " " + mSwipeViewHeigh);
			/*FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,mSwipeViewHeigh);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			goneView.setLayoutParams(layoutParams);*/
			/*LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT,
					mSwipeViewHeigh);
			//84
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			goneView.setLayoutParams(layoutParams);*/






			// 测算menuView高度
			measureChild(goneView, widthMeasureSpec, heightMeasureSpec);
			mWidth = goneView.getMeasuredWidth();
			mHeight = goneView.getMeasuredHeight();
			//保存测量高度以及测量宽度
			setMeasuredDimension(mSwipeViewWidth,mSwipeViewHeigh);
			LogUtil.e("xll","onMeasure goneview w h "+mWidth+" "+mHeight);
		}
	}


	boolean  onSwipe(MotionEvent event){
		boolean isEat=false;
			if(this.getChildCount()>1){
				view=this.getChildAt(1);
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
							// open
							smoothOpenMenu();
						} else {
							//close
							smoothCloseMenu();
						}
					} else {

						if (view.getRight() > (goneView.getLeft() + goneView
								.getRight()) / 2) {
							//close
							smoothCloseMenu();
						} else {
							//open
							smoothOpenMenu();
						}
					}
				}
			}else{
				LogUtil.e("xll","deleteRelativeLayout child size less than 1");
				return isEat;
			}


			return true;

	}
	public boolean isOpen() {
		return state == STATE_OPEN;
	}


	public void smoothCloseMenu() {
		if (isOpen()) {
			view.layout(startL, view.getTop(), startR,
					view.getBottom());
			postInvalidate();
			state = STATE_CLOSE;
			if (swipeMenuStatusListener != null)
				swipeMenuStatusListener.onMenuSmoothClose();
		}
	}

	public void smoothOpenMenu() {
		view.layout(startL - goneView.getWidth(),
				view.getTop(), goneView.getLeft(),
				view.getBottom());
		LogUtil.e("xll", "swipeMenu  w h " + goneView.getWidth() + " " + goneView.getHeight());
		postInvalidate();
		state = STATE_OPEN;
		if (swipeMenuStatusListener != null)
			swipeMenuStatusListener.onMenuSmoothOpen();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		LogUtil.e("xll","onLayout "+(b-t));
		if (changed) {
			int height = b - t;

			if(height>mSwipeViewHeigh){
				height=mSwipeViewHeigh;
			}else if(height<mSwipeViewHeigh){
				height=mSwipeViewHeigh;
			}
			LogUtil.e("xll","onLayout ischange "+(b-t));
			LinearLayout linearLayout = (LinearLayout) ((FrameLayout) goneView.getChildAt(0)).getChildAt(0);
			int childCount=linearLayout.getChildCount();
			for(int i=0;i<childCount;i++){
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mSwipeViewHeigh / childCount);
				LinearLayout child=(LinearLayout)linearLayout.getChildAt(i);
				child.setLayoutParams(params);
			}
		}

	}

	public void setSwipeMenuStatusListener(PullToRefreshSwipeMenuListView.SwipeMenuStatusListener listener)
	{
		this.swipeMenuStatusListener = listener;
	}
}


