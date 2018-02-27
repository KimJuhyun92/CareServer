package com.skuniv.CareServer.connect;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skuniv.CareServer.model.InfoNotice;
import com.skuniv.CareServer.model.InfoStudent;

public class CheckNoticeList {

	public String toJsonForInfoNotice(ArrayList<InfoNotice> noticeList) { // NoticeList를 받아오기위한 함수
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		return gson.toJson(noticeList);
	}
	

	public String selectNoticeList() {
		Connection conn = new DBconnection().connDB();
		ArrayList<InfoNotice> noticeList = new ArrayList<InfoNotice>();
		System.out.println("<<<<<<<<<<<<check>>>>>>>>>>>>>>>>>>>");
		try {
			Statement st = null;
			Statement ddlst = null;
			ResultSet rs = null; // 결과값을 받아오기 위한 변수
			int r;
			ddlst = conn.createStatement(); // DDL쿼리처리를 위한 변수
			st = conn.createStatement(); // SELECT를 위한 변수
			rs = st.executeQuery("use care;");
			rs = st.executeQuery("select * from notice order by notice_id desc;");
			
			//new String("한글".getBytes("EUC-KR"),"UTF-8");
			
			while (rs.next()) {
				InfoNotice infoNotice = new InfoNotice();
				infoNotice.setNotice_title(URLEncoder.encode(rs.getString(1),"UTF-8"));
				infoNotice.setNotice_date(rs.getString(2));
				infoNotice.setNotice_id(rs.getString(3));
				infoNotice.setNotice_content(URLEncoder.encode(rs.getString(4),"UTF-8"));
				noticeList.add(infoNotice);
			}
			System.out.println("success Notice!");
			rs.close();
			st.close();
			ddlst.close();
			conn.close();

		} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();
		}

		return toJsonForInfoNotice(noticeList);
	}

}
