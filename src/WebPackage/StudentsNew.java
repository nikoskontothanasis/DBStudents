package WebPackage;
import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/studentsnew")

public class StudentsNew extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	String driver = "org.sqlite.JDBC";
	String dbURL = "jdbc:sqlite:mynewDB.db";

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Connection dbCon;

		try {
			createNewTable();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		PrintWriter out = res.getWriter();

		out.println("<!DOCTYPE html><html><body>");

		if (req.getParameter("edit") != null) {
			
			try {
				Class.forName(driver);
				dbCon = DriverManager.getConnection(dbURL);
				ResultSet rs;
				Statement stmt;
				stmt = dbCon.createStatement();
				String am = req.getParameter("am");

				String qry = "select first_name, last_name, semester, email from students where id=" + am;
				String[] columns = new String[] { "first_name", "last_name", "semester", "email" };

				rs = stmt.executeQuery(qry);

				String on = null, ep = null, ex = null, e = null;
				
				while (rs.next()) {
					on = rs.getString(columns[0]);
					ep = rs.getString(columns[1]);
					ex = rs.getString(columns[2]);
					e = rs.getString(columns[3]);
				}

				printForm2(out, am, on, ep, ex, e);

				rs.close();
				stmt.close();
				dbCon.close();

			} catch (Exception e) {
				out.println(e.toString());
			}

		} else {
			printForm(out);
		}

		printAnyError(out, req);

		try {
			String qry = "select id, first_name, last_name, semester, email from students";
			String[] columns = new String[] { "id", "first_name", "last_name", "semester", "email" };
			String[] columnsVisible = new String[] { "ΑΜ", "ΟΝΟΜΑ", "ΕΠΩΝΥΜΟ", "ΕΞΑΜΗΝΟ", "EMAIL" };

			Class.forName(driver);
			dbCon = DriverManager.getConnection(dbURL);
			ResultSet rs;
			Statement stmt;
			stmt = dbCon.createStatement();
			rs = stmt.executeQuery(qry);

			// Printing the table
			out.println("<hr/>");
			out.println("<table border=1><tr>");
			for (int i = 0; i < columns.length; i++) {
				out.print("<td><b>");
				out.print(columnsVisible[i].toUpperCase());
				out.print("</b></td>");
			}

			while (rs.next()) {
				
				out.println("<tr>");
				for (int i = 0; i < columns.length; i++) {
					out.println("<td>");
					out.println(rs.getString(columns[i]));
					out.println("</td>");

				}
				
				out.println("<td>");
				out.println("<form action=\"studentsnew\" method=\"GET\">");
				out.println("<input type=\"submit\" name=\"edit\"  value=\"Edit\"> ");
				out.println("<input type=\"hidden\" name=\"am\" value=" + rs.getString(columns[0]) + ">");
				out.println("</form>\n");
				out.println("</td>");

				out.println("<td>");
				out.println("<form action=\"studentsnew\" method=\"POST\">");
				out.println("<input type=\"submit\" name=\"delete\" value=\"Delete\" >");
				out.println("<input type=\"hidden\" name=\"am\" value=" + rs.getString(columns[0]) + ">");
				out.println("</form>\n");
				out.println("</td>");
				out.println("</tr>\n");

			}
			out.println("</table></body></html>");

			rs.close();
			stmt.close();
			dbCon.close();

		} catch (Exception e) {
			out.println(e.toString());
		} finally {
			out.close();
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection dbCon;

		req.setCharacterEncoding("utf-8");

		if (req.getParameter("delete") != null) {

			String am = req.getParameter("am");

			String qry = "delete from students where id=" + am;

			try {

				Class.forName(driver);
				dbCon = DriverManager.getConnection(dbURL);

				PreparedStatement stmt;
				stmt = dbCon.prepareStatement(qry);

				stmt.execute();

				res.sendRedirect("studentsnew");

			} catch (Exception e) {
				res.sendRedirect("studentsnew?errormsg=" + e.getMessage());
			}

		} else if (req.getParameter("save") != null) {

			String qry = "insert into students (id, first_name, last_name, semester, email) values (? ,? ,? ,?, ?)";

			String am = req.getParameter("am");
			String onoma = req.getParameter("onoma");
			String eponimo = req.getParameter("eponimo");
			String examino = req.getParameter("examino");
			String email = req.getParameter("email");

			try {

				Class.forName(driver);
				dbCon = DriverManager.getConnection(dbURL);

				PreparedStatement stmt;
				stmt = dbCon.prepareStatement(qry);
				stmt.setString(1, am);
				stmt.setString(2, onoma);
				stmt.setString(3, eponimo);
				stmt.setString(4, examino);
				stmt.setString(5, email);

				int i = stmt.executeUpdate();
				System.out.println("Inserted " + i + " row(s)");

				res.sendRedirect("studentsnew");

			} catch (Exception e) {
				res.sendRedirect("studentsnew?errormsg=" + e.getMessage());
			}

		} else {
			try {

				String am = req.getParameter("am");
				String onoma = req.getParameter("onoma");
				String eponimo = req.getParameter("eponimo");
				String examino = req.getParameter("examino");
				String email = req.getParameter("email");

				String qry = "update students set first_name=?, last_name=?, semester=?, email=? where id=?";

				Class.forName(driver);
				dbCon = DriverManager.getConnection(dbURL);

				PreparedStatement stmt;
				stmt = dbCon.prepareStatement(qry);
				stmt.setString(1, onoma);
				stmt.setString(2, eponimo);
				stmt.setString(3, examino);
				stmt.setString(4, email);
				stmt.setString(5, am);

				stmt.executeUpdate();
				res.sendRedirect("studentsnew");

			} catch (Exception e) {
				res.sendRedirect("studentsnew?errormsg=" + e.getMessage());
			}
		}
	}

	void printForm(PrintWriter out) {

		out.println("<form action=\"studentsnew\" method=\"POST\">");
		out.println("<b> Παρακαλώ δώστε τα ακόλουθα στοιχεία: </b> <br>");
		out.println("<b> Όνομα :  </b> <input type=\"text\" name=\"onoma\" ><br>");
		out.println("<b> Επώνυμο :  </b> <input type=\"text\" name=\"eponimo\" ><br>");
		out.println("<b> Αριθμός Μητρώου: </b> <input type=\"text\" name=\"am\" ><br>");
		out.println("<b> Εξάμηνο: </b>  <input type=\"text\" name=\"examino\" ><br>");
		out.println("<b> Email: </b> <input type=\"text\" name=\"email\" ><br>");
		out.println("<input type=\"submit\" name=\"save\"  value=\"Save\"> ");
		out.println("</form>");

	}

	void printForm2(PrintWriter out, String am, String on, String ep, String ex, String email) {

		out.println("<form action=\"studentsnew\" method=\"POST\">");
		out.println("<b> Παρακαλώ δώστε τα ακόλουθα στοιχεία: </b> <br>");
		out.println("<b> Όνομα :  </b> <input type=\"text\" name=\"onoma\" value=" + on + "><br>");
		out.println("<b> Επώνυμο :  </b> <input type=\"text\" name=\"eponimo\"value=" + ep + "><br>");
		out.println("<b> Αριθμός Μητρώου: </b> <input type=\"text\" name=\"am\" value=" + am + "><br>");
		out.println("<b> Εξάμηνο: </b>  <input type=\"text\" name=\"examino\" value=" + ex + "><br>");
		out.println("<b> Email: </b> <input type=\"text\" name=\"email\" value=" + email + "><br>");
		out.println("<input type=\"submit\" name=\"update\"  value=\"Update\"> ");
		out.println("</form>");

	}

	void printAnyError(PrintWriter out, HttpServletRequest req) {
		String errorMessage = req.getParameter("errormsg");
		if (errorMessage != null) {
			out.println("<br><strong style=\"color:red\"> Error: " + errorMessage + "</strong>");
		}
	}

	public void createNewTable() throws ClassNotFoundException {

		// SQLite connection string
		String driver = "org.sqlite.JDBC";
		Class.forName(driver);
		String url = "jdbc:sqlite:mynewDB.db";
		// SQL statement for creating a new table
		String sql = "CREATE TABLE IF NOT EXISTS students (\n" + "	id varchar (6) PRIMARY KEY,\n"
				+ "	first_name varchar(45) NOT NULL,\n" + "	last_name varchar(45) NOT NULL,\n"
				+ "	semester varchar(2) NOT NULL,\n" + "	email varchar(45) DEFAULT NULL\n" + ");";

		try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
			// create a new table
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}