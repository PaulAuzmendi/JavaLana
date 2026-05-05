import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class actualizarTren extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String idTren        = request.getParameter("idTren");
        String modelo        = request.getParameter("modelo");
        String fechaCreacion = request.getParameter("fechaCreacion");   // "2026-04-30"
        String fechaRevision = request.getParameter("fechaRevision");   // "2026-04-30"

        // Formato Access: #yyyy-MM-dd#
        String fCreacionAccess = "#" + fechaCreacion + "#";
        String fRevisionAccess = "#" + fechaRevision + "#";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            String sql = "UPDATE Trenes SET "
                       + "Modelo = '" + modelo + "', "
                       + "Fecha_Creacion = " + fCreacionAccess + ", "
                       + "Fecha_Ultima_Revision = " + fRevisionAccess + " "
                       + "WHERE ID_Tren = " + idTren;
            System.out.println("SQL: " + sql);
            stmt.executeUpdate(sql);

            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        response.sendRedirect("Inicio");
    }
}