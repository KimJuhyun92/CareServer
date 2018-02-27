package com.skuniv.CareServer.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skuniv.CareServer.model.InfoAttendance;
import com.skuniv.CareServer.model.InfoNotice;
import com.skuniv.CareServer.model.InfoStudent;

public class CheckAttendanceList {

	public String toJsonForInfoAttendance(ArrayList<InfoAttendance> attList) { // NoticeList를 받아오기위한 함수
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		return gson.toJson(attList);
	}
	

	public String selectAttList(String student_id) {
		int std_no = Integer.valueOf(student_id);
		Connection conn = new DBconnection().connDB();
		ArrayList<InfoAttendance> attList = new ArrayList<InfoAttendance>();
		System.out.println("<<<<<<<<<<<<check>>>>>>>>>>>>>>>>>>>");
		try {
			Statement st = null;
			Statement ddlst = null;
			ResultSet rs = null; // 결과값을 받아오기 위한 변수
			int r;
			ddlst = conn.createStatement(); // DDL쿼리처리를 위한 변수
			st = conn.createStatement(); // SELECT를 위한 변수
			rs = st.executeQuery("use care;");
			rs = st.executeQuery("select date,att_state from attendance where student_id = " + std_no + ";");

			while (rs.next()) {
				InfoAttendance infoAttendance = new InfoAttendance();
				infoAttendance.setDate(rs.getString(1));
				infoAttendance.setAtt_state(rs.getInt(2));
				attList.add(infoAttendance);
			}
			System.out.println("success Attendance!");
			rs.close();
			st.close();
			ddlst.close();
			conn.close();

		} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();
		}

		return toJsonForInfoAttendance(attList);
	}

}
