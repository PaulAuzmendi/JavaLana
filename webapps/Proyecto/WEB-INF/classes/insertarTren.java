import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class insertarTren extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String modelo        = request.getParameter("modelo"); //viene dado de anadirTren
        String fechaCreacion = request.getParameter("fechaCreacion");   // "2026-04-30"
        String fechaRevision = request.getParameter("fechaRevision");   // "2026-04-30"

        // Formato Access: #yyyy-MM-dd#
        String fCreacionAccess = "#" + fechaCreacion + "#";
        String fRevisionAccess = "#" + fechaRevision + "#";
        //Para id
        int nuevoIdTren = -1;

        try {
            
            //Establecer conexion
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            String rutaAbsoluta = getServletContext().getRealPath("/Trenes2.accdb");
            String url = "jdbc:ucanaccess://" + rutaAbsoluta;
            Connection connection = DriverManager.getConnection(url);
            Statement stmt = connection.createStatement();

            // 1) Calcular MAX+1
            
            ResultSet rsId = stmt.executeQuery("SELECT MAX(ID_Tren) FROM Trenes");
            if (rsId.next()) {
                nuevoIdTren = rsId.getInt(1) + 1;
            } else {
                nuevoIdTren = 1;
            }
            rsId.close();

            // 2) INSERT en Trenes
            String sqlInsert = "INSERT INTO Trenes (ID_Tren, Modelo, Fecha_Creacion, Fecha_Ultima_Revision) "
                            + "VALUES (" + nuevoIdTren + ", '" + modelo + "', "
                            + fCreacionAccess + ", " + fRevisionAccess + ")";
            System.out.println("SQL: " + sqlInsert); //Solo para el otro ?
            stmt.executeUpdate(sqlInsert); //Intsertar en tabla
            stmt.close();
            connection.close(); 
            //Cerrando conexiones
             } catch (Exception e) { //Para errores
            throw new ServletException(e);
             
        }

           
            
        response.sendRedirect("Inicio");

       
    }
}
