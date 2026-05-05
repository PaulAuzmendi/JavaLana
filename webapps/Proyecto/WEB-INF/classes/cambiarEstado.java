import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class cambiarEstado extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String idTren        = request.getParameter("idTren");
        String idSensor      = request.getParameter("idSensor");
        String estadoActual  = request.getParameter("estado");

       
       
        String nuevoEstado = "Activo".equalsIgnoreCase(estadoActual) ? "Inactivo" : "Activo";

        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            String rutaRelativa = "/Trenes2.accdb";
            String rutaAbsoluta = getServletContext().getRealPath(rutaRelativa);
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;

            Connection connection = DriverManager.getConnection(url);

            
			String sql = "UPDATE Sensores SET Estado_tren_" + idTren
					   + " = '" + nuevoEstado + "' WHERE ID_Sensor = " + idSensor;
			Statement st = connection.createStatement();
			st.executeUpdate(sql);

            st.close();
            connection.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        response.sendRedirect("verTren?idTren=" + idTren);
    }
}