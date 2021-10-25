package com.guru.test;

import com.guru.util.BulkSmsGateway;

public class TestSms {
	public static void main(String[] args) throws Exception {
		String result =BulkSmsGateway.sendSms1();
		System.out.println("result :" + result);
		}
}
