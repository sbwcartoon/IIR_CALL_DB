package com.iirtech.chatbot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snu.ids.ha.ma.MorphemeAnalyzer;

/**
 * @Package   : com.iirtech.chatbot.service.impl
 * @FileName  : ChatbotNLPService.java
 * @작성일       : 2017. 8. 14. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 시스템 입력 메시지 전처리 서비스 인터페이스 
 */
public interface ChatbotNLPService {

	Map<String,Object> preProcess(String procInputText);

	String selectKeyword(String keywordType, Map<String, Object> conditionInfoMap);

	String getSubThemeStatusCd(String procText, MorphemeAnalyzer ma);

	HashMap<String, ArrayList<String>> getMorpListMap(String str, MorphemeAnalyzer ma);
	
	HashMap<String, ArrayList<?>> getMaxSimilarityAndFileName(String filePath, ArrayList<String> candidateList,
			HashMap<String, ArrayList<String>> dictNameListMap, ArrayList<String> keywordList, double minSimilarityScore);

	String getJosaByJongsung(String str, String josaWithJongsung, String josaWithoutJongsung);

	boolean hasLastKoreanWordJongsung(char lastWord);

	public String getEngByKor(String korStr, String clientId, String clientPwd);

	HashMap<String, String> getPauseCondition(String procInputText, MorphemeAnalyzer ma, String clientId, String clientPwd);

	String getAskContent(String procInputText, MorphemeAnalyzer ma);



}