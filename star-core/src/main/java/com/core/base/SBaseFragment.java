package com.core.base;

import android.support.v4.app.Fragment;

public class SBaseFragment extends Fragment {

	public String getFragmentTag() {
		return fragmentTag;
	}

	public void setFragmentTag(String fragmentTag) {
		this.fragmentTag = fragmentTag;
	}

	private String fragmentTag;


}
