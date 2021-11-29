package com.main.Billing.utility;

public class PreAuthenticator {
	private static boolean isValid = false;
	private static FileLockMechanism lock = null;
	public static void getLock() {
		lock = new FileLockMechanism("Lock");
	}

	public static void unLock() {
		lock.releaseLock();
	}

	public static boolean checkApplicationRunning() {
		try {
			getLock();
			if (lock.isAppActive())
				return true;
		} catch (Exception e) {
			
		}
		return false;
	}

	public static boolean validateAuthorizedSystem() throws Exception {
		if(isValid)
			return true;
		
		String valueRetrieved = getRegistryValue();
		if (valueRetrieved == null || (valueRetrieved != null && !valueRetrieved.equals(PropertyUtil.DECRYPTED_KEY))) {
			return false;
		}
		isValid = true;
		return true;
	}

	public static String getRegistryValue() throws Exception {
		String value = null;
		int[] key = { WinRegistry.KEY_WOW64_32KEY, WinRegistry.KEY_WOW64_64KEY };
		int i = 0;
		while (i < key.length) {
			value = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, PropertyUtil.VALUE_KEY,
					PropertyUtil.VALUE_NAME, key[i]);
			if (value == null) {
				value = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, PropertyUtil.VALUE_KEY,
						PropertyUtil.VALUE_NAME, key[i]);
			}
			if (value != null)
				break;
			i++;
		}
		return value;
	}
}
