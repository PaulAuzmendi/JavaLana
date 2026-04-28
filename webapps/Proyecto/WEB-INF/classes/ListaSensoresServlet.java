import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ListaSensoresServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Por ahora la lista está aquí cableada. Más adelante se podrá leer
        // desde una base de datos, un fichero, o el propio bus del tren.
        String[][] sensores = {
            { "Temperatura Motor",     "Térmico"     },
            { "Temperatura Cabina",    "Térmico"     },
            { "Temperatura Exterior",  "Térmico"     },
            { "Velocímetro Delantero", "Cinemático"  },
            { "Velocímetro Trasero",   "Cinemático"  },
            { "Radar Izquierdo",       "Radar"       },
            { "Lidar Izquierdo",       "Lidar"       },
            { "Radar Centro",          "Radar"       },
            { "Lidar Centro",          "Lidar"       },
            { "Radar Derecho",         "Radar"       },
            { "Lidar Derecho",         "Lidar"       }
        };

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Lista de Sensores - Vagón B74</title>");
        out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
        out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
        out.println("<link href='https://fonts.googleapis.com/css2?family=Chakra+Petch:wght@400;500;600;700&display=swap' rel='stylesheet'>");
        out.println("<style>");
        out.println("  * { box-sizing: border-box; margin: 0; padding: 0; }");
        out.println("  body {");
        out.println("    font-family: 'Chakra Petch', sans-serif;");
        out.println("    background: radial-gradient(ellipse at top, rgba(77,208,225,0.06), transparent 60%), #0a1628;");
        out.println("    color: #c5d3e8;");
        out.println("    min-height: 100vh;");
        out.println("    padding: 50px 20px;");
        out.println("  }");
        out.println("  .container { max-width: 900px; margin: 0 auto; }");
        out.println("  h1 {");
        out.println("    color: #4dd0e1;");
        out.println("    text-align: center;");
        out.println("    letter-spacing: 3px;");
        out.println("    text-transform: uppercase;");
        out.println("    font-weight: 500;");
        out.println("    margin-bottom: 8px;");
        out.println("  }");
        out.println("  .subtitle {");
        out.println("    text-align: center;");
        out.println("    color: #6c7a93;");
        out.println("    letter-spacing: 1.5px;");
        out.println("    margin-bottom: 40px;");
        out.println("  }");
        out.println("  table {");
        out.println("    width: 100%;");
        out.println("    border-collapse: collapse;");
        out.println("    background: rgba(20, 35, 60, 0.55);");
        out.println("    border: 1px solid #2c8aa0;");
        out.println("  }");
        out.println("  th, td {");
        out.println("    padding: 14px 22px;");
        out.println("    text-align: left;");
        out.println("    border-bottom: 1px solid rgba(77, 208, 225, 0.12);");
        out.println("    letter-spacing: 1px;");
        out.println("  }");
        out.println("  th {");
        out.println("    color: #4dd0e1;");
        out.println("    text-transform: uppercase;");
        out.println("    font-weight: 600;");
        out.println("    background: rgba(77, 208, 225, 0.06);");
        out.println("    letter-spacing: 2px;");
        out.println("    font-size: 0.9rem;");
        out.println("  }");
        out.println("  td.idx { color: #6c7a93; font-variant-numeric: tabular-nums; width: 60px; }");
        out.println("  td.tipo {");
        out.println("    color: #4dd0e1;");
        out.println("    font-size: 0.85rem;");
        out.println("    letter-spacing: 1.5px;");
        out.println("    text-transform: uppercase;");
        out.println("  }");
        out.println("  tbody tr { transition: background 0.15s; }");
        out.println("  tbody tr:hover { background: rgba(77, 208, 225, 0.08); }");
        out.println("  tbody tr:last-child td { border-bottom: none; }");
        out.println("  .footer {");
        out.println("    margin-top: 30px;");
        out.println("    display: flex;");
        out.println("    justify-content: space-between;");
        out.println("    align-items: center;");
        out.println("  }");
        out.println("  .total { color: #6c7a93; letter-spacing: 1.5px; }");
        out.println("  .total strong { color: #4dd0e1; }");
        out.println("  .back-btn {");
        out.println("    display: inline-block;");
        out.println("    padding: 12px 28px;");
        out.println("    background: transparent;");
        out.println("    border: 1px solid #4dd0e1;");
        out.println("    color: #4dd0e1;");
        out.println("    text-decoration: none;");
        out.println("    letter-spacing: 2.5px;");
        out.println("    text-transform: uppercase;");
        out.println("    font-family: inherit;");
        out.println("    font-size: 0.9rem;");
        out.println("    cursor: pointer;");
        out.println("    transition: all 0.25s;");
        out.println("  }");
        out.println("  .back-btn:hover {");
        out.println("    background: rgba(77, 208, 225, 0.15);");
        out.println("    box-shadow: 0 0 20px rgba(77, 208, 225, 0.3);");
        out.println("  }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>Lista de Todos los Sensores</h1>");
        out.println("<p class='subtitle'>Vagón B74 - Inventario Completo</p>");
        out.println("<table>");
        out.println("  <thead>");
        out.println("    <tr><th>#</th><th>Sensor</th><th>Tipo</th></tr>");
        out.println("  </thead>");
        out.println("  <tbody>");

        for (int i = 0; i < sensores.length; i++) {
            out.println("    <tr>");
            out.println("      <td class='idx'>" + String.format("%02d", i + 1) + "</td>");
            out.println("      <td>" + sensores[i][0] + "</td>");
            out.println("      <td class='tipo'>" + sensores[i][1] + "</td>");
            out.println("    </tr>");
        }

        out.println("  </tbody>");
        out.println("</table>");
        out.println("<div class='footer'>");
        out.println("  <span class='total'>Total: <strong>" + sensores.length + "</strong> sensores</span>");
        out.println("  <a class='back-btn' href='javascript:history.back()'>← Volver al Panel</a>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
