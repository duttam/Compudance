package com.onetouch.view.util;

import java.util.Date;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.onetouch.model.domainobject.EventPosition;

public class PageNumber extends PdfPageEventHelper 
{ 
	protected BaseFont helv;

	  public void onOpenDocument(PdfWriter writer, Document document) {

	    try {
	      helv = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
	    } catch (Exception e) {
	      
	    }
	  }
	  
	public void onEndPage (PdfWriter writer, Document document) 
	{ 
		PdfContentByte cb = writer.getDirectContent();
	    cb.saveState();
	    String text = "Page " + writer.getPageNumber() + ", "+ new Date()+"\n"+" ";
	    cb.beginText();
	    cb.setFontAndSize(helv, 10);
	    cb.setTextMatrix(0, 0);
	    cb.showText(text);
	    
	    cb.endText();
	    cb.restoreState();
	    
	   
		
	}
	
}