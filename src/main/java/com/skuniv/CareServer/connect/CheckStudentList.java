package com.skuniv.CareServer.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skuniv.CareServer.model.InfoStudent;

public class CheckStudentList {

	public String toJsonForInfoStudent(ArrayList<InfoStudent> studentList) { // StrudentList를 받아오기위한 함수
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		return gson.toJson(studentList);
	}

	public String selectStudentList() {
		Connection conn = new DBconnection().connDB();
		ArrayList<InfoStudent> studentList = new ArrayList<InfoStudent>();
		try {
			Statement st = null;
			Statement ddlst = null;
			ResultSet rs = null; // 결과값을 받아오기 위한 변수
			int r;
			ddlst = conn.createStatement(); // DDL쿼리처리를 위한 변   수
			st = conn.createStatement(); // SELECT를 위한 변수
			rs = st.executeQuery("use care;");
			rs = st.executeQuery("select * from student");

			while (rs.next()) {
				InfoStudent infoStudent = new InfoStudent();
				infoStudent.setStd_no(Integer.parseInt(rs.getString(1)));
				infoStudent.setStd_name(rs.getString(2));
				infoStudent.setGrade(Integer.parseInt(rs.getString(3)));
				infoStudent.setParent_id(rs.getInt(4));
				studentList.add(infoStudent);
			}
			System.out.println("success Student!");
			rs.close();
			st.close();
			ddlst.close();
			conn.close();

		} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();
		}

		return toJsonForInfoStudent(studentList);
	}

}
