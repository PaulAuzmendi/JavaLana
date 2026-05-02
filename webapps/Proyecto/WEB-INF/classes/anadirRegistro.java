import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class anadirRegistro extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren   = request.getParameter("idTren");
        String idSensor = request.getParameter("idSensor");

        String nombreSensor = "";
        String tipoSensor   = "";
        String localizacion = "";
        String unidad       = "";

        // ===== Consulta info del sensor y los valores por defecto =====
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String rutaRelativa = "/Trenes2.accdb"; 
            String rutaAbsoluta = getServletContext().getRealPath(rutaRelativa);
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            // Step 4
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            // Nombre y tipo del sensor
            String sql1 = "Select * from Sensores where ID_Sensor = " + idSensor;
            ResultSet rs = stmt.executeQuery(sql1);
            if (rs.next()) {
                nombreSensor = rs.getString("Nombre_Sensor");
                tipoSensor   = rs.getString("Tipo_Sensor");
            }
            rs.close();

            // Localizacion y Unidad por defecto: las del último registro de ese sensor
            String sql2 = "Select * from DatosSensor where ID_Sensor = " + idSensor;
            ResultSet rs2 = stmt.executeQuery(sql2);
            if (rs2.next()) {
                localizacion = rs2.getString("Localizacion");
                unidad       = rs2.getString("Unidad_medida");
            }
            rs2.close();

            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        boolean esBinario = tipoSensor.equalsIgnoreCase("Lidar") || tipoSensor.equalsIgnoreCase("Radar");

        // ===== HTML =====
        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Añadir Registro - " + nombreSensor + "</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>TREN #" + idTren + " &middot; " + nombreSensor.toUpperCase() + "</p>");
        out.println("  <div class='section-title'>Añadir Registro</div>");
        out.println("</header>");

        // ===== Formulario =====
        out.println("<div class='form-container'>");
        out.println("  <form action='insertarRegistro' method='get'>");

        // Hidden: los datos automáticos
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
        out.println("    <input type='hidden' name='localizacion' value='" + localizacion + "'>");
        out.println("    <input type='hidden' name='unidad' value='" + unidad + "'>");

        // Info de los campos automáticos para que el usuario los vea
        out.println("    <div class='form-info'>");
        out.println("      <div>ID Tren: <span>" + idTren + "</span></div>");
        out.println("      <div>ID Sensor: <span>" + idSensor + "</span></div>");
        out.println("      <div>Localización: <span>" + localizacion + "</span></div>");
        out.println("      <div>Unidad de medida: <span>" + unidad + "</span></div>");
        out.println("    </div>");

        // Fecha y hora
        out.println("    <div class='form-field'>");
        out.println("      <label>Fecha y Hora</label>");
        out.println("      <input type='datetime-local' name='fechaHora' required>");
        out.println("    </div>");

        // Valor
        out.println("    <div class='form-field'>");
        out.println("      <label>Valor</label>");

        if (esBinario) {
            // Sensor Lidar/Radar -> dos botones 0 y 1
            out.println("      <div class='binary-buttons'>");
            out.println("        <button type='submit' name='valor' value='0' class='action-btn'>0</button>");
            out.println("        <button type='submit' name='valor' value='1' class='action-btn'>1</button>");
            out.println("      </div>");
            out.println("    </div>");
        } else {
            // Sensor normal -> caja de texto numérica
            out.println("      <input type='number' step='0.0001' name='valor' required>");
            out.println("    </div>");
            // Botón submit normal
            out.println("    <div class='action-row' style='margin-top:10px;'>");
            out.println("      <button type='submit' class='action-btn'>Añadir Registro</button>");
            out.println("    </div>");
        }

        out.println("  </form>");
        out.println("</div>");

        // ===== Botón volver =====
        out.println("<div class='action-row'>");
        out.println("  <form action='verSensor' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
        out.println("    <button type='submit' class='action-btn'>Cancelar</button>");
        out.println("  </form>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}