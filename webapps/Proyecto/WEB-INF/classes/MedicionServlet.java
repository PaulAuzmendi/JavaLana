import java.io.*;
import java.sql.Timestamp;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/agregarMedicion")
public class MedicionServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //Real rute 
        String ruta = getServletContext().getRealPath("/WEB-INF/tren.accdb");
        InsertadorSensores insertador = new InsertadorSensores(ruta);

        //Fecha/hora actual 
        Timestamp ahora = new Timestamp(System.currentTimeMillis());

        try {
            insertador.insertarMedicion(ahora);
            resp.getWriter().write("{\"status\":\"ok\"}");
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
