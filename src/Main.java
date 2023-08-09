import javax.swing.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.itextpdf.text.DocumentException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Aplicar los estilos personalizados antes de mostrar cualquier ventana emergente
                CustomStyle.setCustomUIStyle();

                FirmaPDF firmaPDF = new FirmaPDF();
                firmaPDF.signPdf();
            } catch (GeneralSecurityException | IOException | DocumentException e) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
            }
        });
    }
}
