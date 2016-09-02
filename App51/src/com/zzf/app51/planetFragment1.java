package com.zzf.app51;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class planetFragment1 extends Fragment{
	public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
		TextView textView=new TextView(getActivity());
		textView.setWidth(400);
		return textView;
	}
}
