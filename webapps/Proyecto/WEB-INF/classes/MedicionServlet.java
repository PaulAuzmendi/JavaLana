import java.io.*;
import java.sql.Timestamp;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/agregarMedicion")
public class MedicionServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Obtener ruta real de la base de datos
        String ruta = getServletContext().getRealPath("/WEB-INF/tren.accdb");
        InsertadorSensores insertador = new InsertadorSensores(ruta);

        // Fecha/hora actual (o la que quieras, ej. +1 minuto)
        Timestamp ahora = new Timestamp(System.currentTimeMillis());

        try {
            insertador.insertarMedicion(ahora);
            resp.getWriter().write("{\"status\":\"ok\"}");
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
