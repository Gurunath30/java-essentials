package com.guru.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {
	public static final char GRAVE = '`';
	public static final String DOUBLECODES = "\"";
	public static final String COMMA = ",";

	/**
	 * This method checks whether the Object is null or blank.
	 * 
	 * @param Object
	 * @return boolean
	 */
	public static boolean isBlank(Object o) {
		if (o == null)
			return true;
		else if (o instanceof String) {
			if (((String) o).trim().equals(""))
				return true;
		} else if (o instanceof Collection<?>) {
			if (((Collection<?>) o).isEmpty())
				return true;
		} else if (o instanceof Integer) {
			if (((Integer) o) <= 0)
				return true;
		} else if (o instanceof Long) {
			if (((Long) o) <= 0)
				return true;
		} else if (o instanceof Short) {
			if (((Short) o) <= 0)
				return true;
		} else if (o instanceof Byte) {
			if (((Byte) o) <= 0)
				return true;
		} else if (o instanceof Double) {
			if (((Double) o) <= 0)
				return true;
		} else if (o instanceof Float) {
			if (((Float) o) <= 0)
				return true;
		} else if (o instanceof Map<?, ?>) {
			if (((Map<?, ?>) o).isEmpty())
				return true;
		} else if (o.getClass().isArray()) {
			return Array.getLength(o) == 0;
		} else {
			if (o.toString().trim().equals(""))
				return true;
		}
		return false;
	}

	/**
	 * This method checks whether the Object has some data or not.
	 * 
	 * @param Object
	 * @return boolean
	 */
	public static boolean haveData(Object obj) {
		return !isBlank(obj);
	}

	/**
	 * Establishes the connection for Mysql
	 * 
	 * @param host
	 * @param portNumber
	 * @param schema
	 * @param userName
	 * @param password
	 * @return Connection
	 * @throws Exception
	 */
	public static Connection getConnection(String host, int portNumber, String schema, String userName, String password)
			throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + portNumber + "/" + schema
				+ "?useUnicode=true&characterEncoding=UTF-8" + "&rewriteBatchedStatements=true", userName, password);
		connection.setAutoCommit(false);
		return connection;
	}

	/**
	 * Closes the connection
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public static void closeConnection(Connection connection) throws SQLException {
		if (!connection.getAutoCommit())
			connection.commit();
		if (connection != null && !connection.isClosed())
			connection.close();
	}

	/**
	 * Inserts the data into the db and returns the pk id of last insterted record
	 * 
	 * @param connection
	 * @param sql
	 * @param args
	 * @return Integer
	 * @throws SQLException
	 */
	public static Integer getGeneratedKey(Connection connection, String sql, Object... args) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		int index = 0;
		for (Object value : args) {
			preparedStatement.setObject(++index, value);
		}
		preparedStatement.executeUpdate();
		ResultSet resultSet = preparedStatement.getGeneratedKeys();
		if (resultSet.next()) {
			int id = resultSet.getInt(1);
			preparedStatement.close();
			resultSet.close();
			return id;
		}
		return 0;
	}

	/**
	 * Sets the values to the place holders of a query
	 * 
	 * @param conn
	 * @param sql
	 * @param args
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	private static PreparedStatement query(Connection conn, String sql, Object... args) throws SQLException {
		int index = 0;
		PreparedStatement preStmt = conn.prepareStatement(sql);
		for (Object value : args)
			preStmt.setObject(++index, value);
		return preStmt;
	}

	/**
	 * This method either updates or deletes the record based on query
	 * 
	 * @param conn
	 * @param sql
	 * @param args
	 * @return int
	 * @throws SQLException
	 */
	public static int execute(Connection conn, String sql, Object... args) throws SQLException {
		PreparedStatement preStmt = query(conn, sql, args);
		int result = preStmt.executeUpdate();
		preStmt.close();
		return result;
	}

	/**
	 * Returns the db record as a map based on query if it exists
	 * 
	 * @param conn
	 * @param sql
	 * @param args
	 * @return Map<String, Object>
	 * @throws SQLException
	 */
	public static Map<String, Object> get(Connection conn, String sql, Object... args) throws SQLException {
		if (getMapList(conn, sql, args) != null)
			return getMapList(conn, sql, args).get(0);
		else
			return null;
	}

	/**
	 * Returns the db records as a list of maps
	 * 
	 * @param conn
	 * @param sql
	 * @param args
	 * @return List<Map<String, Object>>
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> getMapList(Connection conn, String sql, Object... args)
			throws SQLException {
		PreparedStatement preStmt = query(conn, sql, args);
		ResultSet resultSet = preStmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		List<Map<String, Object>> listOfRec = new ArrayList<Map<String, Object>>();
		Map<String, Object> record = null;
		while (resultSet.next()) {
			record = new HashMap<String, Object>();
			for (int i = 1; i <= metaData.getColumnCount(); i++)
				record.put(metaData.getColumnName(i), resultSet.getObject(i));
			listOfRec.add(record);
		}
		preStmt.close();
		resultSet.close();
		if (listOfRec.size() == 0)
			return null;
		return listOfRec;
	}

	/**
	 * Submits a batch of commands to the database for execution andif all commands
	 * execute successfully, returns an array of update counts.The int elements of
	 * the array that is returned are orderedto correspond to the commands in the
	 * batch, which are orderedaccording to the order in which they were added to
	 * the batch.
	 * 
	 * @param conn
	 * @param sql
	 * @param list
	 * @throws SQLException
	 */
	public static void batchUpdate(Connection conn, String sql, List<List<Object>> list) throws SQLException {
		PreparedStatement preStmt = conn.prepareStatement(sql);
		for (int record = 0; record < list.size(); record++) {
			int index = 0;
			for (Object value : list.get(record)) {
				preStmt.setObject(++index, value);
			}
			preStmt.addBatch();
		}
		preStmt.executeBatch();
		preStmt.close();
	}

	/**
	 * Encloses the given string in grave symbol
	 * 
	 * @param s
	 * @return
	 */
	public static String encloseGrave(String s) {
		if (Utility.isBlank(s))
			return null;
		else
			return GRAVE + s.trim() + GRAVE;
	}

	/**
	 * Generates the insert query for given columns
	 * 
	 * @param tableName
	 * @param columns
	 * @return String
	 */
	public static String generateInsertQuery(String tableName, String[] columns) {
		String colNames = "", placeHolders = "";
		for (String c : columns) {
			colNames += encloseGrave(c) + ",";
			placeHolders += "?,";
		}
		colNames = colNames.substring(0, colNames.length() - 1);
		placeHolders = placeHolders.substring(0, placeHolders.length() - 1);
		return "INSERT INTO " + tableName + " (" + colNames + ") VALUES (" + placeHolders + ");";
	}

	/**
	 * Generates the update query for given columns
	 * 
	 * @param tableName
	 * @param columns
	 * @return String
	 */
	public static String generateUpdateQuery(String tableName, String[] columns) {
		String colNames = "";
		for (String c : columns) {
			colNames += encloseGrave(c) + "=?, ";
		}
		colNames = colNames.substring(0, colNames.length() - 2);
		return "UPDATE " + encloseGrave(tableName) + " SET " + colNames + " WHERE `pk_id`=?;";
	}

	/**
	 * Generates the delete query based on pk id for given table
	 * 
	 * @param tableName
	 * @return String
	 */
	public static String generateDeleteQuery(String tableName) {
		return "DELETE FROM " + encloseGrave(tableName) + " WHERE `pk_id`=?;";
	}

	/**
	 * Generates select query
	 * 
	 * @param tableName
	 * @return String
	 */
	public static String generateSelectAllQuery(String tableName) {
		return "SELECT * FROM " + encloseGrave(tableName);
	}

	/**
	 * Generates select query based on id
	 * 
	 * @param tableName
	 * @return String
	 */
	public static String generateSelectById(String tableName) {
		return "SELECT * FROM " + encloseGrave(tableName) + " WHERE " + encloseGrave("pk_id") + "=?";
	}

	/**
	 * Generates select query based on user name and pasword
	 * 
	 * @param tableName
	 * @return String
	 */
	public static String genQueryForUserAndPwd(String tableName) {
		return "SELECT * FROM " + encloseGrave(tableName) + " WHERE " + encloseGrave("user_name") + "=? AND "
				+ encloseGrave("password") + "=?;";
	}

	/**
	 * This method returns the string having double codes for each word in it, where
	 * original string has comma separted words
	 */
	public static String doubleCodedString(String strWithCommas) {
//		String modified="";
//		String[] values = strWithCommas.split(",");
//		for(String value : values) {
//			modified =modified+COMMA+ DOUBLECODES+value+DOUBLECODES;
//		}
//		return modified.substring(1, modified.length()).replace(" ", "");
		return DOUBLECODES + strWithCommas.replaceAll(",", "\",\"").concat("\"").replace(" ", "");
	}

	/**
	 * Generates select query
	 * 
	 * @param tableName
	 * @return String
	 */
	public static String getColsWithGraveOpe(String cols, String tabDotCol,boolean placeHoldersReq) {
		String[] values = cols.split(",");
		String op = "";
		String plcHldr = "";
		for (String col : values) {
			if (haveData(tabDotCol)) {
				op = op + tabDotCol + "." + encloseGrave(col) + COMMA;
			}else {
				op = op + encloseGrave(col) + COMMA;
			}
			if(placeHoldersReq) plcHldr+="?,";
		}
		return op+" "+plcHldr;
	}
	public static String getColsSample(String cols,String dotVal) {
		String[] values = cols.split(", ");
		String op = "";
		for (String col : values) {
			op = op + dotVal + DOUBLECODES + col + DOUBLECODES + ")" + COMMA;
		}
		return op;
	}

	/**
	 * Deletes the records from db based on given flag
	 * 
	 * @param conn
	 * @param flag
	 * @param tables
	 * @throws Exception
	 */
	public static void delRecords(Connection conn, String flag, Object... tables) throws Exception {
		execute(conn, "SET FOREIGN_KEY_CHECKS=0");
		for (Object value : tables)
			execute(conn, "DELETE FROM " + value + " WHERE " + flag + " = 1");
		execute(conn, "SET FOREIGN_KEY_CHECKS=1");
	}

	/**
	 * Inserts the column into the given db's
	 * 
	 * @param conn
	 * @param flag
	 * @param tables
	 * @throws Exception
	 */
	public static void insertFlag(Connection conn, String flag, Object... tables) throws Exception {
		for (Object value : tables) {
			execute(conn, "ALTER TABLE " + value + "ADD COLUMN " + flag
					+ " TINYINT(1) NOT NULL DEFAULT 0 AFTER `is_deleted`");
		}
	}

	/**
	 * Removes the given column from the tables
	 * 
	 * @param conn
	 * @param colName
	 * @param tables
	 * @throws Exception
	 */
	public static void delColumn(Connection conn, String colName, Object... tables) throws Exception {
		for (Object value : tables) {
			execute(conn, "ALTER TABLE " + value + "DROP COLUMN" + colName);
		}
	}

	/**
	 * Removes the db's based on given tables
	 * 
	 * @param conn
	 * @param tables
	 * @throws Exception
	 */
	public static void delDb(Connection conn, Object... tables) throws Exception {
		for (Object value : tables) {
			execute(conn, "DROP DATABASE " + value);
		}
	}

	/**
	 * Creates the schema with the given name
	 * 
	 * @param conn
	 * @param tables
	 * @throws Exception
	 */
	public static void createDb(Connection conn, Object... tables) throws Exception {
		for (Object value : tables) {
			execute(conn, "CREATE SCHEMA " + value);
		}
	}

	/**
	 * Inserts the data coming from the db to an excel file based on a query
	 * 
	 * @param file
	 * @param conn
	 * @param query
	 * @throws Exception
	 */
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

	/**
	 * Removes all special chars in a given object
	 * 
	 * @param data
	 * @return String
	 */
	public static String removeAllSpeChar(Object data) {
		if (isBlank(data))
			return null;
		return (data.toString()).replaceAll("[^a-zA-Z0-9.%+()']", " ").trim().replaceAll("\\s+", "_").toLowerCase();
	}

	/**
	 * Returns string (null incase of blank obj) based on the given Object
	 * 
	 * @param data
	 * @return String
	 */
	public static String checkIsBlank(Object data) {
		if (isBlank(data))
			return null;
		return data.toString();
	}

	/**
	 * Returns pkid (null incase of blank pkid)
	 * 
	 * @param pkId
	 * @return Long
	 */
	public static Long checkPkId(long pkId) {
		if (isBlank(pkId))
			return null;
		return pkId;
	}

	/**
	 * Generates zc client names
	 * 
	 * @param template
	 * @param filePath
	 * @param from
	 * @param to
	 * @param client
	 * @throws Exception
	 */

	public static void generateLicenses(String template, String filePath, int from, int to, String client)
			throws Exception {
		for (int i = from; i <= to; i++) {
			String fileName = (i < 10) ? (client + "0" + i + ".zc_lic") : (client + i + ".zc_lic");
			File file = new File(filePath + "\\" + fileName);
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(template.replace("c$no$", client + i));
			System.out.println(fileName);
			fileWriter.flush();
			fileWriter.close();
		}
	}

	/**
	 * Returns sql format date
	 * 
	 * @param date
	 * @return Date
	 * @throws Exception
	 */
	public static Date getDate(Object date) throws Exception {
		if (isBlank(date))
			return null;
		String format = "dd-MMM-yyyy";
		return new Date(new SimpleDateFormat(format).parse(date.toString()).getTime());
	}

	/**
	 * Converts input blob into bytes and returns a string of base64 format.
	 * 
	 * @param blob
	 * @return String
	 * @throws Exception
	 */
	public static String convertToBase64(Blob blob) throws Exception {
		if (isBlank(blob))
			return "";
		InputStream inputStream = blob.getBinaryStream();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inputStream.read(buffer)) != -1)
			outputStream.write(buffer, 0, bytesRead);
		byte[] imageBytes = outputStream.toByteArray();
		String base64Image = Base64.getEncoder().encodeToString(imageBytes);
		return base64Image;
	}

	/**
	 * This method checks whether the String has valid mobile number or not.
	 * 
	 * @param String
	 * @return boolean
	 */
	public static boolean phoneValidation(String phone) {
		Pattern pattern = Pattern.compile("^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[6789]\\d{5,14}$");
		return phone == null ? false : pattern.matcher(phone).matches();
	}

	/**
	 * This method serializes the given object
	 * 
	 * @param Object
	 * @param File
	 * @return boolean
	 */
	public static boolean toSerialize(Object obj, File file) {
		if (isBlank(file) || isBlank(obj)) {
			return false;
		}
		FileOutputStream outputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(obj);
			objectOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (objectOutputStream != null || outputStream != null) {
				try {
					objectOutputStream.close();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * Deserializes the given object
	 * 
	 * @param File
	 * @return Object
	 * @throws ClassNotFoundException
	 */
	public static Object toDeserialize(File file) {
		if (isBlank(file)) {
			return null;
		}
		FileInputStream inputStream = null;
		ObjectInputStream objectInputStream = null;
		Object obj = null;
		try {
			inputStream = new FileInputStream(file);
			objectInputStream = new ObjectInputStream(inputStream);
			obj = objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (objectInputStream != null || inputStream != null) {
				try {
					inputStream.close();
					objectInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	/**
	 * This method removes outer spaces and extra spaces in the middle as well from
	 * a String.
	 * 
	 * @param String
	 * @return String
	 */
	public static String removeExtraSpaces(String data) {
		if (isBlank(data)) {
			return null;
		}
		return data.replaceAll("\\s+", " ").trim();
	}

	/**
	 * This method generates given number of zeros.
	 * 
	 * @param int
	 * @return String
	 */
	public static String generateZeros(int count) {
		if (isBlank(count)) {
			return "";
		}
		return new String(new char[count]).replace("\0", "0");
	}

	/**
	 * This method removes special characters except space char.
	 * 
	 * @param String
	 * @return String
	 */
	public static String removeSpecialChars(String data) {
		if (isBlank(data)) {
			return null;
		}
		return removeExtraSpaces(data.replaceAll("[^a-zA-Z0-9 ]+", ""));
	}

	/**
	 * This method converts given String to a camel case String.
	 * 
	 * @param String
	 * @return String
	 */
	public static String toCamelCase(String data) {
		if (isBlank(data)) {
			return null;
		}
		return toPascalCase(data).substring(0, 1).toLowerCase() + toPascalCase(data).substring(1);
	}

	/**
	 * This method converts given String to a Pascal case String.
	 * 
	 * @param String
	 * @return String
	 */
	public static String toPascalCase(String data) {
		if (isBlank(data)) {
			return null;
		}
		String[] words = removeSpecialChars(data).split(" ");
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			output.append(words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase());
		}
		return output.toString();
	}

	/**
	 * This method converts given String to a Toggle case String.
	 * 
	 * @param String
	 * @return String
	 */
	public static String toToggleCase(String data) {
		if (isBlank(data)) {
			return null;
		}
		String[] words = removeSpecialChars(data).split(" ");
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			output.append(words[i].substring(0, 1).toLowerCase() + (words[i].substring(1)).toUpperCase() + " ");
		}
		return output.toString().trim();
	}

	/**
	 * This method replaces the given String with a new String in the original
	 * String.
	 * 
	 * @param String
	 * @param String
	 * @param String
	 * @return String
	 */
	public static String replaceString(String data, String replace, String with) {
		if (isBlank(data)) {
			return null;
		} else if (replace == null || with == null || replace.isEmpty() || with.isEmpty()
				|| data.indexOf(replace) == -1) {
			return data;
		} else {
			int index = data.indexOf(replace);
			StringBuilder output = new StringBuilder(data.substring(0, index));
			while (true) {
				output.append(with);
				int lastIndex = index + replace.length();
				index = data.indexOf(replace, index + 1);
				if (index != -1) {
					output.append(data.substring(lastIndex, index));
				} else {
					return output.append(data.substring(lastIndex)).toString();
				}
			}
		}
	}

	/**
	 * This method generates an alphanumeric String having atleast one lowercase,
	 * uppercase and digits in it.
	 * 
	 * @param Integer
	 * @return String
	 */
	public static String generateAlphaNumeric(int count) {
		if (isBlank(count) || count < 3) {
			return null;
		}
		String alphaNumStr = "CDEFGHIJKLMNOPQRSTUVWXYZ0123456789ABabcdefghijklmnopqrstuvwxyz";
		StringBuilder output = new StringBuilder();
		Random rnd = new Random();
		for (int i = 0; i < count; i++) {
			output.append(alphaNumStr.charAt(rnd.nextInt(alphaNumStr.length())));
		}
		if (Utility.validateRandStr(output.toString())) {
			return output.toString();
		} else {
			return generateAlphaNumeric(count);
		}
	}

	/**
	 * This method checks for a presence of alphanumeric chars in a String.
	 * 
	 * @param String
	 * @return boolean
	 */
	public static boolean validateRandStr(String data) {
		return checkRegExp(data, "[0-9]") && checkRegExp(data, "[A-Z]") && checkRegExp(data, "[a-z]");
	}

	/**
	 * This method checks for a given pattern in the provided String.
	 * 
	 * @param String
	 * @param String
	 * @return boolean
	 */
	public static boolean checkRegExp(String data, String regex) {
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(data).find();
	}

	/**
	 * This method returns Date from a given String.
	 * 
	 * @param date   First parameter
	 * @param format Second parameter
	 * @return Date
	 * @throws ParseException
	 */
	public static java.util.Date getDate(String date, String format) throws ParseException {
		if (isBlank(date)) {
			return null;
		}
		if (isBlank(format)) {
			format = "dd-MM-yyyy";
		}
		return new SimpleDateFormat(format).parse(date);
	}

	/**
	 * This method returns date String from a given String.
	 * 
	 * @param Date
	 * @param String
	 * @return String
	 */
	public static String getDateString(java.util.Date date, String format) throws Exception {
		if (isBlank(date)) {
			return null;
		}
		if (isBlank(format)) {
			format = "dd-MM-yyyy";
		}
		try {
			return new SimpleDateFormat(format).format(date);
		} catch (IllegalArgumentException ie) {
			throw new Exception("Invalid format ", ie);
		}
	}

	/**
	 * Overloaded method which returns Date by taking single String parameter.
	 * 
	 * @param date
	 * @return Date
	 * @throws ParseException
	 */
	public static java.util.Date getDate(String date) throws ParseException {
		return getDate(date, null);
	}

	/**
	 * This method finds the difference in current date from given date.
	 * 
	 * @param date   First parameter
	 * @param format Second parameter
	 * @param type   Third parameter
	 * @return Integer
	 * @throws ParseException
	 */

	public static int dateDiffWithToday(String date, String format, String type) throws ParseException {
		return dateDiffByType(getDate(date, format), new java.util.Date(), type);
	}

	/**
	 * Overloaded method which finds the difference in current date from given date
	 * by taking two String parameters
	 * 
	 * @param date First parameter
	 * @param type Second parameter
	 * @return Integer
	 * @throws ParseException
	 */
	public static int dateDiffWithToday(String date, String type) throws ParseException {
		return dateDiffWithToday(date, null, type);
	}

	/**
	 * This method finds the difference in given date from current date.
	 * 
	 * @param Date First parameter
	 * @param Date Second parameter
	 * @param type Third parameter
	 * @return Integer
	 * @throws ParseException
	 */
	public static int dateDiffByType(java.util.Date date, java.util.Date date2, String type) throws ParseException {
		if (date == null || date2 == null || type == null) {
			return -1;
		}
		LocalDate fromLocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate toLocalDate = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		switch (type.toLowerCase()) {
		case "years":
			return (int) ChronoUnit.YEARS.between(fromLocalDate, toLocalDate);
		case "months":
			return (int) ChronoUnit.MONTHS.between(fromLocalDate, toLocalDate);
		case "days":
			return (int) ChronoUnit.DAYS.between(fromLocalDate, toLocalDate);
		default:
			return -1;
		}
	}

	/**
	 * Checks the give ip ipadress is valid or not
	 * 
	 * @param ipAdress
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isIpAddress(String ipAdress) throws Exception {
		if (isBlank(ipAdress)) {
			throw new Exception("Given ip is blank");
		}
		String zeroTo255 = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
		String regex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
		Pattern pattern = Pattern.compile(regex);
		if (!pattern.matcher(ipAdress).matches()) {
			throw new Exception("Given ip is Invalid");
		}
		String[] arrayOfIPAddresses = { "192.26.12.11", "186.13.166.180", "176.14.25.35", "156.16.28.34" };
		return Arrays.asList(arrayOfIPAddresses).contains(ipAdress);
	}

	/**
	 * Creates and returns a file if not exists and if file alreadys exists then
	 * simply returns it.
	 * 
	 * @param path
	 * @return file
	 * @throws Exception
	 */
	public static File getFile(String path) throws Exception {
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		if (file.isDirectory()) {
			throw new Exception("Given path is folder");
		}
		return file;
	}

	/**
	 * Appends the given line to the end of a given file.
	 * 
	 * @param line
	 * @param file
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean appendData(String line, File file) throws Exception {
		if (isBlank(file) || !file.exists()) {
			return false;
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			writer.append(line + "\n");
			writer.flush();
		} catch (IOException ioException) {
			throw ioException;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception exception) {
				throw exception;
			}
		}
		return true;
	}

	/**
	 * Reads the given file and adds each line as an array of elements.
	 * 
	 * @param file
	 * @return String[]
	 * @throws Exception
	 */
	public static String[] readData(File file) throws Exception {
		if (isBlank(file)) {
			return null;
		}
		BufferedReader reader = null;
		List<String> lines = new ArrayList<String>();
		try {
			String line = "";
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception exception) {
			throw exception;
		} finally {
			try {
				reader.close();
			} catch (Exception exception) {
				throw exception;
			}
		}
		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * Reading from text file as string in Java
	 * 
	 * @param fileName
	 * @return String
	 * @throws Exception
	 */
	public static String readFileAsString(String fileName) throws Exception {
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		return data;
	}

	/**
	 * Converts json contains in the .json file to string
	 * 
	 * @param filePath
	 * @return String
	 * @throws Exception
	 */

	public static String jsonString(String filePath) throws Exception {
		if (isBlank(filePath))
			return null;
		return FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
	}

	/**
	 * Removes the given line of data from a given file.
	 * 
	 * @param lineNo
	 * @param file
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean removeData(int lineNo, File file) throws Exception {
		if (isBlank(lineNo) || isBlank(file)) {
			return false;
		}
		return dataToDelOrModify(lineNo, file, null);
	}

	/**
	 * Updates or removes the given line of data into the file.
	 * 
	 * @param line
	 * @param lineNo
	 * @param file
	 * @return boolean
	 * @throws Exception
	 */
	private static boolean dataToDelOrModify(int lineNo, File file, String line) throws Exception {
		boolean status = false;
		File tempFile = getFile("temp.csv");
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			writer = new BufferedWriter(new FileWriter(tempFile));
			String lineFromFile = "";
			int count = 0;
			while ((lineFromFile = reader.readLine()) != null) {
				count++;
				if (count == lineNo) {
					status = true;
					if (haveData(line)) {
						writer.write(line + "\n");
					}
				} else {
					writer.write(lineFromFile + "\n");
				}
			}
		} catch (Exception exception) {
			throw exception;
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (writer != null)
					writer.close();
			} catch (Exception exception) {
				throw exception;
			}
		}
		if (status) {
			if (!file.delete()) {
				throw new Exception("Problem while deleting a old file");
			}
			if (!tempFile.renameTo(file)) {
				throw new Exception("Problem while renaming a temp file");
			}
		} else {
			if (!tempFile.delete()) {
				throw new Exception("Problem while deleting a temp file");
			}
		}
		return status;
	}

	/**
	 * Updates the given line of data based on lineNo into a given file.
	 * 
	 * @param line
	 * @param lineNo
	 * @param file
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean modifyData(String line, int lineNo, File file) throws Exception {
		if (isBlank(line) || isBlank(lineNo) || isBlank(file)) {
			return false;
		}
		return dataToDelOrModify(lineNo, file, line);
	}

	public static String getJson(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String requestBean = mapper.writeValueAsString(json);
		return requestBean;	
	}
	
	/**
	 * Return file data as string array
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	 
	public static String[] fileDataToStrArr(String fileName) throws Exception {
		FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) 
        {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
	}
	
	/**
	 * This method reads and images from given excel file and stores them in the given folder
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void imageFrmExcelToFolder(String file) throws Exception {
		FileInputStream inpStream = new FileInputStream(new File(file));
		XSSFWorkbook workbook = new XSSFWorkbook(inpStream);
		XSSFSheet sheet = workbook.getSheetAt(0);
        int pictureColumn = 4;

        for (XSSFShape shape : sheet.getDrawingPatriarch()) {
            if (shape instanceof XSSFPicture) {
            	XSSFPicture picture = (XSSFPicture) shape;
            	XSSFClientAnchor anchor = (XSSFClientAnchor) picture.getAnchor();

                // Ensure to use only relevant pictures
                if (anchor.getCol1() == pictureColumn) {

                    // Use the row from the anchor
                	XSSFRow pictureRow = sheet.getRow(anchor.getRow1());
                    if (pictureRow != null) {
                    	XSSFPictureData data = picture.getPictureData();
						byte data1[] = data.getData();
						FileOutputStream out = new FileOutputStream(
								"F:\\images-naarm\\logos\\logo_" + (pictureRow.getRowNum()+1) + ".png");
						out.write(data1);
						out.close();
                       }
                }
            }
        }
        inpStream.close();
		workbook.close();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(fileDataToStrArr("F:\\TSS-Works\\dump\\narmdata.txt"));
	}
}
