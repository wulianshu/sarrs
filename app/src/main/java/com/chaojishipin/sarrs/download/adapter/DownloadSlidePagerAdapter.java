package com.chaojishipin.sarrs.download.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class DownloadSlidePagerAdapter extends FragmentPagerAdapter {
	
	private ArrayList<Fragment> mFragmentArray;
	
	public DownloadSlidePagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentArray) {
		this(fm);
		this.mFragmentArray = fragmentArray;
	}

	public DownloadSlidePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragmentArray == null?null:mFragmentArray.get(arg0);
	}

	@Override
	public int getCount() {
		return mFragmentArray == null?0:this.mFragmentArray.size();
	}

}
