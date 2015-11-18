package com.slife.gopapa.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.slife.gopapa.R;

public class SmileyParserUtil {
	private static SmileyParserUtil sInstance;

	public static SmileyParserUtil getInstance() {
		return sInstance;
	}

	public static void init(Context context) {
		sInstance = new SmileyParserUtil(context);
	}

	private final Context mContext;
	private final String[] mSmileyTexts;
	private final Pattern mPattern;
	private final HashMap<String, Integer> mSmileyToRes;

	private SmileyParserUtil(Context context) {
		mContext = context;
		mSmileyTexts = mContext.getResources().getStringArray(DEFAULT_SMILEY_TEXTS);
		mSmileyToRes = buildSmileyToRes();
		mPattern = buildPattern();
	}

	static class Smileys {
		// 表情图片集合
		public static final int[] sIconIds = { R.drawable.bq1, R.drawable.bq2,
				R.drawable.bq3, R.drawable.bq4, R.drawable.bq5, R.drawable.bq6,
				R.drawable.bq7, R.drawable.bq8, R.drawable.bq9,
				R.drawable.bq10, R.drawable.bq11, R.drawable.bq12,
				R.drawable.bq13, R.drawable.bq14, R.drawable.bq15,
				R.drawable.bq16, R.drawable.bq17, R.drawable.bq18,
				R.drawable.bq19, R.drawable.bq20, R.drawable.bq21,
				R.drawable.bq22, R.drawable.bq23, R.drawable.bq24,
				R.drawable.bq25, R.drawable.bq26, R.drawable.bq27,
				R.drawable.bq28, R.drawable.bq29, R.drawable.bq30 };
		// 将图片映射为 文字
		public static int bq1 = 0;
		public static int bq2 = 1;
		public static int bq3 = 2;
		public static int bq4 = 3;
		public static int bq5 = 4;
		public static int bq6 = 5;
		public static int bq7 = 6;
		public static int bq8 = 7;
		public static int bq9 = 8;
		public static int bq10 = 9;
		public static int bq11 = 10;
		public static int bq12 = 11;
		public static int bq13 = 12;
		public static int bq14 = 13;
		public static int bq15 = 14;
		public static int bq16 = 15;
		public static int bq17 = 16;
		public static int bq18 = 17;
		public static int bq19 = 18;
		public static int bq20 = 19;
		public static int bq21 = 20;
		public static int bq22 = 21;
		public static int bq23 = 22;
		public static int bq24 = 23;
		public static int bq25 = 24;
		public static int bq26 = 25;
		public static int bq27 = 26;
		public static int bq28 = 27;
		public static int bq29 = 28;
		public static int bq30 = 29;

		// 得到图片表情 根据id
		public static int getSmileyResource(int which) {
			return sIconIds[which];
		}
	}

	public static final int[] DEFAULT_SMILEY_RES_IDS = {
			Smileys.getSmileyResource(Smileys.bq1), 
			Smileys.getSmileyResource(Smileys.bq2), 
			Smileys.getSmileyResource(Smileys.bq3), 
			Smileys.getSmileyResource(Smileys.bq4), 
			Smileys.getSmileyResource(Smileys.bq5), 
			Smileys.getSmileyResource(Smileys.bq6), 
			Smileys.getSmileyResource(Smileys.bq7), 
			Smileys.getSmileyResource(Smileys.bq8), 
			Smileys.getSmileyResource(Smileys.bq9), 
			Smileys.getSmileyResource(Smileys.bq10), 
			Smileys.getSmileyResource(Smileys.bq11), 
			Smileys.getSmileyResource(Smileys.bq12), 
			Smileys.getSmileyResource(Smileys.bq13), 
			Smileys.getSmileyResource(Smileys.bq14), 
			Smileys.getSmileyResource(Smileys.bq15), 
			Smileys.getSmileyResource(Smileys.bq16), 
			Smileys.getSmileyResource(Smileys.bq17), 
			Smileys.getSmileyResource(Smileys.bq18), 
			Smileys.getSmileyResource(Smileys.bq19), 
			Smileys.getSmileyResource(Smileys.bq20), 
			Smileys.getSmileyResource(Smileys.bq21), 
			Smileys.getSmileyResource(Smileys.bq22), 
			Smileys.getSmileyResource(Smileys.bq23), 
			Smileys.getSmileyResource(Smileys.bq24), 
			Smileys.getSmileyResource(Smileys.bq25), 
			Smileys.getSmileyResource(Smileys.bq26), 
			Smileys.getSmileyResource(Smileys.bq27), 
			Smileys.getSmileyResource(Smileys.bq28), 
			Smileys.getSmileyResource(Smileys.bq29), 
			Smileys.getSmileyResource(Smileys.bq30)
	};

	public static final int DEFAULT_SMILEY_TEXTS = R.array.smiley_array;

	private HashMap<String, Integer> buildSmileyToRes() {
		if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
			throw new IllegalStateException("Smiley resource ID/text mismatch");
		}
		HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>(
				mSmileyTexts.length);
		for (int i = 0; i < mSmileyTexts.length; i++) {
			smileyToRes.put(mSmileyTexts[i], DEFAULT_SMILEY_RES_IDS[i]);
		}
		return smileyToRes;
	}

	// 构建正则表达式
	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);
		patternString.append('(');
		for (String s : mSmileyTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");
		return Pattern.compile(patternString.toString());
	}

	// 根据文本替换成图片
	public CharSequence strToSmiley(CharSequence text,int width,int height) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = mPattern.matcher(text);
		while (matcher.find()) {
			int resId = mSmileyToRes.get(matcher.group());
			Drawable drawable = mContext.getResources().getDrawable(resId);
			drawable.setBounds(0, 0, width, height);// 这里设置图片的大小
			ImageSpan imageSpan = new ImageSpan(drawable,ImageSpan.ALIGN_BOTTOM);
			builder.setSpan(imageSpan, matcher.start(), matcher.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return builder;
	}
}