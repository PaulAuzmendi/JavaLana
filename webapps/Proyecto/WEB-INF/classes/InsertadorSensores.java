import java.sql.*;
import java.util.Random;

public class InsertadorSensores {

    private String url;
    private Random rnd;

    // Localizaciones, unidades y tipo de sensor por ID (1..12)
    private static final String[] LOCALIZACIONES = {
        "",                               // índice 0 no usado
        "Motor", "Cabina", "Exterior",    // 1,2,3
        "Frontal-Izquierdo", "Frontal-Centro", "Frontal-Derecho", // 4,5,6
        "Frontal-Izquierdo", "Frontal-Centro", "Frontal-Derecho", // 7,8,9
        "Rueda delantera", "Rueda trasera", "GPS"                // 10,11,12
    };

    private static final String[] UNIDADES = {
        "",
        "°C", "°C", "°C",
        "bool", "bool", "bool",
        "bool", "bool", "bool",
        "km/h", "km/h", "km/h"
    };

    // true si el sensor devuelve solo 0 o 1
    private static final boolean[] ES_BINARIO = {
        false,
        false, false, false,
        true, true, true,
        true, true, true,
        false, false, false
    };

    /**
     * Constructor: recibe la ruta completa al archivo .accdb
     */
    public InsertadorSensores(String rutaAccess) {
        this.url = "jdbc:ucanaccess://" + rutaAccess;
        this.rnd = new Random();
    }

    /**
     * Inserta una medición completa (12 registros) en el instante dado.
     * @param timestamp Instante exacto de la medición
     */
    public void insertarMedicion(Timestamp timestamp) throws SQLException {
        String sqlBase = "INSERT INTO Datos_Sensor_? (ID_Sensor, FechaHora, Localizacion, Dato_Valor, Unidad_Medida) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);  // transacción

            for (int idSensor = 1; idSensor <= 12; idSensor++) {
                // Reemplazar el "?" de la plantilla con el número de tabla
                String sql = sqlBase.replaceFirst("\\?", String.valueOf(idSensor));

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, idSensor);                         // ID_Sensor
                    stmt.setTimestamp(2, timestamp);                  // FechaHora
                    stmt.setString(3, LOCALIZACIONES[idSensor]);      // Localizacion
                    stmt.setDouble(4, generarValor(idSensor));        // Dato_Valor
                    stmt.setString(5, UNIDADES[idSensor]);            // Unidad_Medida
                    stmt.executeUpdate();
                }
            }
            conn.commit();  // confirmar todas las inserciones
        }
    }

    /**
     * Calcula el valor adecuado según el tipo de sensor.
     */
    private double generarValor(int idSensor) {
        if (ES_BINARIO[idSensor]) {
            // 3% de probabilidad de 1 (obstáculo)
            return rnd.nextDouble() < 0.03 ? 1.0 : 0.0;
        }

        // Sensores continuos
        switch (idSensor) {
            case 1:  // Temp Motor: 20..100 °C
                return redondear(20 + rnd.nextDouble() * 80, 1);
            case 2:  // Temp Cabina: 18..30 °C
                return redondear(18 + rnd.nextDouble() * 12, 1);
            case 3:  // Temp Exterior: -10..45 °C
                return redondear(-10 + rnd.nextDouble() * 55, 1);
            case 10: case 11: case 12:  // Velocímetros: 0..300 km/h
                return redondear(rnd.nextDouble() * 300, 1);
            default:
                return 0.0;
        }
    }

    /** Redondea a un número de decimales */
    private double redondear(double valor, int decimales) {
        double factor = Math.pow(10, decimales);
        return Math.round(valor * factor) / factor;
    }

    public static void main(String[] args) {
        InsertadorSensores ins = new InsertadorSensores(
            "C:\\Users\\amelendezam\\Documents\\GitHub\\JavaLana\\webapps\\Proyecto\\WEB-INF\\tren.accdb"
        );
        try {
            ins.insertarMedicion(new Timestamp(System.currentTimeMillis()));
            System.out.println("¡12 registros insertados correctamente!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 