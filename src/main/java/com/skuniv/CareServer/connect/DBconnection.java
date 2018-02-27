package com.skuniv.CareServer.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class DBconnection {
	public Connection connDB() {
		
		Connection conn = null; // null로 초기화 한다.
		
		try {
			String url = "jdbc:mysql://back.ciesutepj7im.ap-northeast-2.rds.amazonaws.com?characterEncoding=utf8"; // 사용하려는 데이터베이스명을 포함한 URL																							
			String db_id = "root"; // 사용자 계정
			String db_pw = "12345678"; // 사용자 계정의 패스워드

			Class.forName("com.mysql.jdbc.Driver"); // 데이터베이스와 연동하기 위해 DriverManager에 등록한다.
			conn = DriverManager.getConnection(url, db_id, db_pw); // DriverManager 객체로부터 Connection 객체를 얻어온다.
			System.out.println("success connect");
		} catch (Exception e) { // 예외가 발생하면 예외 상황을 처리한다.
			System.out.println("failed connect");
			e.printStackTrace();
		}
		
		return conn;
	}
}
