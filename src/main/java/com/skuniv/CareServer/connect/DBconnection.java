package com.skuniv.CareServer.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class DBconnection {
	public Connection connDB() {
		
		Connection conn = null; // null�� �ʱ�ȭ �Ѵ�.
		
		try {
			String url = "jdbc:mysql://back.ciesutepj7im.ap-northeast-2.rds.amazonaws.com?characterEncoding=utf8"; // ����Ϸ��� �����ͺ��̽����� ������ URL																							
			String db_id = "root"; // ����� ����
			String db_pw = "12345678"; // ����� ������ �н�����

			Class.forName("com.mysql.jdbc.Driver"); // �����ͺ��̽��� �����ϱ� ���� DriverManager�� ����Ѵ�.
			conn = DriverManager.getConnection(url, db_id, db_pw); // DriverManager ��ü�κ��� Connection ��ü�� ���´�.
			System.out.println("success connect");
		} catch (Exception e) { // ���ܰ� �߻��ϸ� ���� ��Ȳ�� ó���Ѵ�.
			System.out.println("failed connect");
			e.printStackTrace();
		}
		
		return conn;
	}
}
