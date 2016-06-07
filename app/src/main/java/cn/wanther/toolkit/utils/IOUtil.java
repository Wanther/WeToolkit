package cn.wanther.toolkit.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import android.content.res.AssetManager;

public class IOUtil {
	public static void close(Closeable c) {
		if (c == null) {
			return;
		}

		try {
			c.close();
		} catch (Exception e) {
		}
	}

	public static void disconnect(HttpURLConnection con) {
		if (con == null) {
			return;
		}

		try {
			con.disconnect();
		} catch (Exception e) {
		}
	}

	public static void createDirIfNotExsists(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static String toString(InputStream in) throws IOException {
		if (in == null) {
			return null;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new BufferedInputStream(in, 8 * 1024)));
		StringBuilder result = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	public static boolean copyAssetFolder2(AssetManager assetManager,
			String fromAssetPath, String toPath) {
		try {
			String[] files = assetManager.list(fromAssetPath);
			new File(toPath).mkdirs();
			boolean res = true;
			for (String file : files)
				if (file.contains("."))
					res &= copyAsset(assetManager, fromAssetPath + File.separator + file,
							toPath + File.separator + file);
				else
					res &= copyAssetFolder2(assetManager, fromAssetPath + File.separator
							+ file, toPath + File.separator + file);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean copyAsset(AssetManager assetManager,
			String fromAssetPath, String toPath) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(fromAssetPath);
			new File(toPath).createNewFile();
			out = new FileOutputStream(toPath);
			copyFile2(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void copyFile2(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static void copyDir2(String src, String dst) {
		File target = new File(dst);
		if (!target.exists()) {
			target.mkdirs();
		}
		File[] files = (new File(src)).listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				File s = files[i];
				File t = new File(target.getAbsolutePath() + File.separator
						+ files[i].getName());
				copyFile2(s, t);
			} else if (files[i].isDirectory()) {
				String d = src + File.separator + files[i].getName();
				String t = dst + File.separator + files[i].getName();
				copyDir2(d, t);
			}
		}
	}

	public static boolean copyFile2(File src, File dst) {
		FileInputStream i;
		try {
			i = new FileInputStream(src);
			BufferedInputStream in = new BufferedInputStream(i);
			FileOutputStream o = new FileOutputStream(dst);
			BufferedOutputStream out = new BufferedOutputStream(o);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.flush();
			in.close();
			out.close();
			o.close();
			i.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static long getFileSize(File dirOrFile) {

		if (dirOrFile == null || !dirOrFile.exists()) {
			return 0;
		}

		if (dirOrFile.isFile()) {
			return dirOrFile.length();
		}

		if (dirOrFile.isDirectory()) {
			File[] subDirOrFiles = dirOrFile.listFiles();
			if (subDirOrFiles == null || subDirOrFiles.length <= 0) {
				return 0;
			}
			long size = 0;
			for (File subDirOrFile : subDirOrFiles) {
				size += getFileSize(subDirOrFile);
			}
			return size;
		}

		return 0;
	}
}
