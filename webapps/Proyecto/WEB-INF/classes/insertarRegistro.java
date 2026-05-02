import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class insertarRegistro extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String idTren       = request.getParameter("idTren");
        String idSensor     = request.getParameter("idSensor");
        String fechaHora    = request.getParameter("fechaHora");      // "2026-04-30T14:27"
        String localizacion = request.getParameter("localizacion");
        String valor        = request.getParameter("valor");
        String unidad       = request.getParameter("unidad");

        // Convertir el formato datetime-local de HTML al formato que entiende Access:
        // "2026-04-30T14:27"  ->  #2026-04-30 14:27:00#
        String fechaAccess = "#" + fechaHora.replace("T", " ") + ":00#";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String rutaRelativa = "/Trenes2.accdb"; 
            String rutaAbsoluta = getServletContext().getRealPath(rutaRelativa);
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            // Step 4
            Connection connection = DriverManager.getConnection(url);

            // ===== DIAGNOSTICO =====
            System.out.println("==========================================");
            System.out.println("Conexion read-only? " + connection.isReadOnly());
            System.out.println("DB URL: " + connection.getMetaData().getURL());
            System.out.println("DB Product: " + connection.getMetaData().getDatabaseProductName() + " " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName() + " " + connection.getMetaData().getDriverVersion());
            try { connection.setReadOnly(false); } catch (Exception ex) { System.out.println("setReadOnly fallo: " + ex.getMessage()); }
            System.out.println("Read-only despues de forzar: " + connection.isReadOnly());
            // =======================

            Statement stmt = connection.createStatement();

            // INSERT - ID_Datos lo genera Access automáticamente (autonumérico)
            String sql = "INSERT INTO DatosSensor "
                       + "(ID_Tren, ID_Sensor, FechaHora, Localizacion, Dato_valor, Unidad_medida) "
                       + "VALUES ("
                       + idTren + ", "
                       + idSensor + ", "
                       + fechaAccess + ", "
                       + "'" + localizacion + "', "
                       + valor + ", "
                       + "'" + unidad + "')";
            System.out.println("SQL: " + sql);
            stmt.executeUpdate(sql);

            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        // Volver a la página del sensor
        response.sendRedirect("verSensor?idTren=" + idTren + "&idSensor=" + idSensor);
    }
}