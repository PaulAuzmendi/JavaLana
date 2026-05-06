import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class anadirTren extends HttpServlet{ //This part always the same 
    public void doGet(HttpServletRequest request, HttpServletResponse response) //Para acceder al servlet
        throws ServletException, IOException {

        PrintWriter out = response.getWriter(); //Para preparar la salida que es lo que se mandara a el html

        out.println("<!DOCTYPE html>");  // esta y abajo permite que entienda que abrimos una web
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>"); //Para añadir tildes
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>"); //Realmente solo para movil
        out.println("<title>Añadir Tren</title>"); //Añade un titulo en la pagina
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");// todo lo del estilo y el preconnect 
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>"); 
        out.println("</head>"); //cerramos cabecera
        out.println("<body>"); //Abre el cuerpo

        out.println("<header>"); //Titulos o logos
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>"); // H1 titulo
        out.println("  <div class='section-title'>Añadir Nuevo Tren</div>"); //div contenedor generico 
        out.println("</header>");
    
       // ===== Formulario =====
            out.println("<div class='form-container'>");
            out.println("  <form action='insertarTren' method='get'>");
    
            // Modelo
            out.println("    <div class='form-field'>");
            out.println("      <label>Modelo</label>");
            out.println("      <input type='text' name='modelo' required>"); //name lo que luego recupero 
            out.println("    </div>");
    
            // Fecha Creacion
            out.println("    <div class='form-field'>");
            out.println("      <label>Fecha de Creación</label>");
            out.println("      <input type='date' name='fechaCreacion' required>");
            out.println("    </div>");
    
            // Fecha Ultima Revision
            out.println("    <div class='form-field'>");
            out.println("      <label>Fecha última Revisión</label>");
            out.println("      <input type='date' name='fechaRevision' required>");
            out.println("    </div>");
    
            // Submit
            out.println("    <div class='action-row' style='margin-top:10px;'>");
            out.println("      <button type='submit' class='action-btn'>Añadir Tren</button>");
            out.println("    </div>");
    
            out.println("  </form>");
            out.println("</div>");

        // ===== Boton cancelar =====
        out.println("<div class='action-row'>");
        out.println("  <form action='Inicio' method='get'>");
        out.println("    <button type='submit' class='action-btn'>Cancelar</button>");
        out.println("  </form>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
