/**   
 * @Title: Ranking.java
 * @Package com.slife.gopapa.model
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 肖邦
 * @date 2015-1-23 上午10:07:01
 * @version V1.0
 */
package com.slife.gopapa.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @ClassName: Ranking
 * @Description: 排名信息
 * @author 肖邦
 * @date 2015-1-23 上午10:07:01
 * 
 */
public class Ranking implements Serializable{
	private static final long serialVersionUID = 3642853160872415624L;
	public ArrayList<RankingList> rankingList;
	public int user_ranking;
	public ArrayList<RankingList> getRankingList() {
		return rankingList;
	}
	public void setRankingList(ArrayList<RankingList> rankingList) {
		this.rankingList = rankingList;
	}
	public int getUser_ranking() {
		return user_ranking;
	}
	public void setUser_ranking(int user_ranking) {
		this.user_ranking = user_ranking;
	}
	
}
