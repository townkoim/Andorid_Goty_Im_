package com.slife.gopapa.broadcast;

import com.igexin.sdk.PushManager;
import com.slife.gopapa.application.MyApplication;
import com.slife.gopapa.common.NetWorkState;
import com.slife.gopapa.service.VerifyUserService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/****
 * @ClassName: ListenNetStateReveiver
 * @Description: 
 *               监控网络状态发生改变的广播接受者。一旦网络发生改变，都会判断我的登陆状态。如果我没登陆，而且后台登陆的服务还未在运行就会调用登陆服务器的后台服务
 *               用来保证推送 (只在WIFI网络下才进行推送)
 * @author 菲尔普斯
 * @date 2015-2-2 上午9:36:14
 * 
 */
public class ListenNetStateReveiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (NetWorkState.isWIFI(context)) {
			if (MyApplication.clientID == null||"".equals(MyApplication.clientID)) {
				MyApplication.clientID = PushManager.getInstance().getClientid(context);
			}
			if(NetWorkState.checkNet(context)){
				if (!MyApplication.verifyUserServiceRuning) {
					context.startService(new Intent(context,VerifyUserService.class));
				}
			}
		}
	}

}
