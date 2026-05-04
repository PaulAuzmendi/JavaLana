import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class anadirRegistro extends HttpServlet {

    /** GET → muestra el formulario de alta */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren   = request.getParameter("idTren");
        String idSensor = request.getParameter("idSensor");

        String modelo       = "";
        String nombreSensor = "";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            if (idTren != null) {
                ResultSet rsT = stmt.executeQuery("SELECT Modelo FROM Trenes WHERE ID_Tren = " + idTren);
                if (rsT.next()) modelo = rsT.getString("Modelo");
                rsT.close();
            }
            if (idSensor != null) {
                ResultSet rsS = stmt.executeQuery("SELECT Nombre_Sensor FROM Sensores WHERE ID_Sensor = " + idSensor);
                if (rsS.next()) nombreSensor = rsS.getString("Nombre_Sensor");
                rsS.close();
            }

            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        printForm(out, idTren, idSensor, modelo, nombreSensor, null, null);
    }

    /** POST → inserta el nuevo registro */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren       = request.getParameter("idTren");
        String idSensor     = request.getParameter("idSensor");
        String localizacion = request.getParameter("localizacion");
        String valorStr     = request.getParameter("valor");
        String unidad       = request.getParameter("unidad");
        String fecha        = request.getParameter("fecha"); // "yyyy-MM-dd HH:mm:ss"

        String modelo       = "";
        String nombreSensor = "";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmtNombre = connection.createStatement();

            ResultSet rsT = stmtNombre.executeQuery("SELECT Modelo FROM Trenes WHERE ID_Tren = " + idTren);
            if (rsT.next()) modelo = rsT.getString("Modelo");
            rsT.close();
            ResultSet rsS = stmtNombre.executeQuery("SELECT Nombre_Sensor FROM Sensores WHERE ID_Sensor = " + idSensor);
            if (rsS.next()) nombreSensor = rsS.getString("Nombre_Sensor");
            rsS.close();
            stmtNombre.close();

            // Generar nuevo ID_Datos
            Statement stmtMax = connection.createStatement();
            ResultSet rsMax = stmtMax.executeQuery("SELECT MAX(ID_Datos) AS MaxID FROM DatosSensor");
            int nuevoId = 1;
            if (rsMax.next()) nuevoId = rsMax.getInt("MaxID") + 1;
            rsMax.close();
            stmtMax.close();

            // Parsear fecha y valor
            Timestamp ts;
            try {
                java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ts = new Timestamp(fmt.parse(fecha).getTime());
            } catch (Exception ex) {
                ts = new Timestamp(System.currentTimeMillis());
            }
            double valor = Double.parseDouble(valorStr.replace(",", "."));

            // Insertar
            String sql = "INSERT INTO DatosSensor (ID_Datos, ID_Tren, ID_Sensor, FechaHora, Localizacion, Dato_valor, Unidad_medida) " +
                         "VALUES (" + nuevoId + ", " + idTren + ", " + idSensor + ", #" +
                         new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(ts) +
                         "#, '" + localizacion + "', " +
                         String.format(java.util.Locale.US, "%.4f", valor) + ", '" + unidad + "')";

            Statement stmtIns = connection.createStatement();
            stmtIns.executeUpdate(sql);
            stmtIns.close();
            connection.close();

            printForm(out, idTren, idSensor, modelo, nombreSensor,
                      null, "Registro #" + nuevoId + " añadido correctamente.");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // ──────────────────────────────────────────────────────────────

    private void printForm(PrintWriter out, String idTren, String idSensor,
            String modelo, String nombreSensor, String error, String success) {

        // Fecha/hora actual por defecto en el campo
        String ahora = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date());

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Añadir Registro</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>TREN #" + idTren + " &middot; " + modelo.toUpperCase() + "</p>");
        out.println("  <div class='section-title'>Añadir Registro &mdash; " + nombreSensor.toUpperCase() + "</div>");
        out.println("</header>");

        if (error != null) {
            out.println("<div class='action-row'>");
            out.println("  <p style='color:#ff8080;letter-spacing:1px;font-family:Chakra Petch,sans-serif'>" + error + "</p>");
            out.println("</div>");
        }
        if (success != null) {
            out.println("<div class='action-row'>");
            out.println("  <p style='color:#4dd0e1;letter-spacing:1px;font-family:Chakra Petch,sans-serif'>" + success + "</p>");
            out.println("</div>");
        }

        out.println("<div class='form-wrapper'>");
        out.println("  <form action='anadirRegistro' method='post'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='fecha'>Fecha y Hora (yyyy-MM-dd HH:mm:ss)</label>");
        out.println("      <input type='text' id='fecha' name='fecha' value='" + ahora + "' required>");
        out.println("    </div>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='localizacion'>Localización</label>");
        out.println("      <input type='text' id='localizacion' name='localizacion' placeholder='Ej: Motor' required>");
        out.println("    </div>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='valor'>Valor</label>");
        out.println("      <input type='text' id='valor' name='valor' placeholder='Ej: 42.5' required>");
        out.println("    </div>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='unidad'>Unidad de medida</label>");
        out.println("      <input type='text' id='unidad' name='unidad' placeholder='Ej: Celsius, km/h, bool' required>");
        out.println("    </div>");

        out.println("    <div class='action-row'>");
        out.println("      <button type='submit' class='action-btn'>Guardar Registro</button>");
        out.println("    </div>");

        out.println("  </form>");
        out.println("</div>");

        out.println("<div class='action-row'>");
        out.println("  <form action='verSensor' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
        out.println("    <button type='submit' class='action-btn'>Volver al Sensor</button>");
        out.println("  </form>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
