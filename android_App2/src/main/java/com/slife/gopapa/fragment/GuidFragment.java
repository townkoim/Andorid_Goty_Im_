package com.slife.gopapa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.slife.gopapa.R;
import com.slife.gopapa.activity.login.LoginActivity;

/**
* @ClassName: GuidFragment 
* @Description:引导界面的Fragment
* @author 菲尔普斯
* @date 2015-1-4 下午5:04:28 
*
 */
public class GuidFragment extends Fragment {
	private int guideID = 0;
	private View view1, view2, view3,view4=null;// 三个引导页的View视图
	private ImageView imgLogin; //按钮监听器
	public GuidFragment(int guideID) {
		this.guideID = guideID;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (guideID == 1) {
			view1 = inflater.inflate(R.layout.guide_01, container, false);
			return view1;
		} else if (guideID == 2) {
			view2 = inflater.inflate(R.layout.guide_02, container, false);
			return view2;
		} else if (guideID == 3) {
			view3=inflater.inflate(R.layout.guide_03, container, false);
			return view3;
		}else if(guideID == 4){
			view4=inflater.inflate(R.layout.guide_04, container, false);
			imgLogin = (ImageView) view4.findViewById(R.id.guide_login);
			imgLogin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(getActivity(),LoginActivity.class));
					getActivity().finish();
				}
			});
			
			return view4;
		}
		else{
			return null;
		}
	}
}
