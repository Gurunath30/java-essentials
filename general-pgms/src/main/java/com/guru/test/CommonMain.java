package com.guru.test;

import java.awt.Toolkit;
import java.sql.Connection;

import com.guru.tss.CommonUtils;
import com.guru.util.Utility;

public class CommonMain {
	public static void main(String[] args) throws Exception {
		Connection conn = Utility.getConnection("localhost", 3306 ,"zc_naarm_data", "admin", "12345");
		try {
			CommonUtils.insertDataFromExcelToDb("F:\\TSS-Works\\dump\\ICAR Institutes Logo's & Pics (1).xlsx", conn);
			CommonUtils.updateLogoPic("F:\\TSS-Works\\dump\\ICAR Institutes Logo's & Pics (1).xlsx", conn);
		} catch (Exception e) {
			conn.rollback();
		} finally {
			Utility.closeConnection(conn);
		}
		Toolkit.getDefaultToolkit().beep();
		System.out.println("success");
	}
}
