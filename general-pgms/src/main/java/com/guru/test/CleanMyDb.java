package com.guru.test;

import java.awt.Toolkit;
import java.sql.Connection;

import com.guru.util.Utility;


public class CleanMyDb {
	public static void main(String[] args) throws Exception{
		Connection conn = Utility.getConnection("localhost", 3306 ,"", "admin", "12345");
		try {
			// "`zc_mr_log`","`zc_mr_admin`","`zc_mr`","`zc_mrs_admin`","`zc_mrs_log`","`zc_mrs`"
			Utility.delDb(conn,"`zc_zeco`","`zc_zeco_admin`","`zc_base_admin`");
		} catch (Exception e) {
			conn.rollback();
		} finally {
			Utility.closeConnection(conn);
		}
		Toolkit.getDefaultToolkit().beep();
		System.out.println("success");
	}
}
