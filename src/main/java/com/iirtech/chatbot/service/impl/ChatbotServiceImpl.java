package com.iirtech.chatbot.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.service.ChatbotService;
import com.iirtech.common.enums.DialogStatus;
import com.iirtech.common.utils.ChatbotUtil;

/**
 * @Package   : com.iirtech.chatbot.service.impl
 * @FileName  : ChatbotServiceImpl.java
 * @작성일       : 2017. 8. 13. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 메인 서비스 코드 구현파트 
 */
@Service
public class ChatbotServiceImpl implements ChatbotService{

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ChatbotUtil cbu;
	
	/**
	 * 시스템 프로퍼티 변수 
	 * systemFilePath: 시스템 파일 저장경로 
	 * userFilePath: 사용자별 파일 저장경로 
	 * userSeqFileName: 사용자 id pwd 별 seq 정보 저장파일명  
	 */
	@Value("#{systemProp['filepath']}") 
	String filePath;
	@Value("#{systemProp['userseqfilename']}") 
	String userSeqFileName;
	@Value("#{systemProp['systemdelimeter']}") 
	String systemDelimeter;

	@Override
	public Map<String, Object> mergeSystemFile(Map<String, Object> param, HttpSession session) {
		log.debug("***********************mergeUserFile**********************");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			String id = String.valueOf(param.get("id"));
			String password = String.valueOf(param.get("password"));		
			//비밀번호 암호화 
			String encPassword = cbu.encryptPwd(password);
			String userType = "newUser";
			// userInfos : ["userSeq|id|pwd", "userSeq|id|pwd" ...]
			
			String systemFilePath = System.getProperty("user.home") + "/Documents/chatbot/systemfile/";
//			String systemFilePath = session.getServletContext().getRealPath("resources/file/systemfile");
			log.debug("path>>>"+systemFilePath);
			List<String> userInfos = cbu.readFileByLine(systemFilePath, userSeqFileName);
//
			String userSeq = "";
//			if(userInfos.isEmpty()) {
//				//unique한 유저시퀀스생성 
//				userSeq = UUID.randomUUID().toString().replace("-", "").replace(systemDelimeter, "");
//				//시스템 최초로딩시 사용자관련 파일 없으므로 생성
//				String userSeqContent = userSeq + systemDelimeter + id + systemDelimeter + encPassword;
//				userInfos.add(userSeqContent);
//				cbu.writeFile(systemFilePath, userSeqFileName, userInfos);
//				
//			}else {
				//사용자정보 파일이 있는 경우 꺼내서 id & pwd 비교 후 없으면 추가 
				for (String userInfo : userInfos) {
					String[] userData = userInfo.split("\\"+systemDelimeter);
					String fileUserSeq = userData[0];
					String fileUserId = userData[1];
					String fileUserPassword = userData[2];
					log.debug(fileUserId+":"+id);
					log.debug(fileUserPassword+":"+encPassword);
					if((fileUserId.equals(id)) && (fileUserPassword.equals(encPassword))) {
						userType = "oldUser";
						userSeq = fileUserSeq;
					}
//				//없는 회원이므로 추가
//				if(userType.equals("newUser")) {
//					userSeq = UUID.randomUUID().toString().replace("-", "").replace(systemDelimeter, "");
//					//시스템 파일 폴더의 userSeq.txt 에 정보추가 
//					String content = ""; 
//					content = userSeq + systemDelimeter + id + systemDelimeter + encPassword;
//					userInfos.add(content);
//					cbu.writeFile(systemFilePath, userSeqFileName, userInfos);
//				}
			}
			resultMap.put("id", id);
			resultMap.put("password", encPassword);
			resultMap.put("userSeq", userSeq);
			resultMap.put("userType", userType);
			resultMap.put("loginTime", cbu.getYYYYMMDDhhmmssTime(	System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();		
		}
		
		return resultMap;
	}

	@Override
	public void mergeUserHistFile(Map<String, Object> userInfoMap, HttpSession session) {
		log.debug("*************************mergeUserHistFile*************************");
		//사용자 파일 폴더의 {userSeq}_hist.txt 파일 생성하고 정보추가 
		//line0: logintime|logouttime|usingtime
		try {
//			String userSeq = userInfoMap.get("userSeq").toString();
//			String userHistFileName = userSeq + "_hist.txt";
			String userId = userInfoMap.get("id").toString();
			String userHistFileName = userId + "_hist.txt";
//			String userFilePath = filePath + "userfile/";
			String userFilePath = session.getServletContext().getRealPath("resources/file/userfile");
//			List<String> userHistInfos = cbu.readFileByLine(userFilePath, userHistFileName);

			List<String> userHistInfos = new ArrayList<String>();
			String content = ""; 
			Long loginTime = Long.valueOf(userInfoMap.get("loginTime").toString());
			Long logoutTime = Long.valueOf(cbu.getYYYYMMDDhhmmssTime(System.currentTimeMillis()));
			Long usingTime = (logoutTime - loginTime)/1000; //sec
			content = loginTime + systemDelimeter + logoutTime + systemDelimeter + usingTime;
			userHistInfos.add(content);
			cbu.writeFile(userFilePath, userHistFileName, userHistInfos, true);
			
		} catch (Exception e) {
			e.printStackTrace();		
		}

	}

	/*
	 * make, update 통합
	 * @see com.iirtech.chatbot.service.ChatbotService#makeUserDialogFile(java.util.Map)
	 */
	@Override
	public void makeUserDialogFile(Map<String, Object> userInfoMap, String rootPath) {
		log.debug("*************************makeUserDialogFile*************************");
		//사용자 파일 폴더의 {currentTimeMillis}_dialog.txt 파일 생성하고 정보추가 
		//line0: topic
		//line1: time|speacker|orgnl_text|prcssd_text
		List<String> userDialogContents = new ArrayList<String>();
		String content = ""; 
		try {
//			String userSeq = userInfoMap.get("userSeq").toString();
			String userId = userInfoMap.get("id").toString();
//			String userFilePath = filePath + "userfile/";
//			String userFilePath = session.getServletContext().getRealPath("resources/file/userfile");
			String userFilePath = rootPath + "/file/userfile/";
			String userDialogFileDir = userFilePath + userId + "/";
			log.debug(userDialogFileDir);
//			File targetDir = new File(userDialogFileDir);
			
			//File이 존재하지 않을 경우 만들고 "topic" 문자열 쓰기
//			if (!targetDir.exists()) {
//				targetDir.mkdirs();
//				content = "topic";
//				userDialogContents.add(content);
//			}
			String userDialogFileName = userInfoMap.get("loginTime").toString() + "_dialog.txt";
			log.debug(userDialogFileName);
			
			String orglMessage = userInfoMap.get("orglMessage").toString().replaceAll("\n", "<br>");
			String speecher = "";
			if (Boolean.valueOf( String.valueOf((userInfoMap.get("isUser"))) )) {
				speecher = "User";
			} else {
				speecher = "Bot";
			}

			//파일내용은 하기와 같이 쓰여져야한다.
			//statusCd|msgIdx|Bot|BotText|time|seq
			//statusCd|msgIdx|Fix|fixedText|time|seq
			//statusCd|msgIdx|User|UserText|time|seq
			String dialogTime = userInfoMap.get("dialogTime").toString();
			String statusCd = userInfoMap.get("statusCd").toString();
			String idx = null;
			if (!DialogStatus.get(statusCd).name().contains("SUB_")) {
				idx = userInfoMap.get("messageIdx").toString();
			} else {
				idx = userInfoMap.get("subMessageIdx").toString();
			}
			
			//파일의 마지막 라인에서 seq값 읽어오기 
			List<String> dialogs = cbu.readFileByLine(userDialogFileDir,userDialogFileName);
			int dialogSeq = 0;//초기값 
			if(!dialogs.isEmpty()) {
				String lastDialogLine = dialogs.get(dialogs.size()-1);
				dialogSeq = Integer.parseInt(lastDialogLine.split("\\|")[5]) + 1;
			}
			
			content = statusCd + systemDelimeter + idx + systemDelimeter + speecher 
					+ systemDelimeter + orglMessage + systemDelimeter + dialogTime 
					+ systemDelimeter + dialogSeq + systemDelimeter + 0;
			
			userDialogContents.add(content);
			cbu.writeFile(userDialogFileDir, userDialogFileName, userDialogContents, true);
			
		} catch (Exception e) {
			e.printStackTrace();		
		}

	}

	@Override
	public void addFixedTextToDialogFile(Map<String, Object> param, String rootPath) {
		//1. 기존 대화로그파일 읽어비교 
		String userFilePath = rootPath + "/file/userfile/";
		String userDialogFileDir = userFilePath + param.get("id").toString() + "/";
		String userDialogFileName = param.get("loginTime").toString() + "_dialog.txt";
		String statusCd = param.get("statusCd").toString();
		String workType = param.get("workType").toString();//ADD, MODIFY, DELETE
		String fixedTextIdx = param.get("fixedTextIdx").toString();
		String idx = null;
		// statusCd가 서브 테마가 아니라면
		if (!DialogStatus.get(statusCd).name().contains("SUB_")) {
			idx = param.get("messageIdx").toString();
		} else {
			idx = param.get("subMessageIdx").toString();
		}
		
		List<String> dialogs = cbu.readFileByLine(userDialogFileDir, userDialogFileName);
		//삽입할 위치 구하기 //정규표현식 사용 (S000[|]0[|]Bot[|].*)
		String botMatchingStr = statusCd + "[|]" + idx + "[|]Bot[|]";
		String fixMatchingStr = statusCd + "[|]" + idx + "[|]Fix[|]";

		if(workType.equals("ADD")) {
			
			int insertPoint = 0;
			int fixedTextCnt = 0;
			for (int i = 0; i < dialogs.size(); i++) {
				String dialog = dialogs.get(i);
				if(dialog.matches("("+ botMatchingStr +".*)")) {//첫 수정문 추가 
					insertPoint = i + 1;
					fixedTextIdx = String.valueOf(fixedTextCnt);
				}else if(dialog.matches("("+ fixMatchingStr +".*)")) {//이미 수정문이 있는 상태에서 추가
					insertPoint = i + 1;
					fixedTextCnt++;
//					fixedTextIdx = String.valueOf(fixedTextCnt + 1);
					fixedTextIdx = String.valueOf(fixedTextCnt);
				}
			}
			
			//삽입할 문자열 만들어서 삽입위치에 삽입
			String fixedText = param.get("fixedText").toString().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
			String speecher = "Fix";
			String dialogTime = cbu.getYYYYMMDDhhmmssTime(System.currentTimeMillis());
			String content = statusCd + systemDelimeter + idx + systemDelimeter + speecher 
					+ systemDelimeter + fixedText + systemDelimeter + dialogTime 
					+ systemDelimeter + insertPoint + systemDelimeter + fixedTextIdx;
			
			dialogs.add(insertPoint, content);//추가
			
		}else if(workType.equals("MODIFY")){
			
			int modifyPoint = 0;
//			int fixedTextCnt = 0;
			for (int i = 0; i < dialogs.size(); i++) {
				String dialog = dialogs.get(i);
				if(dialog.matches("("+ fixMatchingStr +".*[|]"+fixedTextIdx+")")) {//이미 수정문이 있는 상태에서 추가
					modifyPoint = i;
//					fixedTextIdx = String.valueOf(fixedTextCnt);
				}
			}
		
			//삽입할 문자열 만들어서 삽입위치에 삽입
			String fixedText = param.get("fixedText").toString().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
			String speecher = "Fix";
			String dialogTime = cbu.getYYYYMMDDhhmmssTime(System.currentTimeMillis());
			String content = statusCd + systemDelimeter + idx + systemDelimeter + speecher 
					+ systemDelimeter + fixedText + systemDelimeter + dialogTime 
					+ systemDelimeter + modifyPoint + systemDelimeter + fixedTextIdx;
			
			dialogs.set(modifyPoint, content);//수정 
			
		}else if(workType.equals("DELETE")) {
			
			int deletePoint = 0;
			for (int i = 0; i < dialogs.size(); i++) {
				String dialog = dialogs.get(i);
				if(dialog.matches("("+ fixMatchingStr +".*[|]"+fixedTextIdx+")")) {//이미 수정문이 있는 상태에서 추가
					deletePoint = i;
				}
			
			}
			
			dialogs.remove(deletePoint);//제거
		}
				
		//루프돌면서 dialogSeq 재 정렬시키기
		List<String> newDialogs = new ArrayList<String>();
		for (int i = 0; i < dialogs.size(); i++) {
			String dialog = dialogs.get(i);
			String[] elmnts = dialog.split("\\|");
			//statusCd|msgIdx|Bot|BotText|time|seq
			String newDialog = elmnts[0] + systemDelimeter + elmnts[1] + systemDelimeter + elmnts[2] + systemDelimeter + elmnts[3]
					 + systemDelimeter + elmnts[4] + systemDelimeter + i + systemDelimeter + elmnts[6];
			newDialogs.add(newDialog);
		}
		//기존 파일 지우고 새로 쓰기 
		cbu.deleteFile(userDialogFileDir, userDialogFileName);
		cbu.writeFile(userDialogFileDir, userDialogFileName, newDialogs,false);
	}
	
	//로그 시각화 
	@Override
	public String makeDialogLogString(Map<String, Object> param, String rootPath) {
		String result = "<p id='dialogShowBoxText'>";
		
		String userFilePath = rootPath + "/file/userfile/";
		String userDialogFileDir = userFilePath + param.get("id").toString() + "/";
		String userDialogFileName = param.get("loginTime").toString() + "_dialog.txt";
		List<String> dialogs = cbu.readFileByLine(userDialogFileDir, userDialogFileName);
		//현재는 <br>을 \t으로 바꿔주고 line 별로는 <br>태그 붙여준다.
		//statusCd 가 달라지면 <br><br> 붙임
		String prevStatusCd = null;
		String targetStatusCd = null;
		int idx = 0;
		for (int i = 0; i < dialogs.size(); i++) {
			String newLineStr = "<br>";
			String dialog = dialogs.get(i).replaceAll("<br>", "\t");//기존 개행 표시<br>를 \t 으로 변경 
			String[] elmnts = dialog.split("\\|");
			
			prevStatusCd = elmnts[0];
			
			if(i+1 < dialogs.size()) {
				String nextDialog = dialogs.get(i+1).replaceAll("<br>", "\t");//기존 개행 표시<br>를 \t 으로 변경 
				targetStatusCd = nextDialog.split("\\|")[0];
			}
			if(targetStatusCd != null && !targetStatusCd.equals(prevStatusCd)) {
				newLineStr = "<br><br>";
				idx = 0;
			}
			String msgIdx = elmnts[1];
			
			//statusCd|msgIdx|Bot|BotText|time|seq|fixeTextIdx(If Fix)
			//[Bot]: BotText
			
			String otomata = DialogStatus.get(prevStatusCd).toString();
			if(elmnts[2].equals("Fix")) {//Fix일때는 문구 옆에 수정 추가 삭제 버튼 추가해야함
//				String fixedTextIdx = elmnts[6];
				int fixedTextIdx = idx;
				result += "<div>"
							+ "[" + otomata + "(" + msgIdx + ")] " + elmnts[2] + ": " + elmnts[3] 
							+ "<div class='align-right'>"
//								+ "<button class='btnSmall btnUpper' onclick=\"activeFixFixedBox('"+statusCd+"','"+msgIdx+"','" + fixedTextIdx + "')\">수정</button>"
								+ "<button class='btnSmall btnUpper' onclick=\"activeFixFixedBox(this)\">수정</button>"
								+ "<button class='btnSmall btnUpper' onclick=\"addFixText("+elmnts[5]+",'"+prevStatusCd+"','"+elmnts[1]+"','DELETE','" + fixedTextIdx + "', this)\">삭제</button>"
							+ "</div>"
							+ "<div class='fixFixedBox' data-statusCd='"+prevStatusCd+"' data-msgIdx='"+msgIdx+"' data-fixedTextIdx='"+fixedTextIdx+"' style='display:none'>"
								+ "<textarea class='fixFixedText' rows='3' cols='30'></textarea>"
								+ "<br>"
								+ "<button class='btnSmall' onclick=\"addFixText("+elmnts[5]+",'"+prevStatusCd+"','"+elmnts[1]+"','MODIFY','" + fixedTextIdx + "', this)\">확인</button>"
//								+ "<button class='btnSmall' onclick=\"cancleFixFixedText('"+statusCd+"','"+msgIdx+"','" + fixedTextIdx + "')\">취소</button>"
+ "<button class='btnSmall' onclick=\"cancleFixFixedText(this)\">취소</button>"
							+ "</div>"
						+ "</div>"
						+ newLineStr;
				idx++;
				
			}else {
				result += "[" + otomata + "(" + msgIdx + ")] " + elmnts[2] + ": " + elmnts[3] + newLineStr;
			}
		}
		return result + "</p>";
	}

//	@Override
//	public void updateUserDialogFile(Map<String, Object> param) {
//		log.debug("*************************updateUserDialogFile*************************");
//		//line0: topic
//		//line1: time|speacker|orgnl_content|prcssd_content
//		//read 하고 마지막에 add해서 write!
//	}
	
}
