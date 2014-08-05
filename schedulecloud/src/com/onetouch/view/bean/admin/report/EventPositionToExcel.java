package com.onetouch.view.bean.admin.report;



import org.apache.commons.lang.exception.NestableException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;
import org.apache.poi.ss.util.CellUtil;





import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.FormatType;
import com.onetouch.model.domainobject.Position;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventPositionToExcel {

  private HSSFWorkbook workbook;
  private HSSFSheet sheet;
  private HSSFFont boldFont;
  private HSSFDataFormat format;
  private ResultSet resultSet;
  private FormatType[] formatTypes;
  private List<EventPosition> eventPositions;
  
  private Map<String,Class<?>> propNameTypeMap;
  private int headerCols = 0;
  public EventPositionToExcel(List<EventPosition> eventPositions, Map<String,Class<?>> propNameTypeMap, String sheetName) {
	    workbook = new HSSFWorkbook();
	    this.eventPositions = eventPositions;
	    sheet = workbook.createSheet(sheetName);
	    boldFont = workbook.createFont();
	    boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    format = workbook.createDataFormat();
	    formatTypes = new  FormatType[6];
	    this.propNameTypeMap = propNameTypeMap;
	    
  }
  
  private FormatType getFormatType(Class _class) {
    if (_class == Integer.class || _class == Long.class) {
      return FormatType.INTEGER;
    } else if (_class == Float.class || _class == Double.class) {
      return FormatType.FLOAT;
    } else if (_class == Timestamp.class || _class == java.util.Date.class) {
      return FormatType.DATE;
    } else {
      return FormatType.TEXT;
    }
  }
  private FormatType getFormatType(String className, String propertyName){
	  Class<?> clazz=null;
	  try {
		  clazz = Class.forName(className);
	  } catch (ClassNotFoundException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }

      Field[] fieldlist = clazz.getDeclaredFields();
      for (Field field : fieldlist) {
    	if(propertyName.equalsIgnoreCase(field.getName()))
    		return getFormatType(field.getType());
    	else
    		return FormatType.TEXT;
         
      }
      
      return FormatType.TEXT;
  }
  public void generate(OutputStream outputStream) throws Exception {
    try {
      
      int currentRow = 0;
      HSSFRow row = sheet.createRow(currentRow);
      
      
      int numCols = headerCols;
      int col = 0,colHeader=0;
      for(String colName : propNameTypeMap.keySet()){
    	  writeCell(row, col++, colName, FormatType.TEXT, boldFont);
      }

      for (String proName : propNameTypeMap.keySet()) {
          Class _class = propNameTypeMap.get(proName);
          formatTypes[colHeader++] = getFormatType(_class);
          
      }

      currentRow++;

      // Write report rows
      for(EventPosition eventPosition : eventPositions){
        row = sheet.createRow(currentRow++);
        Position position = eventPosition.getPosition();
        writeCell(row, 0, position.getDescription(), formatTypes[0]);
        writeCell(row, 1, position.getName(), formatTypes[1]);
        writeCell(row, 2, eventPosition.getStartTime(), formatTypes[2]);
        writeCell(row, 3, eventPosition.getEndTime(), formatTypes[3]);
        
      }

      // Autosize columns
      for (int i = 0; i < colHeader; i++) {
        sheet.autoSizeColumn((short) i);
      }

      workbook.write(outputStream);
    }
    finally {
      outputStream.close();
    }
  }

  public void generate(File file) throws Exception {
    generate(new FileOutputStream(file));
  }

  private void writeCell(HSSFRow row, int col, Object value, FormatType formatType) throws NestableException {
    writeCell(row, col, value, formatType, null, null);
  }

  private void writeCell(HSSFRow row, int col, Object value, FormatType formatType, HSSFFont font) throws NestableException {
    writeCell(row, col, value, formatType, null, font);
  }

  private void writeCell(HSSFRow row, int col, Object value, FormatType formatType,
                         Short bgColor, HSSFFont font) throws NestableException {
    HSSFCell cell = HSSFCellUtil.createCell(row, col, null);
    if (value == null) {
      return;
    }

    if (font != null) {
      HSSFCellStyle style = workbook.createCellStyle();
      style.setFont(font);
      cell.setCellStyle(style);
    }

    switch (formatType) {
      case TEXT:
        cell.setCellValue(value.toString());
        break;
      case INTEGER:
        cell.setCellValue(((Number) value).intValue());
        HSSFCellUtil.setCellStyleProperty(cell, workbook, CellUtil.DATA_FORMAT,
            HSSFDataFormat.getBuiltinFormat(("#,##0")));
        break;
      case FLOAT:
        cell.setCellValue(((Number) value).doubleValue());
        HSSFCellUtil.setCellStyleProperty(cell, workbook, CellUtil.DATA_FORMAT,
            HSSFDataFormat.getBuiltinFormat(("#,##0.00")));
        break;
      case DATE:
        cell.setCellValue((Date) value);
        HSSFCellUtil.setCellStyleProperty(cell, workbook, CellUtil.DATA_FORMAT,
            HSSFDataFormat.getBuiltinFormat(("m/d/yy")));
        break;
      case MONEY:
        cell.setCellValue(((Number) value).intValue());
        HSSFCellUtil.setCellStyleProperty(cell, workbook,
            CellUtil.DATA_FORMAT, format.getFormat("($#,##0.00);($#,##0.00)"));
        break;
      case PERCENTAGE:
        cell.setCellValue(((Number) value).doubleValue());
        HSSFCellUtil.setCellStyleProperty(cell, workbook,
            CellUtil.DATA_FORMAT, HSSFDataFormat.getBuiltinFormat("0.00%"));
    }

    if (bgColor != null) {
      HSSFCellUtil.setCellStyleProperty(cell, workbook, CellUtil.FILL_FOREGROUND_COLOR, bgColor);
      HSSFCellUtil.setCellStyleProperty(cell, workbook, CellUtil.FILL_PATTERN, HSSFCellStyle.SOLID_FOREGROUND);
    }
  }

  
}