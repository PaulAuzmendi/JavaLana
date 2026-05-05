import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class verSensor extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idSensor = request.getParameter("idSensor");
        String idTren   = request.getParameter("idTren");
        String nombreSensor = "";

        // ===== Consulta el nombre del sensor =====
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String rutaRelativa = "/Trenes2.accdb"; 
            String rutaAbsoluta = getServletContext().getRealPath(rutaRelativa);
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            // Step 4
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("Select Nombre_Sensor from Sensores where ID_Sensor = " + idSensor);
            if (rs.next()) {
                nombreSensor = rs.getString("Nombre_Sensor");
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
        out.println("<title>Datos del Sensor " + idSensor + "</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>TREN #" + idTren + " &middot; " + nombreSensor.toUpperCase() + "</p>");
        out.println("  <div class='section-title'>Datos del Sensor " + idSensor + ": "+nombreSensor+" </div>");
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
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String rutaRelativa = "/Trenes2.accdb"; 
            String rutaAbsoluta = getServletContext().getRealPath(rutaRelativa);
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            // Step 4
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();
            String orden = request.getParameter("orden");
            if (orden == null) orden = "ID_Datos";
            ResultSet rs = stmt.executeQuery(
                "Select * from DatosSensor where ID_Tren = " + idTren +
                " and ID_Sensor = " + idSensor + " order by " + orden);
            java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            while (rs.next()) {
                String idDatos     = rs.getString("ID_Datos");
                String idT         = rs.getString("ID_Tren");
                String idS         = rs.getString("ID_Sensor");
                java.sql.Timestamp ts = rs.getTimestamp("FechaHora");
                String fechaHora   = fmt.format(ts);
                String localizacion= rs.getString("Localizacion");
                double valorNum    = rs.getDouble("Dato_valor");
                String valor = String.format(java.util.Locale.US, "%.2f", valorNum);
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
                out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
                out.println("    <input type='hidden' name='idDatos' value='" + idDatos + "'>");
                out.println("    <input type='hidden' name='origen' value='verSensor'>");  
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

        // ===== Botones inferiores =====
        out.println("<div class='action-row'>");

        out.println("  <form action='anadirRegistro' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
        out.println("    <button type='submit' class='action-btn'>Añadir Registro</button>");
        out.println("  </form>");

        out.println("  <form action='ordenarRegistro' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
		out.println("    <input type='hidden' name='origen' value='verSensor'>");  

        out.println("    <button type='submit' class='action-btn'>Reordenar</button>");
        out.println("  </form>");

        out.println("  <form action='grafico' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
        out.println("    <button type='submit' class='action-btn'>Dibujar Gráfico</button>");
        out.println("  </form>");

        out.println("</div>");
        out.println("<div class='action-row'>");
        out.println("  <form action='verTren' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <button type='submit' class='action-btn'>Volver</button>");
        out.println("  </form>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}