import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class grafico extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String idTren   = request.getParameter("idTren");
        String idSensor = request.getParameter("idSensor");

        String nombreSensor = "";
        String unidad = "";

        // Construyo dos arrays JS: las fechas y los valores
        StringBuilder labelsJS = new StringBuilder();
        StringBuilder dataJS   = new StringBuilder();

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            // Nombre del sensor
            ResultSet rsS = stmt.executeQuery("Select Nombre_Sensor from Sensores where ID_Sensor = " + idSensor);
            if (rsS.next()) nombreSensor = rsS.getString("Nombre_Sensor");
            rsS.close();

            // Datos ordenados por fecha
            ResultSet rs = stmt.executeQuery(
                "Select FechaHora, Dato_valor, Unidad_medida from DatosSensor " +
                "where ID_Tren = " + idTren + " and ID_Sensor = " + idSensor +
                " order by FechaHora");

            java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("dd/MM HH:mm:ss");

            boolean primero = true;
            while (rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp("FechaHora");
                String fecha = fmt.format(ts);
                double valor = rs.getDouble("Dato_valor");

                if (!primero) {
                    labelsJS.append(", ");
                    dataJS.append(", ");
                }
                labelsJS.append("'").append(fecha).append("'");
                dataJS.append(valor);
                primero = false;

                if (unidad.equals("")) unidad = rs.getString("Unidad_medida");
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
        out.println("<title>Gráfico - Sensor " + idSensor + "</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@300;400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("<script src='https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js'></script>"); // Carga Chart.js desde un CDN. Define la clase global Chart.
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("  <h1>Software de Gestión de Datos de Sensores para Trenes</h1>");
        out.println("  <p class='subtitle'>TREN #" + idTren + " &middot; " + nombreSensor.toUpperCase() + "</p>");
        out.println("  <div class='section-title'>Gráfico Temporal</div>");
        out.println("</header>");

        // Contenedor del gráfico (Chart.js dibuja sobre un <canvas>)
        out.println("<div class='grafico-container'>");
        out.println("  <canvas id='miGrafico'></canvas>"); // Lienzo HTML donde Chart.js pintará. El id permite que el JS lo encuentre.
        out.println("</div>");

        // ===== Botón volver =====
        out.println("<div class='action-row'>");
        out.println("  <form action='verSensor' method='get'>");
        out.println("    <input type='hidden' name='idTren' value='" + idTren + "'>");
        out.println("    <input type='hidden' name='idSensor' value='" + idSensor + "'>");
        out.println("    <button type='submit' class='action-btn'>Volver</button>");
        out.println("  </form>");
        out.println("</div>");

        // ===== Script Chart.js =====
        out.println("<script>");
        out.println("  const ctx = document.getElementById('miGrafico');");		// Busca el <canvas> por su id y lo guarda en la constante ctx.
        out.println("  new Chart(ctx, {");		// Crea un gráfico sobre ese canvas. Recibe un objeto con toda la configuración.
        out.println("    type: 'line',");		// Tipo de gráfico: línea. Otros: 'bar', 'pie', 'doughnut', 'radar'...
        out.println("    data: {");		// Bloque "data": qué información se va a representar.
        out.println("      labels: [" + labelsJS + "],");		// Array de etiquetas del eje X. Una por cada punto. Aquí inyectamos las fechas leídas de la BD.
        out.println("      datasets: [{");		// Array de "series". Solo una (un sensor). Si quisieras dos sensores, habría dos objetos aquí.
        out.println("        label: '" + nombreSensor + " (" + unidad + ")',");		// Texto que aparece en la leyenda arriba del gráfico.
        out.println("        data: [" + dataJS + "],");		// Array de valores numéricos. data[i] corresponde con labels[i].
        out.println("        borderColor: '#4dd0e1',");		// Color de la línea principal.
        out.println("        backgroundColor: 'rgba(77, 208, 225, 0.15)',");		// Color del relleno bajo la línea (semi-transparente).
        out.println("        borderWidth: 2,");		// Grosor de la línea en píxeles.
        out.println("        pointBackgroundColor: '#7de4ed',");		// Color de relleno de cada punto/círculo.
        out.println("        pointBorderColor: '#0a1628',");	// Color del borde de cada punto.
        out.println("        pointRadius: 4,");		// Radio normal de los puntos en píxeles.
        out.println("        pointHoverRadius: 7,");		// Radio de los puntos al pasar el ratón por encima.
        out.println("        tension: 0.2,");	// Curvatura entre puntos. 0 = picos rectos. 1 = muy curvado.
        out.println("        fill: true");	// Si rellenar (true) o no (false) el área bajo la línea.
        out.println("      }]");	// Fin del único dataset.
        out.println("    },");		// Fin del bloque "data".
        out.println("    options: {");		// Bloque "options": cómo se comporta y se ve el gráfico.
        out.println("      responsive: true,");		// El gráfico se redibuja al cambiar el tamaño de la ventana.
        out.println("      maintainAspectRatio: false,");	// No mantiene una proporción ancho/alto fija. Permite ocupar el alto del contenedor.
        out.println("      plugins: {");	// Configuración de elementos auxiliares (leyenda, tooltip).
        out.println("        legend: {");	// Configuración de la leyenda (caja con el nombre del dataset).
        out.println("          labels: { color: '#c5d3e8', font: { family: 'Chakra Petch', size: 13 } }");   // Estilo del texto de la leyenda: color, fuente y tamaño.
        out.println("        },");		// Fin de legend.
        out.println("        tooltip: {");		// Configuración del tooltip que aparece al pasar el ratón sobre un punto.
        out.println("          backgroundColor: 'rgba(5, 13, 26, 0.95)',");		// Color de fondo del tooltip (azul oscuro casi opaco).
        out.println("          titleColor: '#4dd0e1',");	// Color del título del tooltip (la fecha del punto).
        out.println("          bodyColor: '#c5d3e8',");		// Color del texto del cuerpo del tooltip (el valor).
        out.println("          borderColor: '#2c8aa0',");	// Color del borde del tooltip.
        out.println("          borderWidth: 1,");	// Grosor del borde del tooltip.
        out.println("          titleFont: { family: 'Chakra Petch' },");	// Fuente del título.
        out.println("          bodyFont: { family: 'Chakra Petch' }");	// Fuente del cuerpo.
        out.println("        }");	// Fin de tooltip.
        out.println("      },");	// Fin de plugins.
        out.println("      scales: {");		// Configuración de los ejes (escalas) X e Y.
        out.println("        x: {");	// Configuración del eje X (horizontal, donde van las fechas).
        out.println("          ticks: { color: '#6c7a93', font: { family: 'Chakra Petch' } },");	// Estilo de las etiquetas que aparecen en el eje.
        out.println("          grid:  { color: 'rgba(74, 93, 122, 0.2)' }");	// Color de las líneas verticales de la cuadrícula.
        out.println("        },");		// Fin del eje X.
        out.println("        y: {");	// Configuración del eje Y (vertical, donde van los valores).
        out.println("          title: {");	// Título del eje Y (etiqueta lateral, p.ej. "Celsius").
        out.println("            display: true,");	// Mostrar (true) u ocultar (false) el título.
        out.println("            text: '" + unidad + "',");		// El texto del título. Inyectamos la unidad leída de la BD.
        out.println("            color: '#4dd0e1',");	// Color del título del eje.
        out.println("            font: { family: 'Chakra Petch', size: 13, weight: '600' }");	// Fuente del título: family, tamaño y grosor (600 = semi-bold).
        out.println("          },");	// Fin del title.
        out.println("          ticks: { color: '#6c7a93', font: { family: 'Chakra Petch' } },");	// Estilo de los números en cada marca del eje Y.
        out.println("          grid:  { color: 'rgba(74, 93, 122, 0.2)' }");	// Color de las líneas horizontales de la cuadrícula.
        out.println("        }");	// Fin del eje Y.
        out.println("      }");	// Fin de scales.
        out.println("    }");	// Fin de options.
        out.println("  });");	// Cierre del new Chart(...).
        out.println("</script>");

        out.println("</body>");
        out.println("</html>");
    }
}