import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class editarRegistro extends HttpServlet {

    /** GET → carga el registro y muestra el formulario de edición */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren   = request.getParameter("idTren");
        String idSensor = request.getParameter("idSensor");
        String idDatos  = request.getParameter("idDatos");
        String origen   = request.getParameter("origen");
        if (origen == null) origen = "verDatos";

        // Si no viene idDatos, mostrar formulario de búsqueda
        if (idDatos == null || idDatos.trim().isEmpty()) {
            printBuscador(out, idTren, idSensor, origen, null);
            return;
        }

        String modelo       = "";
        String nombreSensor = "";
        String fechaHora    = "";
        String localizacion = "";
        String valor        = "";
        String unidad       = "";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            ResultSet rsT = stmt.executeQuery("SELECT Modelo FROM Trenes WHERE ID_Tren = " + idTren);
            if (rsT.next()) modelo = rsT.getString("Modelo");
            rsT.close();

            ResultSet rsS = stmt.executeQuery("SELECT Nombre_Sensor FROM Sensores WHERE ID_Sensor = " + idSensor);
            if (rsS.next()) nombreSensor = rsS.getString("Nombre_Sensor");
            rsS.close();

            ResultSet rs = stmt.executeQuery("SELECT * FROM DatosSensor WHERE ID_Datos = " + idDatos);
            if (rs.next()) {
                java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                fechaHora    = fmt.format(rs.getTimestamp("FechaHora"));
                localizacion = rs.getString("Localizacion");
                valor        = String.format(java.util.Locale.US, "%.4f", rs.getDouble("Dato_valor"));
                unidad       = rs.getString("Unidad_medida");
            } else {
                printBuscador(out, idTren, idSensor, origen, "No existe ningún registro con ID " + idDatos);
                stmt.close(); connection.close();
                return;
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        printFormEdicion(out, idTren, idSensor, idDatos, origen,
            modelo, nombreSensor, fechaHora, localizacion, valor, unidad, null, null);
    }

    /** POST → aplica los cambios */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren       = request.getParameter("idTren");
        String idSensor     = request.getParameter("idSensor");
        String idDatos      = request.getParameter("idDatos");
        String origen       = request.getParameter("origen");
        String localizacion = request.getParameter("localizacion");
        String valorStr     = request.getParameter("valor");
        String unidad       = request.getParameter("unidad");
        String fecha        = request.getParameter("fecha");

        String modelo       = "";
        String nombreSensor = "";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmtNom = connection.createStatement();

            ResultSet rsT = stmtNom.executeQuery("SELECT Modelo FROM Trenes WHERE ID_Tren = " + idTren);
            if (rsT.next()) modelo = rsT.getString("Modelo");
            rsT.close();
            ResultSet rsS = stmtNom.executeQuery("SELECT Nombre_Sensor FROM Sensores WHERE ID_Sensor = " + idSensor);
            if (rsS.next()) nombreSensor = rsS.getString("Nombre_Sensor");
            rsS.close();
            stmtNom.close();

            Timestamp ts;
            try {
                java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ts = new Timestamp(fmt.parse(fecha).getTime());
            } catch (Exception ex) {
                ts = new Timestamp(System.currentTimeMillis());
            }
            double valor = Double.parseDouble(valorStr.replace(",", "."));
            String fechaAccess = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(ts);

            String sql = "UPDATE DatosSensor SET " +
                         "FechaHora = #" + fechaAccess + "#, " +
                         "Localizacion = '" + localizacion + "', " +
                         "Dato_valor = " + String.format(java.util.Locale.US, "%.4f", valor) + ", " +
                         "Unidad_medida = '" + unidad + "' " +
                         "WHERE ID_Datos = " + idDatos;
            Statement stmtUpd = connection.createStatement();
            stmtUpd.executeUpdate(sql);
            stmtUpd.close();
            connection.close();

            printFormEdicion(out, idTren, idSensor, idDatos, origen,
                modelo, nombreSensor, fecha, localizacion,
                String.format(java.util.Locale.US, "%.4f", valor), unidad,
                null, "Registro #" + idDatos + " actualizado correctamente.");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // ──────────────────────────────────────────────────────────────

    private void printBuscador(PrintWriter out, String idTren, String idSensor,
            String origen, String error) throws IOException {

        printHeader(out, idTren, "", "", "Editar Registro");

        if (error != null)
            out.println("<div class='action-row'><p style='color:#ff8080;letter-spacing:1px;font-family:Chakra Petch,sans-serif'>" + error + "</p></div>");

        out.println("<div class='form-wrapper'>");
        out.println("  <form action='editarRegistro' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
        out.println("    <input type='hidden' name='origen' value='" + origen + "'>");
        out.println("    <div class='form-group'>");
        out.println("      <label for='idDatos'>ID del Registro a editar</label>");
        out.println("      <input type='number' id='idDatos' name='idDatos' placeholder='Ej: 12' min='1' required>");
        out.println("    </div>");
        out.println("    <div class='action-row'>");
        out.println("      <button type='submit' class='action-btn'>Buscar Registro</button>");
        out.println("    </div>");
        out.println("  </form>");
        out.println("</div>");
        printVolver(out, idTren, idSensor, origen);
        out.println("</body></html>");
    }

    private void printFormEdicion(PrintWriter out, String idTren, String idSensor,
            String idDatos, String origen, String modelo, String nombreSensor,
            String fechaHora, String localizacion, String valor, String unidad,
            String error, String success) throws IOException {

        printHeader(out, idTren, modelo, nombreSensor, "Editar Registro #" + idDatos);

        if (error != null)
            out.println("<div class='action-row'><p style='color:#ff8080;letter-spacing:1px;font-family:Chakra Petch,sans-serif'>" + error + "</p></div>");
        if (success != null)
            out.println("<div class='action-row'><p style='color:#4dd0e1;letter-spacing:1px;font-family:Chakra Petch,sans-serif'>" + success + "</p></div>");

        out.println("<div class='form-wrapper'>");
        out.println("  <form action='editarRegistro' method='post'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
        out.println("    <input type='hidden' name='idDatos' value='" + idDatos + "'>");
        out.println("    <input type='hidden' name='origen' value='" + origen + "'>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='fecha'>Fecha y Hora (yyyy-MM-dd HH:mm:ss)</label>");
        out.println("      <input type='text' id='fecha' name='fecha' value='" + fechaHora + "' required>");
        out.println("    </div>");
        out.println("    <div class='form-group'>");
        out.println("      <label for='localizacion'>Localización</label>");
        out.println("      <input type='text' id='localizacion' name='localizacion' value='" + localizacion + "' required>");
        out.println("    </div>");
        out.println("    <div class='form-group'>");
        out.println("      <label for='valor'>Valor</label>");
        out.println("      <input type='text' id='valor' name='valor' value='" + valor + "' required>");
        out.println("    </div>");
        out.println("    <div class='form-group'>");
        out.println("      <label for='unidad'>Unidad de medida</label>");
        out.println("      <input type='text' id='unidad' name='unidad' value='" + unidad + "' required>");
        out.println("    </div>");

        out.println("    <div class='action-row'>");
        out.println("      <button type='submit' class='action-btn'>Guardar Cambios</button>");
        out.println("    </div>");
        out.println("  </form>");
        out.println("</div>");

        printVolver(out, idTren, idSensor, origen);
        out.println("</body></html>");
    }

    private void printHeader(PrintWriter out, String idTren, String modelo,
            String nombreSensor, String titulo) throws IOException {
        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>" + titulo + "</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        if (!modelo.isEmpty())
            out.println("  <p class='subtitle'>TREN #" + idTren + " &middot; " + modelo.toUpperCase() + "</p>");
        out.println("  <div class='section-title'>" + titulo.toUpperCase() + "</div>");
        out.println("</header>");
    }

    private void printVolver(PrintWriter out, String idTren, String idSensor, String origen) {
        out.println("<div class='action-row'>");
        out.println("  <form action='" + origen + "' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
        out.println("    <button type='submit' class='action-btn'>Volver</button>");
        out.println("  </form>");
        out.println("</div>");
    }
}
