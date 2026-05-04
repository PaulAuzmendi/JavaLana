import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class lista extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren = request.getParameter("idTren"); // opcional, para el botón volver

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Lista de Sensores</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>Inventario completo de sensores</p>");
        out.println("  <div class='section-title'>Lista de Sensores</div>");
        out.println("</header>");

        // ===== Tabla de sensores =====
        out.println("<div class='table-wrapper'>");
        out.println("<table class='trenes-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>ID Sensor</th>");
        out.println("<th>Nombre</th>");
        out.println("<th>Tipo</th>");
        out.println("<th>Estado</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        int total = 0;

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM Sensores ORDER BY ID_Sensor");

            while (rs.next()) {
                total++;
                String idSensor     = rs.getString("ID_Sensor");
                String nombreSensor = rs.getString("Nombre_Sensor");
                String tipoSensor   = rs.getString("Tipo_Sensor");
                String estado       = rs.getString("Estado");

                // Color del badge de estado
                String estadoColor = "Activo".equalsIgnoreCase(estado) ? "#4dd0e1" : "#ff8080";

                out.println("<tr>");
                out.println("<td class='col-id'>" + idSensor + "</td>");
                out.println("<td>" + nombreSensor + "</td>");
                out.println("<td>" + tipoSensor + "</td>");
                out.println("<td><span style='color:" + estadoColor + ";letter-spacing:1.5px;text-transform:uppercase;font-size:0.85em'>" + estado + "</span></td>");
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

        // ===== Total =====
        out.println("<div class='action-row'>");
        out.println("  <span style='font-family:Chakra Petch,sans-serif;color:#6c7a93;letter-spacing:1.5px'>Total: <strong style='color:#4dd0e1'>" + total + "</strong> sensores</span>");
        out.println("</div>");

        // ===== Botón volver =====
        out.println("<div class='action-row'>");
        if (idTren != null && !idTren.isEmpty()) {
            out.println("  <form action='verDatos' method='get'>");
            out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
            out.println("    <button type='submit' class='action-btn'>Volver al Tren</button>");
            out.println("  </form>");
        } else {
            out.println("  <form action='index.html' method='get'>");
            out.println("    <button type='submit' class='action-btn'>Volver al Panel</button>");
            out.println("  </form>");
        }
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
