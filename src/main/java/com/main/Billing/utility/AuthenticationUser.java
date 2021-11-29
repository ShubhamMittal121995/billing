package com.main.Billing.utility;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.main.Billing.controller.MainWindowController;

public class AuthenticationUser {

	public static boolean validatingUser(String userName, String pwd) {
		InputStream input = MainWindowController.class.getClassLoader().getResourceAsStream("application.properties");
		Properties appProp = new Properties();
		try {
			appProp.load(input);
		} catch (IOException e) {
		}
		String url = appProp.getProperty("db3.url");
		String username = appProp.getProperty("db3.username");
		String password = appProp.getProperty("db3.password");
		String driver = appProp.getProperty("db3.driver");
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			ps = conn.prepareStatement(
					"select count(*) from user_info where user_name = ? and user_password = ? and status = 'active' ");
			ps.setString(1, userName);
			ps.setString(2, pwd);

			ResultSet rs = ps.executeQuery();
			int count = 0;
			while (rs.next())
				count = rs.getInt(1);

			return count > 0 ? true : false;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null || conn != null) {
					ps.close();
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
