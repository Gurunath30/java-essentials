package com.guru.test;

import com.guru.util.Utility;

public class ZcLicenses {
	public static void main(String[] args) throws Exception {
		String template = Utility.jsonString(".\\files\\lisence.json");
		Utility.generateLicenses(template, "F:\\Javaworkspace\\zerocode-v3.1\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\ZC-V3.1-LICENSE\\clients", 0, 0, "morefresh");
	}
}
