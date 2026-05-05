import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ordenarTren extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Reordenar Trenes</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <div class='section-title'>Ordenar por:</div>");
        out.println("</header>");

        // ===== Botones de ordenación =====
        out.println("<div class='action-row'>");

        out.println("  <form action='Inicio' method='get'>");
        out.println("    <input type='hidden' name='orden' value='ID_Tren'>");
        out.println("    <button type='submit' class='action-btn'>ID Tren</button>");
        out.println("  </form>");

        out.println("  <form action='Inicio' method='get'>");
        out.println("    <input type='hidden' name='orden' value='Modelo'>");
        out.println("    <button type='submit' class='action-btn'>Modelo</button>");
        out.println("  </form>");

        out.println("  <form action='Inicio' method='get'>");
        out.println("    <input type='hidden' name='orden' value='Fecha_Creacion'>");
        out.println("    <button type='submit' class='action-btn'>Fecha de Creación</button>");
        out.println("  </form>");

        out.println("  <form action='Inicio' method='get'>");
        out.println("    <input type='hidden' name='orden' value='Fecha_Ultima_Revision'>");
        out.println("    <button type='submit' class='action-btn'>Fecha última Revisión</button>");
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