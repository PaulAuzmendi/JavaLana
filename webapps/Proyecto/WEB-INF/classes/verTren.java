import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class verTren extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String id = request.getParameter("idTren");
        String modelo = "";

        // ===== Consulta el modelo del tren =====
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String rutaRelativa = "/Trenes2.accdb"; 
            String rutaAbsoluta = getServletContext().getRealPath(rutaRelativa);
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            // Step 4
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("Select Modelo from Trenes where ID_Tren = " + id);
            if (rs.next()) {
                modelo = rs.getString("Modelo");
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
        out.println("<title>Tren " + id + " - " + modelo + "</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>TREN #" + id + " &middot; " + modelo.toUpperCase() + "</p>");
        out.println("  <div class='section-title'>Sensores Principales</div>");
        out.println("</header>");

        out.println("<button class='toggle-btn' id='toggleBtn'>Vista de Arriba</button>");

        out.println("<div class='train-container'>");

        // ====================== SIDE VIEW ======================
        out.println("<svg class='view active' id='sideView' viewBox='0 0 1200 620' xmlns='http://www.w3.org/2000/svg'>");

        // Cuerpo del tren
        out.println("  <rect class='train-body' x='250' y='240' width='700' height='180' rx='8'/>");
        out.println("  <rect class='train-window' x='290' y='262' width='120' height='78' rx='4'/>");
        out.println("  <rect class='train-window' x='430' y='262' width='120' height='78' rx='4'/>");
        out.println("  <rect class='train-window' x='580' y='250' width='38' height='170'/>");
        out.println("  <rect class='train-window' x='622' y='250' width='38' height='170'/>");
        out.println("  <rect class='train-window' x='690' y='262' width='120' height='78' rx='4'/>");
        out.println("  <rect class='train-window' x='830' y='262' width='120' height='78' rx='4'/>");
        out.println("  <rect class='train-body' x='240' y='420' width='720' height='10'/>");
        out.println("  <circle class='train-wheel' cx='370' cy='450' r='40'/>");
        out.println("  <circle class='train-wheel' cx='830' cy='450' r='40'/>");

        // TEMPERATURA EXTERIOR (ID_Sensor = 3)
        out.println("  <path class='sensor-line' d='M 240 142 L 240 200 L 350 200 L 350 240'/>");
        out.println("  <circle class='sensor-dot' cx='350' cy='240' r='4.5'/>");
        out.println("  <foreignObject x='80' y='100' width='240' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='3'/>");
        out.println("      <button type='submit' class='sensor-btn'>TEMPERATURA EXTERIOR</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // TEMPERATURA CABINA (ID_Sensor = 2)
        out.println("  <path class='sensor-line' d='M 600 142 L 600 200 L 490 200 L 490 300'/>");
        out.println("  <circle class='sensor-dot' cx='490' cy='300' r='4.5'/>");
        out.println("  <foreignObject x='490' y='100' width='220' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='2'/>");
        out.println("      <button type='submit' class='sensor-btn'>TEMPERATURA CABINA</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // TEMPERATURA MOTOR (ID_Sensor = 1)
        out.println("  <path class='sensor-line' d='M 920 142 L 920 220 L 880 220 L 880 410'/>");
        out.println("  <circle class='sensor-dot' cx='880' cy='410' r='4.5'/>");
        out.println("  <foreignObject x='900' y='100' width='220' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='1'/>");
        out.println("      <button type='submit' class='sensor-btn'>TEMPERATURA MOTOR</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // VELOCIMETRO TRASERO (ID_Sensor = 11)
        out.println("  <path class='sensor-line' d='M 360 540 L 360 480 L 370 460'/>");
        out.println("  <circle class='sensor-dot' cx='370' cy='450' r='4.5'/>");
        out.println("  <foreignObject x='170' y='540' width='250' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='11'/>");
        out.println("      <button type='submit' class='sensor-btn'>VELOCIMETRO TRASERO</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // VELOCIMETRO DELANTERO (ID_Sensor = 10)
        out.println("  <path class='sensor-line' d='M 850 540 L 850 480 L 830 460'/>");
        out.println("  <circle class='sensor-dot' cx='830' cy='450' r='4.5'/>");
        out.println("  <foreignObject x='780' y='540' width='270' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='10'/>");
        out.println("      <button type='submit' class='sensor-btn'>VELOCIMETRO DELANTERO</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        out.println("</svg>");

        // ====================== TOP VIEW ======================
        out.println("<svg class='view' id='topView' viewBox='0 0 1200 620' xmlns='http://www.w3.org/2000/svg'>");

        out.println("  <path class='train-body' d='M 450 540 L 750 540 L 750 130 Q 750 85 600 75 Q 450 85 450 130 Z'/>");
        out.println("  <path class='windshield' d='M 470 135 Q 470 102 600 95 Q 730 102 730 135 Z'/>");

        // Side windows left
        out.println("  <rect class='train-window' x='447' y='190' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='447' y='270' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='447' y='380' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='447' y='460' width='10' height='60' rx='2'/>");
        // Side windows right
        out.println("  <rect class='train-window' x='743' y='190' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='743' y='270' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='743' y='380' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='743' y='460' width='10' height='60' rx='2'/>");
        // Doors
        out.println("  <rect class='train-window' x='447' y='345' width='10' height='30'/>");
        out.println("  <rect class='train-window' x='743' y='345' width='10' height='30'/>");

        out.println("  <line class='center-line' x1='600' y1='150' x2='600' y2='535'/>");
        out.println("  <rect x='490' y='200' width='220' height='50' fill='none' stroke='var(--train-stroke)' stroke-width='1' stroke-dasharray='3 3' opacity='0.4' rx='4'/>");
        out.println("  <rect x='490' y='440' width='220' height='50' fill='none' stroke='var(--train-stroke)' stroke-width='1' stroke-dasharray='3 3' opacity='0.4' rx='4'/>");

        // Anchor dots
        out.println("  <circle class='sensor-dot' cx='495' cy='115' r='5'/>");
        out.println("  <circle class='sensor-dot' cx='600' cy='92' r='5'/>");
        out.println("  <circle class='sensor-dot' cx='705' cy='115' r='5'/>");

        // RADAR IZQUIERDO (ID_Sensor = 4)
        out.println("  <path class='sensor-line' d='M 280 176 L 380 176 L 380 130 L 495 115'/>");
        out.println("  <foreignObject x='60' y='155' width='220' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='4'/>");
        out.println("      <button type='submit' class='sensor-btn'>RADAR IZQUIERDO</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // LIDAR IZQUIERDO (ID_Sensor = 7)
        out.println("  <path class='sensor-line' d='M 280 236 L 360 236 L 360 145 L 495 115'/>");
        out.println("  <foreignObject x='60' y='215' width='220' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='7'/>");
        out.println("      <button type='submit' class='sensor-btn'>LIDAR IZQUIERDO</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // RADAR CENTRO (ID_Sensor = 5)
        out.println("  <path class='sensor-line' d='M 560 67 L 600 92'/>");
        out.println("  <foreignObject x='380' y='25' width='200' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='5'/>");
        out.println("      <button type='submit' class='sensor-btn'>RADAR CENTRO</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // LIDAR CENTRO (ID_Sensor = 8)
        out.println("  <path class='sensor-line' d='M 640 67 L 600 92'/>");
        out.println("  <foreignObject x='620' y='25' width='200' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='8'/>");
        out.println("      <button type='submit' class='sensor-btn'>LIDAR CENTRO</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // RADAR DERECHO (ID_Sensor = 6)
        out.println("  <path class='sensor-line' d='M 920 176 L 820 176 L 820 130 L 705 115'/>");
        out.println("  <foreignObject x='920' y='155' width='220' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='6'/>");
        out.println("      <button type='submit' class='sensor-btn'>RADAR DERECHO</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // LIDAR DERECHO (ID_Sensor = 9)
        out.println("  <path class='sensor-line' d='M 920 236 L 840 236 L 840 145 L 705 115'/>");
        out.println("  <foreignObject x='920' y='215' width='220' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='9'/>");
        out.println("      <button type='submit' class='sensor-btn'>LIDAR DERECHO</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        // VELOCIMETRO GPS (ID_Sensor = 12) - centro del tren
        out.println("  <circle class='sensor-dot' cx='600' cy='335' r='5'/>");
        out.println("  <path class='sensor-line' d='M 280 335 L 600 335'/>");
        out.println("  <foreignObject x='60' y='314' width='220' height='42'>");
        out.println("    <form xmlns='http://www.w3.org/1999/xhtml' action='verSensor' method='get' class='sensor-form'>");
        out.println("      <input type='hidden' name='idTren' value='" + id + "'/>");
        out.println("      <input type='hidden' name='idSensor' value='12'/>");
        out.println("      <button type='submit' class='sensor-btn'>VELOCIMETRO GPS</button>");
        out.println("    </form>");
        out.println("  </foreignObject>");

        out.println("</svg>");
        out.println("</div>");

        // ===== Botones inferiores: Ver Datos y Colisión =====
        out.println("<div class='action-row'>");
        out.println("  <form action='verDatos' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + id + "'>");
        out.println("    <button type='submit' class='action-btn'>Ver Todos los Datos</button>");
        out.println("  </form>");
        out.println("  <form action='colision' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + id + "'>");
        out.println("    <button type='submit' class='action-btn colision-btn'>Colisión</button>");
        out.println("  </form>");
        out.println("</div>");

        // ===== Lista de Sensores en Modo Tabla =====
        out.println("<div class='subsection-title-wrapper'>");
        out.println("  <div class='section-title'>Lista de Sensores en Modo Tabla</div>");
        out.println("</div>");

        out.println("<div class='table-wrapper'>");
        out.println("<table class='trenes-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>ID Sensor</th>");
        out.println("<th>Nombre</th>");
        out.println("<th>Tipo</th>");
        out.println("<th>Estado</th>");
        out.println("<th class='col-acciones'></th>");
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
            Connection conn2 = DriverManager.getConnection(url);
            Statement stmt2 = conn2.createStatement();
            ResultSet rs2 = stmt2.executeQuery("Select * from Sensores");

            while (rs2.next()) {
                String idSensor = rs2.getString("ID_Sensor");
                String nombre   = rs2.getString("Nombre_Sensor");
                String tipo     = rs2.getString("Tipo_Sensor");
                String estado   = rs2.getString("Estado");

                out.println("<tr>");
                out.println("<td class='col-id'>" + idSensor + "</td>");
                out.println("<td>" + nombre + "</td>");
                out.println("<td>" + tipo + "</td>");
                out.println("<td>" + estado + "</td>");
				out.println("<td class='col-acciones'>");
                out.println("  <form action='cambiarEstado' method='get'>");
                out.println("    <input type='hidden' name='idTren' value='" + id + "'>");
                out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
				out.println("    <input type='hidden' name='estado' value='"+estado+"'>");
                out.println("    <button type='submit' class='action-btn action-btn-sm'>Des/Activar</button>");
                out.println("  </form>");
                out.println("</td>");
                out.println("<td class='col-acciones'>");
                out.println("  <form action='verSensor' method='get'>");
                out.println("    <input type='hidden' name='idTren' value='" + id + "'>");
                out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
                out.println("    <button type='submit' class='action-btn action-btn-sm'>Ver Datos</button>");
                out.println("  </form>");
                out.println("</td>");
                out.println("</tr>");
            }

            rs2.close();
            stmt2.close();
            conn2.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        out.println("</tbody>");
        out.println("</table>");
        out.println("</div>");

        out.println("<div class='action-row'>");
        out.println("  <form action='Inicio' method='get'>");
        out.println("    <button type='submit' class='action-btn'>Volver</button>");
        out.println("  </form>");
        out.println("</div>");

        // ===== Script para alternar vistas =====
        out.println("<script>");
        out.println("  var toggleBtn = document.getElementById('toggleBtn');");
        out.println("  var sideView  = document.getElementById('sideView');");
        out.println("  var topView   = document.getElementById('topView');");
        out.println("  var isTopView = false;");
        out.println("  toggleBtn.addEventListener('click', function() {");
        out.println("    isTopView = !isTopView;");
        out.println("    if (isTopView) {");
        out.println("      sideView.classList.remove('active');");
        out.println("      topView.classList.add('active');");
        out.println("      toggleBtn.textContent = 'Vista Lateral';");
        out.println("    } else {");
        out.println("      topView.classList.remove('active');");
        out.println("      sideView.classList.add('active');");
        out.println("      toggleBtn.textContent = 'Vista de Arriba';");
        out.println("    }");
        out.println("  });");
        out.println("</script>");

        out.println("</body>");
        out.println("</html>");
    }
}