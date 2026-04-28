import java.io.*;
import java.sql.Timestamp;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/insertarMedicion")
public class InsertarMedicionServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Ruta real del archivo .accdb dentro del proyecto
        String ruta = getServletContext().getRealPath("/WEB-INF/tren.accdb");
        
        // Usamos la clase que ya tienes
        InsertadorSensores insertador = new InsertadorSensores(ruta);
        Timestamp ahora = new Timestamp(System.currentTimeMillis());

        try {
            insertador.insertarMedicion(ahora);
            resp.getWriter().write("{\"status\": \"ok\", \"fecha\": \"" + ahora + "\"}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"status\": \"error\", \"mensaje\": \"" + e.getMessage() + "\"}");
        }
    }
}