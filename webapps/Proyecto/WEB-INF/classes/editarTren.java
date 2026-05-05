import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class editarTren extends HttpServlet {

    /** GET → carga el tren por idTren y muestra formulario relleno */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren = request.getParameter("idTren");

        if (idTren == null || idTren.trim().isEmpty()) {
            printError(out, "No se especificó el ID del tren.");
            return;
        }

        String modelo        = "";
        String fechaCreacion = "";
        String fechaRevision = "";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM Trenes WHERE ID_Tren = " + idTren);

            if (rs.next()) {
                modelo        = rs.getString("Modelo");
                fechaCreacion = rs.getDate("Fecha_Creacion").toString();    // yyyy-MM-dd
                fechaRevision = rs.getDate("Fecha_Ultima_Revision").toString();
            } else {
                printError(out, "No existe ningún tren con ID " + idTren + ".");
                rs.close(); stmt.close(); connection.close();
                return;
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            printError(out, "Error de base de datos: " + e.getMessage());
            return;
        }

        printForm(out, idTren, modelo, fechaCreacion, fechaRevision, null, null);
    }

    /** POST → aplica el UPDATE */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren        = request.getParameter("idTren");
        String modelo        = request.getParameter("modelo");
        String fechaCreacion = request.getParameter("fechaCreacion");
        String fechaRevision = request.getParameter("fechaRevision");

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);

            java.text.SimpleDateFormat fmt    = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.text.SimpleDateFormat fmtAcc = new java.text.SimpleDateFormat("MM/dd/yyyy");
            String fcAcc = fmtAcc.format(fmt.parse(fechaCreacion));
            String frAcc = fmtAcc.format(fmt.parse(fechaRevision));

            String sql = "UPDATE Trenes SET " +
                         "Modelo = '" + modelo.trim() + "', " +
                         "Fecha_Creacion = #" + fcAcc + "#, " +
                         "Fecha_Ultima_Revision = #" + frAcc + "# " +
                         "WHERE ID_Tren = " + idTren;

            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            connection.close();

            printForm(out, idTren, modelo, fechaCreacion, fechaRevision,
                      null, "Tren #" + idTren + " actualizado correctamente.");

        } catch (Exception e) {
            printForm(out, idTren, modelo, fechaCreacion, fechaRevision,
                      "Error al actualizar: " + e.getMessage(), null);
        }
    }

    // ──────────────────────────────────────────────────────────────

    private void printForm(PrintWriter out, String idTren, String modelo,
            String fechaCreacion, String fechaRevision, String error, String success) {

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Editar Tren #" + idTren + "</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head><body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>TREN #" + idTren + "</p>");
        out.println("  <div class='section-title'>Editar Tren</div>");
        out.println("</header>");

        if (error != null)
            out.println("<div class='action-row'><p class='msg-error'>" + error + "</p></div>");
        if (success != null)
            out.println("<div class='action-row'><p class='msg-ok'>" + success + "</p></div>");

        out.println("<div class='form-wrapper'>");
        out.println("  <form action='editarTren' method='post'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='modelo'>Modelo del Tren</label>");
        out.println("      <input type='text' id='modelo' name='modelo' value='" + esc(modelo) + "' required>");
        out.println("    </div>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='fechaCreacion'>Fecha de Creación</label>");
        out.println("      <input type='date' id='fechaCreacion' name='fechaCreacion' value='" + fechaCreacion + "' required>");
        out.println("    </div>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='fechaRevision'>Fecha Última Revisión</label>");
        out.println("      <input type='date' id='fechaRevision' name='fechaRevision' value='" + fechaRevision + "' required>");
        out.println("    </div>");

        out.println("    <div class='action-row'>");
        out.println("      <button type='submit' class='action-btn'>Guardar Cambios</button>");
        out.println("    </div>");
        out.println("  </form>");
        out.println("</div>");

        out.println("<div class='action-row'>");
        out.println("  <form action='Inicio' method='get'>");
        out.println("    <button type='submit' class='action-btn'>Volver a la Lista</button>");
        out.println("  </form>");
        out.println("</div>");

        out.println("</body></html>");
    }

    private void printError(PrintWriter out, String msg) {
        out.println("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@400;600&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'></head><body>");
        out.println("<header><h1>Software de Gestión de Datos de Sensores para Trenes</h1></header>");
        out.println("<div class='action-row'><p class='msg-error'>" + msg + "</p></div>");
        out.println("<div class='action-row'><form action='Inicio' method='get'>");
        out.println("<button type='submit' class='action-btn'>Volver a la Lista</button></form></div>");
        out.println("</body></html>");
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
