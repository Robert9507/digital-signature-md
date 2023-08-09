import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

public class PasswordDialog {
    public String getPassword(File contenedorP12) {
        while (true) {
            String password = showPasswordDialog();
            if (password == null) {
                // Si el usuario cancela la entrada de la contraseña, se sale del proceso de firma
                return null;
            }

            // Verificar la contraseña ingresada
            if (verifyPassword(password, contenedorP12)) {
                return password;
            }

            // Mostrar ventana de error
            JOptionPane.showMessageDialog(null, "Contraseña incorrecta, vuelva a intentarlo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String showPasswordDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Nuevo título
        String nuevoTitulo = "Ingrese la contraseña del archivo P12";

        // Agregamos la imagen y el título al JPanel
        panel.add(new JLabel(nuevoTitulo), BorderLayout.CENTER);

        // Mostramos el cuadro de diálogo con el JPanel personalizado
        return JOptionPane.showInputDialog(null, panel, "Contraseña", JOptionPane.PLAIN_MESSAGE);
    }

    private boolean verifyPassword(String password, File contenedorP12) {
        try {
            // Se instancia un keystore de tipo pkcs12 para leer el contenedor p12 o pfx
            KeyStore ks = KeyStore.getInstance("pkcs12");

            // Se entrega la ruta y la clave del p12 o pfx
            ks.load(new FileInputStream(contenedorP12.getAbsolutePath()), password.toCharArray());

            // Se verifica si la contraseña es correcta al intentar obtener el alias del certificado
            if (ks.aliases().hasMoreElements()) {
                return true; // La contraseña es válida
            }
        } catch (Exception e) {
            // No es necesario hacer nada aquí, ya que el resultado será false por defecto
        }
        return false;
    }
}
