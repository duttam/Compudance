package com.onetouch.view.util;

/*
 * SpreadSheet.java
 *
 * Created on April 3, 2007, 11:53 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

/**
 *
 * @author liup
 */
public class SpreadSheet {
    private HSSFWorkbook workBook;
    private HSSFSheet sheet;
    
    /** Creates a new instance of SpreadSheet */
    public SpreadSheet() {
	workBook = new HSSFWorkbook();
	sheet = getWorkBook().createSheet();
    }
    
    
    public void export(OutputStream outStream) throws IOException {
	getWorkBook().write(outStream);
    }
    
    public void setSheetTitle(String sheetTitle) {
	getWorkBook().setSheetName(0, sheetTitle);
    }
    
    public void nextSheet(String sheetName) {
        sheet = getWorkBook().createSheet(sheetName);
    }
    
    protected HSSFWorkbook getWorkBook() {
	return workBook;
    }
    
    protected HSSFSheet getSheet() {
	return sheet;
    }
    
    public void setColumnWidths(int[] widths) {
        int max = widths.length;
        
        for (int i=0; i < max; i++) {
            getSheet().setColumnWidth((short)i ,(short)(widths[i] * 256));
        }        
    }
    
    public int writeMergedColumns(Object[] fields, int row, int numCols, boolean header, short alignment) {
	HSSFCellStyle cellStyle;
	HSSFFont cellFont;
	HSSFRow sheetRow;
	HSSFCell sheetCell;
	int currentRow = row;
	
	/*
	 * Cell Style
	 */
	cellStyle = getWorkBook().createCellStyle();
	cellStyle.setAlignment(alignment);
        cellStyle.setWrapText(true);
	cellFont = getWorkBook().createFont();
	if (header)
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	else
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
	cellStyle.setFont(cellFont);
	
	/*
	 * Date Style
	 */
	HSSFCellStyle dateStyle = getWorkBook().createCellStyle();
	dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
	dateStyle.setFont(cellFont);
        
        	
	sheetRow = getSheet().createRow(currentRow);
	short columnNum = 0;
	StringBuffer sBuff = new StringBuffer();
	for (int i=0; i< fields.length; i++) {
            if (fields[i] instanceof Date) {                
                sBuff.append(StringUtilities.formatDate((Date)fields[i]));
                sBuff.append(" ");
            } else {
		sBuff.append(StringUtilities.nonNullString(fields[i].toString()));
                sBuff.append(" ");
	    }
	}
 	sheetCell = sheetRow.createCell((short)0);
        sheetCell.setCellStyle(cellStyle);
        sheetCell.setCellValue(sBuff.toString());
        // Merge Cells
        getSheet().addMergedRegion(new Region((short)currentRow, (short)0, (short)currentRow , (short) numCols));

	return currentRow;
    }
    
   public int writeMergedColumns(Object field, int row, int numCols, boolean header, short height, short alignment) {
	HSSFCellStyle cellStyle;
	HSSFFont cellFont;
	HSSFRow sheetRow;
	HSSFCell sheetCell;
	int currentRow = row;
	
	/*
	 * Cell Style
	 */
	cellStyle = getWorkBook().createCellStyle();
	cellStyle.setAlignment(alignment);
        cellStyle.setWrapText(true);
	cellFont = getWorkBook().createFont();       
      	cellFont.setFontHeightInPoints(height);

	if (header)
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	else
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
	cellStyle.setFont(cellFont);
	
	/*
	 * Date Style
	 */
	HSSFCellStyle dateStyle = getWorkBook().createCellStyle();
	dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
	dateStyle.setFont(cellFont);
        
        	
	sheetRow = getSheet().createRow(currentRow);
	short columnNum = 0;
	StringBuffer sBuff = new StringBuffer();
	
        if (field instanceof Date) {                
            sBuff.append(StringUtilities.formatDate((Date)field));
            sBuff.append(" ");
        } else {
            sBuff.append(StringUtilities.nonNullString(field.toString()));
            sBuff.append(" ");
        }
	
 	sheetCell = sheetRow.createCell((short)0);
        sheetCell.setCellStyle(cellStyle);
        sheetCell.setCellValue(sBuff.toString());
        // Merge Cells
        getSheet().addMergedRegion(new Region((short)currentRow, (short)0, (short)currentRow , (short) numCols));

	return currentRow;
    }
   
    public int writeRow(Object[] fields, int startRow, boolean header, short alignment) {
	HSSFCellStyle cellStyle;
	HSSFFont cellFont;
	HSSFRow sheetRow;
	HSSFCell sheetCell;
	int currentRow = startRow;
	
	/*
	 * Cell Style
	 */
	cellStyle = getWorkBook().createCellStyle();
	cellStyle.setAlignment(alignment);
        cellStyle.setWrapText(true);
	cellFont = getWorkBook().createFont();
	if (header)
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	else
	    cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
	cellStyle.setFont(cellFont);
	
	/*
	 * Date Style
	 */
	HSSFCellStyle dateStyle = getWorkBook().createCellStyle();
	dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
	dateStyle.setFont(cellFont);
	
	sheetRow = getSheet().createRow(currentRow++);
	short columnNum = 0;
	
	for (int i=0; i< fields.length; i++) {
	    sheetCell = sheetRow.createCell(columnNum);
	    if (fields[i] instanceof Date) {
		sheetCell.setCellStyle(dateStyle);
		sheetCell.setCellValue((Date)fields[i]);
	    } else {
		sheetCell.setCellStyle(cellStyle);
	    }
	    sheetCell.setCellValue(fields[i].toString());
	    columnNum++;
	}
	
	return currentRow;
    }
    
    public int writeRow(String field, int startRow, short height, short alignment) {
	HSSFCellStyle cellStyle;
	HSSFFont cellFont;
	HSSFRow sheetRow;
	HSSFCell sheetCell;
	int currentRow = startRow;
	
	/*
	 * Cell Style
	 */
	cellStyle = getWorkBook().createCellStyle();
	cellStyle.setAlignment(alignment);
	cellFont = getWorkBook().createFont();
	cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	cellFont.setFontHeightInPoints(height);
	cellStyle.setFont(cellFont);
	
	
	sheetRow = getSheet().createRow(currentRow);
	short columnNum = 0;
	
	sheetCell = sheetRow.createCell(columnNum);
	sheetCell.setCellStyle(cellStyle);
	sheetCell.setCellValue(field);
	
	return currentRow;
    }
    
    public int writeCell(Object field, int startRow, short columnNum, short alignment) {
	HSSFCellStyle cellStyle;
	HSSFFont cellFont;
	HSSFRow sheetRow;
	HSSFCell sheetCell;
	int currentRow = startRow;
	
	/*
	 * Cell Style
	 */
	cellStyle = getWorkBook().createCellStyle();
	cellStyle.setAlignment(alignment);
	cellFont = getWorkBook().createFont();
	cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
	cellStyle.setFont(cellFont);
	
	/*
	 * Date Style
	 */
	HSSFCellStyle dateStyle = getWorkBook().createCellStyle();
	dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
	dateStyle.setFont(cellFont);
	
	sheetRow = getSheet().getRow(startRow);
	
	sheetCell = sheetRow.createCell(columnNum);
	if (field instanceof Date) {
	    sheetCell.setCellStyle(dateStyle);
	    sheetCell.setCellValue((Date)field);
	} else {
	    sheetCell.setCellStyle(cellStyle);
	}
	sheetCell.setCellValue(field.toString());
	
	return currentRow;
    }
    
}
