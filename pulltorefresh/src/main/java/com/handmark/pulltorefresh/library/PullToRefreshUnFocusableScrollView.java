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
package com.handmark.pulltorefresh.library;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class PullToRefreshUnFocusableScrollView extends PullToRefreshBase<ScrollView> {

	public PullToRefreshUnFocusableScrollView(Context context) {
		super(context);
	}

	public PullToRefreshUnFocusableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshUnFocusableScrollView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshUnFocusableScrollView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected ScrollView createRefreshableView(Context context, AttributeSet attrs) {
		UnFocusableScrollView scrollView;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			scrollView = new InternalScrollViewSDK9(context, attrs);
		} else {
			scrollView = new UnFocusableScrollView(context, attrs);
		}

		scrollView.setId(R.id.scrollview);
		return scrollView;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		View scrollViewChild = mRefreshableView.getChildAt(0);
		if (null != scrollViewChild) {
			return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
		}
		return false;
	}

	@TargetApi(9)
	final class InternalScrollViewSDK9 extends UnFocusableScrollView {

		public InternalScrollViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshUnFocusableScrollView.this, deltaX, scrollX, deltaY, scrollY,
					getScrollRange(), isTouchEvent);

			return returnValue;
		}

		/**
		 * Taken from the AOSP ScrollView source
		 */
		private int getScrollRange() {
			int scrollRange = 0;
			if (getChildCount() > 0) {
				View child = getChildAt(0);
				scrollRange = Math.max(0, child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
			}
			return scrollRange;
		}
	}
	/**
	 * 为了解决scrollview里有抢占焦点的子控件时，scrollvie会自动滚动到焦点控件的位置的bug。
	 * @author daipei
	 *
	 */
	private class UnFocusableScrollView extends ScrollView {

		public UnFocusableScrollView(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
		}

		public UnFocusableScrollView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public UnFocusableScrollView(Context context) {
			super(context);
		}
	    @Override
	    public void requestChildFocus(View child, View focused) {
	    }
		  @Override
		  protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
		    return true;
		  }
		  @Override
		  public ArrayList<View> getFocusables(int direction) {
		    return new ArrayList<View>();
		  }
	}
}
