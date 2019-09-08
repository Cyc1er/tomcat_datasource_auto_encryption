package com.tomcat.datasource.encryption;

import java.io.File;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @category 用于Tomcat数据源自动加密
 * 
 * @author cyc1er
 *
 * @date 2019年5月6日
 */
public class DataSourceAutoEncrypt {
	static String USERNAME;
	static String PASSWORD;
	static String FACTORY;
	static List<Attribute> ATTRIBUTES;
	static List<Attribute> NEWATTRIBUTES;

	public static void main(String[] args) throws Exception {
		if (args.equals(null)) {
			System.out.println("未找到数据源配置文件，退出程序");
			System.exit(0);
		}
		DoDataSourceAutoEncrypt(args[0]);
	}

	public static void DoDataSourceAutoEncrypt(String datasourceXMLs) throws Exception {
		final String javaVersion = System.getProperty("java.version");
		if (Integer.parseInt(javaVersion.split("\\.")[1]) <= 6) {
			throw new Exception("JDK版本不适用！需要jdk1.7及以上");
		}
		File inputXml = new File("../conf/" + datasourceXMLs);
		SAXReader saxReader = new SAXReader();
		Document readDocument = saxReader.read(inputXml);
		Element root = readDocument.getRootElement();
		ATTRIBUTES = root.attributes();
		for (Attribute attr : ATTRIBUTES) {
			if (attr.getName().equals("factory")) {
				FACTORY = attr.getValue();
			}
			if (attr.getName().equals("username")) {
				USERNAME = attr.getValue();
			}
			if (attr.getName().equals("password")) {
				PASSWORD = attr.getValue();
			}
		}
		System.out.println(FACTORY);
		String[] factorySplit = FACTORY.split("\\.");
		if (factorySplit.equals(null)) {
			throw new Exception("未监测到的所需的数据源工厂类");
		}
		switch (factorySplit[factorySplit.length - 1]) {
		case "EncryptedDBCPDataSourceFactory":
		case "EncryptedTomcatJdbcDataSourceFactory":
			EncodeProperties.validateProperties(USERNAME, PASSWORD);
			String enUsername = EncodeProperties.encrypt(USERNAME, javaVersion);
			String enPassword = EncodeProperties.encrypt(PASSWORD, javaVersion);
			RewriteToXML.reWriteToXML(enUsername, enPassword, ATTRIBUTES, datasourceXMLs);
			break;
		default:
			System.out.println(factorySplit[factorySplit.length - 1]);
			throw new Exception("检测到配置文件中Factory不适用于加密");
		}
	}

}
