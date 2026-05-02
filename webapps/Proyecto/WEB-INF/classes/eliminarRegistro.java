import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class eliminarRegistro extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String idTren   = request.getParameter("idTren");
        String idSensor = request.getParameter("idSensor");
        String idDatos  = request.getParameter("idDatos");
        String origen   = request.getParameter("origen");

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String rutaRelativa = "/Trenes2.accdb";
            String rutaAbsoluta = getServletContext().getRealPath(rutaRelativa);
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            String sql = "DELETE FROM DatosSensor WHERE ID_Datos = " + idDatos;
            System.out.println("SQL: " + sql);
            stmt.executeUpdate(sql);

            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        // Volver a la página correspondiente
        if (origen.equals("verDatos")) {
            response.sendRedirect("verDatos?id=" + idTren);
        } else {
            response.sendRedirect("verSensor?idTren=" + idTren + "&idSensor=" + idSensor);
        }
    }
}