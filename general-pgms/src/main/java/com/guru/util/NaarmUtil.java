package com.guru.util;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class NaarmUtil {
	
	public static void insertDataFromDbToExcel(String file, Connection conn, String query) throws Exception {
		Statement statement = conn.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData metaData = resultSet.getMetaData();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		XSSFRow row = null;
		XSSFCell cell = null;
		int rowNum = 0;
		row = sheet.createRow(rowNum);
		for (int cellNum = 0; cellNum < metaData.getColumnCount(); cellNum++) {
			cell = row.createCell(cellNum);
			cell.setCellValue(metaData.getColumnLabel(cellNum + 1));
			sheet.autoSizeColumn(cellNum);
		}
		rowNum++;
		while (resultSet.next()) {
			row = sheet.createRow(rowNum);
			for (int cellNum = 0; cellNum < metaData.getColumnCount(); cellNum++) {
				cell = row.createCell(cellNum);
				cell.setCellValue(resultSet.getString(cellNum + 1));
			}
			rowNum++;
		}
		FileOutputStream out = new FileOutputStream(new File(file));
		workbook.write(out);
		out.close();
		workbook.close();
	}
	
	public static void insertDataFromDbToExcelForNaarm(String file, Connection conn, String query) throws Exception {
		Statement statement = conn.createStatement();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		String[] cols =Utility.fileDataToStrArr("F:\\TSS-Works\\dump\\narmdata.txt");
		XSSFRow row = null;
		XSSFCell cell = null;
		int rowNum = 0;
		row = sheet.createRow(rowNum);
		List<Map<String, Object>> headings = Utility.getMapList(conn, "SELECT distinct temp_research_areas FROM `zc_naarm`.`wos_research_areas_data`;");
		List<String> headingsList = new ArrayList<String>();
		headingsList.add("INSTITUTE NAME");
		for(Map<String, Object> m : headings) m.forEach((key, value) -> headingsList.add(value.toString()));
		
		//headings
		for (int cellNum = 0; cellNum < headingsList.size(); cellNum++) {
			cell = row.createCell(cellNum);
			cell.setCellValue(headingsList.get(cellNum));
			sheet.autoSizeColumn(cellNum);
		}
		rowNum++;
		
		List<Map<String, Object>> institute = Utility.getMapList(conn, "SELECT   distinct `temp_institution`  FROM  `zc_naarm`.`wos_research_areas_data`;");
		List<String> instituteList = new ArrayList<String>();
		for(Map<String, Object> m : institute) m.forEach((key, value) -> instituteList.add(value.toString()));
		
		for(int a = 0; a < institute.size(); a++) {
			String s = instituteList.get(a);
			ResultSet set = statement.executeQuery( "SELECT temp_institution, data FROM `zc_naarm`.`wos_research_areas_data` WHERE  temp_institution = '"+s+"';");
			row = sheet.createRow(rowNum);
			int cellNum = 0;
			cell = row.createCell(cellNum++);
			cell.setCellValue(s); //insertion
			sheet.autoSizeColumn(cellNum);
			while (set.next()) {
				cell = row.createCell(cellNum++);
				if (cols[cellNum-1].equalsIgnoreCase(set.getString(3))) {
					cell.setCellValue(set.getString(2)); // insertion
				}
			}
			rowNum++;
		}
		FileOutputStream out = new FileOutputStream(new File(file));
		workbook.write(out);
		out.close();
		workbook.close();
	}

}
