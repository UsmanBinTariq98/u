package DAL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import DTO.File;
import DTO.File;
public class sql implements sqlinterface {
    public static final String URL = "jdbc:mysql://localhost:3306/project";
    public static final String USER = "root";
    public static final String PASSWORD = "";
    List<File>files = new ArrayList<>();
    public boolean savetodb(String name, String content) throws SQLException {
    	 String check = "SELECT COUNT(*) FROM document WHERE FileName = ?";   
    	 String query = "INSERT INTO document (FileName, DocumentText) VALUES (?, ?)";
        String update="UPDATE document SET DocumentText = ? WHERE FileName= ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(check)) {
        	stmt.setString(1, name);
        	ResultSet rs=stmt.executeQuery();
        	  rs.next();
              int count = rs.getInt(1);
              if (count > 0) {
                  PreparedStatement updatePstmt = conn.prepareStatement(update);
                  updatePstmt.setString(1,content);
                  updatePstmt.setString(2,name);
                  updatePstmt.executeUpdate();
              } else {  
                  PreparedStatement insertPstmt = conn.prepareStatement(query);
                  insertPstmt.setString(1, name);
                  insertPstmt.setString(2, content);
                  insertPstmt.executeUpdate();
              }
              return true;
          } catch (SQLException e) {
              e.printStackTrace();
              return false;
          }
    }
    public List <File> openfilefromdb(String name){
    	
    	String query="SELECT FileName,DocumentText FROM document WHERE FileName = ?";
    	try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query)) {
               stmt.setString(1, name);
               ResultSet rs = stmt.executeQuery();
               while(rs.next()) {
            	   String fname=rs.getString("FileName");
            	   String content=rs.getString("DocumentText");
            	   files.add(new File(fname,content));
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
    	return files;	
    }

    public List<File> viewallfilesdb(){
    	String query="SELECT FileName FROM document ";
    	try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query)) {
               ResultSet rs = stmt.executeQuery();
               while(rs.next()) {
         	   String fname=rs.getString("FileName");
            	   files.add(new File(fname));
               }
           } catch (SQLException e) {
               e.printStackTrace();
               
           }
    return files;
    }	
    
    public String txttostring(String name) {
        StringBuilder content = new StringBuilder();  
        String line;  
        try (BufferedReader reader = new BufferedReader(new FileReader(name))) {
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator()); 
            }
        } catch (IOException e) {
        	return "No file found";   
        }
        return content.toString(); 
    }
    public boolean deleteFileFromDb(String fileName) {
        String query = "DELETE FROM document WHERE FileName = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fileName);  
            int rowsAffected = stmt.executeUpdate(); 
            return rowsAffected > 0;  
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }
}

