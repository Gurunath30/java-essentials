package com.guru.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class BulkSmsGateway {
	@SuppressWarnings("deprecation")
	public static String sendSms1() {
		@SuppressWarnings("unused")
		String sResult = null;
		try {
			// Construct data
			String phonenumbers = "8106771995";
			String data = "user=" + URLEncoder.encode("zerocode", "UTF-8");
			data += "&password=" + URLEncoder.encode("Zer0c@de", "UTF-8");
			data += "&message=" + URLEncoder.encode("OTP is {#var#} for {#var#} from ZEROCODE. Do not share OTP for security reasons.");
			data += "&sender=" + URLEncoder.encode("ZEROTP", "UTF-8");
			data += "&mobile=" + URLEncoder.encode(phonenumbers, "UTF-8");
			data += "&type=" + URLEncoder.encode("3", "UTF-8");
			data += "&template_id=" + URLEncoder.encode("1507161847886466270", "UTF-8");
			// Send data
			URL url = new URL("http://api.bulksmsgateway.in/sendmessage.php?" + data);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			String sResult1 = "";
			while ((line = rd.readLine()) != null) {
				// Process line...
				sResult1 = sResult1 + line + " ";
			}
			wr.close();
			rd.close();
			return sResult1;
		} catch (Exception e) {
			System.out.println("Error SMS " + e);
			return "Error " + e;
		}
	}
}
