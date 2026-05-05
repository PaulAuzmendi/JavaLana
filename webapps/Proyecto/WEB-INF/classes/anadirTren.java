import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class anadirTren extends HttpServlet {

    /** GET → muestra el formulario vacío */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        printForm(response.getWriter(), null, null);
    }

    /** POST → inserta el nuevo tren y confirma */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String modelo        = request.getParameter("modelo");
        String fechaCreacion = request.getParameter("fechaCreacion");
        String fechaRevision = request.getParameter("fechaRevision");

        if (modelo == null || modelo.trim().isEmpty()) {
            printForm(out, "El modelo es obligatorio.", null);
            return;
        }

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);

            // Calcular nuevo ID
            Statement stmtMax = connection.createStatement();
            ResultSet rsMax = stmtMax.executeQuery("SELECT MAX(ID_Tren) AS MaxID FROM Trenes");
            int nuevoId = 1;
            if (rsMax.next()) nuevoId = rsMax.getInt("MaxID") + 1;
            rsMax.close();
            stmtMax.close();

            // Parsear fechas (formato esperado: yyyy-MM-dd)
            java.text.SimpleDateFormat fmt    = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.text.SimpleDateFormat fmtAcc = new java.text.SimpleDateFormat("MM/dd/yyyy");
            String fcAcc = fmtAcc.format(fmt.parse(fechaCreacion));
            String frAcc = fmtAcc.format(fmt.parse(fechaRevision));

            String sql = "INSERT INTO Trenes (ID_Tren, Modelo, Fecha_Creacion, Fecha_Ultima_Revision) " +
                         "VALUES (" + nuevoId + ", '" + modelo.trim() + "', #" + fcAcc + "#, #" + frAcc + "#)";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            connection.close();

            printForm(out, null, "Tren #" + nuevoId + " (" + modelo.trim() + ") añadido correctamente.");

        } catch (Exception e) {
            printForm(out, "Error al guardar: " + e.getMessage(), null);
        }
    }

    // ──────────────────────────────────────────────────────────────

    private void printForm(PrintWriter out, String error, String success) {
        String hoy = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Añadir Tren</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head><body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <div class='section-title'>Añadir Nuevo Tren</div>");
        out.println("</header>");

        if (error != null)
            out.println("<div class='action-row'><p class='msg-error'>" + error + "</p></div>");
        if (success != null)
            out.println("<div class='action-row'><p class='msg-ok'>" + success + "</p></div>");

        out.println("<div class='form-wrapper'>");
        out.println("  <form action='anadirTren' method='post'>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='modelo'>Modelo del Tren</label>");
        out.println("      <input type='text' id='modelo' name='modelo' placeholder='Ej: CAF Serie 100' required>");
        out.println("    </div>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='fechaCreacion'>Fecha de Creación</label>");
        out.println("      <input type='date' id='fechaCreacion' name='fechaCreacion' value='" + hoy + "' required>");
        out.println("    </div>");

        out.println("    <div class='form-group'>");
        out.println("      <label for='fechaRevision'>Fecha Última Revisión</label>");
        out.println("      <input type='date' id='fechaRevision' name='fechaRevision' value='" + hoy + "' required>");
        out.println("    </div>");

        out.println("    <div class='action-row'>");
        out.println("      <button type='submit' class='action-btn'>Guardar Tren</button>");
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
}
