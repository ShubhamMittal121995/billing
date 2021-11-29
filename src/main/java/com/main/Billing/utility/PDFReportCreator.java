package com.main.Billing.utility;

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.main.Billing.HibernateUtil;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.HorizontalBandAlignment;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.ImageBanner;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.ImageScaleMode;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.constants.Stretching;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroupVariable;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;

@SuppressWarnings("deprecation")
public class PDFReportCreator {
	
	public JasperPrint getReport(Map<String, String> inputParam)
			throws ColumnBuilderException, JRException, ClassNotFoundException {
		Map<String, Object> outputParam = new HashMap<String, Object>();
		DynamicReport dynaReport = getReport(inputParam, outputParam);
		JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dynaReport, new ClassicLayoutManager(), outputParam);
		return jp;
	}
	
	private Style createHeaderStyle() {
		StyleBuilder sb = new StyleBuilder(true);
		sb.setFont(new Font(8, "DejaVu Sans", true));
		sb.setBorder(Border.THIN());
		sb.setBorderColor(Color.BLACK);
		sb.setBackgroundColor(Color.lightGray);
		sb.setTextColor(Color.BLACK);
		sb.setHorizontalAlign(HorizontalAlign.CENTER);
		sb.setVerticalAlign(VerticalAlign.MIDDLE);
		sb.setTransparency(Transparency.OPAQUE);
		return sb.build();
	}

	private Style createDetailTextStyle() {
		StyleBuilder sb = new StyleBuilder(true);
		sb.setFont(new Font(8, "DejaVu Sans", false));
		sb.setBorderColor(Color.BLACK);
		sb.setTextColor(Color.BLACK);
		sb.setBorder(Border.THIN());
		sb.setHorizontalAlign(HorizontalAlign.CENTER);
		sb.setVerticalAlign(VerticalAlign.MIDDLE);
		sb.setPaddingLeft(5);
		return sb.build();
	}

	private Style createDetailNumberStyle() {
		StyleBuilder sb = new StyleBuilder(true);
		sb.setFont(new Font(8, "DejaVu Sans", false));
		sb.setBorder(Border.THIN());
		sb.setBorderColor(Color.BLACK);
		sb.setTextColor(Color.BLACK);
		sb.setHorizontalAlign(HorizontalAlign.RIGHT);
		sb.setVerticalAlign(VerticalAlign.MIDDLE);
		sb.setPaddingRight(5);
		return sb.build();
	}

	private Style createTotalStyle() {
		StyleBuilder sb = new StyleBuilder(true);
		sb.setFont(new Font(8, "DejaVu Sans", true));
		sb.setBorderTop(Border.THIN());
		sb.setBorderColor(Color.BLACK);
		sb.setTextColor(Color.BLACK);
		sb.setHorizontalAlign(HorizontalAlign.RIGHT);
		sb.setVerticalAlign(VerticalAlign.MIDDLE);
		sb.setBackgroundColor(Color.lightGray);
		sb.setPaddingRight(5);
		sb.setTransparency(Transparency.OPAQUE);
		return sb.build();
	}

	@SuppressWarnings("rawtypes")
	private AbstractColumn createColumn(String property, Class type, String title, int width, Style headerStyle,
			Style detailStyle) throws ColumnBuilderException {
		AbstractColumn columnState = ColumnBuilder.getNew().setColumnProperty(property, type.getName())
				.setTitle(title.replace("_", " ").toUpperCase()).setWidth(Integer.valueOf(width)).setStyle(detailStyle)
				.setHeaderStyle(headerStyle).build();
		return columnState;
	}

	@SuppressWarnings("rawtypes")
	private DynamicReport getReport(Map inputParam, Map outputParam)
			throws ColumnBuilderException, ClassNotFoundException {

		DynamicReportBuilder drb = new DynamicReportBuilder();
		drb.setDetailHeight(new Integer(15)).setMargins(20, 20, 20, 20).setUseFullPageWidth(true)
				.setWhenNoDataAllSectionNoDetail();
		// .addAutoText(AutoText.AUTOTEXT_CREATED_ON, AutoText.POSITION_FOOTER,
		// AutoText.ALIGMENT_RIGHT, AutoText.PATTERN_DATE_DATE_ONLY, 150,150);

		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		JRPropertiesUtil util = JRPropertiesUtil.getInstance(context);
		util.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
		util.setProperty("net.sf.jasperreports.default.font.name", "SansSerif");
		util.setProperty("net.sf.jasperreports.default.pdf.font.name", "Helvetica");
		util.setProperty("net.sf.jasperreports.default.pdf.encoding", "UTF-8");
		util.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
		System.setProperty("java.awt.headless", "true");

		drb.setPageSizeAndOrientation(Page.Page_A4_Portrait());

		drb.addImageBanner(PropertyUtil.IMAGE_PATH,71, 50, ImageBanner.ALIGN_RIGHT, ImageScaleMode.FILL_PROPORTIONALLY);
		
		buildHeader(drb);
		buildSubHeader(drb);
		buildSubHeader2(drb);
		buildFooter(drb);

		DynamicReport report1 = createBillingDetail(inputParam, outputParam);
		if (report1 != null) {
			drb.addConcatenatedReport(report1, new ClassicLayoutManager(), "billDetail",
					DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
		}
		
		DynamicReport report5 = createBillHeading(inputParam, outputParam);
		if (report5 != null) {
			drb.addConcatenatedReport(report5, new ClassicLayoutManager(), "billHeader",
					DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
		}

		DynamicReport report = createTable(inputParam, outputParam);
		if (report != null) {
			drb.addConcatenatedReport(report, new ClassicLayoutManager(), "billTable",
					DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
		}
		
		DynamicReport report2 = createTotalAmount(inputParam, outputParam);
		if (report2 != null) {
			drb.addConcatenatedReport(report2, new ClassicLayoutManager(), "amount",
					DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
		}
		
		Alert alert = Utility.getAlert("Confirmation", "Payment Table !!", "Do you want to include Payment?", null, AlertType.CONFIRMATION);
		if (alert.getResult().equals(ButtonType.OK)) {
			DynamicReport report3 = createPaymentTable(inputParam, outputParam);
			if (report3 != null) {
				DynamicReport report4 = createPaymentHeading(inputParam, outputParam);
				if (report4 != null) {
					drb.addConcatenatedReport(report4, new ClassicLayoutManager(), "paymentHeader",
							DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, true);
				}
				drb.addConcatenatedReport(report3, new ClassicLayoutManager(), "paymentTable",
						DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
			}
		}
		
		Alert alert1 = Utility.getAlert("Confirmation", "Ledger !!", "Do you want to include Ledger?", null, AlertType.CONFIRMATION);
		if (alert1.getResult().equals(ButtonType.OK)) {
			DynamicReport report7 = createOpeningBalance(inputParam, outputParam);
			if (report7 != null) {
				drb.addConcatenatedReport(report7, new ClassicLayoutManager(), "opening",
						DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, true);
			}

			DynamicReport report6 = createTable2(inputParam, outputParam);
			if (report6 != null) {
				drb.addConcatenatedReport(report6, new ClassicLayoutManager(), "billTable2",
						DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
			}

			DynamicReport report8 = createClosingBalance(inputParam, outputParam);
			if (report8 != null) {
				drb.addConcatenatedReport(report8, new ClassicLayoutManager(), "closing",
						DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
			}
		}
		return drb.build();
	}

	public void buildHeader(DynamicReportBuilder reportBuilder) {
		AutoText header = new AutoText("M/s SHAHEED FILLING STATION", AutoText.POSITION_HEADER,
				HorizontalBandAlignment.LEFT);
		header.setWidth(550);
		StyleBuilder headerStyle = new StyleBuilder(true);
		headerStyle.setFont(new Font(24, "DejaVu Sans", true));
		headerStyle.setTextColor(Color.blue);

		header.setStyle(headerStyle.build());
		reportBuilder.addAutoText(header);
	}

	public void buildSubHeader(DynamicReportBuilder reportBuilder) {
		AutoText subHeader = new AutoText("NH-65 (52), Hisar Rajgarh Road, Jhumpa Kalan (Bhiwani)",
				AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		subHeader.setWidth(550);
		StyleBuilder headerStyle = new StyleBuilder(true);
		headerStyle.setFont(new Font(10, "DejaVu Sans", true));
		headerStyle.setPaddingTop(2);
		subHeader.setStyle(headerStyle.build());
		reportBuilder.addAutoText(subHeader);
	}

	public void buildSubHeader2(DynamicReportBuilder reportBuilder) {
		AutoText subHeader = new AutoText("GSTIN:06AQPPD6109CIZ6 TIN:06641112484 Phone:9812034475 ",
				AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
		subHeader.setWidth(550);
		StyleBuilder headerStyle = new StyleBuilder(true);
		headerStyle.setFont(new Font(8, "DejaVu Sans", false));
		headerStyle.setPaddingBottom(2);
		headerStyle.setBorderBottom(Border.THIN());
		subHeader.setStyle(headerStyle.build());
		reportBuilder.addAutoText(subHeader);
	}

	public void buildFooter(DynamicReportBuilder reportBuilder) {
		String footer = "All Subject to Siwani Jurisdiction<1>Produced By M/s. Shaheed Filling Station";
		String text[] = footer.split("<1>", 0);

		StyleBuilder footerStyle = new StyleBuilder(true);
		footerStyle.setFont(new Font(10, "DejaVu Sans", true));
		footerStyle.setBorderTop(Border.THIN());

		for (int i = 0; i < text.length; i++) {
			HorizontalBandAlignment alignment = HorizontalBandAlignment.RIGHT;
			if (i == 0)
				alignment = HorizontalBandAlignment.LEFT;

			AutoText textElement = new AutoText(text[i], AutoText.POSITION_FOOTER, alignment);
			textElement.setWidth(550);
			textElement.setStyle(footerStyle.build());
			reportBuilder.addAutoText(textElement);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DynamicReport createBillingDetail(Map inputParam, Map outputParam) {
		String partyName = (String) inputParam.get("partyName");

		Statement stmt = null;
		ResultSet rs = null;

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			
			stmt = HibernateUtil.getConnection().createStatement();
			String query = "select concat(cast('Bill To' as char),'\r\n', (select party_name from party where party_name = '"
					+ partyName + "'),'\r\n', cast('' as char), (select address from party where party_name = '"
					+ partyName
					+ "'), '\r\n', cast('' as char), (select contact_number from party where party_name = '"
					+ partyName
					+ "')) as line1, cast('	' as char) as line2, concat(cast('Invoice:#' as char),(select ifNull(max(id)+1,1) from report_info),'\r\n',cast('InvoiceDate:' as char),to_char(current_date(), 'dd-Mon-yyyy')) as line3;";

			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columncount = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columncount; i++) {
					String columnName = rsmd.getColumnLabel(i);
					map.put(columnName, rs.getObject(i).toString());
				}
				data.add(map);
			}

			if (data.size() != 0) {
				DynamicReportBuilder rb = new DynamicReportBuilder();
				rb.setMargins(5, 5, 20, 20).setUseFullPageWidth(true).setWhenNoData("", createHeaderStyle(), true, true)
						.setPrintColumnNames(false);

				outputParam.put("billDetail", data);

				Style style = new StyleBuilder(true).setFont(new Font(10, "DejaVu Sans", true))
						.setStretching(Stretching.RELATIVE_TO_TALLEST_OBJECT).setStretchWithOverflow(true)
						.setHorizontalAlign(HorizontalAlign.LEFT).setVerticalAlign(VerticalAlign.MIDDLE).build();

				Style style1 = new StyleBuilder(true).setFont(new Font(10, "DejaVu Sans", false))
						.setStretching(Stretching.RELATIVE_TO_TALLEST_OBJECT).setStretchWithOverflow(true)
						.setHorizontalAlign(HorizontalAlign.LEFT).setVerticalAlign(VerticalAlign.MIDDLE).build();

				AbstractColumn column1 = createColumn("LINE1", String.class, "line1", 50, style, style);
				rb.addColumn(column1);
				AbstractColumn column2 = createColumn("LINE2", String.class, "line2", 50, style, style);
				rb.addColumn(column2);
				AbstractColumn column3 = createColumn("LINE3", String.class, "line3", 50, style1, style1);
				rb.addColumn(column3);

				return rb.build();
			}
		} catch (SQLException | ColumnBuilderException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				}
				stmt = null;
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DynamicReport createTable(Map inputParam, Map outputParam) {
		String partyName = (String) inputParam.get("partyName");
		String fromDate = (String) inputParam.get("fromDate");
		String toDate = (String) inputParam.get("toDate");

		Statement stmt = null;
		ResultSet rs = null;

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			stmt = HibernateUtil.getConnection().createStatement();
			String query = "SELECT  to_char(posted_date, 'dd-Mon-yyyy') as posted_date,vehicle_number as vehicle_number,fuel_type as fuel_type,slip_number as slip_no,round(quantity,2) as quantity,rate as rate,round(amount,2) as amount, round(cash,2) as cash, round(total_amount,2) as total_amount FROM bill b WHERE";
			query += " b.party_name = '" + partyName + "' and b.posted_date >='" + fromDate + "' and b.posted_date <='"
					+ toDate + "' and record_active_ind = 'Y' order by b.posted_date;";
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsd = rs.getMetaData();
			int columncount = rsd.getColumnCount();
			int count = 0;
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				for (int i = 1; i <= columncount; i++) {
					String columnName = rsd.getColumnLabel(i);
					if (columnName.equalsIgnoreCase("total_amount")) {
						dataMap.put(columnName.toLowerCase(), BigDecimal.valueOf(rs.getDouble(i)));
					} else
						dataMap.put(columnName.toLowerCase(), rs.getObject(i));
				}
				dataMap.put("s_no", ++count);
				data.add(dataMap);
			}

			if (data.size() != 0) {
				DynamicReportBuilder rb = new DynamicReportBuilder();
				rb.setMargins(5, 5, 20, 20).setUseFullPageWidth(true).setWhenNoData("", createHeaderStyle(), true, true)
						.setPrintColumnNames(true).setGrandTotalLegend("Total Amount")
						.setGrandTotalLegendStyle(createTotalStyle());

				outputParam.put("billTable", data);

				AbstractColumn columnSerial = createColumn("s_no", Integer.class, "S. No.", 14, createHeaderStyle(),
						createDetailNumberStyle());
				AbstractColumn columnPostedDate = createColumn("posted_date", String.class, "Date", 24,
						createHeaderStyle(), createDetailTextStyle());
				AbstractColumn columnFuel = createColumn("fuel_type", String.class, "Fuel", 15, createHeaderStyle(),
						createDetailTextStyle());
				AbstractColumn columnQuantity = createColumn("quantity", Double.class, "Qty.", 22,
						createHeaderStyle(), createDetailNumberStyle());
				AbstractColumn columnRate = createColumn("rate", Double.class, "Rate", 18, createHeaderStyle(),
						createDetailNumberStyle());
				AbstractColumn columnSlip = createColumn("slip_no", Integer.class, "Slip No.", 20, createHeaderStyle(),
						createDetailNumberStyle());
				AbstractColumn columnVehicle = createColumn("vehicle_number", String.class, "Vehicle Number", 58,
						createHeaderStyle(), createDetailTextStyle());				
				AbstractColumn columnAmount = createColumn("amount", Double.class, "Amount", 22, createHeaderStyle(),
						createDetailNumberStyle());
				AbstractColumn columnCash = createColumn("cash", Double.class, "Cash", 22, createHeaderStyle(),
						createDetailNumberStyle());
				AbstractColumn columnTotalAmount = createColumn("total_amount", BigDecimal.class, "Total", 35, createHeaderStyle(),
						createDetailNumberStyle());

				rb.addColumn(columnSerial).addColumn(columnFuel).addColumn(columnPostedDate).addColumn(columnVehicle)
						.addColumn(columnSlip).addColumn(columnQuantity).addColumn(columnRate).addColumn(columnAmount)
						.addColumn(columnCash).addColumn(columnTotalAmount);

				rb.addGlobalFooterVariable(new DJGroupVariable(columnTotalAmount, DJCalculation.SUM, createTotalStyle()));

				return rb.build();
			}
		} catch (SQLException | ColumnBuilderException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				}
				stmt = null;
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DynamicReport createTable2(Map inputParam, Map outputParam) {
		String partyName = (String) inputParam.get("partyName");
		String fromDate = (String) inputParam.get("fromDate");
		String toDate = (String) inputParam.get("toDate");

		Statement stmt = null;
		ResultSet rs = null;

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			stmt = HibernateUtil.getConnection().createStatement();
			String query = "SELECT to_char(pj.posted_date, 'dd-Mon-yyyy') as posted_date, pj.mode as voucher_type, pj.reference as voucher_number, round(pj.debit_amount,2) as debit_amount, round(pj.credit_amount,2) as credit_amount FROM posting_journal pj " + 
					" WHERE"; 
			query += " pj.party_name = '" + partyName + "' and pj.posted_date >='" + fromDate + "' and pj.posted_date <='"
					+ toDate + "' and pj.record_active_ind = 'Y' order by pj.posted_date;";
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsd = rs.getMetaData();
			int columncount = rsd.getColumnCount();
			int count = 0;
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				for (int i = 1; i <= columncount; i++) {
					String columnName = rsd.getColumnLabel(i);
					if (columnName.equalsIgnoreCase("debit_amount")) {
						dataMap.put(columnName.toLowerCase(), BigDecimal.valueOf(rs.getDouble(i)));
					} else if (columnName.equalsIgnoreCase("credit_amount")) {
						dataMap.put(columnName.toLowerCase(), BigDecimal.valueOf(rs.getDouble(i)));
					} else
						dataMap.put(columnName.toLowerCase(), rs.getObject(i));
				}
				dataMap.put("s_no", ++count);
				data.add(dataMap);
			}
			
			
			if (data.size() != 0) {
				DynamicReportBuilder rb = new DynamicReportBuilder();
				rb.setMargins(5, 5, 20, 20).setUseFullPageWidth(true).setWhenNoData("", createHeaderStyle(), true, true)
						.setPrintColumnNames(true).setGrandTotalLegend("Total")
						.setGrandTotalLegendStyle(createTotalStyle());

				outputParam.put("billTable2", data);

				AbstractColumn columnSerial = createColumn("s_no", Integer.class, "S. No.", 15, createHeaderStyle(),
						createDetailNumberStyle());
				AbstractColumn columnPostedDate = createColumn("posted_date", String.class, "Date", 25,
						createHeaderStyle(), createDetailTextStyle());
				AbstractColumn columnType = createColumn("voucher_type", String.class, "Voucher Type", 25, createHeaderStyle(),
						createDetailTextStyle());
				AbstractColumn columnVNumber = createColumn("voucher_number", String.class, "Voucher No.", 60,
						createHeaderStyle(), createDetailTextStyle());
				AbstractColumn columnDebit = createColumn("debit_amount", BigDecimal.class, "Debit", 35, createHeaderStyle(),
						createDetailNumberStyle());
				AbstractColumn columnCredit = createColumn("credit_amount", BigDecimal.class, "Credit", 35, createHeaderStyle(),
						createDetailNumberStyle());

				rb.addColumn(columnSerial).addColumn(columnPostedDate).addColumn(columnType).addColumn(columnVNumber)
				.addColumn(columnDebit).addColumn(columnCredit);

				rb.addGlobalFooterVariable(new DJGroupVariable(columnDebit, DJCalculation.SUM, createTotalStyle()));
				rb.addGlobalFooterVariable(new DJGroupVariable(columnCredit, DJCalculation.SUM, createTotalStyle()));

				return rb.build();
			}
		} catch (SQLException | ColumnBuilderException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				}
				stmt = null;
			}
		}
		return null;
	}

	@SuppressWarnings({ "resource", "rawtypes", "unchecked" })
	public DynamicReport createTotalAmount(Map inputParam, Map outputParam) {
		String partyName = (String) inputParam.get("partyName");
		String fromDate = (String) inputParam.get("fromDate");
		String toDate = (String) inputParam.get("toDate");

		Double amount = null;

		Statement stmt = null;
		ResultSet rs = null;

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			
			stmt = HibernateUtil.getConnection().createStatement();
			String query = "SELECT  sum(total_amount) as amount FROM bill WHERE";
			query += " party_name = '" + partyName + "' and posted_date >='" + fromDate + "' and posted_date <='"
					+ toDate + "' and record_active_ind = 'Y';";

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				amount = rs.getDouble(1);
			}

			String str = ConvertNumToWord.convertNumber(amount.longValue());
			String amountInWords = str.substring(0, 2).toUpperCase() + str.substring(2);

			query = "SELECT  concat(cast('Total Amount in words : ' as char),cast('" + amountInWords
					+ " only' as char)) as total";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("amount", rs.getObject(1).toString());
				data.add(dataMap);
			}

			if (data.size() != 0) {
				DynamicReportBuilder rb = new DynamicReportBuilder();
				rb.setMargins(5, 5, 20, 20).setUseFullPageWidth(true).setWhenNoData("", createHeaderStyle(), true, true)
						.setPrintColumnNames(false);

				outputParam.put("amount", data);

				Style style = new StyleBuilder(true).setFont(new Font(10, "DejaVu Sans", true))
						.setStretching(Stretching.RELATIVE_TO_TALLEST_OBJECT).setStretchWithOverflow(true)
						.setHorizontalAlign(HorizontalAlign.LEFT).setVerticalAlign(VerticalAlign.MIDDLE).build();

				AbstractColumn column1 = createColumn("amount", String.class, "line1", 100, style, style);
				rb.addColumn(column1);
				return rb.build();
			}
		} catch (SQLException | ColumnBuilderException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				}
				stmt = null;
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DynamicReport createPaymentHeading(Map inputParam, Map outputParam) {
		SimpleDateFormat iFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat oFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String fromDate = (String) inputParam.get("fromDate");
		String toDate = (String) inputParam.get("toDate");
		
		Statement stmt = null;
		ResultSet rs = null;

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			stmt = HibernateUtil.getConnection().createStatement();
			String query = "SELECT  concat(cast('Details of payment received for the period of ' as char),cast(' "+oFormatter.format(iFormatter.parse(fromDate))+" to "+oFormatter.format(iFormatter.parse(toDate))+" as per follows:' as char)) as header";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("header", rs.getObject(1).toString());
				data.add(dataMap);
			}

			if (data.size() != 0) {
				DynamicReportBuilder rb = new DynamicReportBuilder();
				rb.setMargins(10, 5, 20, 20).setUseFullPageWidth(true).setWhenNoData("", createHeaderStyle(), true, true)
						.setPrintColumnNames(false);

				outputParam.put("paymentHeader", data);

				Style style = new StyleBuilder(true).setFont(new Font(8, "DejaVu Sans", true))
						.setStretching(Stretching.RELATIVE_TO_TALLEST_OBJECT).setStretchWithOverflow(true)
						.setHorizontalAlign(HorizontalAlign.LEFT).setVerticalAlign(VerticalAlign.MIDDLE).build();

				AbstractColumn column1 = createColumn("header", String.class, "line1", 100, style, style);
				rb.addColumn(column1);
				return rb.build();
			}
		} catch (SQLException | ColumnBuilderException | ParseException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				}
				stmt = null;
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DynamicReport createPaymentTable(Map inputParam, Map outputParam) {
		String partyName = (String) inputParam.get("partyName");
		String fromDate = (String) inputParam.get("fromDate");
		String toDate = (String) inputParam.get("toDate");

		Statement stmt = null;
		ResultSet rs = null;

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			stmt = HibernateUtil.getConnection().createStatement();
			String query = "SELECT to_char(posted_date, 'dd-Mon-yyyy') as posted_date,mode as mode,remarks as  remarks,amount as amount FROM payment WHERE";
			query += " party_name = '" + partyName + "' and posted_date >='" + fromDate + "' and posted_date <='"
					+ toDate + "' and active_ind = 'Y';";
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsd = rs.getMetaData();
			int columncount = rsd.getColumnCount();
			int count = 0;
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				for (int i = 1; i <= columncount; i++) {
					String columnName = rsd.getColumnLabel(i);
					dataMap.put(columnName.toLowerCase(), rs.getObject(i));
				}
				dataMap.put("s_no", ++count);
				data.add(dataMap);
			}

			if (data.size() != 0) {
				DynamicReportBuilder rb = new DynamicReportBuilder();
				rb.setMargins(5, 5, 20, 20).setUseFullPageWidth(true).setWhenNoData("There are no records availables.", createHeaderStyle(), true, true)
						.setPrintColumnNames(true).setGrandTotalLegend("Total Amount")
						.setGrandTotalLegendStyle(createTotalStyle());

				outputParam.put("paymentTable", data);

				AbstractColumn columnSerial = createColumn("s_no", Integer.class, "S. No.", 18, createHeaderStyle(),
						createDetailNumberStyle());
				AbstractColumn columnPostedDate = createColumn("posted_date", String.class, "Date", 25,
						createHeaderStyle(), createDetailTextStyle());
				AbstractColumn columnMode = createColumn("mode", String.class, "Mode of Payment", 30, createHeaderStyle(),
						createDetailTextStyle());
				AbstractColumn columnRemarks = createColumn("remarks", String.class, "Remarks", 50,
						createHeaderStyle(), createDetailNumberStyle());
				AbstractColumn columnAmount = createColumn("amount", Double.class, "Amount", 20, createHeaderStyle(),
						createDetailNumberStyle());

				rb.addColumn(columnSerial).addColumn(columnPostedDate).addColumn(columnMode)
						.addColumn(columnRemarks).addColumn(columnAmount);

				rb.addGlobalFooterVariable(new DJGroupVariable(columnAmount, DJCalculation.SUM, createTotalStyle()));

				return rb.build();
			}
		} catch (SQLException | ColumnBuilderException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				}
				stmt = null;
			}
		}
		return null;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DynamicReport createBillHeading(Map inputParam, Map outputParam) {
		SimpleDateFormat iFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat oFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String fromDate = (String) inputParam.get("fromDate");
		String toDate = (String) inputParam.get("toDate");
		
		Statement stmt = null;
		ResultSet rs = null;

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			stmt = HibernateUtil.getConnection().createStatement();
			String query = "SELECT  concat(cast('Details of slip created for the period of ' as char),cast(' "+oFormatter.format(iFormatter.parse(fromDate))+" to "+oFormatter.format(iFormatter.parse(toDate))+" as per follows:' as char)) as header";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("header", rs.getObject(1).toString());
				data.add(dataMap);
			}

			if (data.size() != 0) {
				DynamicReportBuilder rb = new DynamicReportBuilder();
				rb.setMargins(10, 5, 20, 20).setUseFullPageWidth(true).setWhenNoData("", createHeaderStyle(), true, true)
						.setPrintColumnNames(false);

				outputParam.put("billHeader", data);

				Style style = new StyleBuilder(true).setFont(new Font(8, "DejaVu Sans", true))
						.setStretching(Stretching.RELATIVE_TO_TALLEST_OBJECT).setStretchWithOverflow(true)
						.setHorizontalAlign(HorizontalAlign.LEFT).setVerticalAlign(VerticalAlign.MIDDLE).build();

				AbstractColumn column1 = createColumn("header", String.class, "line1", 100, style, style);
				rb.addColumn(column1);
				return rb.build();
			}
		} catch (SQLException | ColumnBuilderException | ParseException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				}
				stmt = null;
			}
		}
		return null;
	}
	
	
	@SuppressWarnings({ "resource", "rawtypes", "unchecked" })
	public DynamicReport createOpeningBalance(Map inputParam, Map outputParam) {
		String partyName = (String) inputParam.get("partyName");
		String fromDate = (String) inputParam.get("fromDate");
		
		Double amount = null;

		Statement stmt = null;
		ResultSet rs = null;

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			stmt = HibernateUtil.getConnection().createStatement();
			String query = "select (select ifNull(sum(b.total_amount),0) from bill b where b.record_active_ind = 'Y' and "
					+ "b.party_name = '"+partyName+"' and b.posted_date  < '"+fromDate+"') - "
					+ "(select ifNUll(sum(p.amount),0) from payment p  where active_ind = 'Y' and "
					+ "p.party_name = '"+partyName+"' and p.posted_date  < '"+fromDate+"') as opening;";
			
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				amount = rs.getDouble(1);
			}

			query = "SELECT  concat(cast('Opening Balance : ' as char),cast('" + amount
					+ "' as char)) as total";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("opening", rs.getObject(1).toString());
				data.add(dataMap);
			}

			if (data.size() != 0) {
				DynamicReportBuilder rb = new DynamicReportBuilder();
				rb.setMargins(5, 5, 20, 20).setUseFullPageWidth(true).setWhenNoData("", createHeaderStyle(), true, true)
						.setPrintColumnNames(false);

				outputParam.put("opening", data);

				Style style = new StyleBuilder(true).setFont(new Font(10, "DejaVu Sans", true))
						.setStretching(Stretching.RELATIVE_TO_TALLEST_OBJECT).setStretchWithOverflow(true)
						.setHorizontalAlign(HorizontalAlign.LEFT).setVerticalAlign(VerticalAlign.MIDDLE).build();

				AbstractColumn column1 = createColumn("opening", String.class, "line1", 100, style, style);
				rb.addColumn(column1);
				return rb.build();
			}
		} catch (SQLException | ColumnBuilderException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				}
				stmt = null;
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "resource", "rawtypes", "unchecked" })
	public DynamicReport createClosingBalance(Map inputParam, Map outputParam) {
		String partyName = (String) inputParam.get("partyName");
		String toDate = (String) inputParam.get("toDate");

		Double amount = null;

		Statement stmt = null;
		ResultSet rs = null;

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		try {
			stmt = HibernateUtil.getConnection().createStatement();
			String query = "select (select ifNull(sum(b.total_amount),0) from bill b where "
					+ "record_active_ind = 'Y' and "
					+ "b.party_name = '"+partyName+"' and b.posted_date  <= '"+toDate+"') - "
					+ "(select ifNUll(sum(p.amount),0) from payment p  where active_ind = 'Y' and "
					+ "p.party_name = '"+partyName+"' and p.posted_date  <= '"+toDate+"') as closing;";
			
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				amount = rs.getDouble(1);
			}

			query = "SELECT  concat(cast('Closing Balance : ' as char),cast('" + amount
					+ "' as char)) as total";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("closing", rs.getObject(1).toString());
				data.add(dataMap);
			}

			if (data.size() != 0) {
				DynamicReportBuilder rb = new DynamicReportBuilder();
				rb.setMargins(5, 5, 20, 20).setUseFullPageWidth(true).setWhenNoData("", createHeaderStyle(), true, true)
						.setPrintColumnNames(false);

				outputParam.put("closing", data);

				Style style = new StyleBuilder(true).setFont(new Font(10, "DejaVu Sans", true))
						.setStretching(Stretching.RELATIVE_TO_TALLEST_OBJECT).setStretchWithOverflow(true)
						.setHorizontalAlign(HorizontalAlign.LEFT).setVerticalAlign(VerticalAlign.MIDDLE).build();

				AbstractColumn column1 = createColumn("closing", String.class, "line1", 100, style, style);
				rb.addColumn(column1);
				return rb.build();
			}
		} catch (SQLException | ColumnBuilderException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				}
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				}
				stmt = null;
			}
		}
		return null;
	}
	
	public JasperPrint getPaymentReport(Map<String, String> inputParam)
			throws ColumnBuilderException, JRException, ClassNotFoundException {
		Map<String, Object> outputParam = new HashMap<String, Object>();
		DynamicReport dynaReport = getPaymentReport(inputParam, outputParam);
		JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dynaReport, new ClassicLayoutManager(), outputParam);
		return jp;
	}
	
	@SuppressWarnings("rawtypes")
	private DynamicReport getPaymentReport(Map inputParam, Map outputParam)
			throws ColumnBuilderException, ClassNotFoundException {

		DynamicReportBuilder drb = new DynamicReportBuilder();
		drb.setDetailHeight(new Integer(15)).setMargins(20, 20, 20, 20).setUseFullPageWidth(true)
				.setWhenNoDataAllSectionNoDetail();
		// .addAutoText(AutoText.AUTOTEXT_CREATED_ON, AutoText.POSITION_FOOTER,
		// AutoText.ALIGMENT_RIGHT, AutoText.PATTERN_DATE_DATE_ONLY, 150,150);

		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		JRPropertiesUtil util = JRPropertiesUtil.getInstance(context);
		util.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
		util.setProperty("net.sf.jasperreports.default.font.name", "SansSerif");
		util.setProperty("net.sf.jasperreports.default.pdf.font.name", "Helvetica");
		util.setProperty("net.sf.jasperreports.default.pdf.encoding", "UTF-8");
		util.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
		System.setProperty("java.awt.headless", "true");

		drb.setPageSizeAndOrientation(Page.Page_A4_Portrait());

		drb.addImageBanner(PropertyUtil.IMAGE_PATH, 71, 50, ImageBanner.ALIGN_RIGHT,
				ImageScaleMode.FILL_PROPORTIONALLY);

		buildHeader(drb);
		buildSubHeader(drb);
		buildSubHeader2(drb);
		buildFooter(drb);

		DynamicReport report1 = createBillingDetail(inputParam, outputParam);
		if (report1 != null) {
			drb.addConcatenatedReport(report1, new ClassicLayoutManager(), "billDetail",
					DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
		}

		DynamicReport report3 = createPaymentTable(inputParam, outputParam);
		if (report3 != null) {
			DynamicReport report4 = createPaymentHeading(inputParam, outputParam);
			if (report4 != null) {
				drb.addConcatenatedReport(report4, new ClassicLayoutManager(), "paymentHeader",
						DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
			}
			drb.addConcatenatedReport(report3, new ClassicLayoutManager(), "paymentTable",
					DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION, false);
		}

		return drb.build();
	}
	
}
