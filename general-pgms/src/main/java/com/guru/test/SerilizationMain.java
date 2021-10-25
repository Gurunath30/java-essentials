package com.guru.test;

import java.io.File;

import com.guru.pojo.Student;
import com.guru.util.Utility;

public class SerilizationMain {
	public static void main(String[] args) {
		System.out.println(Utility.toSerialize(
				new Student(150, "Gurunath", "gurunathreddy39@gmail.com", "23-12-1997", "8106772995", 56),
				new File("F:/Threshold/Serilization/Student.txt")));
	}
}
