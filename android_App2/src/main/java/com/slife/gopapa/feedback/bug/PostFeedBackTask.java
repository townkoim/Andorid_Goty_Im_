package com.slife.gopapa.feedback.bug;

import java.io.File;

import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.http.MyHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
* @ClassName: FeedBackBugActivity 
* @Description: BUG/建议反馈数据提交
* @author 纳兹
* @date 2015-3-3 下午2:47:22 
*
*/
public class PostFeedBackTask extends AsyncTask<Void, Void, Boolean> {
	private Context context;
	private String feedback_type;// 反馈类型
	private String feedback_name;// 反馈人
	private String feedback_introduction;// 反馈摘要
	private String feedback_description;// 反馈描述
	private String feedback_occur_frequency;// 出现概率
	private String feedback_mobile;// 手机
	private String feedback_client_version;// 手机型号
	private String feedback_client_system;// 系统版本
	private String firstPath;// 图片路径
	private String secondPath;// 图片路径
	private String thirdPath;// 图片路径
	private File file;// 图片文件
	private File file1;// 图片文件
	private File file2;// 图片文件
	private File[] files;// 文件组
	private boolean isSuccess;// 上传是否成功
	private static ProgressDialog progressDialog;
	private String URL_FEED_BACK_BUG = "https://125.94.212.253/PaPaPublicWebService/feedback/addfeedback?";// 反馈提交链接
	private String[] key = new String[]{"feedback_type", "feedback_name", "feedback_introduction", "feedback_description", "feedback_occur_frequency", "feedback_app_type", "feedback_mobile", "feedback_client_version", "feedback_client_type", "feedback_client_system"};
	private String[] value_problem;// 值：问题
	private String[] value_suggest;// 值：建议

	public PostFeedBackTask(Context context, String feedback_type,
			String feedback_name, String feedback_introduction,
			String feedback_description, String feedback_occur_frequency,
			String feedback_mobile, String feedback_client_version,
			String feedback_client_system, String firstPath, String secondPath,
			String thirdPath) {
		this.context = context;
		this.feedback_type = feedback_type;
		this.feedback_name = feedback_name;
		this.feedback_introduction = feedback_introduction;
		this.feedback_description = feedback_description;
		this.feedback_occur_frequency = feedback_occur_frequency;
		this.feedback_mobile = feedback_mobile;
		this.feedback_client_version = feedback_client_version;
		this.feedback_client_system = feedback_client_system;
		this.firstPath = firstPath;
		this.secondPath = secondPath;
		this.thirdPath = thirdPath;
	}
	
	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);// 点击屏幕对话框不消失
		progressDialog.setMessage("数据上传中...");
		progressDialog.show();
		/*--------------------- 创建相应图片文件 ---------------------------*/
		if(firstPath != null && !"".equals(firstPath)){			
			file = new File(firstPath);
		}
		if(secondPath != null && !"".equals(secondPath)){			
			file1 = new File(secondPath);
		}
		if(thirdPath != null && !"".equals(thirdPath)){			
			file2 = new File(thirdPath);
		}
		/*--------------------- 创建相应文件组 ---------------------------*/
		if((firstPath != null && !"".equals(firstPath)) && (secondPath != null && !"".equals(secondPath)) && (thirdPath != null && !"".equals(thirdPath))){// 有三张图片时
			files = new File[]{file, file1, file2};
		}else if(!"".equals(firstPath) && firstPath != null && !"".equals(secondPath) && secondPath != null){// 有两张图片时
			files = new File[]{file, file1};
		}else if(!"".equals(firstPath) && firstPath != null && !"".equals(thirdPath) && thirdPath != null){// 有两张图片时
			files = new File[]{file, file2};
		}else if(!"".equals(secondPath) && secondPath != null && !"".equals(thirdPath) && thirdPath != null){// 有两张图片时
			files = new File[]{file1, file2};
		}else if(!"".equals(firstPath) && firstPath != null){// 有一张图片时
			files = new File[]{file};
		}else if(!"".equals(secondPath) && secondPath != null){// 有一张图片时
			files = new File[]{file1};
		}else if(!"".equals(thirdPath) && thirdPath != null){// 有一张图片时
			files = new File[]{file2};
		}
		
		value_problem = new String[]{feedback_type, feedback_name, feedback_introduction, feedback_description, feedback_occur_frequency, "2", feedback_mobile, feedback_client_version, "android", feedback_client_system};
		value_suggest = new String[]{feedback_type, feedback_name, feedback_introduction, feedback_description, "-1", "2", feedback_mobile, feedback_client_version, "android", feedback_client_system};
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String json[] = null;
		if("1".equals(feedback_type)){// 反馈类型为BUG时调用以下接口
			if(file != null && file1 != null && file2 != null){
				json = MyHttpClient.postDataToService(context, URL_FEED_BACK_BUG, MyApplication.commonToken, key, value_problem, new String[]{"feedback_img", "feedback_img1", "feedback_img2"}, files);
			}else if((file != null && file1 != null) || (file != null && file2 != null) || (file1 != null && file2 != null)){
				json = MyHttpClient.postDataToService(context, URL_FEED_BACK_BUG, MyApplication.commonToken, key, value_problem, new String[]{"feedback_img", "feedback_img1"}, files);
			}else if(file != null || file1 != null || file2 != null){
				json = MyHttpClient.postDataToService(context, URL_FEED_BACK_BUG, MyApplication.commonToken, key, value_problem, new String[]{"feedback_img"}, files);
			}else{
				json = MyHttpClient.postDataToService(context, URL_FEED_BACK_BUG, MyApplication.commonToken, key, value_problem, null, null);
			}
		}else{// 反馈类型为建议时调用以下接口
			if(file != null && file1 != null && file2 != null){
				json = MyHttpClient.postDataToService(context, URL_FEED_BACK_BUG, MyApplication.commonToken, key, value_suggest, new String[]{"feedback_img", "feedback_img1", "feedback_img2"}, files);
			}else if((file != null && file1 != null) || (file != null && file2 != null) || (file1 != null && file2 != null)){
				json = MyHttpClient.postDataToService(context, URL_FEED_BACK_BUG, MyApplication.commonToken, key, value_suggest, new String[]{"feedback_img", "feedback_img1"}, files);
			}else if(file != null || file1 != null || file2 != null){
				json = MyHttpClient.postDataToService(context, URL_FEED_BACK_BUG, MyApplication.commonToken, key, value_suggest, new String[]{"feedback_img"}, files);
			}else{
				json = MyHttpClient.postDataToService(context, URL_FEED_BACK_BUG, MyApplication.commonToken, key, value_suggest, null, null);
			}
		}
		if("200".equals(json[0])){
			isSuccess = true;
		}else{
			isSuccess = false;
		}
		return isSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if(result){
			Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
			MyApplication.preferences.edit().putString("bug_people_name", feedback_name).commit();
		}else{
			Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT).show();
		}
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
}
