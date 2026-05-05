import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class eliminarTren extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String idTren = request.getParameter("idTren");

        // 1) Borrar las filas de DatosSensor (hijos) primero
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            String sqlDelDatos = "DELETE FROM DatosSensor WHERE ID_Tren = " + idTren;
            System.out.println("SQL: " + sqlDelDatos);
            stmt.executeUpdate(sqlDelDatos);

            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        // 2) Borrar la fila del tren en Trenes
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            String sqlDelTren = "DELETE FROM Trenes WHERE ID_Tren = " + idTren;
            System.out.println("SQL: " + sqlDelTren);
            stmt.executeUpdate(sqlDelTren);

            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        

        response.sendRedirect("Inicio");
    }
}