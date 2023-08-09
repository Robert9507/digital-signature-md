import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class FirmaPDF {
    public void signPdf() throws IOException, GeneralSecurityException, DocumentException {
        PdfSigner pdfSigner = new PdfSigner();
        PasswordDialog passwordDialog = new PasswordDialog();

        // <============================================== ARCHIVO PDF ==============================================>
        // Ventana de aviso antes de elegir el archivo
        String mensajePDF = "Seleccione el documento PDF";
        JOptionPane.showMessageDialog(null, mensajePDF, "Aviso", JOptionPane.INFORMATION_MESSAGE);

        // Se muestra un cuadro de diálogo para que el usuario elija el archivo PDF a firmar
        JFileChooser fileChooser = new JFileChooser();

        // Establecer el tamaño personalizado para el JFileChooser
        Dimension customSizePdf = new Dimension(900, 500);
        fileChooser.setPreferredSize(customSizePdf);

        // Filtro para mostrar solo archivos con extensión .pdf
        FileNameExtensionFilter filterPDF = new FileNameExtensionFilter("Archivos PDF", "pdf");
        fileChooser.setFileFilter(filterPDF);

        fileChooser.setDialogTitle("Seleccione el archivo PDF a firmar");
        int result;
        File pdfOrigen = null;

        // Bucle para asegurarse de que el usuario seleccione un archivo PDF antes de continuar
        while (pdfOrigen == null) {
            result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                pdfOrigen = fileChooser.getSelectedFile();
            } else {
                int exitConfirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de salir?", "Confirmar salida", JOptionPane.YES_NO_OPTION);
                if (exitConfirm == JOptionPane.YES_OPTION) {
                    System.out.println("Proceso de firma PDF cancelado por el usuario.");
                    return;
                }
            }
        }

        // <============================================== ARCHIVO P12 ==============================================>
        // Ventana de aviso antes de elegir el archivo p12
        String mensajeP12 = "Seleccione el archivo P12";
        JOptionPane.showMessageDialog(null, mensajeP12, "Aviso", JOptionPane.INFORMATION_MESSAGE);

        // Se muestra un cuadro de diálogo para que el usuario elija el archivo P12/PFX
        JFileChooser p12FileChooser = new JFileChooser();

        // Establecer el tamaño personalizado para el JFileChooser
        Dimension customSizep12 = new Dimension(900, 500);
        p12FileChooser.setPreferredSize(customSizep12);

        // Filtro para mostrar solo archivos con extensión .p12
        FileNameExtensionFilter filterP12 = new FileNameExtensionFilter("Archivos P12/PFX", "p12", "pfx");
        p12FileChooser.setFileFilter(filterP12);

        p12FileChooser.setDialogTitle("Seleccione el archivo P12");

        File contenedorP12 = null;

        // Bucle para asegurarse de que el usuario seleccione un archivo P12 antes de continuar
        while (contenedorP12 == null) {
            result = p12FileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                contenedorP12 = p12FileChooser.getSelectedFile();
            } else {
                int exitConfirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de salir?", "Confirmar salida", JOptionPane.YES_NO_OPTION);
                if (exitConfirm == JOptionPane.YES_OPTION) {
                    System.out.println("Proceso de firma PDF cancelado por el usuario.");
                    return;
                }
            }
        }

        // <============================================== CONTRASEÑA ==============================================>
        // Obtener la contraseña del archivo P12/PFX usando el PasswordDialog
        String contenedorP12clave = passwordDialog.getPassword(contenedorP12);

        if (contenedorP12clave == null) {
            System.out.println("Proceso de firma PDF cancelado por el usuario.");
            return;
        }

        // <============================================== PÁGINA DE FIRMA ==============================================>
        // Se instancia un lector de PDF
        PdfReader reader = new PdfReader(pdfOrigen.getAbsolutePath());

        // Se obtiene el número total de páginas en el PDF
        int totalPaginas = reader.getNumberOfPages();

        // Ventana para ingresar la página en la que se desea firmar
        int paginaFirma = 1;
        boolean paginaValida = false;

        while (!paginaValida) {
            try {
                String paginaInput = JOptionPane.showInputDialog(null, "El documento tiene "+ totalPaginas + " páginas\nIngrese el número de página en la que desea firmar (1-" + totalPaginas + ")", "Página de Firma", JOptionPane.PLAIN_MESSAGE);
                if (paginaInput != null) {
                    paginaFirma = Integer.parseInt(paginaInput);
                    if (paginaFirma < 1 || paginaFirma > totalPaginas) {
                        JOptionPane.showMessageDialog(null, "Número de página no válido. Debe estar entre 1 y " + totalPaginas + ".", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        paginaValida = true;
                    }
                } else {
                    return; // Sale del proceso de firma si el usuario cancela
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Número de página no válido. Debe ser un valor numérico.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // <============================================== COORDENADAS ==============================================>
        // Ventana para ingresar las coordenadas de la firma
        boolean coordenadasIngresadas = false;
        float posicionX = -1;
        float posicionY = -1;

        while (!coordenadasIngresadas) {
            // Ventana para ingresar las coordenadas de la firma
            String coordenadasInput = JOptionPane.showInputDialog(null, "\nLa firma por defecto está ubicada en la posición [x=230, y=270] en el documento.\n\nIngrese las coordenadas (x, y) para la posición de la firma. Ejemplo: 100,200\n\nSi no desea cambiarlas, deje el campo vacío.\n\n", "Posición de Firma", JOptionPane.PLAIN_MESSAGE);

            // Verificar si el usuario presionó el botón "Cancelar"
            if (coordenadasInput == null) {
                System.out.println("Proceso de firma PDF cancelado por el usuario.");
                return;
            }

            // Verificar si el usuario ingresó las coordenadas o dejó los campos vacíos
            if (coordenadasInput != null && !coordenadasInput.trim().isEmpty()) {
                String[] coordenadas = coordenadasInput.split(",");
                if (coordenadas.length == 2) {
                    try {
                        posicionX = Float.parseFloat(coordenadas[0]);
                        posicionY = Float.parseFloat(coordenadas[1]);
                        coordenadasIngresadas = true;
                    } catch (NumberFormatException e) {
                        // En caso de que el usuario ingrese coordenadas inválidas, se usan las coordenadas predeterminadas
                        JOptionPane.showMessageDialog(null, "Coordenadas inválidas. Se utilizarán las coordenadas predeterminadas.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // En caso de que el usuario ingrese un formato inválido, se usan las coordenadas predeterminadas
                    JOptionPane.showMessageDialog(null, "Formato de coordenadas inválido. Se utilizarán las coordenadas predeterminadas.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Si el usuario dejó los campos vacíos, se utilizan las coordenadas predeterminadas
                posicionX = 230;
                posicionY = 270;
                coordenadasIngresadas = true;
            }
        }
        // Si las coordenadas siguen siendo -1, establecer las coordenadas predeterminadas
        if (posicionX == -1 || posicionY == -1) {
            posicionX = 230;
            posicionY = 270;
        } else {
            // Invertir la coordenada Y para que el origen esté en la esquina inferior izquierda
            posicionY = reader.getPageSize(paginaFirma).getHeight() - posicionY;
        }

        // <============================================== GUARDAR EL DOCUMENTO FIRMADO ==============================================>
        // Ventana de aviso antes de elegir el archivo la ubicación y nombre del archivo PDF firmado
        String mensajeSAVE = "Seleccione la ubicación para guardar el documento.";
        JOptionPane.showMessageDialog(null, mensajeSAVE, "Aviso", JOptionPane.INFORMATION_MESSAGE);

        // Bucle para asegurarse de que el usuario ingrese el nombre del archivo PDF ya firmado
        File pdfDestino = null;
        boolean fileNameEmpty = true;
        while (fileNameEmpty) {
            JFileChooser saveFileChooser = new JFileChooser();

            // Establecer el tamaño personalizado para el JFileChooser
            Dimension customSizeGuardarPdf = new Dimension(900, 500);
            saveFileChooser.setPreferredSize(customSizeGuardarPdf);

            saveFileChooser.setDialogTitle("Seleccione la ubicación y nombre para guardar el archivo PDF firmado");

            // Configurar el nombre de archivo predeterminado (nombre original + sufijo)
            String originalFileName = pdfOrigen.getName();
            int extensionIndex = originalFileName.lastIndexOf(".");
            String baseFileName = extensionIndex == -1 ? originalFileName : originalFileName.substring(0, extensionIndex);
            String defaultFileName = baseFileName + "-firmado.pdf";
            saveFileChooser.setSelectedFile(new File(defaultFileName));

            result = saveFileChooser.showSaveDialog(null);
            if (result != JFileChooser.APPROVE_OPTION) {
                int exitConfirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de salir?", "Confirmar salida", JOptionPane.YES_NO_OPTION);
                if (exitConfirm == JOptionPane.YES_OPTION) {
                    System.out.println("Proceso de firma PDF cancelado por el usuario.");
                    return;
                }
            } else {
                pdfDestino = saveFileChooser.getSelectedFile();
                if (!pdfDestino.getName().toLowerCase().endsWith(".pdf")) {
                    pdfDestino = new File(pdfDestino.getParentFile(), pdfDestino.getName() + ".pdf");
                }

                // Verificar si ya existe un archivo con el mismo nombre en la ubicación deseada
                if (pdfDestino.exists()) {
                    int overwriteConfirm = JOptionPane.showConfirmDialog(null, "Ya existe un archivo con el mismo nombre. ¿Desea reemplazarlo?", "Confirmar reemplazo", JOptionPane.YES_NO_OPTION);
                    if (overwriteConfirm == JOptionPane.NO_OPTION) {
                        continue; // Volver al inicio del bucle para seleccionar otro nombre
                    }
                }

                if (pdfDestino.getName().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Debe ingresar el nombre del archivo PDF ya firmado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                } else {
                    fileNameEmpty = false;
                }
            }
        }

        // Luego, llama al método signPdf de la clase PdfSigner para realizar la firma
        pdfSigner.signPdf(pdfOrigen, contenedorP12, contenedorP12clave, pdfDestino, paginaFirma, posicionX, posicionY);

        // Ventana de aviso del proceso finalizado
        String mensajeConf = "Documento firmado con éxito.";
        JOptionPane.showMessageDialog(null, mensajeConf, "Aviso", JOptionPane.INFORMATION_MESSAGE);

        // Abrir el archivo firmado con el programa predeterminado
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(pdfDestino);
        } catch (IOException e) {
            // Manejar cualquier error que pueda ocurrir al intentar abrir el archivo
            e.printStackTrace();
        }
    }
}
