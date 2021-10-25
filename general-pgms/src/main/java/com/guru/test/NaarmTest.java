package com.guru.test;

import java.awt.Toolkit;
import java.sql.Connection;

import com.guru.util.NaarmUtil;
import com.guru.util.Utility;

public class NaarmTest {
	public static void main(String[] args) throws Exception{
//		Connection conn = Utility.getConnection("localhost", 3306 ,"", "admin", "12345");
	    Connection conn = Utility.getConnection("15.206.220.70", 3406 ,"", "admin", "Zc@MysqL2020");
		try {
//			String  metaQuery ="SELECT * FROM zc_naarm.wos_research_areas;";
			String query="SELECT zci.name as 'NAME OF ORGANIZATION', rad.data,ra.name \r\n" + 
					"FROM `zc_naarm`.`wos_research_areas_data` rad\r\n" + 
					"  JOIN `zc_naarm`.`zcn_institution` zci ON   zci.pk_id = rad.institution\r\n" + 
					"  JOIN `zc_naarm`.`wos_research_areas` ra ON  ra.pk_id =rad.wos_research_areas ;";
			NaarmUtil.insertDataFromDbToExcelForNaarm("F:\\TSS-Works\\codefiles\\naarm.xlsx", conn, query);;
		} catch (Exception e) {
			conn.rollback();
		} finally {
			Utility.closeConnection(conn);
		}
		Toolkit.getDefaultToolkit().beep();
		System.out.println("success");
	}
}
