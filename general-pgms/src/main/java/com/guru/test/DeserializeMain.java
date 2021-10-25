package com.guru.test;

import java.io.File;

import com.guru.pojo.Student;
import com.guru.util.Utility;

public class DeserializeMain {
	public static void main(String[] args)  {
		Student student = (Student) (Utility.toDeserialize(new File("F:/Threshold/Serilization/Student.txt")));
		System.out.println(student);
	}
}
