package com.main.Billing.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtil {

	public static Logger log = LoggerFactory.getLogger(PropertyUtil.class);

	public static Properties userProperties = null;

	public static File CONFIG_FILE = new File("config/to/config.properties");
	//public static File CONFIG_FILE = new File("C:\\Users\\Shubham Mittal\\Documents\\Software\\config\\to\\config.properties");

	public static String VALUE_KEY = "Software\\Classes\\Applications\\BillingPortalApplication";
	public static String VALUE_NAME = "ValidateKey";
	public static String SECRET_KEY = "billingPortal";
	public static String DOWNLOAD_PDF_PATH = System.getProperty("user.home").concat("/Downloads/BillingPortal");
	public static String IMAGE_PATH = "assets/HP_Logo.jpeg";
	public static String BACKUP_PATH = "backup/backup.sql";
	public static String CIPHER = "AES";
	public static String CIPHER_PWD = "SA";
	public static String ENCRPYTED_KEY = "CM/nR3lbDI10gukjuggxHw==";
	public static String DECRYPTED_KEY = "HKEY-HPA-2896";
	public static String EMAIL = null;
	public static String PASSWORD = null;

	static {
		if (CONFIG_FILE.exists()) {
			try (InputStream input = new FileInputStream(CONFIG_FILE)) {
				Properties prop = new Properties();
				prop.load(input);
				populateProperties(prop);
			} catch (Exception ex) {
				log.error("Error caught while creating Properties....", ex);
			}
		}
	}

	private static void populateProperties(Properties prop) {
		if (getStringValue(prop.get("downloadPdfPath")) != null) {
			DOWNLOAD_PDF_PATH = getStringValue(prop.get("downloadPdfPath"));
		}
		if (getStringValue(prop.get("imagePath")) != null) {
			IMAGE_PATH = getStringValue(prop.get("imagePath"));
		}
		if (getStringValue(prop.get("backupPath")) != null) {
			BACKUP_PATH = getStringValue(prop.get("backupPath"));
		}
		if (getStringValue(prop.get("email")) != null) {
			EMAIL = getStringValue(prop.get("email"));
		}
		if (getStringValue(prop.get("password")) != null) {
			PASSWORD = getStringValue(prop.get("password"));
		}
		if (getStringValue(prop.get("key")) != null) {
			ENCRPYTED_KEY = getStringValue(prop.get("key"));
		}
		if (ENCRPYTED_KEY != null) {
			try {
				DECRYPTED_KEY = KeyProtector.decrypt(ENCRPYTED_KEY, SECRET_KEY);
			} catch (Exception e) {
				DECRYPTED_KEY = "";
			}
		}
	}

	public static String getStringValue(Object value) {
		if (value == null)
			return null;

		String result = value.toString().trim();
		if (result.length() <= 0)
			result = null;
		return result;
	}
}
