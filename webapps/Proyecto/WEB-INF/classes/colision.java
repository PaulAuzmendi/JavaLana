import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class colision extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren = request.getParameter("idTren");

        // ===== Consulta: últimos registros de sensores Radar y Lidar =====
        // Un valor Dato_valor = 1.0 en estos sensores indica detección de obstáculo.
        // Recogemos el último registro por sensor para mostrar el estado actual.

        // Estructura: lista de filas [idSensor, nombreSensor, tipoSensor, valor, unidad, fechaHora, localizacion]
        java.util.List<String[]> alertas  = new java.util.ArrayList<>();
        java.util.List<String[]> normales = new java.util.ArrayList<>();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            // IDs de sensores Radar y Lidar
            ResultSet rsSensores = stmt.executeQuery(
                "SELECT ID_Sensor, Nombre_Sensor, Tipo_Sensor FROM Sensores " +
                "WHERE Tipo_Sensor = 'Radar' OR Tipo_Sensor = 'Lidar' " +
                "ORDER BY ID_Sensor");

            java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rsSensores.next()) {
                String idSensor     = rsSensores.getString("ID_Sensor");
                String nombreSensor = rsSensores.getString("Nombre_Sensor");
                String tipoSensor   = rsSensores.getString("Tipo_Sensor");

                // Último registro de este sensor para el tren dado
                String sqlDato = "SELECT TOP 1 Dato_valor, Unidad_medida, FechaHora, Localizacion " +
                                 "FROM DatosSensor " +
                                 "WHERE ID_Sensor = " + idSensor;
                if (idTren != null && !idTren.isEmpty())
                    sqlDato += " AND ID_Tren = " + idTren;
                sqlDato += " ORDER BY FechaHora DESC";

                Statement stmtD = connection.createStatement();
                ResultSet rsDato = stmtD.executeQuery(sqlDato);

                if (rsDato.next()) {
                    double valor        = rsDato.getDouble("Dato_valor");
                    String unidad       = rsDato.getString("Unidad_medida");
                    String fechaHora    = fmt.format(rsDato.getTimestamp("FechaHora"));
                    String localizacion = rsDato.getString("Localizacion");

                    String[] fila = {
                        idSensor, nombreSensor, tipoSensor,
                        String.format(java.util.Locale.US, "%.2f", valor),
                        unidad, fechaHora, localizacion
                    };

                    if (valor >= 1.0) alertas.add(fila);
                    else              normales.add(fila);
                }
                rsDato.close();
                stmtD.close();
            }
            rsSensores.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        boolean hayColision = !alertas.isEmpty();

        // ===== HTML =====
        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Estado Colisión</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");

        // Parpadeo si hay alerta
        if (hayColision) {
            out.println("<style>");
            out.println("  @keyframes pulse { 0%,100% { opacity:1; } 50% { opacity:0.4; } }");
            out.println("  .alerta-banner { animation: pulse 1.2s ease-in-out infinite; }");
            out.println("</style>");
        }

        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        if (idTren != null && !idTren.isEmpty())
            out.println("  <p class='subtitle'>TREN #" + idTren + "</p>");
        out.println("  <div class='section-title'>Estado de Colisión</div>");
        out.println("</header>");

        // ===== Banner de estado global =====
        if (hayColision) {
            out.println("<div class='alerta-banner' style='margin:30px auto;max-width:800px;background:rgba(255,80,80,0.12);border:2px solid #ff6060;padding:22px 30px;text-align:center;font-family:Chakra Petch,sans-serif'>");
            out.println("  <div style='font-size:2rem;color:#ff6060;letter-spacing:2px;text-transform:uppercase;font-weight:700'>⚠ ALERTA DE COLISIÓN</div>");
            out.println("  <div style='color:#c5d3e8;margin-top:8px;letter-spacing:1px'>" + alertas.size() + " sensor(es) con obstáculo detectado</div>");
            out.println("</div>");
        } else {
            out.println("<div style='margin:30px auto;max-width:800px;background:rgba(77,208,225,0.08);border:1px solid #2c8aa0;padding:22px 30px;text-align:center;font-family:Chakra Petch,sans-serif'>");
            out.println("  <div style='font-size:1.5rem;color:#4dd0e1;letter-spacing:2px;text-transform:uppercase;font-weight:600'>✔ SIN RIESGO DE COLISIÓN</div>");
            out.println("  <div style='color:#6c7a93;margin-top:8px;letter-spacing:1px'>Todos los sensores Radar y Lidar reportan vía libre</div>");
            out.println("</div>");
        }

        // ===== Tabla de alertas =====
        if (hayColision) {
            out.println("<div style='font-family:Chakra Petch,sans-serif;color:#ff8080;letter-spacing:2px;text-transform:uppercase;font-size:0.8rem;margin:20px auto;max-width:800px'>Sensores en alerta</div>");
            printTabla(out, alertas, "#ff8080");
        }

        // ===== Tabla normal =====
        if (!normales.isEmpty()) {
            out.println("<div style='font-family:Chakra Petch,sans-serif;color:#6c7a93;letter-spacing:2px;text-transform:uppercase;font-size:0.8rem;margin:20px auto;max-width:800px'>Sensores sin obstáculo</div>");
            printTabla(out, normales, "#4dd0e1");
        }

        // ===== Botón volver =====
        out.println("<div class='action-row'>");
        if (idTren != null && !idTren.isEmpty()) {
            out.println("  <form action='verDatos' method='get'>");
            out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
            out.println("    <button type='submit' class='action-btn'>Volver al Tren</button>");
            out.println("  </form>");
        } else {
            out.println("  <form action='index.html' method='get'>");
            out.println("    <button type='submit' class='action-btn'>Volver al Panel</button>");
            out.println("  </form>");
        }
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }

    private void printTabla(PrintWriter out, java.util.List<String[]> filas, String colorValor) {
        out.println("<div class='table-wrapper' style='max-width:800px;margin:0 auto 10px'>");
        out.println("<table class='trenes-table'>");
        out.println("<thead><tr>");
        out.println("<th>ID Sensor</th>");
        out.println("<th>Nombre</th>");
        out.println("<th>Tipo</th>");
        out.println("<th>Localización</th>");
        out.println("<th>Valor</th>");
        out.println("<th>Unidad</th>");
        out.println("<th>Última lectura</th>");
        out.println("</tr></thead><tbody>");
        for (String[] f : filas) {
            // f = [idSensor, nombreSensor, tipoSensor, valor, unidad, fechaHora, localizacion]
            out.println("<tr>");
            out.println("<td class='col-id'>" + f[0] + "</td>");
            out.println("<td>" + f[1] + "</td>");
            out.println("<td>" + f[2] + "</td>");
            out.println("<td>" + f[6] + "</td>");
            out.println("<td style='color:" + colorValor + ";font-weight:600'>" + f[3] + "</td>");
            out.println("<td>" + f[4] + "</td>");
            out.println("<td style='color:#6c7a93;font-size:0.88em'>" + f[5] + "</td>");
            out.println("</tr>");
        }
        out.println("</tbody></table></div>");
    }
}
