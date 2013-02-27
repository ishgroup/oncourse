package ish.oncourse.admin.services.billing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MWExportLine {
	
	public static final String MWEXPORT_HEADER = "";
	
	private static final String EXPORT_LINE_FORMAT = "%s\t%s\t%s\t%d\t%s\t%s\t%s\n";
	private static final DateFormat TRANSACTION_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private String nameCode;
	private String detailStockCode;
	private String detailDescription;
	private int detailStockQty;
	private String detailUnitPrice;
	private String description;
	private Date transactionDate;
	
	public MWExportLine(String nameCode, String detailStockCode, int detailStockQty, String detailDescription, String detailUnitPrice, 
			String description, Date transactionDate) {
		this.nameCode = nameCode;
		this.detailStockCode = detailStockCode;
		this.detailStockQty = detailStockQty;
		this.detailDescription = detailDescription;
		this.detailUnitPrice = detailUnitPrice;
		this.description = description;
		this.transactionDate = transactionDate;
	}
	
	@Override
	public String toString() {
		return String.format(EXPORT_LINE_FORMAT, nameCode, detailStockCode, detailStockQty, detailDescription, 
				detailUnitPrice, description, TRANSACTION_DATE_FORMAT.format(transactionDate));
	}

}
