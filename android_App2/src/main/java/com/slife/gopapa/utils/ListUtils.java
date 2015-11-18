package com.slife.gopapa.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.slife.gopapa.model.ContactsPerson;
import com.slife.gopapa.model.MyChatMessage;
import com.slife.gopapa.model.PushAboutMatch;
import com.slife.gopapa.model.RecentNews;

/***
 * @ClassName: ListUtils
 * @Description: 集合工具类,用来对集合进行处理
 * @author 菲尔普斯
 * @date 2014-11-28 下午12:51:10
 * 
 */
public class ListUtils {
	/**
	 * @Title: getNewList
	 * @Description: 去掉ListView当中重复项
	 * @param @param list MyChatMessage集合
	 * @param @return
	 * @return List<MyChatMessage> 没有重复的 MyChatMessage集合
	 * @throws
	 */
	public static List<RecentNews> removeRepeatData(List<RecentNews> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			int count = 1;
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).getUser_extend_account()
						.equals(list.get(i).getUser_extend_account())) {
					list.remove(j);
					count += 1;
				}
			}
			list.get(i).setMsgCount(count);
		}
		return list;
	}
	
	/**
	 * @Title: getNewList
	 * @Description: 去掉ListView当中重复项
	 * @param @param list MyChatMessage集合
	 * @param @return
	 * @return List<MyChatMessage> 没有重复的 MyChatMessage集合
	 * @throws
	 */
	public static List<RecentNews> removeRepeatData1(List<RecentNews> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).getUser_extend_account()
						.equals(list.get(i).getUser_extend_account())) {
					list.remove(j);
				}
			}
		}
		return list;
	}

	/**
	 * @Title: removeRepeatMatch
	 * @Description: 移除约赛消息的重复项
	 * @param @param list
	 * @param @return
	 * @return List<PushAboutMatch>
	 * @throws
	 */
	public static List<PushAboutMatch> removeRepeatMatch(
			List<PushAboutMatch> list) {
		if (list != null) {
			for (int i = 0; i < list.size() - 1; i++) {
				for (int j = list.size() - 1; j > i; j--) {
					if (list.get(j).getUser_account().equals(list.get(i).getUser_account())&& list.get(j).getRice_id().equals(list.get(i).getRice_id())) {
						list.remove(j);
					}
				}
			}
		}
		return list;
	}

	/**
	 * @Title: removeRepeatContact
	 * @Description: 去掉从手机读取出来的 联系人的重复项
	 * @param @param listPerson
	 * @param @return
	 * @return List<ContactsPerson>
	 * @throws
	 */
	public static List<ContactsPerson> removeRepeatContact(
			List<ContactsPerson> listPerson) {
		for (int i = 0; i < listPerson.size() - 1; i++) {
			for (int j = listPerson.size() - 1; j > i; j--) {
				if (listPerson.get(j).getPhone() != null
						&& !"".equals(listPerson.get(j).getPhone())
						&& listPerson.get(i).getPhone() != null
						&& !"".equals(listPerson.get(i).getPhone())) {
					if (listPerson.get(j).getPhone()
							.equals(listPerson.get(i).getPhone())) {
						listPerson.remove(j);
					}
				}
			}
		}
		return listPerson;

	}

	/**
	 * @Title: hasMessage
	 * @Description: 判断list集合当中有没有你mMsg对象,有就返回true
	 * @param @param list
	 * @param @param mMsg
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean hasMessage(List<RecentNews> list, RecentNews mMsg) {
		Iterator<RecentNews> ite = list.iterator();
		while (ite.hasNext()) {
			RecentNews msg = ite.next();
			if (msg.getUser_extend_account().equals(
					mMsg.getUser_extend_account())) {
				return true;
			}
		}

		return false;
	}

	/***
	 * @Title: ReverseOrder
	 * @Description: 将集合内容倒序排序输出新的集合
	 * @param @param list 要倒序的集合
	 * @param @return
	 * @return List<MyChatMessage> 倒序后的集合
	 * @throws
	 */
	public static List<MyChatMessage> ReverseOrder(List<MyChatMessage> list) {
		List<MyChatMessage> reverseList = new ArrayList<>();
		for (int i = list.size() - 1; i >= 0; i--) {
			reverseList.add(list.get(i));
		}
		return reverseList;
	}

}
