package com.threshold.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.exporting.PdfImageInfo;
import com.spire.pdf.graphics.PdfBitmap;
import com.spire.pdf.graphics.PdfImageType;

public class Utility {
	/*
	 * Pdf compression using spire library
	 */
	public static String compressPdfDoc(String ipFile, String opFile, int imgComRate) throws Exception {
		if (isBlank(ipFile) || isBlank(opFile))
			throw new Exception("Input or output filepath is blank");
		if (imgComRate < 1 || imgComRate > 100)
			throw new Exception("Enter compression rate between 0 to 100");
		PdfDocument doc = new PdfDocument();
		doc.loadFromFile(ipFile);
		doc.getFileInfo().setIncrementalUpdate(false);
		IntStream.rangeClosed(0, doc.getPages().getCount() - 1).forEach(i -> {
			PdfPageBase page = doc.getPages().get(i);
			PdfImageInfo[] images = page.getImagesInfo();
			if (images != null && images.length > 0)
				IntStream.rangeClosed(0, images.length - 1).forEach(index -> {
					PdfImageInfo image = images[index];
					PdfBitmap bp = new PdfBitmap(image.getImage());
					bp.setQuality(imgComRate);
					page.replaceImage(index, bp);
				});
		});
		doc.saveToFile(opFile, FileFormat.PDF);
		doc.close();
		return opFile;
	}

	/*
	 * Pdf to Jpg using spire library
	 */
	public static void pdfToJpg(String ipFile, String opFile) throws Exception {
		PdfDocument doc = new PdfDocument();
		doc.loadFromFile(ipFile);
		BufferedImage image;
		for (int i = 0; i < doc.getPages().getCount(); i++) {
			image = doc.saveAsImage(i);
			doc.saveAsImage(i, PdfImageType.Bitmap, 300, 300);
			File file = new File(opFile + "/" + String.format(("image-%d.jpeg"), i));
			ImageIO.write(image, "JPEG", file);
		}
//	        for (int i = 0; i < doc.getPages().getCount(); i++) {
//	            image = doc.saveAsImage(i);
//	            BufferedImage outImage=new BufferedImage(image.getWidth(),
//	                    image.getHeight(),BufferedImage.TYPE_INT_RGB);
//
//	            Graphics2D g=outImage.createGraphics();
//	            g.setBackground(new Color(255,255,255,255));
//	            g.clearRect(0,0,image.getWidth(),image.getHeight());
//	            g.drawImage(image,0,0,new Color(0,0,0,0),null);
//	            File file = new File(String.format(("image-%d.JPEG"), i));
//	            ImageIO.write(outImage, "JPEG", file);
		doc.close();
	}

	/*
	 * Using itext library
	 */
	public static String optimizePdf(String ipFile, String opFile) throws Exception {
		PdfReader reader = new PdfReader(new FileInputStream(ipFile));
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(opFile));
		int total = reader.getNumberOfPages() + 1;
		for (int i = 1; i < total; i++) {
			reader.setPageContent(i + 1, reader.getPageContent(i + 1));
		}
		stamper.setFullCompression();
		stamper.close();
		return opFile;
	}

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
}
