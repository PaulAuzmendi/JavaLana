import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Inicio extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter(); //Alwais same yez
        
        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Software de Gestión de Datos de Sensores para Trenes</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("<h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("<div class='section-title'>Lista de Trenes</div>");
        out.println("</header>");

        out.println("<div class='table-wrapper'>");
        out.println("<table class='trenes-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>ID Tren</th>");
        out.println("<th>Modelo</th>");
        out.println("<th>Fecha Creación</th>");
        out.println("<th>Fecha última Revisión</th>");
        out.println("<th class='col-acciones'></th>");
        out.println("<th class='col-acciones'></th>");
        out.println("<th class='col-acciones'></th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        try {
            // Step 3
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String rutaRelativa = "/Trenes2.accdb"; 
            String rutaAbsoluta = getServletContext().getRealPath(rutaRelativa);
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            
            //
            Statement stmt = connection.createStatement();
            
            // Chekea el orden y asigna ID si no hay ninguno
            String orden = request.getParameter("orden");
            if (orden == null) orden = "ID_Tren";
            
            ResultSet rs = stmt.executeQuery("Select * from Trenes order by " + orden); //For ordenar.trenes

            
            // 
            while (rs.next())  //para cada fila 
                String idTren        = rs.getString("ID_Tren");
                String modelo        = rs.getString("Modelo");
                String fechaCreacion = rs.getDate("Fecha_Creacion").toString(); //.toString para que se pueda leer el formato
                String fechaRevision = rs.getDate("Fecha_Ultima_Revision").toString();

                out.println("<tr>"); //Table Row y <td> Table data
                out.println("<td class='col-id'>" + idTren + "</td>");
                out.println("<td>" + modelo + "</td>"); 
                out.println("<td>" + fechaCreacion + "</td>");
                out.println("<td>" + fechaRevision + "</td>");
                out.println("<td class='col-acciones'>");
                out.println("  <form action='verTren' method='get'>"); //boton con el metoodo get dirigido a verTren
                out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>"); //envia el dato 
                out.println("    <button type='submit' class='action-btn action-btn-sm'>Ver</button>"); //submit pa ejecutar accion 
                out.println("  </form>");
                out.println("</td>");
                out.println("<td class='col-acciones'>");
                out.println("  <form action='editarTren' method='get'>");
                out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
                out.println("    <button type='submit' class='action-btn action-btn-sm'>Editar</button>");
                out.println("  </form>");
                out.println("</td>");
                out.println("<td class='col-acciones'>");
                out.println("  <form action='eliminarTren' method='get'>");
                out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
                out.println("    <button type='submit' class='action-btn action-btn-sm'>Eliminar</button>");
                out.println("  </form>");
                out.println("</td>");
                out.println("</tr>");
            }

            // Step 8
            rs.close();
            stmt.close();
            // Step 9
            connection.close();

        } catch (Exception e) { //el error
            out.println("<tr><td colspan='5' class='tabla-error'>Error: " + e.getMessage() + "</td></tr>");
            e.printStackTrace();
        }

        out.println("</tbody>");
        out.println("</table>");
        out.println("</div>");

        out.println("<div class='action-row'>");
        out.println("  <form action='anadirTren' method='get'>");
        out.println("    <button type='submit' class='action-btn'>Añadir Tren</button>");
        out.println("  </form>");
        out.println("  <form action='ordenarTren' method='get'>");
        out.println("    <button type='submit' class='action-btn'>Reordenar lista</button>");
        out.println("  </form>");
        out.println("</div>");


        out.println("</body>");
        out.println("</html>");
    }
}
