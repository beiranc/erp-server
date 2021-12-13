package com.beiran.common.utils;

import com.beiran.common.exception.FileDownloadException;
import com.beiran.core.quotation.entity.BasicInfo;
import com.beiran.core.quotation.entity.Quotation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.streaming.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
			response.setHeader("Content-Disposition", "attachment; filename=" + new String(newFileName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
			response.setHeader("FileName", new String(newFileName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
			response.setHeader("Access-Control-Expose-Headers", "FileName");
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
			InputStream inputStream = new FileInputStream(file.getAbsolutePath());
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			int length = 0;
			byte[] temp = new byte[1024 * 10];
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
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
				assert stream != null;
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 生成报价单 Excel
	 * @param quotation 报价单数据
	 * @return Excel File
	 */
	public static File createQuotationFile(Quotation quotation) {
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		SXSSFSheet sheet = workbook.createSheet("quotationSheet");
		// 设置高度为 40
		sheet.setColumnWidth(0, 12 * 256);
		sheet.setColumnWidth(1, 40 * 256);
		for (int i = 2; i < 22; i++) {
			sheet.setColumnWidth(i, 20 * 256);
		}
		// 一个 Sheet 只能获取一个 SXSSFDrawing
		SXSSFDrawing patriarch = sheet.createDrawingPatriarch();

		// 需要先合并再生成行
		// firstRow: 开始行, lastRow: 结束行
		// firstCol: 开始列, lastCol: 结束列
		// 均从 0 开始
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 21));
		SXSSFRow headFistRow = sheet.createRow(0);
		createFirstHead(workbook, headFistRow, quotation);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 16));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 17, 18));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 19, 21));
		SXSSFRow headSecondRow = sheet.createRow(1);
		// 设置第二级标题高度
		headSecondRow.setHeight((short) 800);
		createSecondHead(workbook, headSecondRow);

		// 创建第三级标题
		int columnIndex = createThirdHead(workbook, sheet);

		// 数据
		Set<BasicInfo> basicInfoSet = quotation.getBasicInfos();
		List<BasicInfo> basicInfos = new ArrayList<>(basicInfoSet);

		for (int i = 0; i < basicInfos.size(); i++) {
			BasicInfo basicInfo = basicInfos.get(i);
			SXSSFRow row = sheet.createRow(i + 3);
			for (int j = 0; j < columnIndex + 1; j++) {
				row.createCell(j);
			}
			columnIndex = 0;
			// 序号
			SXSSFCell aCell = row.getCell(columnIndex);
			aCell.setCellValue(i + 1);
			aCell.setCellStyle(getContentStyle(workbook));
			int col1 = 1;
			int row1 = i + 3;
			int col2 = 1;
			int row2 = i + 3;
			addImg(basicInfo.getMainPicture(), workbook, row, patriarch, col1, row1, col2, row2);
			// 跳过主图这一列
			++columnIndex;
			// 型号
			SXSSFCell cCell = row.getCell(++columnIndex);
			cCell.setCellValue(basicInfo.getModel());
			cCell.setCellStyle(getContentStyle(workbook));
			// 前叉
			SXSSFCell dCell = row.getCell(++columnIndex);
			dCell.setCellValue(basicInfo.getFrontFork());
			dCell.setCellStyle(getContentStyle(workbook));
			// 显示器
			SXSSFCell eCell = row.getCell(++columnIndex);
			eCell.setCellValue(basicInfo.getDisplay().getValue());
			eCell.setCellStyle(getContentStyle(workbook));
			// 油门转把
			SXSSFCell fCell = row.getCell(++columnIndex);
			fCell.setCellValue(basicInfo.getThrottle().getValue());
			fCell.setCellStyle(getContentStyle(workbook));
			// 变速器
			SXSSFCell gCell = row.getCell(++columnIndex);
			gCell.setCellValue(basicInfo.getDerailleur());
			gCell.setCellStyle(getContentStyle(workbook));
			// 功率
			SXSSFCell hCell = row.getCell(++columnIndex);
			hCell.setCellValue(basicInfo.getPower());
			hCell.setCellStyle(getContentStyle(workbook));
			// 电压
			SXSSFCell iCell = row.getCell(++columnIndex);
			iCell.setCellValue(basicInfo.getVoltage());
			iCell.setCellStyle(getContentStyle(workbook));
			// 轮胎尺寸
			SXSSFCell jCell = row.getCell(++columnIndex);
			jCell.setCellValue(basicInfo.getWheelSize());
			jCell.setCellStyle(getContentStyle(workbook));
			// 车架材质
			SXSSFCell kCell = row.getCell(++columnIndex);
			kCell.setCellValue(basicInfo.getFrame());
			kCell.setCellStyle(getContentStyle(workbook));
			// 最大速度
			SXSSFCell lCell = row.getCell(++columnIndex);
			lCell.setCellValue(basicInfo.getMaxSpeed() + "KM/h");
			lCell.setCellStyle(getContentStyle(workbook));
			// 单次充电里程
			SXSSFCell mCell = row.getCell(++columnIndex);
			mCell.setCellValue(basicInfo.getMileagePerCharge() + "KM");
			mCell.setCellStyle(getContentStyle(workbook));
			// 电池容量
			SXSSFCell nCell = row.getCell(++columnIndex);
			nCell.setCellValue(basicInfo.getBatteryCapacity() + "AH");
			nCell.setCellStyle(getContentStyle(workbook));
			// 制动系统/刹车
			SXSSFCell oCell = row.getCell(++columnIndex);
			oCell.setCellValue(basicInfo.getBrakeSystem().getValue());
			oCell.setCellStyle(getContentStyle(workbook));
			// 起订量
			SXSSFCell pCell = row.getCell(++columnIndex);
			pCell.setCellValue(basicInfo.getMoq());
			pCell.setCellStyle(getContentStyle(workbook));
			// 单价
			SXSSFCell qCell = row.getCell(++columnIndex);
			qCell.setCellValue(basicInfo.getMoq());
			qCell.setCellStyle(getContentStyle(workbook));
			// 纸箱尺寸
			SXSSFCell rCell = row.getCell(++columnIndex);
			rCell.setCellValue(basicInfo.getCartonSize() + "CM");
			rCell.setCellStyle(getContentStyle(workbook));
			// 海关编码
			SXSSFCell sCell = row.getCell(++columnIndex);
			sCell.setCellValue(basicInfo.getHsCode());
			sCell.setCellStyle(getContentStyle(workbook));
			// LOGO 定制
			SXSSFCell tCell = row.getCell(++columnIndex);
			tCell.setCellValue(basicInfo.getLogo() ? "ACCEPT" : "NOT ACCEPT");
			tCell.setCellStyle(getContentStyle(workbook));
			// 外包装定制
			SXSSFCell uCell = row.getCell(++columnIndex);
			uCell.setCellValue(basicInfo.getOuterPacking() ? "ACCEPT" : "NOT ACCEPT");
			uCell.setCellStyle(getContentStyle(workbook));
			// 图案定制
			SXSSFCell vCell = row.getCell(++columnIndex);
			vCell.setCellValue(basicInfo.getDesign() ? "ACCEPT" : "NOT ACCEPT");
			vCell.setCellStyle(getContentStyle(workbook));
		}
		String format = DateTimeFormatter.ofPattern("MMdd").format(LocalDateTime.now());
		return FileUtils.createExcelFile(workbook, "PHOENIX BIKE QUOTATION " + format + "_");
	}

	// 添加主图
	private static void addImg(String path, SXSSFWorkbook workbook, SXSSFRow row, SXSSFDrawing patriarch, int col1, int row1, int col2, int row2) {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			BufferedImage bufferedImage = ImageIO.read(new File(path));
			ImageIO.write(bufferedImage, "jpg", outputStream);
			// 获取图片原始宽度
			int width = bufferedImage.getWidth();
			// 获取图片原始高度
			int height = bufferedImage.getHeight();
			// 这里是因为一个 12 号字体的宽度为 13, 前面也设置过列宽为 40
			height = (int) Math.round((height * (40 * 13) * 1.0 / width));
			row.setHeight((short) (height / 2 * 30));
			XSSFClientAnchor anchor = new XSSFClientAnchor(5 * Units.EMU_PER_PIXEL, 5 * Units.EMU_PER_PIXEL, 850 * Units.EMU_PER_PIXEL, 300 * Units.EMU_PER_PIXEL, col1, row1, col2, row2);
			anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
			patriarch.createPicture(anchor, workbook.addPicture(outputStream.toByteArray(), Workbook.PICTURE_TYPE_JPEG));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 创建第一级标题
	private static void createFirstHead(SXSSFWorkbook workbook, SXSSFRow row, Quotation quotation) {
		// 第一列
		SXSSFCell aCell = row.createCell(0);
		aCell.setCellStyle(getFirstHeadStyleA(workbook));
		String now = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now());
		Month month = LocalDateTime.ofInstant(quotation.getValidPeriod().toInstant(), ZoneId.systemDefault()).getMonth();
		int dayOfMonth = LocalDateTime.ofInstant(quotation.getValidPeriod().toInstant(), ZoneId.systemDefault()).getDayOfMonth();
		aCell.setCellValue(
						"DATE: \n" +
						now + "\n" +
						"RATE: " + quotation.getRealTimeRate() + "\n" +
						"\n" +
						"This quotation will remain valid until " + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + dayOfMonth);

		// 第二列
		SXSSFCell bCell = row.createCell(1);
		bCell.setCellStyle(getFirstHeadStyleB(workbook));
		bCell.setCellValue(
				quotation.getChineseName() + "\n" +
						quotation.getEnglishName() + "\n" +
						"Add: " + quotation.getAddress() + "\n" +
						"Tel: " + quotation.getSalesman().getTel() + " | Fax: " + quotation.getSalesman().getFax() + "\n" +
						quotation.getSalesman().getEmail() + "\n" +
						quotation.getOfficialWebsite());
	}

	// 创建第二级标题
	private static void createSecondHead(SXSSFWorkbook workbook, SXSSFRow row) {
		// 第一列
		SXSSFCell aCell = row.createCell(0);
		aCell.setCellStyle(getSecondHeadStyle(workbook));
		aCell.setCellValue("基本信息 Basic Parameter");

		// 第二列
		SXSSFCell bCell = row.createCell(17);
		bCell.setCellStyle(getSecondHeadStyle(workbook));
		bCell.setCellValue("物流信息 Logistics");

		// 第三列
		SXSSFCell cCell = row.createCell(19);
		cCell.setCellStyle(getSecondHeadStyle(workbook));
		cCell.setCellValue("定制服务 Custom Service");
	}

	// 创建第三级标题
	private static int createThirdHead(SXSSFWorkbook workbook, SXSSFSheet sheet) {
		SXSSFRow row = sheet.createRow(2);
		int index = 0;
		row.createCell(index++).setCellValue("NO. 序号");
		row.createCell(index++).setCellValue("PICTURE 图片");
		row.createCell(index++).setCellValue("MODEL 型号");
		row.createCell(index++).setCellValue("FRONT FORK 前叉");
		row.createCell(index++).setCellValue("DISPLAY 显示器");
		row.createCell(index++).setCellValue("THROTTLE 油门转把");
		row.createCell(index++).setCellValue("DERAILLEUR 变速器");
		row.createCell(index++).setCellValue("POWER 功率");
		row.createCell(index++).setCellValue("VOLTAGE 电压");
		row.createCell(index++).setCellValue("WHEEL SIZE 轮胎尺寸");
		row.createCell(index++).setCellValue("FRAME 车架材质");
		row.createCell(index++).setCellValue("MAX SPEED 最大速度");
		row.createCell(index++).setCellValue("MILEAGE PER CHARGE 单次充电里程");
		row.createCell(index++).setCellValue("BATTERY CAPACITY 电池容量");
		row.createCell(index++).setCellValue("BRAKE SYSTEM 制动系统/刹车");
		row.createCell(index++).setCellValue("MOQ 起订量");
		row.createCell(index++).setCellValue("FOB PRICE 单价");
		row.createCell(index++).setCellValue("CARTON SIZE 纸箱尺寸（长*宽*高CM）");
		row.createCell(index++).setCellValue("HS CODE 海关编码");
		row.createCell(index++).setCellValue("LOGO CUSTOM LOGO 定制");
		row.createCell(index++).setCellValue("CUSTOMIZATION OF OUTER PACKING 外包装定制");
		row.createCell(index++).setCellValue("DESIGN CUSTOM 图案定制");
		// 最后统一设置样式
		for (int i = 0; i < index; i++) {
			row.getCell(i).setCellStyle(getThirdHeadStyle(workbook));
		}
		return index;
	}

	// 返回第一级标题的第一个单元格样式
	private static CellStyle getFirstHeadStyleA(SXSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("等线");
		font.setFontHeightInPoints((short) 11);
		style.setFont(font);
		return style;
	}

	// 返回第一级标题的第二个单元格样式
	private static CellStyle getFirstHeadStyleB(SXSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setAlignment(HorizontalAlignment.CENTER);
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("等线");
		font.setFontHeightInPoints((short) 16);
		style.setFont(font);
		return style;
	}

	// 返回第二级标题的单元格样式
	private static CellStyle getSecondHeadStyle(SXSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(HorizontalAlignment.CENTER);
		// 背景色
//        style.setFillForegroundColor(IndexedColors.ROSE.getIndex());
		// SOLID 填充前景色
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("等线");
		font.setFontHeightInPoints((short) 14);
		style.setFont(font);
		return style;
	}

	// 返回第三级标题的单元格样式
	private static CellStyle getThirdHeadStyle(SXSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(true);
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontName("等线");
		font.setFontHeightInPoints((short) 12);
		style.setFont(font);
		return style;
	}

	// 返回内容单元格的样式
	private static CellStyle getContentStyle(SXSSFWorkbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(true);
		Font font = workbook.createFont();
		font.setFontName("等线");
		font.setFontHeightInPoints((short) 11);
		style.setFont(font);
		return style;
	}
}
