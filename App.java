import java.util.Scanner;
import java.sql.*; 
import oracle.jdbc.*;
import java.math.*;
import java.io.*;
import java.awt.*;
import oracle.jdbc.pool.OracleDataSource;

public class App {

    static void showTable(String table, Connection conn) {
    /*
    This function prints out the given table in terminal. We take name of table and calls PL/SQL function called get + "name of the table", which returns a cursor
    that is convereted to a result set, and we loop through the result set and print it out.
    */
	try{
        CallableStatement cs = conn.prepareCall("begin ? := refcursor_jdbc.get" + table + "(); end;");
        cs.registerOutParameter(1, OracleTypes.CURSOR);
        cs.execute();
        ResultSet rs = (ResultSet)cs.getObject(1);
	    ResultSetMetaData rsmd = rs.getMetaData();
        int column_count = rsmd.getColumnCount();

        while (rs.next()) {
	
		    for (int i = 1; i <= column_count; i++) {
        		System.out.print("\t" + rs.getString(i) + "\t");
        	}
        	System.out.print("\n");
        }
        cs.close();
	}
	catch (SQLException ex) { System.out.println ("\n*** Error:" + ex.getMessage() + "***\n");}
    }

    static void addStudent2Table(String sid, String firstname, String lastname, String status, String GPA, String email, Connection conn) {
    /*
    This function adds student to the table with the given attributes: sid, firstname, lastname, status, GPA, and email taken in as parameters. 
    We take in these attributes and call a PL/SQL function which inserts these attributes as a tuple in the students table. 
    */
	try {
    		CallableStatement cs = conn.prepareCall("begin srs.insert_student(:1,:2,:3,:4,:5,:6); end;");
    		cs.setString(1, sid); cs.setString(2, firstname); cs.setString(3, lastname);
    		cs.setString(4, status); cs.setString(5, GPA); cs.setString(6, email);
    		
    		cs.executeQuery();
    		cs.close();
    		
    }
    catch (SQLException ex) { System.out.println ("\n*** Error:" + ex.getMessage() + "***\n");}

    }

    static void listClassStudent(String sid, Connection conn) {
    /*
    This function lists all classes taken by a student, we take in the sid as a parameter. 
    We take in the sid and call a PL/SQL function which gets the information of the student, and then a function which lists all the classes taken.
    The tuples required are returned by cursors converted to result set, looped, and printed out so, we get the student info and classes taken. 
    */
	try {
    		CallableStatement cs1 = conn.prepareCall("begin ? := refcursor_jdbc.getstudent(?); end;");
    		CallableStatement cs2 = conn.prepareCall("begin ? := refcursor_jdbc.all_classes_taken(?); end;");
    		cs1.registerOutParameter(1, OracleTypes.CURSOR);
    		cs1.setString(2, sid);
    		cs2.registerOutParameter(1, OracleTypes.CURSOR);
    		cs2.setString(2, sid);
    		cs1.execute();
    		cs2.execute();
            ResultSet rs1 = (ResultSet)cs1.getObject(1);
            ResultSet rs2 = (ResultSet)cs2.getObject(1);
            
            rs1.next();
            System.out.println("Student Info: " + rs1.getString(1) + "\t" + rs1.getString(3) + "\t" + rs1.getString(4));
            System.out.println("Classes taken:");
            
            
            int rowcount = 0;
            while (rs2.next()) {
                System.out.println(rs2.getString(1) + "\t" +
                    rs2.getString(2) + rs2.getString(3));
                rowcount++;
            }
            
            if (rowcount == 0) {
            	System.out.format("%s hasn't taken any classes\n", rs1.getString(1).strip());
            }
            
            cs1.close();
	        cs2.close();
            
    	}
    	catch (SQLException ex) { System.out.println ("\n*** Error:" + ex.getMessage() + "***\n");}


    }

    static void showCoursePrereq(String dept_code, String course_no, Connection conn) {
    /*
    This function lists all classes taken by a student, we take in the sid as a parameter. 
    We take in the sid and call a PL/SQL function which gets the information of the student, and then a function which lists all the classes taken.
    The tuples required are returned by cursors converted to result set, looped, and printed out so, we get the student info and classes taken. 
    */
	    try {
    		CallableStatement cs = conn.prepareCall("begin ? := refcursor_jdbc.get_direct_prereq(?,?); end;");
    		cs.registerOutParameter(1, OracleTypes.CURSOR);
    		cs.setString(2, dept_code);
    		cs.setString(3, course_no);
    		cs.execute();
    		ResultSet rs = (ResultSet)cs.getObject(1);
    		
    		while (rs.next()) {
    			System.out.println(rs.getString(1).strip() + rs.getString(2).strip());
			    showCoursePrereq(rs.getString(1).strip(), rs.getString(2).strip(), conn);
    		}

            cs.close();
    	}
    	catch (SQLException ex) { System.out.println ("\n*** Error:" + ex.getMessage() + "***\n");}
    }

    static void showStudentsClass(String classid, Connection conn) {
    /*
    This function lists all students in a class, we take in the classid. 
    We take in the classid and call a PL/SQL function which gets the information of the class, and then a function which lists all the students enrolled in that class.
    The tuples required are returned by cursors converted to result set, looped, and printed out so, we get the class info and students taking said class. 
    */
        try {
    		CallableStatement cs1 = conn.prepareCall("begin ? := refcursor_jdbc.getclass(?); end;");
    		CallableStatement cs2 = conn.prepareCall("begin ? := refcursor_jdbc.all_students_taking_class(?); end;");
    		cs1.registerOutParameter(1, OracleTypes.CURSOR);
    		cs1.setString(2, classid);
    		cs2.registerOutParameter(1, OracleTypes.CURSOR);
    		cs2.setString(2, classid);
    		cs1.execute();
    		cs2.execute();
            ResultSet rs1 = (ResultSet)cs1.getObject(1);
            ResultSet rs2 = (ResultSet)cs2.getObject(1);
            
            rs1.next();
            System.out.println("Class Info: " + rs1.getString(1) + "\t" + rs1.getString(2));
            System.out.println("Students taking class:");
            
            
            int rowcount = 0;
            while (rs2.next()) {
                System.out.println(rs2.getString(1) + "\t" +
                    rs2.getString(2) + "\t" + rs2.getString(3));
                rowcount++;
            }
            
            if (rowcount == 0) {
            	System.out.println("empty class.");
            }
            
            cs1.close();
	        cs2.close();
            
    	}
    	catch (SQLException ex) { System.out.println ("\n*** Error:" + ex.getMessage() + "***\n");}
    }

    static void enrollStudentClass(String sid, String classid, Connection conn) {
    /*
    This function enrolls a studnent in  class, and we take in the sid and classid as parameters.
    We take in the classid and sid and call a PL/SQL function which inserts a tuple of sid and classid attributes in the enrollments table.
    We assume initial grade is NULL based on the Pl/SQL function, all erorr checking is done within PL/SQL code.
    */
        try {
    		CallableStatement cs = conn.prepareCall("begin srs.enroll(:1,:2); end;");
    		cs.setString(1, sid); cs.setString(2, classid);
    		cs.executeQuery();

    		cs.close();
    		
         }
        catch (SQLException ex) { System.out.println ("\n*** Error:" + ex.getMessage() + "***\n");}
    }

    static void dropStudentClass(String sid, String classid, Connection conn) {
    /*
    This function unenroll a studnent in  class, and we take in the sid and classid as parameters.
    We take in the classid and sid and call a PL/SQL function which deletes a tuple of said sid and classid attributes in the enrollments table.
    All erorr checking is done within PL/SQL code. We also grab the class information of the specified
    class using a PL/SQL procedure to check if it becomes empty.
    */
        try {
    		CallableStatement cs = conn.prepareCall("begin srs.unenroll(:1,:2); end;");
            CallableStatement cs2 = conn.prepareCall("begin ? := refcursor_jdbc.getclass(?); end;");
    		cs.setString(1, sid); cs.setString(2, classid);
            cs2.registerOutParameter(1, OracleTypes.CURSOR); cs2.setString(2, classid);
    		cs.executeQuery(); cs2.executeQuery();
            ResultSet rs = (ResultSet)cs2.getObject(1);
            rs.next();
            if (rs.getInt(3) == 0){
                System.out.println("no student in this class.");
            }

    		cs.close();
    		
         }
        catch (SQLException ex) { System.out.println ("\n*** Error:" + ex.getMessage() + "***\n");}
    }

    static void deleteStudentTable(String sid, Connection conn) {
    /*
    This function deletes a student from the student table, we take in sid as parameter.
    We take in the sid and call a PL/SQL function which deletes a tuple of where the sid is a key of that tuple (aka student).
    */
        try {
    		CallableStatement cs = conn.prepareCall("begin ? := srs.delete_student(?); end;");
            cs.registerOutParameter(1, Types.INTEGER);
    		cs.setString(2, sid);
    		
    		cs.executeQuery();
            int err = (int)cs.getInt(1);

            if (err == 1){
                System.out.println("sid not found");
            }
    		cs.close();
    		
        }
        catch (SQLException ex) { System.out.println ("\n*** Error:" + ex.getMessage() + "***\n");}

    }

    public static void main(String[] args) throws Exception {
        /*
        This is essentially the UI of the textual menu interface, where we check for user input and give options for different database functionality.
        */
        try 
        {
            OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();
            ds.setURL("jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:ACAD111");
            Connection conn = ds.getConnection("achaudh9", "");

            while(true) {
                System.out.println("Enter: \n 1 for table \n 2 to add student into students table \n 3 to list all classes taken by students \n 4 to show all prereq courses \n 5 to show all students in a class \n 6 to enroll student in class \n 7 to drop student from class \n 8 to delete student from student table, \n 9 to exit: ");
                Scanner in = new Scanner(System.in);
                String x = in.nextLine();
                int s = Integer.parseInt(x);
                if (s == 1) {
                    System.out.println("Enter table to display: ");
                    String next = in.nextLine();
                    String nextstr = next.strip();
                    if (nextstr.isEmpty()) {
                        System.out.println("No Table Selected");
                    }
                    else {
                        showTable(next, conn);
                    }
                }
                else if (s == 2) {
                    System.out.println("Enter sid: ");
                    String s1 = in.nextLine();
                    String sid = s1.strip();
                    System.out.println("Enter first name: ");
                    String s2 = in.nextLine();
                    String firstname = s2.strip();
                    System.out.println("Enter last name: ");
                    String s3 = in.nextLine();
                    String lastname = s3.strip();
                    System.out.println("Enter status: ");
                    String s4 = in.nextLine();
                    String status = s4.strip();
                    System.out.println("Enter GPA: ");
                    String s5 = in.nextLine();
                    String GPA = s5.strip();
                    System.out.println("Enter email: ");
                    String s6 = in.nextLine();
                    String email = s6.strip();

                    addStudent2Table(sid, firstname, lastname, status, GPA, email, conn);

                }
                else if (s == 3) {
                    System.out.println("Enter sid: ");
                    String s1 = in.nextLine();
                    String sid = s1.strip();

                    if (sid.isEmpty()) {
                        System.out.println("No sid entered!");
                    }
                    else {
                        listClassStudent(sid, conn);
                    }
                }
                else if (s == 4) {
                    System.out.println("Enter dept_code: ");
                    String s1 = in.nextLine();
                    String dept_code = s1.strip();
                    System.out.println("Enter course_no: ");
                    String s2 = in.nextLine();
                    String course_no = s2.strip();

                    showCoursePrereq(dept_code, course_no, conn);
                }
                else if (s == 5) {
                    System.out.println("Enter classid: ");
                    String s1 = in.nextLine();
                    String classid = s1.strip();

                    showStudentsClass(classid, conn);
                }
                else if (s == 6) {
                    System.out.println("Enter sid: ");
                    String s1 = in.nextLine();
                    String sid = s1.strip();
                    System.out.println("Enter classid: ");
                    String s2 = in.nextLine();
                    String classid = s2.strip();

                    enrollStudentClass(sid, classid, conn);
                }
                else if (s == 7) {
                    System.out.println("Enter sid: ");
                    String s1 = in.nextLine();
                    String sid = s1.strip();
                    System.out.println("Enter classid: ");
                    String s2 = in.nextLine();
                    String classid = s2.strip();

                    dropStudentClass(sid, classid, conn);
                }
                else if (s == 8) {
                    System.out.println("Enter sid: ");
                    String s1 = in.nextLine();
                    String sid = s1.strip();

                    deleteStudentTable(sid, conn);
                }
                else if (s == 9) {
                    break;
                }
                else {
                    System.out.println("Invalid Argument!");
                }
            }
            conn.close();
        }
        catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n");}
        catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}
    }
}
