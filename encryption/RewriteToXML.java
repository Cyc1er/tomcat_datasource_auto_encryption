package com.tomcat.datasource.encryption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class RewriteToXML {
	/**
	 * @param enUsername
	 * @param enPassword
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void reWriteToXML(String enUsername, String enPassword, List<Attribute> attributes, String fileName)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		Document document = DocumentHelper.createDocument();
		Element resource = document.addElement("Resource");
		for (Attribute attr : attributes) {
			if (attr.getName().equals("username")) {
				attr.setValue(enUsername);
			}
			if (attr.getName().equals("password")) {
				attr.setValue(enPassword);
			}
		}
		resource.setAttributes(attributes);
//		设置生成xml的格式
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
//		File file = new File(fileName);
		File file = new File("../conf/" + fileName);
		XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
//		设置是否转义，默认使用转义字符
		writer.setEscapeText(false);
		writer.write(document);
		writer.close();
	}
}
