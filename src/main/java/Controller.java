import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    @FXML
    private TextField txtNumero;

    @FXML
    private Button btnBuscar;

    @FXML
    private Label lblResultado;

    // Mapa para almacenar habitaciones (Número -> Estado)
    private Map<String, String> habitaciones = new HashMap<>();

    @FXML
    public void initialize() {
        cargarDatosXML();
    }

    private void cargarDatosXML() {
        try (InputStream inputStream = getClass().getResourceAsStream("/reservas.xml")) {
            if (inputStream == null) {
                lblResultado.setText("Error: No se encontró reservas.xml en resources.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            doc.getDocumentElement().normalize();

            // Obtenemos todas las etiquetas <habitacion>
            NodeList listaHabitaciones = doc.getElementsByTagName("habitacion");

            for (int i = 0; i < listaHabitaciones.getLength(); i++) {
                Element elem = (Element) listaHabitaciones.item(i);
                
                // Leemos los ATRIBUTOS "numero" y "estado" de la etiqueta <habitacion>
                String numero = elem.getAttribute("numero").trim();
                String estado = elem.getAttribute("estado").trim();

                if (!numero.isEmpty()) {
                    habitaciones.put(numero, estado);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblResultado.setText("Error al leer el archivo XML.");
        }
    }

    @FXML
    private void buscarHabitacion() {
        String numIngresado = txtNumero.getText().trim();

        if (numIngresado.isEmpty()) {
            lblResultado.setText("Por favor, introduce un número de habitación.");
            return;
        }

        if (habitaciones.containsKey(numIngresado)) {
            String estado = habitaciones.get(numIngresado);
            
            // Formateamos la respuesta según el estado leído del XML
            switch (estado.toLowerCase()) {
                case "disponible":
                    lblResultado.setText("Habitación " + numIngresado + ": DISPONIBLE");
                    break;
                case "ocupada":
                    lblResultado.setText("Habitación " + numIngresado + ": OCUPADA");
                    break;
                case "mantenimiento":
                    lblResultado.setText("Habitación " + numIngresado + ": EN MANTENIMIENTO");
                    break;
                default:
                    lblResultado.setText("Habitación " + numIngresado + ": Estado (" + estado + ")");
                    break;
            }
        } else {
            lblResultado.setText("La habitación " + numIngresado + " NO EXISTE.");
        }
    }
}