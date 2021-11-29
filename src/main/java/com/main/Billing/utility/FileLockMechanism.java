package com.main.Billing.utility;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLockMechanism {
	private String appName;

	private FileLock lock;
	private FileChannel channel;
	private File f;

	public FileLockMechanism(String appName) {
		this.appName = appName;
	}

	@SuppressWarnings("resource")
	public boolean isAppActive() throws Exception {
		f = new File(System.getProperty("user.home"), appName + ".tmp");
		channel = new RandomAccessFile(f, "rw").getChannel();

		lock = channel.tryLock();
		if (lock == null) {
			channel.close();
			return true;
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					lock.release();
					channel.close();
					f.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return false;
	}

	public void releaseLock() {
		try {
			if (lock != null) {
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
