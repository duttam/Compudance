package com.onetouch.view.bean.admin.event;



import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datalist.DataList;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.row.Row;
import org.primefaces.component.rowexpansion.RowExpansion;
import org.primefaces.component.subtable.SubTable;
import org.primefaces.component.summaryrow.SummaryRow;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.extensions.component.exporter.Exporter;
import org.primefaces.util.Constants;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.onetouch.model.domainobject.DetailedAvailReportByEmployee;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.OneTouchReport;

public class EventPositionPDFExporter {

	public void exportEventPositionPDFTable(Document pdf, List<EventPosition> exportEventPositions) throws DocumentException {
		
		/*Font tableTitleFont = FontFactory.getFont(FontFactory.TIMES, Font.DEFAULTSIZE, Font.BOLD);
		Paragraph title = new Paragraph("Event Schedule", tableTitleFont);
		pdf.add(title);

		Paragraph preface = new Paragraph();
		addEmptyLine(preface, 3);
		pdf.add(preface);*/
		
		String[] headers = new String[] {"Id", "Name", "Time In", "Break Out", "Break In", "Time Out","Total Hour","Signature"};
		float[] widths = { 2f,4f, 2f, 2f,2f, 2f, 2f,4f };
    	PdfPTable pdfTable = new PdfPTable(widths);
    	pdfTable.setWidthPercentage(100);
    	for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            PdfPCell cell = new PdfPCell();
            
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setGrayFill(0.6f);
            cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.HELVETICA, 10, Font.BOLD)));
            pdfTable.addCell(cell);
        }
    	pdfTable.setHeaderRows(1);
        

        for(EventPosition eventPosition : exportEventPositions) { 

        	SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
			String eventPositionTime = timeformat.format(eventPosition.getStartTime())+"-"+timeformat.format(eventPosition.getEndTime());
        	String positionName = eventPosition.getPosition().getName()+", "+eventPositionTime ;
            PdfPCell positionCell = new PdfPCell();
            positionCell.setColspan(8);
            positionCell.setGrayFill(0.6f);
            positionCell.setPhrase(new Phrase(positionName.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(positionCell);
            exportSubTable(eventPosition,pdfTable);
        	
        	
        }	
        pdf.add(pdfTable);
        
	}
	
	private void exportSubTable(EventPosition eventPosition,PdfPTable pdfTable) {
	
        List<Employee> allAcceptedEmployees = eventPosition.getPosition().getAcceptedEmps();
        for(Employee employee: allAcceptedEmployees){
        	String employeeId = employee.getId().toString();
            PdfPCell employeeIdCell = new PdfPCell();
            employeeIdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            employeeIdCell.setPhrase(new Phrase(employeeId.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(employeeIdCell);
            
        	String employeeName = employee.getLastname()+", "+employee.getFirstname();
            PdfPCell employeeNameCell = new PdfPCell();
            employeeNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            employeeNameCell.setPhrase(new Phrase(employeeName.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(employeeNameCell);
            
            for(int i=0;i<6;i++){
            	PdfPCell pdfCell = new PdfPCell();
                pdfCell.setPhrase(new Phrase(""));
                pdfTable.addCell(pdfCell);
            }
                        
            pdfTable.completeRow();
        }
		
	}

	public void staffReportTable(Document pdf, List<OneTouchReport> staffReqtListRpt) throws DocumentException{
		String[] headers = new String[] {"EventId", "Event Time", "Event Type", "Event Name", "Location", "Count","Required","Staffed","Open","Pending Notification"};
    	PdfPTable pdfTable = new PdfPTable(headers.length);
    	pdfTable.setWidthPercentage(100);
    	for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            PdfPCell cell = new PdfPCell();
            cell.setGrayFill(0.6f);
            cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.HELVETICA, 10, Font.BOLD)));
            pdfTable.addCell(cell);
        }
        //pdfTable.completeRow();
        
    	pdfTable.setHeaderRows(1);
        
        for(OneTouchReport oneTouchReport : staffReqtListRpt) { 
        	/*PdfPTable subPdfTable = new PdfPTable(1);
        	subPdfTable.setWidthPercentage(100);*/
        	String eventDate = oneTouchReport.getEventDate();
            PdfPCell positionCell = new PdfPCell();
            positionCell.setColspan(10);
            positionCell.setGrayFill(0.6f);
            positionCell.setPhrase(new Phrase(eventDate.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(positionCell);
            /*subPdfTable.completeRow();
            pdf.add(subPdfTable);*/
            exportStaffReportSubTable(oneTouchReport,pdfTable);
            exportStaffReportFooterTable(oneTouchReport,pdfTable);
        	//pdf.add(subTableCols);
        	
        	
        }	
        //PdfPTable footerTable = 
    	//pdf.add(footerTable);
        pdf.add(pdfTable);
	}

	private void exportStaffReportFooterTable(OneTouchReport oneTouchReport,PdfPTable pdfTable ) {
		/*PdfPTable pdfTable = new PdfPTable(10);
		pdfTable.setWidthPercentage(100);*/
		List<Event> eventList = oneTouchReport.getEventList();
		
		String totalCnt = "Daily Event Count: "+eventList.size();
        PdfPCell totalCntCell = new PdfPCell();
        totalCntCell.setColspan(3);
        totalCntCell.setGrayFill(0.6f);
        totalCntCell.setPhrase(new Phrase(totalCnt.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
        pdfTable.addCell(totalCntCell);
        
       
        String s = "Total for Day: ";
        PdfPCell sCell = new PdfPCell();
        sCell.setColspan(2);
        sCell.setGrayFill(0.6f);
        sCell.setPhrase(new Phrase(s.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
        pdfTable.addCell(sCell);
        
        String totalGuestCnt = oneTouchReport.getTotalGuestCnt().toString();
        PdfPCell totalGuestCntCell = new PdfPCell();
        totalGuestCntCell.setGrayFill(0.6f);
        totalGuestCntCell.setPhrase(new Phrase(totalGuestCnt.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
        pdfTable.addCell(totalGuestCntCell);
        
        String totalReqdCnt = oneTouchReport.getTotalReqd().toString();
        PdfPCell totalReqdCntCell = new PdfPCell();
        totalReqdCntCell.setGrayFill(0.6f);
        totalReqdCntCell.setPhrase(new Phrase(totalReqdCnt.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
        pdfTable.addCell(totalReqdCntCell);
        
        String totalStaffedCnt = oneTouchReport.getTotalStaffed().toString();
        PdfPCell totalStaffedCntCell = new PdfPCell();
        totalStaffedCntCell.setGrayFill(0.6f);
        totalStaffedCntCell.setPhrase(new Phrase(totalStaffedCnt.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
        pdfTable.addCell(totalStaffedCntCell);
        
        Integer totalRemain = oneTouchReport.getTotalReqd()-oneTouchReport.getTotalStaffed();
        String totalRemainCnt = totalRemain.toString();
        PdfPCell totalRemainCntCell = new PdfPCell();
        totalRemainCntCell.setGrayFill(0.6f);
        totalRemainCntCell.setPhrase(new Phrase(totalRemainCnt.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
        pdfTable.addCell(totalRemainCntCell);
        
        Integer totalPendingNotification = oneTouchReport.getTotalPendingNotification();
        String totalPendingNotificationCnt = totalPendingNotification.toString();
        PdfPCell totalPendingNotificationCntCell = new PdfPCell();
        totalPendingNotificationCntCell.setGrayFill(0.6f);
        totalPendingNotificationCntCell.setPhrase(new Phrase(totalPendingNotificationCnt.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
        pdfTable.addCell(totalPendingNotificationCntCell);
        
        pdfTable.completeRow();
        //return pdfTable;
	}

	public void exportStaffReportSubTable(OneTouchReport oneTouchReport,PdfPTable pdfTable ){
		
		//PdfPTable pdfTable = new PdfPTable(10);
		
		//pdfTable.setWidthPercentage(100);
		List<Event> eventList = oneTouchReport.getEventList();
		for(Event event : eventList){
			String eventId = event.getId().toString();
			PdfPCell eventIdCell = new PdfPCell();
			eventIdCell.setPhrase(new Phrase(eventId.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(eventIdCell);
			
			SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
			String eventTime = timeformat.format(event.getStartTime());
			PdfPCell eventTimeCell = new PdfPCell();
			eventTimeCell.setPhrase(new Phrase(eventTime.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(eventTimeCell);
			
			String eventType = event.getEventType().getName();
			PdfPCell eventTypeCell = new PdfPCell();
			eventTypeCell.setPhrase(new Phrase(eventType.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(eventTypeCell);
			
			String eventName = event.getName();
			PdfPCell eventNameCell = new PdfPCell();
			eventNameCell.setPhrase(new Phrase(eventName.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(eventNameCell);
			
			String location = event.getLocation().getLocationTitle()+" "+ event.getLocation().getName();
			PdfPCell locationCell = new PdfPCell();
			locationCell.setPhrase(new Phrase(location.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(locationCell);
			
			String count = event.getGuestCount().toString();
			PdfPCell countCell = new PdfPCell();
			countCell.setPhrase(new Phrase(count.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(countCell);
			
			String requiredCount = event.getPositionRequiredCount().toString();
			PdfPCell requiredCountCell = new PdfPCell();
			requiredCountCell.setPhrase(new Phrase(requiredCount.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(requiredCountCell);
			
			String staffedCount = event.getPositionStaffedCount().toString();
			PdfPCell staffedCountCell = new PdfPCell();
			staffedCountCell.setPhrase(new Phrase(staffedCount.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(staffedCountCell);
			
			String openCount = event.getPositionOpenCount().toString();
			PdfPCell openCountCell = new PdfPCell();
			openCountCell.setPhrase(new Phrase(openCount.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(openCountCell);
			
			String pendingNotifyCount = "";
			if(event.getPendingNotificationCount()!=null)
				pendingNotifyCount = event.getPendingNotificationCount().toString();
			PdfPCell pendingNotifyCountCell = new PdfPCell();
			pendingNotifyCountCell.setPhrase(new Phrase(pendingNotifyCount.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
			pdfTable.addCell(pendingNotifyCountCell);
			
			pdfTable.completeRow();
		}
        
		//return pdfTable;
		
	}
	
	public void getExtraStaffRpt(Document pdf) throws DocumentException{
		
		String[] headers = new String[] {"Event", "Position","Shift-Time","Id", "Name", "Time In", "Break Out", "Break In","Time Out","Total(hr)","Signature"};
		float[] widths = { 2f, 2f, 2f,1f,4f, 2f, 2f,2f, 2f, 2f,4f };
	    
    	PdfPTable pdfTable = new PdfPTable(widths);
    	pdfTable.setWidthPercentage(100);
    	
    	for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            PdfPCell cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setGrayFill(0.6f);
            cell.setFixedHeight(50f);
            cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.HELVETICA, 10, Font.BOLD)));
            pdfTable.addCell(cell);
        }
        pdfTable.completeRow();
        for(int row=0;row<8;row++){
        	 for(int i=0;i<11;i++){
             	PdfPCell pdfCell = new PdfPCell();
             	pdfCell.setFixedHeight(50f);
             	pdfCell.setPhrase(new Phrase(" "));
                pdfTable.addCell(pdfCell);
             }
                         
             pdfTable.completeRow();
        }
        pdf.add(pdfTable);
        
        	
        
	}
	
	
	

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	
}
