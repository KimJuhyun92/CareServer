package com.skuniv.CareServer;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skuniv.CareServer.connect.DBconnection;

public class HomeControllerTest {

	@Test
	public void test() {

		Connection conn = new DBconnection().connDB();
		Statement st = null;
		ResultSet rs = null;
		Statement ddlst = null;
		int r;
		try {
			ddlst = conn.createStatement();
			st = conn.createStatement();
			rs = st.executeQuery("use care;");
			System.out.println("<<<<test>>>>");

			r = ddlst.executeUpdate("Insert into notice(notice_title, notice_date, notice_id, notice_content)"
					+ "values('하이요',now(),'6','안녕');");

			if (st.execute("select * from notice;")) 
				 rs = st.getResultSet();
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
			conn.close();
		} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();
		}
	}
}