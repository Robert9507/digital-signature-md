import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Locale;

public class CustomStyle {
    public static void setCustomUIStyle() {
        // Obtener el color del botón predeterminado del sistema
        Color buttonColor = UIManager.getColor("Button.background");

        // Establecer una nueva fuente y tamaño para las ventanas emergentes
        Font font = new Font("Segoe UI", Font.PLAIN, 18);

        // Cambiar el color de letra del mensaje en las ventanas de JOptionPane
        UIManager.put("OptionPane.messageForeground", new Color(0, 51, 102));

        // Cambiar el color de letra de los botones en las ventanas de JOptionPane a un tono más oscuro
        UIManager.put("OptionPane.buttonForeground", new Color(38, 78, 146)); // Azul oscuro suav

        UIManager.put("Button.background", new ColorUIResource(buttonColor));
        UIManager.put("Button.foreground", new ColorUIResource(Color.BLACK));
        UIManager.put("Button.font", new FontUIResource(font));
        UIManager.put("Label.font", new FontUIResource(font));
        UIManager.put("TextField.font", new FontUIResource(font));
        UIManager.put("TextArea.font", new FontUIResource(font));
        UIManager.put("OptionPane.font", new FontUIResource(font));
        UIManager.put("OptionPane.messageFont", new FontUIResource(font));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(font));

        // Establecer el idioma de las ventanas emergentes (JOptionPane) a español
        setSpanishLocale();
    }
    private static void setSpanishLocale() {
        // Establecer el idioma a español para las ventanas emergentes (JOptionPane)
        Locale.setDefault(new Locale("es", "ES"));

        // Establecer el idioma a español para el JFileChooser
        UIManager.put("FileChooser.lookInLabelText", "Buscar en:");
        UIManager.put("FileChooser.saveInLabelText", "Guardar en:");
        UIManager.put("FileChooser.openDialogTitleText", "Abrir");
        UIManager.put("FileChooser.saveDialogTitleText", "Guardar");
        UIManager.put("FileChooser.cancelButtonText", "Cancelar");
        UIManager.put("FileChooser.saveButtonText", "Guardar");
        UIManager.put("FileChooser.openButtonText", "Abrir");
        UIManager.put("FileChooser.fileNameLabelText", "Nombre de archivo:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Tipo de archivos:");
        UIManager.put("FileChooser.acceptAllFileFilterText", "Todos los archivos");
    }
}
