package com.beiran.common.utils;

import com.beiran.common.exception.FileDownloadException;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 文件相关操作，若出现异常，会抛出 FileDownloadException
 */
public class FileUtils {

	/**
	 * 文件下载工具
	 * @param response
	 * @param file
	 * @param newFileName
	 * @throws Exception
	 */
	public static void downloadFile(HttpServletResponse response, File file, String newFileName) throws Exception {
		try {
			response.setHeader("Content-Disposition", "attachment; filename=" + new String(newFileName.getBytes("ISO-8859-1"), "UTF-8"));
			response.setHeader("FileName", new String(newFileName.getBytes("ISO-8859-1"), "UTF-8"));
			response.setHeader("Access-Control-Expose-Headers", "FileName");
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
			InputStream inputStream = new FileInputStream(file.getAbsolutePath());
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			int length = 0;
			byte[] temp = new byte[1 * 1024 * 10];
			while ((length = bufferedInputStream.read(temp)) != -1) {
				bufferedOutputStream.write(temp, 0, length);
			}
			bufferedOutputStream.flush();
			bufferedInputStream.close();
			bufferedOutputStream.close();
			inputStream.close();
		} catch (Exception e) {
			throw new FileDownloadException("文件下载异常");
		}
	}

	/**
	 * 生成 Excel 文件
	 * @param workbook
	 * @param fileName
	 * @return
	 */
	public static File createExcelFile(Workbook workbook, String fileName) {
		OutputStream stream = null;
		File file = null;
		try {
			file = File.createTempFile(fileName, ".xlsx");
			stream = new FileOutputStream(file.getAbsoluteFile());
			workbook.write(stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
