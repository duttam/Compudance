import java.awt.Point;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


public class UpdateHireDate {
	// JDBC driver name and database URL

	   private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
		private static final String DB_CONNECTION = "jdbc:mysql://scprod.cwneql7j1g7c.us-east-1.rds.amazonaws.com:3306/schedule_cloud";
		private static final String DB_USER = "root";
		private static final String DB_PASSWORD = "Onetouch0806";//"scloud123";
	 
		public static void main(String[] argv) throws SQLException {
	 
			/*Connection dbConnection = null;
			Statement preparedStatementSelect = null;
			PreparedStatement preparedStatementUpdate = null;
	 
			String selectHiredateSQL = "select e.id, er.start_date,e.company_id from employee e, employee_rate er where e.id = er.emp_id and er.start_date is not null";
	 
			String updateEmployeeHiredateSQL = "update employee set hiredate = ? where id = ? and company_id = ?";
	 
			try {
				dbConnection = getDBConnection();
	 
				dbConnection.setAutoCommit(false);
	 
				preparedStatementSelect = dbConnection.createStatement();		
				preparedStatementUpdate = dbConnection.prepareStatement(updateEmployeeHiredateSQL);
				
				ResultSet rs = preparedStatementSelect.executeQuery(selectHiredateSQL);
				while(rs.next()){
					int empid  = rs.getInt("id");
					String hiredate = rs.getString("start_date");
					int companyid = rs.getInt("company_id");


					
					preparedStatementUpdate.setString(1, hiredate);
					preparedStatementUpdate.setInt(2, empid);
					preparedStatementUpdate.setInt(3, companyid);
					preparedStatementUpdate.executeUpdate();
				}
				dbConnection.commit();
	 
				System.out.println("Done!");
	 
			} catch (SQLException e) {
	 
				System.out.println(e.getMessage());
				dbConnection.rollback();
	 
			} finally {
	 
				if (preparedStatementSelect != null) {
					preparedStatementSelect.close();
				}
	 
				if (preparedStatementUpdate != null) {
					preparedStatementUpdate.close();
				}
	 
				if (dbConnection != null) {
					dbConnection.close();
				}
	 
			}*/
			UpdateHireDate.generatePDFTableWithHeaderRows();
		}
	 
		private static void generatePDFTableWithHeaderRows(){
			Document document = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
		    try {
		      PdfWriter.getInstance(document,  new FileOutputStream("BigTableWithHeaderRowsPDF.pdf"));

		      document.open();
		      int NumColumns = 12;

		      PdfPTable datatable = new PdfPTable(NumColumns);
		      int headerwidths[] = { 9, 4, 8, 10, 8, 11, 9, 7, 9, 10, 4, 10 }; // percentage
		      datatable.setWidths(headerwidths);
		      for(int i=0;i<headerwidths.length;i++){
		        datatable.addCell("Header "+ i);  
		      }
		      datatable.setHeaderRows(1);

		      datatable.getDefaultCell().setBorderWidth(1);
		      for (int i = 1; i < 1000; i++) {
		        
		        for (int x = 0; x < NumColumns; x++) {
		          datatable.addCell("data");
		        }
		        
		      }
		      document.add(datatable);
		    } catch (Exception de) {
		      de.printStackTrace();
		    }
		    document.close();
		}
		private static Connection getDBConnection() {
	 
			Connection dbConnection = null;
	 
			try {
	 
				Class.forName(DB_DRIVER);
	 
			} catch (ClassNotFoundException e) {
	 
				System.out.println(e.getMessage());
	 
			}
	 
			try {
	 
				dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,
						DB_PASSWORD);
				return dbConnection;
	 
			} catch (SQLException e) {
	 
				System.out.println(e.getMessage());
	 
			}
	 
			return dbConnection;
	 
		}
	 
		private static java.sql.Timestamp getCurrentTimeStamp() {
	 
			java.util.Date today = new java.util.Date();
			return new java.sql.Timestamp(today.getTime());
	 
		}}
