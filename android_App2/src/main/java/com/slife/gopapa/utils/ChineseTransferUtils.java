package com.slife.gopapa.utils;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import com.slife.gopapa.model.ContactsPerson;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/****
 * @ClassName: ChineseTransferUtils
 * @Description: 汉字与拼音以及首字母转换的助手类(pingyin4j的处理)
 * @author 菲尔普斯
 * @date 2015-2-2 上午9:59:21
 * 
 */
public class ChineseTransferUtils {
	/**
	 * @Title: getPingYin
	 * @Description: 得到 汉字的全部拼音
	 * @param @param src 要转换的对象
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getPingYin(String src) {
		char[] stringCharArray = null;
		stringCharArray = src.toCharArray();
		String[] stringArray = new String[stringCharArray.length];
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		// 设置输出格式
		outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		String preString = "";
		int t0 = stringCharArray.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				if (java.lang.Character.toString(stringCharArray[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					stringArray = PinyinHelper.toHanyuPinyinStringArray(
							stringCharArray[i], outputFormat);
					preString += stringArray[0];
				} else {
					preString += java.lang.Character
							.toString(stringCharArray[i]);
				}
			}
			return preString;
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
		return preString;
	}

	/**
	 * @Title: getHeadChar
	 * @Description: 得到首字母
	 * @param @param str
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getHeadChar(String str) {

		String convert = "";
		char word = str.charAt(0);
		String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
		if (pinyinArray != null) {
			convert += pinyinArray[0].charAt(0);
		} else {
			convert += word;
		}
		return convert.toUpperCase();
	}

	/**
	 * @Title: getPinYinHeadChar
	 * @Description: 得到中文首字母缩写
	 * @param @param str
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getPinYinHeadChar(String str) {

		String convert = "";
		for (int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			if (pinyinArray != null) {
				convert += pinyinArray[0].charAt(0);
			} else {
				convert += word;
			}
		}
		return convert.toUpperCase();
	}
	
	/**
	 * @Title: sortIndex
	 * @Description: 字母排序。获取排序后的新数据
	 * @param @param persons
	 * @param @return
	 * @return String[]
	 * @throws
	 */
	public static String[] sortIndex(List<ContactsPerson> contacts) {
		TreeSet<String> set = new TreeSet<String>();
		String a = null;
		// 获取初始化数据源中的首字母，添加到set中
		for (ContactsPerson person : contacts) {
			String pre = ChineseTransferUtils.getPinYinHeadChar(person.getName()).substring(0, 1);
			if (65 <= pre.charAt(0) && pre.charAt(0) <= 90 || 97 <= pre.charAt(0) && pre.charAt(0) <= 122) {
				set.add(pre);
			} else {
				set.add("!");
			}
		}
		// 新数组的长度为原数据加上set的大小(set内部已经按字母大小排序，将set里的数据装载到数组中以便往后调用系统拷贝方法)
		String[] names = new String[contacts.size() + set.size()];
		int i = 0;
		for (String string : set) {
			names[i] = string;
			i++;
		}
		String[] pinYinNames = new String[contacts.size()];
		for (int j = 0; j < contacts.size(); j++) {
			contacts.get(j).setPinyinName(a = ChineseTransferUtils.getPingYin(contacts.get(j).getName().toString()));
			pinYinNames[j] = a + "&^-@";
		}
		// 将原数据拷贝到新数据中(将pinYinNames数组中的元素从0下标开始复制元素到names数组)
		// src:源数组； srcPos:源数组要复制的起始位置； dest:目的数组； destPos:目的数组放置的起始位置；
		// length:复制的长度。 注意：src and dest都必须是同类型或者可以进行转换类型的数组．
		System.arraycopy(pinYinNames, 0, names, set.size(), pinYinNames.length);
		// 复制后自动按照首字母排序
		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);// 这个方法得到的数组不是以A为开头的,所以下面的方法就是重新排布用户名的顺序
		/**************** 重新排布用户名的顺序开始 ************************/
		String[] newNames = new String[names.length + 1];// 装载以A为首的数组
		String[] postNames = new String[names.length];// 装载非字母为首的数组
		int k = 0;
		int postCount = 0;
		int temp = 0;
		for (String pre : names) {
			if (65 <= pre.charAt(0) && pre.charAt(0) <= 90 || 97 <= pre.charAt(0) && pre.charAt(0) <= 122) {
				// 字母开头的数组
				newNames[k] = names[temp];
				k++;
				temp++;
			} else {
				// 非字母开头的数组
				postNames[postCount] = names[postCount];
				temp = ++postCount;
			}
		}
		// 将非字母开头的数组加到字母开头数组的后面
		for (int j = 0; j < postCount; j++, k++) {
			newNames[k] = postNames[j];
		}
		temp = 0;
		// 防止数组中有null
		for (String pew : newNames) {
			if (pew != null)
				postNames[temp++] = pew;
		}
		/**************** 重新排布用户名的顺序结束 ************************/
		return postNames;
	}
}
