package com.onetouch.view.bean.admin.report;





import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
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

public class DetailAvailabilityPDFExporter {

	
public void exportAvailabilityPDFTable(Document pdf, List<OneTouchReport> oneTouchReports) throws DocumentException {
		
		
		
		String[] headers = new String[] {"Emp Id", "Employee Name", "Position Name", "Shift Start Time", "Shift End Time", "Avail Start Time","Avail End Time",
										 "Event Id","Event Name","Location Code","Status"};
    	PdfPTable pdfTable = new PdfPTable(headers.length);
    	float[] columnWidths = new float[] {5f, 20f, 15f, 10f,10f, 10f,10f,6f, 30f,6f,15f};
		pdfTable.setWidths(columnWidths);
		pdfTable.setWidthPercentage(100);
    	for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            PdfPCell cell = new PdfPCell();
            cell.setGrayFill(0.6f);
            cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.HELVETICA, 10, Font.BOLD)));
            pdfTable.addCell(cell);
        }
    	pdfTable.setHeaderRows(1);
        
        
       
        for(OneTouchReport oneTouchReport : oneTouchReports) { 
        	
        	SimpleDateFormat timeformat = new SimpleDateFormat("MM-dd-yyyy");
			String eventPositionTime = timeformat.format(oneTouchReport.getReportDate());
        	String positionName = oneTouchReport.getPosition().getName()+", "+eventPositionTime +", Total Employees: "+oneTouchReport.getDetailedAvailReports().size();
            PdfPCell positionCell = new PdfPCell();
            positionCell.setColspan(11);
            positionCell.setGrayFill(0.6f);
            positionCell.setPhrase(new Phrase(positionName.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(positionCell);
            exportSubTable(oneTouchReport.getDetailedAvailReports(),pdfTable);
        	
        	
        	
        }	
        pdf.add(pdfTable);
       
	}
	
	private void exportSubTable(List<DetailedAvailReportByEmployee> availReports,PdfPTable pdfTable) throws DocumentException{
		
		SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
        for(DetailedAvailReportByEmployee reportByEmployee:availReports ){
        	String startTime = "";
            String endTime = "";
            String availStartTime = "";
            String availEndTime = "";
            String eventId = "";
            String eventName = "";
            String locationCode = "";
            String status = "";
        	String employeeId = reportByEmployee.getEmployeeId().toString();
            PdfPCell employeeIdCell = new PdfPCell();
            employeeIdCell.setPhrase(new Phrase(employeeId.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(employeeIdCell);
            
        	String employeeName = reportByEmployee.getEmployeeLastname()+", "+reportByEmployee.getEmployeeFirstname();
            PdfPCell employeeNameCell = new PdfPCell();
            employeeNameCell.setPhrase(new Phrase(employeeName.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(employeeNameCell);
            
            String pos = reportByEmployee.getPosition();
            String position = (pos==null)?"":pos;
            PdfPCell positionCell = new PdfPCell();
            positionCell.setPhrase(new Phrase(position.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(positionCell);
                       
            
            if(reportByEmployee.getStartTime()!=null)
	            startTime = timeformat.format(reportByEmployee.getStartTime());
	        PdfPCell startTimeCell = new PdfPCell();
            startTimeCell.setPhrase(new Phrase(startTime.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(startTimeCell);
            
            if(reportByEmployee.getEndTime()!=null)
            	endTime = timeformat.format(reportByEmployee.getEndTime());
            PdfPCell endTimeCell = new PdfPCell();
            endTimeCell.setPhrase(new Phrase(endTime.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(endTimeCell);
            
            if(reportByEmployee.getAvailStartTime()!=null)
            	availStartTime = timeformat.format(reportByEmployee.getAvailStartTime());
            PdfPCell availStartTimeCell = new PdfPCell();
            availStartTimeCell.setPhrase(new Phrase(availStartTime.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(availStartTimeCell);
            
            if(reportByEmployee.getAvailEndTime()!=null)
            	availEndTime = timeformat.format(reportByEmployee.getAvailEndTime());
            PdfPCell availEndTimeCell = new PdfPCell();
            availEndTimeCell.setPhrase(new Phrase(availEndTime.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(availEndTimeCell);
            
            if(reportByEmployee.getEventId()!=null && reportByEmployee.getEventId()>0)
            	eventId = reportByEmployee.getEventId().toString();
            PdfPCell eventIdCell = new PdfPCell();
            eventIdCell.setPhrase(new Phrase(eventId.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(eventIdCell);
            
            if(reportByEmployee.getEventName()!=null)
            	eventName = reportByEmployee.getEventName();
            PdfPCell eventNameCell = new PdfPCell();
            eventNameCell.setPhrase(new Phrase(eventName.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(eventNameCell);
            
            if(reportByEmployee.getLocationCode()!=null)
            	locationCode = reportByEmployee.getLocationCode();
            PdfPCell locationCodeCell = new PdfPCell();
            locationCodeCell.setPhrase(new Phrase(locationCode.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(locationCodeCell);
                      
            if(reportByEmployee.getStatus()!=null)
            	status = reportByEmployee.getStatus();
            PdfPCell statusCell = new PdfPCell();
            statusCell.setPhrase(new Phrase(status.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(statusCell);
            
            pdfTable.completeRow();
        }
		
	}

	

	private PdfPTable exportStaffReportFooterTable(OneTouchReport oneTouchReport) {
		PdfPTable pdfTable = new PdfPTable(9);
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
        
        
        pdfTable.completeRow();
        return pdfTable;
	}

	
	

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	
}
