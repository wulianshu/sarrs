/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.chaojishipin.sarrs.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.chaojishipin.sarrs.R;
import com.chaojishipin.sarrs.swipe.SwipeMenu;
import com.chaojishipin.sarrs.swipe.SwipeMenuCreator;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.handmark.pulltorefresh.library.LoadingLayoutProxy;
import com.handmark.pulltorefresh.library.OverscrollHelper;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

/**
 *  add by xll
 *  为pull2Refresh添加侧滑菜单
 *
 * */
public class PullToRefreshSwipeMenuListView extends PullToRefreshAdapterViewBase<ListView> {

	private LoadingLayout mHeaderLoadingView;
	private LoadingLayout mFooterLoadingView;
	private FrameLayout mLvFooterLoadingFrame;
	private boolean mListViewExtrasEnabled;
	// swipe
	private int mTouchPosition;
	//old touchView
	//private SwipeMenuLayout mTouchView;
	//new  touchView
	private DeleteRelativelayout mTouchView;
	private OnSwipeListener mOnSwipeListener;
	private SwipeMenuCreator mMenuCreator;
	private OnMenuItemClickListener mOnMenuItemClickListener;
	private SwipeMenuStatusListener mSwipeMenuStatusListener;
	private static final int TOUCH_STATE_NONE = 0;
	private static final int TOUCH_STATE_X = 1;
	private static final int TOUCH_STATE_Y = 2;
	private int MAX_Y = 5;
	private int MAX_X = 3;
	private float mDownX;
	private float mDownY;
	private int mTouchState;
	public boolean loadingListFlag = false;
	private int oldPos=0;
	public PullToRefreshSwipeMenuListView(Context context) {
		super(context);
	}

	public PullToRefreshSwipeMenuListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshSwipeMenuListView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshSwipeMenuListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}
	public DeleteRelativelayout getSwipeMenuLayout(){
		return mTouchView;
	}
	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}
	SarrsMainMenuView.onSlideMenuItemClick mClick;
	public void setOnMenuItemClick(SarrsMainMenuView.onSlideMenuItemClick click){
		this.mClick=click;
	}
	@Override
	protected void onRefreshing(final boolean doScroll) {
		/**
		 * If we're not showing the Refreshing view, or the list is empty, the
		 * the header/footer views won't show so we use the normal method.
		 */
		ListAdapter adapter = mRefreshableView.getAdapter();
		if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
			super.onRefreshing(doScroll);
			return;
		}

		super.onRefreshing(false);

		final LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
		final int selection, scrollToY;

		switch (getCurrentMode()) {
			case MANUAL_REFRESH_ONLY:
			case PULL_FROM_END:
				origLoadingView = getFooterLayout();
				listViewLoadingView = mFooterLoadingView;
				oppositeListViewLoadingView = mHeaderLoadingView;
				selection = mRefreshableView.getCount() - 1;
				scrollToY = getScrollY() - getFooterSize();
				break;
			case PULL_FROM_START:
			default:
				origLoadingView = getHeaderLayout();
				listViewLoadingView = mHeaderLoadingView;
				oppositeListViewLoadingView = mFooterLoadingView;
				selection = 0;
				scrollToY = getScrollY() + getHeaderSize();
				break;
		}

		// Hide our original Loading View
		origLoadingView.reset();
		origLoadingView.hideAllViews();

		// Make sure the opposite end is hidden too
		oppositeListViewLoadingView.setVisibility(View.GONE);

		// Show the ListView Loading View and set it to refresh.
		listViewLoadingView.setVisibility(View.VISIBLE);
		listViewLoadingView.refreshing();

		if (doScroll) {
			// We need to disable the automatic visibility changes for now
			disableLoadingLayoutVisibilityChanges();

			// We scroll slightly so that the ListView's header/footer is at the
			// same Y position as our normal header/footer
			setHeaderScroll(scrollToY);

			// Make sure the ListView is scrolled to show the loading
			// header/footer
			mRefreshableView.setSelection(selection);

			// Smooth scroll as normal
			smoothScrollTo(0);
		}
	}

	@Override
	protected void onReset() {
		/**
		 * If the extras are not enabled, just call up to super and return.
		 */
		if (!mListViewExtrasEnabled) {
			super.onReset();
			return;
		}

		final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
		final int scrollToHeight, selection;
		final boolean scrollLvToEdge;

		switch (getCurrentMode()) {
			case MANUAL_REFRESH_ONLY:
			case PULL_FROM_END:
				originalLoadingLayout = getFooterLayout();
				listViewLoadingLayout = mFooterLoadingView;
				selection = mRefreshableView.getCount() - 1;
				scrollToHeight = getFooterSize();
				scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition() - selection) <= 1;
				break;
			case PULL_FROM_START:
			default:
				originalLoadingLayout = getHeaderLayout();
				listViewLoadingLayout = mHeaderLoadingView;
				scrollToHeight = -getHeaderSize();
				selection = 0;
				scrollLvToEdge = Math.abs(mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
				break;
		}

		// If the ListView header loading layout is showing, then we need to
		// flip so that the original one is showing instead
		if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {

			// Set our Original View to Visible
			originalLoadingLayout.showInvisibleViews();

			// Hide the ListView Header/Footer
			listViewLoadingLayout.setVisibility(View.GONE);

			/**
			 * Scroll so the View is at the same Y as the ListView
			 * header/footer, but only scroll if: we've pulled to refresh, it's
			 * positioned correctly
			 */
			if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
				mRefreshableView.setSelection(selection);
				setHeaderScroll(scrollToHeight);
			}
		}

		// Finally, call up to super
		super.onReset();
	}
	boolean canS=false;
	public void setSwipeable(boolean isf){
		this.canS=isf;
	}
	@Override
	protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
		LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);

		if (mListViewExtrasEnabled) {
			final Mode mode = getMode();

			if (includeStart && mode.showHeaderLoadingLayout()) {
				proxy.addLayout(mHeaderLoadingView);
			}
			if (includeEnd && mode.showFooterLoadingLayout()) {
				proxy.addLayout(mFooterLoadingView);
			}
		}

		return proxy;
	}

	protected ListView createListView(Context context, AttributeSet attrs) {
		final ListView lv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			lv = new InternalListViewSDK9(context, attrs);
		} else {
			lv = new InternalListView(context, attrs);
		}
		return lv;
	}

	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		ListView lv = createListView(context, attrs);
		lv.setVerticalFadingEdgeEnabled(false);
		lv.setHorizontalFadingEdgeEnabled(false);
		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	@Override
	protected void handleStyledAttributes(TypedArray a) {
		super.handleStyledAttributes(a);
		mListViewExtrasEnabled = a.getBoolean(com.handmark.pulltorefresh.library.R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);
		if (mListViewExtrasEnabled) {
			final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

			// Create Loading Views ready for use later
			FrameLayout frame = new FrameLayout(getContext());
			mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
			mHeaderLoadingView.setVisibility(View.GONE);
			frame.addView(mHeaderLoadingView, lp);
			mRefreshableView.addHeaderView(frame, null, false);

			mLvFooterLoadingFrame = new FrameLayout(getContext());
			mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
			mFooterLoadingView.setVisibility(View.GONE);
			mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

			/**
			 * If the value for Scrolling While Refreshing hasn't been
			 * explicitly set via XML, enable Scrolling While Refreshing.
			 */
			if (!a.hasValue(com.handmark.pulltorefresh.library.R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
				setScrollingWhileRefreshingEnabled(true);
			}
		}
	}

	@TargetApi(9)
	final public class InternalListViewSDK9 extends InternalListView {

		public InternalListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			LogUtil.e("xll"," swipeList intecept" +ev.getAction());
			return super.onInterceptTouchEvent(ev);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			LogUtil.e("xll"," swipeList dispatchTouchEvent" +ev.getAction());
			return super.dispatchTouchEvent(ev);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
									   int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshSwipeMenuListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}

	public class InternalListView extends ListView implements EmptyViewMethodAccessor {

		private boolean mAddedLvFooter = false;

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				super.dispatchDraw(canvas);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			boolean flag;
			switch (ev.getAction()){

				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_UP:
					flag=true;
					break;
				default:
					flag=false;
					break;
			}
			LogUtil.e("xll ","swipeList dispatch " +flag);
			return flag;
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			LogUtil.e("xll ","swipeList dispatch "+ev.getAction());
			try {
				return super.dispatchTouchEvent(ev);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public void setAdapter(ListAdapter adapter) {
			// Add the Footer View at the last possible moment
			if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
				addFooterView(mLvFooterLoadingFrame, null, false);
				mAddedLvFooter = true;
			}

			super.setAdapter(adapter);

		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			LogUtil.e("xll", "pullSwipe event" + ev.getAction());
			if ((ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null) || loadingListFlag)
				return super.onTouchEvent(ev);
			int action = ev.getAction();

			switch (action) {
				case MotionEvent.ACTION_DOWN:
					mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
					int itemClickPostion=mTouchPosition-getFirstVisiblePosition();

//					oldPos= mTouchPosition;
					mDownX = ev.getX();
					mDownY = ev.getY();
					mTouchState = TOUCH_STATE_NONE;
					/**
					 *  获取点击listview的item positon
					 * */
//
					if (mTouchPosition == oldPos && mTouchView != null
							&& mTouchView.isOpen()&&canS) {
						mTouchState = TOUCH_STATE_X;
						mTouchView.onSwipe(ev);
						if(mTouchView.getChildAt(0)!=null&&mTouchView.getChildAt(0) instanceof SarrsMainMenuView){
							SarrsMainMenuView menuView1=(SarrsMainMenuView)mTouchView.getChildAt(0);
							boolean isInMenuView=Utils.inRangeOfView(menuView1,ev);
							//点击非按钮区域
							if(!isInMenuView){
								mTouchView.smoothCloseMenu();
								return true;
							}
							FrameLayout conentLayout=(FrameLayout)menuView1.getChildAt(0);
							LinearLayout cs=(LinearLayout)conentLayout.getChildAt(0);
							for(int i=0;i<cs.getChildCount();i++){
								boolean isInRect=Utils.inRangeOfView(cs.getChildAt(i),ev);
								if(isInRect){
									if(mClick!=null){
										if (mTouchView.isOpen())
											mTouchView.smoothCloseMenu();
										/**
										 * loadingListFlag 控制删除出现时，再次侧滑删除的问题，adapter复用会出现delete
										 */
										loadingListFlag = true;
										mClick.onItemClick(i, this, mTouchPosition, getAdapter());
										new Handler().postDelayed(new Runnable() {
											public void run() {
												//execute the task
												loadingListFlag = false;
											}
										}, 1000);

										break;
									}
								}
							}
						}


						LogUtil.e("xll", "menu onSwipeTouch positon " + mTouchPosition);
						return true;
					}
					//currentPosition和oldPosition不同
				    if(mTouchView != null && mTouchPosition!=oldPos&&canS){
						if(mTouchView.getChildAt(0)!=null&&mTouchView.getChildAt(0) instanceof SarrsMainMenuView &&mTouchView.isOpen()){
								mTouchView.smoothCloseMenu();
							mTouchView = null;
							return true;
						}
					}
					oldPos= mTouchPosition;
					LogUtil.e("xll","menu onSwipeClick positon "+itemClickPostion);
					View view = getChildAt(itemClickPostion);
					View child=null;
					if(view!=null){
						child=view.findViewById(R.id.save_item);
						/*if (mTouchView != null && mTouchView.isOpen()) {
							mTouchView.smoothCloseMenu();
							mTouchView = null;
							return super.onTouchEvent(ev);
						}*/
						if (child instanceof DeleteRelativelayout) {
							mTouchView = (DeleteRelativelayout) child;
							mTouchView.setSwipeMenuStatusListener(mSwipeMenuStatusListener);
							// 设置Menu宽度作为listItemView滑动距离
							if (mTouchView != null&&canS) {
								mTouchView.onSwipe(ev);
							}
						}

					}

					break;
				case MotionEvent.ACTION_MOVE:

					float dy = Math.abs((ev.getY() - mDownY));
					float dx = Math.abs((ev.getX() - mDownX));
					if (mTouchState == TOUCH_STATE_X ) {
						if (mTouchView != null&&canS) {
							mTouchView.onSwipe(ev);
						}
						getSelector().setState(new int[] { 0 });
						ev.setAction(MotionEvent.ACTION_CANCEL);
						super.onTouchEvent(ev);
						return true;
					} else if (mTouchState == TOUCH_STATE_NONE) {
						if (Math.abs(dy) > MAX_Y) {
							mTouchState = TOUCH_STATE_Y;
						} else if (dx > MAX_X) {
							mTouchState = TOUCH_STATE_X;
							if (mOnSwipeListener != null) {
								mOnSwipeListener.onSwipeStart(mTouchPosition);
							}
						}
					}else{
					}


					break;
				case MotionEvent.ACTION_UP:
					if (mTouchState == TOUCH_STATE_X) {
						if (mTouchView != null&&canS) {
							mTouchView.onSwipe(ev);
							if (!mTouchView.isOpen()) {
								mTouchPosition = -1;
								mTouchView = null;
							}
						}
						if (mOnSwipeListener != null) {
							mOnSwipeListener.onSwipeEnd(mTouchPosition);
						}
						ev.setAction(MotionEvent.ACTION_CANCEL);
						super.onTouchEvent(ev);
						return true;
					}

					break;
			}
			return super.onTouchEvent(ev);
		}


		@Override
		public void setEmptyView(View emptyView) {
			this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

	}

	/**
	 *
	 *   定义 swipeMenu接口
	 *
	 * */

	public void setMenuCreator(SwipeMenuCreator menuCreator) {
		this.mMenuCreator = menuCreator;
	}

	public void setOnMenuItemClickListener(
			OnMenuItemClickListener onMenuItemClickListener) {
		this.mOnMenuItemClickListener = onMenuItemClickListener;
	}

	public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.mOnSwipeListener = onSwipeListener;
	}

	public void setSwipeMenuStatusListener(SwipeMenuStatusListener menuStatusListener)
	{
		this.mSwipeMenuStatusListener = menuStatusListener;
	}

	public interface OnMenuItemClickListener {
		void onMenuItemClick(int position, SwipeMenu menu, int index);
	}

	public interface OnSwipeListener {
		void onSwipeStart(int position);

		void onSwipeEnd(int position);
	}

	public interface SwipeMenuStatusListener {
		void onMenuSmoothOpen();
		void onMenuSmoothClose();
	}

	public boolean getSwipOpenStatus(){
		if(mTouchView !=null) {
			return mTouchView.isOpen();
		}else{
			return false;
		}
	}

	public void closeMenu()
	{
		if (mTouchView != null)
			mTouchView.smoothCloseMenu();
	}
}
