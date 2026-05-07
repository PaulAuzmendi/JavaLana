import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;

public class colision extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren = request.getParameter("idTren");
        String modelo = "";

        // IDs de los sensores Lidar y Radar
            
        // 4,7 = izquierda  |  5,8 = centro  |  6,9 = derecha
        Map<Integer, Double> ultimoValor = new HashMap<>();
        boolean hayColision = false;

        try {
            
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            // Modelo del tren para cabecera
            
            ResultSet rsT = stmt.executeQuery(
                "SELECT Modelo FROM Trenes WHERE ID_Tren = " + idTren);
            if (rsT.next()) modelo = rsT.getString("Modelo");
            rsT.close();

            // Para cada sensor lidar/radar, su último valor (ordenado fecha desc)
            ResultSet rs = stmt.executeQuery(
                "SELECT ID_Sensor, Dato_valor FROM DatosSensor " +
                "WHERE ID_Tren = " + idTren +
                " AND ID_Sensor IN (4,5,6,7,8,9) " +
                "ORDER BY FechaHora DESC");
            while (rs.next()) {
                int idS = rs.getInt("ID_Sensor");
                if (!ultimoValor.containsKey(idS)) {           // primero = más reciente
                    double valor = rs.getDouble("Dato_valor");
                    ultimoValor.put(idS, valor);
                    if (valor == 1.0) hayColision = true;
                }
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
        out.println("<title>Colisión - Tren " + idTren + "</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>TREN #" + idTren + " &middot; " + modelo.toUpperCase() + "</p>");
        out.println("  <div class='section-title'>Detección de Colisión</div>");
        out.println("</header>");

        // Banner de estado
            
        if (hayColision) {
            out.println("<div class='colision-status alert'>&#9888; COLISIóN DETECTADA</div>");
        } else {
            out.println("<div class='colision-status ok'>SIN COLISIONES</div>");
        }

        out.println("<div class='train-container'>");

        // ====================== TOP VIEW ======================
        out.println("<svg class='view active' viewBox='0 0 1200 620' xmlns='http://www.w3.org/2000/svg'>");

        // Cuerpo del tren (idéntico a verTren)
        out.println("  <path class='train-body' d='M 450 540 L 750 540 L 750 130 Q 750 85 600 75 Q 450 85 450 130 Z'/>");
        out.println("  <path class='windshield' d='M 470 135 Q 470 102 600 95 Q 730 102 730 135 Z'/>");

        out.println("  <rect class='train-window' x='447' y='190' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='447' y='270' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='447' y='380' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='447' y='460' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='743' y='190' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='743' y='270' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='743' y='380' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='743' y='460' width='10' height='60' rx='2'/>");
        out.println("  <rect class='train-window' x='447' y='345' width='10' height='30'/>");
        out.println("  <rect class='train-window' x='743' y='345' width='10' height='30'/>");

        out.println("  <line class='center-line' x1='600' y1='150' x2='600' y2='535'/>");
        out.println("  <rect x='490' y='200' width='220' height='50' fill='none' stroke='var(--train-stroke)' stroke-width='1' stroke-dasharray='3 3' opacity='0.4' rx='4'/>");
        out.println("  <rect x='490' y='440' width='220' height='50' fill='none' stroke='var(--train-stroke)' stroke-width='1' stroke-dasharray='3 3' opacity='0.4' rx='4'/>");

        // Anchor dots
        out.println("  <circle class='sensor-dot' cx='495' cy='115' r='5'/>");
        out.println("  <circle class='sensor-dot' cx='600' cy='92' r='5'/>");
        out.println("  <circle class='sensor-dot' cx='705' cy='115' r='5'/>");

        // RADAR IZQUIERDO (4)
        String a4 = alertaClase(ultimoValor.get(4));
        out.println("  <path class='sensor-line" + a4 + "' d='M 280 176 L 380 176 L 380 130 L 495 115'/>");
        out.println("  <foreignObject x='60' y='155' width='220' height='42'>");
        out.println("    <div xmlns='http://www.w3.org/1999/xhtml' class='sensor-static" + a4 + "'>RADAR IZQUIERDO</div>");
        out.println("  </foreignObject>");

        // LIDAR IZQUIERDO (7)
        String a7 = alertaClase(ultimoValor.get(7));
        out.println("  <path class='sensor-line" + a7 + "' d='M 280 236 L 360 236 L 360 145 L 495 115'/>");
        out.println("  <foreignObject x='60' y='215' width='220' height='42'>");
        out.println("    <div xmlns='http://www.w3.org/1999/xhtml' class='sensor-static" + a7 + "'>LIDAR IZQUIERDO</div>");
        out.println("  </foreignObject>");

        // RADAR CENTRO (5)
        String a5 = alertaClase(ultimoValor.get(5));
        out.println("  <path class='sensor-line" + a5 + "' d='M 560 67 L 600 92'/>");
        out.println("  <foreignObject x='380' y='25' width='200' height='42'>");
        out.println("    <div xmlns='http://www.w3.org/1999/xhtml' class='sensor-static" + a5 + "'>RADAR CENTRO</div>");
        out.println("  </foreignObject>");

        // LIDAR CENTRO (8)
        String a8 = alertaClase(ultimoValor.get(8));
        out.println("  <path class='sensor-line" + a8 + "' d='M 640 67 L 600 92'/>");
        out.println("  <foreignObject x='620' y='25' width='200' height='42'>");
        out.println("    <div xmlns='http://www.w3.org/1999/xhtml' class='sensor-static" + a8 + "'>LIDAR CENTRO</div>");
        out.println("  </foreignObject>");

        // RADAR DERECHO (6)
        String a6 = alertaClase(ultimoValor.get(6));
        out.println("  <path class='sensor-line" + a6 + "' d='M 920 176 L 820 176 L 820 130 L 705 115'/>");
        out.println("  <foreignObject x='920' y='155' width='220' height='42'>");
        out.println("    <div xmlns='http://www.w3.org/1999/xhtml' class='sensor-static" + a6 + "'>RADAR DERECHO</div>");
        out.println("  </foreignObject>");

        // LIDAR DERECHO (9)
        String a9 = alertaClase(ultimoValor.get(9));
        out.println("  <path class='sensor-line" + a9 + "' d='M 920 236 L 840 236 L 840 145 L 705 115'/>");
        out.println("  <foreignObject x='920' y='215' width='220' height='42'>");
        out.println("    <div xmlns='http://www.w3.org/1999/xhtml' class='sensor-static" + a9 + "'>LIDAR DERECHO</div>");
        out.println("  </foreignObject>");

        // VELOCIMETRO GPS (12) — no es lidar/radar, nunca alerta
            
        out.println("  <circle class='sensor-dot' cx='600' cy='335' r='5'/>");
        out.println("  <path class='sensor-line' d='M 280 335 L 600 335'/>");
        out.println("  <foreignObject x='60' y='314' width='220' height='42'>");
        out.println("    <div xmlns='http://www.w3.org/1999/xhtml' class='sensor-static'>VELOCIMETRO GPS</div>");
        out.println("  </foreignObject>");

        out.println("</svg>");
        out.println("</div>");

        // Botón volver
        out.println("<div class='action-row'>");
        out.println("  <form action='verTren' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <button type='submit' class='action-btn'>Volver</button>");
        out.println("  </form>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }

    /** Devuelve " alert" si el último valor es 1, o "" en otro caso. */
    private String alertaClase(Double valor) {
        return (valor != null && valor == 1.0) ? " alert" : "";
    }
}
