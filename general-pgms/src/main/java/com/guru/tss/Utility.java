package com.guru.tss;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {
	
	private static Logger log = Logger.getLogger(Utility.class.toString());
	
	public static final char GRAVE = '`';
	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z]+(\\.[A-Za-z]+)*(\\.[A-Za-z]{2})$";
	private static final String PASSWORD_REGEX = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{5,12})";
	private static final String PHONE_REGEX = "^(\\+91?)?[6789]{1}\\d{9}$";
	
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

	public static boolean hasNotBlank(Object o) {
		return isBlank(o);
	}
	
	public static boolean isValidPath(String path) throws Exception {
		if(isBlank(path)) throw new Exception("Path is Empty");
		File file = new File(path);
		 return (file.isAbsolute() && file.exists())?true:false;
	}
	public static String getJsonFromFile(String path) throws Exception {
		if(!isValidPath(path)) throw new FileNotFoundException("File Not Found");
		return new String(Files.readAllBytes(Paths.get(path)),StandardCharsets.UTF_8);
	}
	public static String getTime() {
		return new SimpleDateFormat("hhmmss").format(new Date());
	}
	private static String getDirectory(String folder,int from, int to, boolean flag, String folderName) {
//		return (flag) ? folder+from+"to"+to : folder+getTime();
		return (!flag) ?  folder+getTime() : !isBlank(folderName) ? folderName : folder+from+"to"+to;
	}
	//this method is used to create the folders based on dates ex: used in copy the downloaded files
	public static String getDirectory(String dir, String folderName) {
		if (isBlank(dir)) return "Path is Empty";
		String _path = getDirectory(null, 0, 0, true, folderName);
		File theDir = new File(dir+"\\"+_path);
		return theDir.exists() ? "Folder is Already Exists": theDir.mkdir() ? theDir.getAbsolutePath(): "Failed"; 
	}
	
	//this is for generating licenses files
	public static String getDirectory(String dir, String folder, boolean flag, int from, int to) {
		if (isBlank(dir)) return "Path is Empty";
//		flag = (from <= 0 || to <= 0) ? false :true;
//		String _path =(flag) ? dir+"\\"+getDirectory(folder, from, to) : dir+"\\"+folder+getTime();
		String _path = getDirectory(folder, from, to, flag,null);
		File theDir = new File(dir+"\\"+_path);
		return theDir.exists() ? "Folder is Already Exists": theDir.mkdir() ? theDir.getAbsolutePath(): "Failed"; 
	}
	
	public static String getStringCases(String s, boolean flag) {
		if(isBlank(s)) return "Empty String";
		return (flag)? s.toLowerCase():s.toUpperCase();
	}
	public static int getPositive(int number) {
		return !isBlank(number) ? 0 : Math.abs(number);
	}
	public static Object getDefaults(Object o) {
		return ((o instanceof Integer) ? (((int) o < 0) ? 0: o) 
				: ((o instanceof Double) ? (((double) o < 0.0) ? 0.0: o)
				: ((o instanceof Long) ? (((long) o < 0L) ? 0L: o): o)));
	}
	public static boolean isExcel(String file) {
		return (isBlank(file))? false : ("xlsx".equalsIgnoreCase(FilenameUtils.getExtension(file)) || "xls".equalsIgnoreCase(FilenameUtils.getExtension(file)) ? true : false);
	}
	public static String dbColumn(String name) {
		String s =  (isBlank(name)) ? "Column name is Empty" : getStringCases(name, true).replaceAll("[^a-zA-Z0-9]+", "_");
		return (s.endsWith("_") ? s = s.substring(0, s.length() - 1): s);
	}
	public static String[] getConvertedColumns(String[] s) {
		for (int index =0; index < s.length; index++){
			  s[index] = s[index].replace(s[index],dbColumn(s[index]));
			}
		return s;
		
	}
	
	public static String encloseGrave(String s) {
		return (Utility.isBlank(s))? null: GRAVE + s.trim() + GRAVE;
	}
	public static String generateInsertQuery(String schemaName, String tableName, String[] columns) {
		String tableWithSchema = (Utility.isBlank(schemaName) ? encloseGrave(tableName): (encloseGrave(schemaName) + "." + encloseGrave(tableName)));
		String columnNames = "", placeHolders = "";
		for (String c : columns) {
			columnNames += encloseGrave(c) + ",";
			placeHolders += "?,";
		}
		columnNames = columnNames.substring(0, columnNames.length() - 1);
		placeHolders = placeHolders.substring(0, placeHolders.length() - 1);
		return "INSERT INTO " + tableWithSchema + " (" + columnNames + ") VALUES (" + placeHolders + ");";
	}
	 
	
	public static Date getSqlDate(String date, String format) throws ParseException {
		return new Date(new SimpleDateFormat(format).parse(date).getTime());
	}
	public static boolean isDate(String date) {
		String regex = "(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((18|19|20|21)\\d\\d)";
		return Pattern.matches(regex, date);
	}
	public static String getFileInfo(String path, boolean flag) {
		if(isBlank(path)) return "Path is Empty";
		return (flag) ? FilenameUtils.removeExtension(path) : FilenameUtils.getExtension(path);
	}
	
	public static String getFileName(String file) {
		if(isBlank(file)) return "File is Empty";
		return FilenameUtils.removeExtension(new File(file).getName());
	}
	public static Set<Object> convertMapToSet(Map<String, Object> map, boolean flag) {
//		Set<String> keys = new LinkedHashSet<String>();
		Set<Object> keys = new LinkedHashSet<Object>();
		if(flag) {
			Set<String> keySet = new LinkedHashSet<String>(map.keySet());
	        keySet.forEach(key-> keys.add(key));
	        return keys;
		} else {
			Set<Object> values = new LinkedHashSet<Object>(map.values());
	        values.forEach(value->  values.add(value));
	        return values;
		}
	}
	
	/**
	 * @author G NARESH
	 * @param map
	 * @return
	 */
	
	public static String mapToJson(GMap map) {
		if(map.isEmpty()) map = new GMap();
		ObjectMapper mapperObj = new ObjectMapper();
		String jsonFormat = null;
		try {
			jsonFormat = mapperObj.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonFormat;
	}
	
	public static GMap jsonToGnMap(String s) {
		try {
			return new ObjectMapper().readValue(s == null ? null : s,GMap.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void fileWriter(String s, File file) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(s);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String toJson(Object obj, boolean compress) {
		try {
			ObjectMapper om = new ObjectMapper();
//			om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			return compress ? om.writer().writeValueAsString(obj) : om.writer().withDefaultPrettyPrinter().writeValueAsString(obj);
//			if (compress) {
//				return om.writer().writeValueAsString(obj);
//			} else {
//				return om.writer().withDefaultPrettyPrinter().writeValueAsString(obj);
//			}
		} catch (JsonProcessingException e) {
			//log.error(e);
			return null;
		}
	}

	public static String toJson(Object obj) {
		return toJson(obj, true);
	}
	
	public static boolean isValidPanCard(String panCard) {
		return panCard.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}");
	}
	public static boolean isValidPassport(String passport) {
		return passport.matches("[A-Z]{1}[0-9]{7}");
	}
	public static String bytesToString(byte[] message) {
		String str = "";
		for (byte b : message) {
			str += Byte.toString(b);
		}
		return str;
	}

	public static String removeSpecialChars(String s) {
		return s.replaceAll("[\\s\\-()]", "");
	}
	
	public static String removeSpaces(String data) {
		 return isBlank(data) ? null : data.replaceAll("\\s+", " ").trim();
	}
	
	public static int getStringCount(String s) {
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != ' ')
				count++;
		}
		return count;
	}

	static int[][] d = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
			{ 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 }, { 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 }, { 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
			{ 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 }, { 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 }, { 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
			{ 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 }, { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
	static int[][] p = new int[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
			{ 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 }, { 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 }, { 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 },
			{ 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 }, { 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 }, { 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 } };
	static int[] inv = { 0, 4, 3, 2, 1, 5, 6, 7, 8, 9 };

	public static boolean validateVerhoeff(String num) {
		int c = 0;
		int[] myArray = StringToReversedIntArray(num);
		for (int i = 0; i < myArray.length; i++) {
			c = d[c][p[(i % 8)][myArray[i]]];
		}
		return (c == 0);
	}

	public static String validAadhar(String num) {
		int c = 0;
		int[] myArray = StringToReversedIntArray(num);
		for (int i = 0; i < myArray.length; i++) {
			c = d[c][p[(i % 8)][myArray[i]]];
		}
		return Integer.toString(inv[c]);
	}

	private static int[] StringToReversedIntArray(String num) {
		int[] myArray = new int[num.length()];
		for (int i = 0; i < num.length(); i++) {
			myArray[i] = Integer.parseInt(num.substring(i, i + 1));
		}
		myArray = Reverse(myArray);
		return myArray;
	}

	private static int[] Reverse(int[] myArray) {
		int[] reversed = new int[myArray.length];
		for (int i = 0; i < myArray.length; i++) {
			reversed[i] = myArray[myArray.length - (i + 1)];
		}
		return reversed;
	}

	public static boolean validateAadharNumber(String aadharNumber) {
		Pattern aadharPattern = Pattern.compile("\\d{12}");
		boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
		if (isValidAadhar) {
			isValidAadhar = Utility.validateVerhoeff(aadharNumber);
		}
		return isValidAadhar;
	}

	public static String generateAadhar() {
		StringBuilder randomAadhar = new StringBuilder();
		Random random = new Random();
		int version = random.nextInt(8) + 2;
		randomAadhar.append(Integer.toString(version));
		for (int i = 0; i < 10; i++) {
			random = new Random();
			int value = random.nextInt(10);
			randomAadhar.append(Integer.toString(value));
		}
		String check = validAadhar(randomAadhar.toString());
		return randomAadhar + check;
	}
	
	public static List<String> generateAadhar(int howMany){
		List<String> aadharList = new ArrayList<String>();
		for(int i = 0;i< howMany;i++) {
			aadharList.add(generateAadhar());
		}
		return aadharList;
	}
	
	public static boolean isValidPhone(String phone) {
		return phone.matches(PHONE_REGEX);
	}
	
	public static boolean isValidEmail(String email) {
		return email.matches(EMAIL_REGEX);
	}
	
	public static boolean isValidPassword(String password) {
		////Password contains at least 5-12 characters and contains at least one Cap, one small, special symbol
		return password.matches(PASSWORD_REGEX);
	}
	
	public static void main(String[] args) {
		System.out.println(isValidPhone("5542706937"));
	}
	
	public static String formatDate(String date,String format) {
		try {
			Date d= new SimpleDateFormat("yyyy-MM-dd").parse(date);
			return new SimpleDateFormat(format).format(d);
		}catch (Exception e) {
			return "";
		}
	}
	public static String formatDateTime(String date,String format) {
		try {
			Date d= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
			return new SimpleDateFormat(format).format(d);
		}catch (Exception e) {
			return "";
		}
	}	
	public static String formatTime(String date,String format) {
		try {
			Date d= new SimpleDateFormat("HH:mm:ss").parse(date);
			return new SimpleDateFormat(format).format(d);
		}catch (Exception e) {
			return "";
		}
	}	
	
	private static SimpleDateFormat simpleDateFormat(String format) {
		SimpleDateFormat sf=new SimpleDateFormat(format);
		sf.setLenient(false);
		return sf;
	}
	
	public static Date getDate(String date) {
		if (date == null)	return null;
		try {
			return simpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			return getDateTime(date);
		}
	}
	
	public static Date getDateTime(String date) { 
		return getDate(date,"yyyy-MM-dd HH:mm:ss");
	}
	
	public static Date getDate(String date,String format) {
		if (date == null)	return null;
		try {
			return simpleDateFormat(format).parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String getDateTimeString(java.util.Date date) {
		return getDateTimeString(date,"yyyy-MM-dd HH:mm:ss");
	}
	public static Date getDateTime(String date, String format) { 
		return getDate(date,format);
	}
	
	public static String getDateTimeString(java.util.Date time, String format) {
		if (time == null)	return null;
		SimpleDateFormat converter=new SimpleDateFormat(format);
		//converter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return converter.format(time); 
	}
	
	public static String toTitleCase(String input) {
		if (input == null)
			return null;
		if (input.trim().equals(""))
			return "";
		input = input.replace("_", " ").toLowerCase();
		String result = "";
		char firstChar = input.charAt(0);
		result = result + Character.toUpperCase(firstChar);
		for (int i = 1; i < input.length(); i++) {
			char currentChar = input.charAt(i);
			char previousChar = input.charAt(i - 1);
			if (previousChar == ' ') {
				result = result + Character.toUpperCase(currentChar);
			} else {
				result = result + currentChar;
			}
		}
		return result;
	}
	
	public static String toSentenceCase(String input) {
		if (input == null)
			return "";
		input = input.trim();
		StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;
		boolean isDot = false;
		for (char c : input.toCharArray()) {
			if (Character.isSpaceChar(c)) {
				nextTitleCase = isDot;
			} else if (c == '.') {
				nextTitleCase = isDot = true;
			} else if (nextTitleCase) {
				c = Character.toUpperCase(c);
				nextTitleCase = isDot = false;
			} else {
				c = Character.toLowerCase(c);
				isDot = false;
			}
			titleCase.append(c);
		}
		return titleCase.toString();
	}
	
	public static Date getTime(String date) {
		return getTime(date,"HH:mm:ss");
	}
	
	public static Date getTime(String date, String format) {
		if (date == null)	return null;
		try {
			return simpleDateFormat(format).parse(date);
		} catch (ParseException e) {
			return null;
		}
	}	
	
	public static Byte parseByte(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0;
		} else {
			try {
				return Byte.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	}

	public static Short parseShort(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0;
		} else {
			try {
				return Short.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0;
			}
		}
	}

	public static Integer parseInt(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0;
		} else {
			try {
				return (int)Double.parseDouble(o.toString().trim());
			} catch (NumberFormatException e) {
				//log.info(ZcUtil.getStackTrace(e));
				return 0;
			}
		}
	}

	public static Long parseLong(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0l;
		} else {
			try {
				return Long.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0l;
			}
		}
	}

	public static Float parseFloat(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0f;
		} else {
			try {
				return Float.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0f;
			}
		}
	}

	public static Double parseDouble(Object o) {
		if (o == null)
			return null;
		if (isBlank(o)) {
			return 0d;
		} else {
			try {
				return Double.valueOf(o.toString().trim());
			} catch (NumberFormatException e) {
				return 0d;
			}
		}
	}

	public static Boolean parseBoolean(Object o) {
		if (o == null)
			return false;
		if (isBlank(o)) {
			return false;
		} else {
			try {
				return Boolean.valueOf(o.toString().trim()) || "1".equals(o.toString().trim());
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}
	
	public static String toYml(Object obj) {
		if (obj == null || isBlank(obj))
			return "";
		if (obj instanceof String) {
			return (new Yaml()).dumpAsMap(jsonToGnMap((String) obj));
		} else
			return (new Yaml()).dumpAsMap(jsonToGnMap(toJson(obj)));
	}

	public static String truncate(String s, int len) {
		return s == null ? null : (s.length() <= len ? s : s.substring(0, len));
	}
	
	public static String getStackTrace(Exception e) {
		if(e==null) return "";
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
	
	public static List<String> readFileIntoList(String file) { 
		List<String> lines = Collections.emptyList(); 
		try { 
			lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8); 
		} catch (IOException e) {
			log.info(e.toString());
		}
		return lines;
	}
	
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
}
