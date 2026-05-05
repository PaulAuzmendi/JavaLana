import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class insertarTren extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String modelo        = request.getParameter("modelo");
        String fechaCreacion = request.getParameter("fechaCreacion");   // "2026-04-30"
        String fechaRevision = request.getParameter("fechaRevision");   // "2026-04-30"

        // Formato Access: #yyyy-MM-dd#
        String fCreacionAccess = "#" + fechaCreacion + "#";
        String fRevisionAccess = "#" + fechaRevision + "#";

        int nuevoIdTren = -1;

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            // 1) Calcular MAX+1
            ResultSet rsId = stmt.executeQuery("SELECT MAX(ID_Tren) FROM Trenes");
            if (rsId.next()) {
                nuevoIdTren = rsId.getInt(1) + 1;
            } else {
                nuevoIdTren = 1;
            }
            rsId.close();

            // 2) INSERT en Trenes
            String sqlInsert = "INSERT INTO Trenes (ID_Tren, Modelo, Fecha_Creacion, Fecha_Ultima_Revision) "
                            + "VALUES (" + nuevoIdTren + ", '" + modelo + "', "
                            + fCreacionAccess + ", " + fRevisionAccess + ")";
            System.out.println("SQL: " + sqlInsert);
            stmt.executeUpdate(sqlInsert);
            stmt.close();
            connection.close();

             } catch (Exception e) {
            throw new ServletException(e);
             
        }

           
            
        response.sendRedirect("Inicio");

       
    }
}