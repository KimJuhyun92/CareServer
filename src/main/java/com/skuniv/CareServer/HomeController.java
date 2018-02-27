package com.skuniv.CareServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skuniv.CareServer.connect.CheckAttendanceList;
import com.skuniv.CareServer.connect.CheckNoticeList;
//import com.mysql.jdbc.Statement;
import com.skuniv.CareServer.connect.CheckStudentList;
import com.skuniv.CareServer.connect.DBconnection;
import com.skuniv.CareServer.model.InfoLogin;
import com.skuniv.CareServer.model.InfoSign;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	/*
	 * Home Test
	 */

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {

		// test
		return "home";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {

		return "index";
	}
	
	// test
		@RequestMapping(value = "/testSTS", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
		public @ResponseBody String testSTS(HttpServletRequest request, Model model) throws Exception {
			String msg = request.getParameter("msg");
			System.out.println(msg);
			return msg;
		}


	// 학생 목록 가져오기
	@RequestMapping(value = "/getStudentList", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody String getStudentList(Model model) throws Exception {
		return new CheckStudentList().selectStudentList();

	}

	// 공지사항 목록 가져오기
	@RequestMapping(value = "/getNoticeList", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody String getNoticeList(Model model) throws Exception {
		return new CheckNoticeList().selectNoticeList();

	}

	// 출석 목록 가져오기
	@RequestMapping(value = "/getAttList", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody String getAttList(HttpServletRequest request, Model model) throws Exception {
		String student_id = request.getParameter("student_id");
		return new CheckAttendanceList().selectAttList(student_id);

	}
	
	
	@RequestMapping(value = "/testServer", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody void testServer(HttpServletRequest request, Model model) throws Exception {
		String id = request.getParameter("id");
		System.out.println("test " + id);

	}

	// Login
	@RequestMapping(value = "/login", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody String login(HttpServletRequest request, Model model) throws Exception {
		ArrayList<InfoLogin> infoLogin = new ArrayList<InfoLogin>();
		String parent_id = request.getParameter("parent_id");
		String parent_pw = request.getParameter("parent_pw");
		String id = "";
		String pw = "";
		String result = "";
		String fail = "fail";
		System.out.println("id : " + parent_id + " password : " + parent_pw);

		Connection dbConn = new DBconnection().connDB();
		Statement st = null;
		ResultSet rs = null;
		Statement ddlst = null;

		try {
			ddlst = dbConn.createStatement();
			st = dbConn.createStatement();
			rs = st.executeQuery("use care;");
			System.out.println("<<<<LOGIN TEST>>>>>");

			if (!parent_id.equals("none")) {
				if (st.execute(
						"select parent_id, parent_pw, std_no from parent inner join student on parent.parent_no = student.parent_no where parent_id ='"
								+ parent_id + "';"))
					rs = st.getResultSet();
				while (rs.next()) {
					InfoLogin loginInfo = new InfoLogin();
					loginInfo.setParent_id(rs.getString(1));
					loginInfo.setParent_pw(rs.getString(2));
					loginInfo.setStd_no(rs.getString(3));
					System.out.println(
							"id : " + id + "pw: " + pw + " id2: " + rs.getString(1) + "pw2: " + rs.getString(2));

					if (parent_id.equals(parent_id) && parent_pw.equals(parent_pw) == true) {
						System.out.println("ok");
						loginInfo.setState("1");
					} else {
						loginInfo.setState("0");
						System.out.println("fail");
					}
					infoLogin.add(loginInfo);
					id = rs.getString(1);
					pw = rs.getString(2);
					System.out.println("id : " + id + " password : " + pw + "state : " + loginInfo.getState());
				}
			}
			else {
				System.out.println("none state!");
				InfoLogin loginInfo = new InfoLogin();
				loginInfo.setParent_id("none");
				loginInfo.setParent_pw("none");
				loginInfo.setStd_no("none");
				loginInfo.setState("0");

			}
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			result = gson.toJson(infoLogin);
			rs.close();
			st.close();
			ddlst.close();
			dbConn.close();

		} catch (

		Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();
		}

		return result;

	}

	// 공지사항 삭제하기
	@RequestMapping(value = "/deleteFromDB", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody void deleteFromDB(HttpServletRequest request, Model model) throws Exception {
		String notice_id = "";
		String std_no = "";
		String type = request.getParameter("type");

		if (type.equals("0")) {
			notice_id = request.getParameter("index");
		} else if (type.equals("1")) {
			std_no = request.getParameter("index");
		}

		System.out.println(type);
		System.out.println(notice_id);
		System.out.println(std_no);

		Statement st = null;
		ResultSet rs = null;
		Statement ddlst = null;
		int r;
		Connection dbconn = new DBconnection().connDB();
		try {

			ddlst = dbconn.createStatement();
			st = dbconn.createStatement();
			rs = st.executeQuery("use care;");

			System.out.println("<<<<delete test>>>>");

			// 공지사항 삭제 쿼리
			if (type.equals("0")) {
				r = ddlst.executeUpdate("delete from notice where notice_id = " + notice_id + ";");
			}

			// 학생 삭제 쿼리
			else if (type.equals("1")) {
				r = ddlst.executeUpdate("delete from student where std_no = " + std_no + ";");
			}

			rs.close();
			st.close();
			ddlst.close();
			dbconn.close();
		} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();
		}
	}

	// 공지사항 입력
	@RequestMapping(value = "/pushNotice", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody void pushNotice(HttpServletRequest request, Model model) throws Exception {
		String token = "";
		// 초기 설정
		String API_KEY = "AAAAoc3RuUE:APA91bHZh2oCqQmecUf-pyu374TneKg-AiGokLg8NGXgQyQz0rU9JZCi3B8yZJiOVP7sc2JIQYsq1gVqFJ_34IPhiquQmgDnyXtf-OAo7O5842II9bLn4KgVq7GJ2HaiwYNkgR8jexBn";
		String FCM_URL = "https://fcm.googleapis.com/fcm/send";

		// Http 연결
		URL url = new URL(FCM_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key=" + API_KEY);
		conn.setRequestProperty("Content-Type", "application/json");

		// 변수 설정
		JsonObject json = new JsonObject();
		JsonObject nBody = new JsonObject();
		JsonObject dBody = new JsonObject();
		Gson gson = new Gson();

		// deviceID
		json.addProperty("to", "topics/notice");

		// Notification body
		nBody.addProperty("body", "공지사항이 등록되었습니다.");
		nBody.addProperty("title", "MentorAcademy");
		// Data Body
		dBody.addProperty("message", "공지사항이 등록되었습니다.");
		dBody.addProperty("title", "BackGround");
		json.add("notification", nBody);
		json.add("data", dBody);

		// 형식 확인
		System.out.println(json);

		// Data 전송
		try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
			wr.write(json.toString());
			wr.flush();

		} catch (Exception e) {
		}

		// Error 처리
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");

		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();
	}

	// 공지사항 입력 받아서 디비에 저장
	@RequestMapping(value = "/getNoticeInput", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody void getNoticeInput(HttpServletRequest request, Model model) throws Exception {
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		title = URLDecoder.decode(title, "UTF-8");
		content = URLDecoder.decode(content, "UTF-8");
		System.out.println(title);
		System.out.println(content);

		Statement st = null;
		ResultSet rs = null;
		Statement ddlst = null;
		int r;
		Connection dbconn = new DBconnection().connDB();
		try {

			ddlst = dbconn.createStatement();
			st = dbconn.createStatement();
			rs = st.executeQuery("use care;");

			System.out.println("<<<<notice input test>>>>");

			// 공지사항 입력 쿼리
			r = ddlst.executeUpdate("Insert into notice(notice_title, notice_date, notice_content) values('" + title
					+ "',now(),'" + content + "');");

			// 확인 작업
			if (st.execute("select * from notice;")) {
				rs = st.getResultSet();
			}
			System.out.println("success!");
			while (rs.next()) {
				System.out.println(rs.getString(1));
				System.out.println(rs.getString(2));
				System.out.println(rs.getString(3));
				System.out.println(rs.getString(4));
			}

			rs.close();
			st.close();
			ddlst.close();
			dbconn.close();
		} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();

		}

		// 초기 설정
		String API_KEY = "AAAAoc3RuUE:APA91bHZh2oCqQmecUf-pyu374TneKg-AiGokLg8NGXgQyQz0rU9JZCi3B8yZJiOVP7sc2JIQYsq1gVqFJ_34IPhiquQmgDnyXtf-OAo7O5842II9bLn4KgVq7GJ2HaiwYNkgR8jexBn";
		String FCM_URL = "https://fcm.googleapis.com/fcm/send";

		// Http 연결
		URL url = new URL(FCM_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key=" + API_KEY);
		conn.setRequestProperty("Content-Type", "application/json");

		// 변수 설정
		JsonObject json = new JsonObject();
		JsonObject nBody = new JsonObject();
		JsonObject dBody = new JsonObject();
		Gson gson = new Gson();

		// deviceID
		json.addProperty("to", "/topics/notice");

		// Notification body
		nBody.addProperty("body", "공지사항이 등록되었습니다.");
		nBody.addProperty("title", "MentorAcademy");
		// Data Body
		dBody.addProperty("message", "공지사항이 등록되었습니다.");
		dBody.addProperty("title", "BackGround");
		json.add("notification", nBody);
		json.add("data", dBody);

		// 형식 확인
		System.out.println(json);

		// Data 전송
		try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
			wr.write(json.toString());
			wr.flush();

		} catch (Exception e) {
		}

		// Error 처리
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");

		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();

	}

	// 부모Client 회원가입 후 디비처리
	@RequestMapping(value = "/getSignUpInfo", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody void getToken(HttpServletRequest request, Model model) throws Exception {
		String getParentToken;
		String getSignUpInfo;
		String parent_no = null;
		getParentToken = request.getParameter("token");
		getSignUpInfo = request.getParameter("signUpInfo");

		// Gson 파싱
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		InfoSign infoSign = new InfoSign();
		infoSign = gson.fromJson(getSignUpInfo, InfoSign.class);

		// 학생 이름 디코딩
		String student_name = infoSign.getStd_name();
		student_name = URLDecoder.decode(student_name, "UTF-8");
		System.out.println(student_name);

		// System.out.println("<<<<<<<<<<<TOKEN>>>>>>>>>>>");
		// System.out.println(getParentToken);
		// System.out.println(infoSign.getId());
		// System.out.println(infoSign.getPw());
		// System.out.println(infoSign.getGrade());
		// System.out.println(infoSign.getParent_hp());

		Connection conn = new DBconnection().connDB();
		Statement st = null;
		ResultSet rs = null;
		Statement ddlst = null;
		int r;

		try {
			ddlst = conn.createStatement();
			st = conn.createStatement();
			rs = st.executeQuery("use care;");
			// 학부모 정보 입력
			r = ddlst.executeUpdate(
					"Insert into parent(parent_id, parent_pw, parent_hp, token)" + "values('" + infoSign.getId() + "','"
							+ infoSign.getPw() + "','" + infoSign.getParent_hp() + "','" + getParentToken + "')");
			if (st.execute("select parent_no from parent where parent_id = '" + infoSign.getId() + "';"))
				rs = st.getResultSet();
			while (rs.next()) {
				parent_no = rs.getString(1);
			}

			// 학생 정보 입력
			r = ddlst.executeUpdate("Insert into student(std_name, grade, parent_no)" + "values('" + student_name
					+ "','" + infoSign.getGrade() + "','" + parent_no + "')");

			if (st.execute("select * from parent;")) {
				rs = st.getResultSet();
			}

			rs.close();
			st.close();
			ddlst.close();
			conn.close();
			// 예외가 발생하면 예외 상황을 처리한다.
		} catch (Exception e) {
			System.out.println("failed connect");
			e.printStackTrace();
		}
	}
 
	// PUSH MESSAGE 보내기
	@RequestMapping(value = "/pushMessage", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody void pushMessage(HttpServletRequest request, Model model) throws Exception {
		String message = request.getParameter("message");
		message = URLDecoder.decode(message, "UTF-8");
		String parent_no = request.getParameter("parent_id");
		String token = "";
		System.out.println(message);
		System.out.println(parent_no);

		// 초기 설정
		String API_KEY = "AAAAoc3RuUE:APA91bHZh2oCqQmecUf-pyu374TneKg-AiGokLg8NGXgQyQz0rU9JZCi3B8yZJiOVP7sc2JIQYsq1gVqFJ_34IPhiquQmgDnyXtf-OAo7O5842II9bLn4KgVq7GJ2HaiwYNkgR8jexBn";
		String FCM_URL = "https://fcm.googleapis.com/fcm/send";

		Connection dbConn = new DBconnection().connDB();
		Statement st = null;
		ResultSet rs = null;
		Statement ddlst = null;
		int r;

		try {
			ddlst = dbConn.createStatement();
			st = dbConn.createStatement();
			rs = st.executeQuery("use care;");

			System.out.println("<<<<test>>>>");
			if (st.execute("select token from parent where parent_no = " + parent_no + ";"))
				rs = st.getResultSet();
			while (rs.next()) {
				token = rs.getString(1);
				// token 출력
				System.out.println(token);
			}
			rs.close();
			st.close();
			ddlst.close();
			dbConn.close();
		} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();
		}

		// Http 연결
		URL url = new URL(FCM_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key=" + API_KEY);
		conn.setRequestProperty("Content-Type", "application/json");

		// 변수 설정
		JsonObject json = new JsonObject();
		JsonObject nBody = new JsonObject();
		JsonObject dBody = new JsonObject();
		Gson gson = new Gson();

		// deviceID
		json.addProperty("to", token);

		// Notification body
		nBody.addProperty("body", message);
		nBody.addProperty("title", "MentorAcademy");
		// Data Body
		dBody.addProperty("message", message);
		dBody.addProperty("title", "BackGround");
		json.add("notification", nBody);
		json.add("data", dBody);

		// 형식 확인
		System.out.println(json);

		// Data 전송
		try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
			wr.write(json.toString());
			wr.flush();

		} catch (Exception e) {
		}

		// Error 처리
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");

		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();
	}

	// 출석 푸쉬 관련
	@RequestMapping(value = "/insertAttendance", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded", 
			produces = "text/json; charset=UTF-8")
	public @ResponseBody void insertAttendance(HttpServletRequest request, Model model) throws Exception {
		String stdNo = request.getParameter("stdNo");
		String state = request.getParameter("state");
		String message = request.getParameter("message");
		message = URLDecoder.decode(message, "UTF-8");
		System.out.println(message);
		String token = "";
		System.out.println(stdNo);

		SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		String todayDate = today.format(calendar.getTime());
		System.out.println(todayDate);
		System.out.println(todayDate);

		// 초기 설정
		String API_KEY = "AAAAoc3RuUE:APA91bHZh2oCqQmecUf-pyu374TneKg-AiGokLg8NGXgQyQz0rU9JZCi3B8yZJiOVP7sc2JIQYsq1gVqFJ_34IPhiquQmgDnyXtf-OAo7O5842II9bLn4KgVq7GJ2HaiwYNkgR8jexBn";
		String FCM_URL = "https://fcm.googleapis.com/fcm/send";

		Statement st = null;
		ResultSet rs = null;
		Statement st2 = null;
		ResultSet rs2 = null;
		Statement ddlst = null;
		int r;
		Connection dbconn = new DBconnection().connDB();
		try {

			ddlst = dbconn.createStatement();
			st = dbconn.createStatement();
			rs = st.executeQuery("use care;");

			System.out.println("<<<<test>>>>");

			if (st.execute("select token from parent where parent_no in (select parent_no from student where std_no ="
					+ stdNo + ");"))
				rs = st.getResultSet();
			while (rs.next()) {
				token = rs.getString(1);
				System.out.println(token);
			}
			if (st.execute("select date from attendance where student_id = " + stdNo + ";"))
				rs = st.getResultSet();

			boolean flag = false; //증복 검사위한 flag
			while (rs.next()) {
				System.out.println("db: " + rs.getString(1));
				System.out.println("today: " + todayDate);
				// 중복 입력이 된다면 갱신
				if (rs.getString(1).equals(todayDate)) {
					flag = true;
					System.out.println("check flag : " + flag);
				}
			}
			if (flag == true) {
				r = ddlst.executeUpdate(
						"update attendance set att_state = '" + state + "' where date = '" + todayDate + "';");
				System.out.println("update!");
			}
			// 중복이 아니라면 출석표 입력 쿼리
			else if (flag == false) {
				r = ddlst.executeUpdate("Insert into attendance(student_id, date, att_state) values('" + stdNo
						+ "',now(),'" + state + "');");
				System.out.println("insert!");
			}
			
			// 확인 작업
			if (st.execute("select * from attendance;")) {
				rs = st.getResultSet();
			}
			System.out.println("success!");
			while (rs.next()) {
				System.out.println(rs.getString(1));
				System.out.println(rs.getString(2));
				System.out.println(rs.getString(3));
				System.out.println(rs.getString(4));
			}

			rs.close();
			st.close();
			ddlst.close();
			dbconn.close();
		} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();

		}

		// Http 연결
		URL url = new URL(FCM_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key=" + API_KEY);
		conn.setRequestProperty("Content-Type", "application/json");

		// 변수 설정
		JsonObject json = new JsonObject();
		JsonObject nBody = new JsonObject();
		JsonObject dBody = new JsonObject();
		Gson gson = new Gson();

		// deviceID
		json.addProperty("to", token);

		// Notification body
		nBody.addProperty("body", message);
		nBody.addProperty("title", "MentorAcademy");
		// Data Body
		dBody.addProperty("message", message);
		dBody.addProperty("title", "BackGround");
		json.add("notification", nBody);
		json.add("data", dBody);

		// 형식 확인
		System.out.println(json);

		// Data 전송
		try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
			wr.write(json.toString());
			wr.flush();

		} catch (Exception e) {
		}

		// Error 처리
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");

		while ((output = br.readLine()) != null) {
			System.out.println(output);
		}

		conn.disconnect();
	}
}
