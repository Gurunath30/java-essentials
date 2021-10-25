package com.guru.test;

import com.guru.util.Email;

public class TestEmail {
	public static void main(String[] args) {
		Email.sendEmail("gurunathreddy30@gmail.com", "gurunathreddy30@gmail.com","pwd", "smtp.gmail.com","465", "From Java Code", "Hello Gurunath");
		System.out.println("Email sent successfully");
	}
}
