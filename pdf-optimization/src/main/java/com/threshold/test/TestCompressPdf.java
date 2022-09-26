package com.threshold.test;

import com.threshold.util.Utility;

public class TestCompressPdf {
	public static void main(String[] args) throws Exception {
		String ipFile = "C:\\Users\\test.LAPTOP-PM0NVFLK\\Desktop\\ThresholdRel\\pdfopt\\AP3fB.pdf";
		String opFile = "C:\\Users\\test.LAPTOP-PM0NVFLK\\Desktop\\ThresholdRel\\pdfops\\new.pdf";
		System.out.println(Utility.compressPdfDoc(ipFile, opFile,20));
		 //Utility.pdfToJpg(ipFile, opFile);
		System.out.println("success");
	}
}
