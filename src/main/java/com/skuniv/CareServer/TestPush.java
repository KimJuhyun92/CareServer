package com.skuniv.CareServer;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import com.skuniv.CareServer.connect.DBconnection;

public class TestPush {
	@RequestMapping(value = "/pushMessage", method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody void pushMessage(HttpServletRequest request, Model model) throws Exception {
		String message = request.getParameter("message");
		String parent_no = request.getParameter("parent_id");
		String getToken;
		String token="";
		System.out.println(message);
		System.out.println(parent_no);
		// msg = URLEncoder.encode(msg, "UTF-8");
		// msg = URLDecoder.decode(msg, "UTF-8");

		// 초기 설정
		String API_KEY = "AAAAoc3RuUE:APA91bHZh2oCqQmecUf-pyu374TneKg-AiGokLg8NGXgQyQz0rU9JZCi3B8yZJiOVP7sc2JIQYsq1gVqFJ_34IPhiquQmgDnyXtf-OAo7O5842II9bLn4KgVq7GJ2HaiwYNkgR8jexBn";
		String FCM_URL = "https://fcm.googleapis.com/fcm/send";

		// db처리
		//ArrayList<String> token = new ArrayList<String>(); // token값을 ArrayList에 저장

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
				getToken = rs.getString(1);
				//token.add(getToken);
				token = getToken;
				System.out.println(getToken);
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

		JsonObject json = new JsonObject();
		JsonObject info = new JsonObject();
		Gson gson = new Gson();

		info.addProperty("body", message); // Notification body
		json.add("notification", info);
		json.addProperty("to", token); // deviceID

		// Data 전송
		try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
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
