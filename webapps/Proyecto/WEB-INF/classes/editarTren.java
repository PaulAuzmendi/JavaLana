import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class editarTren extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
            
        String idTren = request.getParameter("idTren");  //nos viene dado
        //declara variables vacias
        String modelo        = "";
        String fechaCreacion = "";
        String fechaRevision = "";

        // ===== Consulta los datos actuales del tren =====
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
                fechaCreacion = rs.getDate("Fecha_Creacion").toString();   // yyyy-MM-dd
                fechaRevision = rs.getDate("Fecha_Ultima_Revision").toString();
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
        out.println("<title>Editar Tren #" + idTren + "</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>TREN #" + idTren + " &middot; " + modelo.toUpperCase() + "</p>");
        out.println("  <div class='section-title'>Editar Tren</div>");
        out.println("</header>");

        // ===== Formulario =====
        out.println("<div class='form-container'>");
        out.println("  <form action='actualizarTren' method='get'>");

        // Hidden: id del tren (no se cambia)
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");

        // Modelo
        out.println("    <div class='form-field'>");
        out.println("      <label>Modelo</label>");
        out.println("      <input type='text' name='modelo' value='" + modelo + "' required>");
        out.println("    </div>");

        // Fecha Creacion
        out.println("    <div class='form-field'>");
        out.println("      <label>Fecha de Creación</label>");
        out.println("      <input type='date' name='fechaCreacion' value='" + fechaCreacion + "' required>");
        out.println("    </div>");

        // Fecha Ultima Revision
        out.println("    <div class='form-field'>");
        out.println("      <label>Fecha Última Revisión</label>");
        out.println("      <input type='date' name='fechaRevision' value='" + fechaRevision + "' required>");
        out.println("    </div>");

        // Submit
        out.println("    <div class='action-row' style='margin-top:10px;'>");
        out.println("      <button type='submit' class='action-btn'>Guardar Cambios</button>");
        out.println("    </div>");

        out.println("  </form>");
        out.println("</div>");

        // ===== Botón cancelar =====
        out.println("<div class='action-row'>");
        out.println("  <form action='Inicio' method='get'>");
        out.println("    <button type='submit' class='action-btn'>Cancelar</button>");
        out.println("  </form>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
