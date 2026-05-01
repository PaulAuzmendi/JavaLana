import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class verDatos extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren = request.getParameter("id");
        String modelo = "";

        // ===== Consulta el modelo del tren =====
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            Connection connection = DriverManager.getConnection("jdbc:odbc:Trenes2");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("Select Modelo from Trenes where ID_Tren = " + idTren);
            if (rs.next()) {
                modelo = rs.getString("Modelo");
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        // ===== HTML =====
        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Todos los Datos - Tren " + idTren + "</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>TREN #" + idTren + " &middot; " + modelo.toUpperCase() + "</p>");
        out.println("  <div class='section-title'>Todos los Datos del Tren</div>");
        out.println("</header>");

        // ===== Tabla de datos =====
        out.println("<div class='table-wrapper'>");
        out.println("<table class='trenes-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>ID Datos</th>");
        out.println("<th>ID Tren</th>");
        out.println("<th>ID Sensor</th>");
        out.println("<th>Fecha y Hora</th>");
        out.println("<th>Localización</th>");
        out.println("<th>Valor</th>");
        out.println("<th>Unidad</th>");
        out.println("<th class='col-acciones'></th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            Connection connection = DriverManager.getConnection("jdbc:odbc:Trenes2");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "Select * from DatosSensor where ID_Tren = " + idTren + " order by ID_Datos");

            while (rs.next()) {
                String idDatos     = rs.getString("ID_Datos");
                String idT         = rs.getString("ID_Tren");
                String idS         = rs.getString("ID_Sensor");
                String fechaHora   = rs.getString("FechaHora");
                String localizacion= rs.getString("Localizacion");
                String valor       = rs.getString("Dato_valor");
                String unidad      = rs.getString("Unidad_medida");

                out.println("<tr>");
                out.println("<td class='col-id'>" + idDatos + "</td>");
                out.println("<td>" + idT + "</td>");
                out.println("<td>" + idS + "</td>");
                out.println("<td>" + fechaHora + "</td>");
                out.println("<td>" + localizacion + "</td>");
                out.println("<td>" + valor + "</td>");
                out.println("<td>" + unidad + "</td>");
                out.println("<td class='col-acciones'>");
                out.println("  <form action='eliminarRegistro' method='get'>");
                out.println("    <input type='hidden' name='idTren' value='" + idT + "'>");
                out.println("    <input type='hidden' name='idSensor' value='" + idS + "'>");
                out.println("    <input type='hidden' name='idDatos' value='" + idDatos + "'>");
                out.println("    <button type='submit' class='action-btn action-btn-sm'>Eliminar</button>");
                out.println("  </form>");
                out.println("</td>");
                out.println("</tr>");
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        out.println("</tbody>");
        out.println("</table>");
        out.println("</div>");

        // ===== Botón volver =====
        out.println("<div class='action-row'>");
        out.println("  <form action='verTren' method='get'>");
        out.println("    <input type='hidden' name='id' value='" + idTren + "'>");
        out.println("    <button type='submit' class='action-btn'>Volver</button>");
        out.println("  </form>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}