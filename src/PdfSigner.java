import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;

public class PdfSigner {
    public void signPdf(File pdfOrigen, File contenedorP12, String contenedorP12clave, File pdfDestino, int paginaFirma, float posicionX, float posicionY) throws IOException, GeneralSecurityException, DocumentException {
        // Se agrega bouncyCastle al provider de java, si no se realiza, arroja un error
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Se instancia un keystore de tipo pkcs12 para leer el contenedor p12 o pfx
        KeyStore ks = KeyStore.getInstance("pkcs12");

        // Se entrega la ruta y la clave del p12 o pfx
        ks.load(new FileInputStream(contenedorP12.getAbsolutePath()), contenedorP12clave.toCharArray());

        // Se obtiene el nombre del certificado
        String alias = (String) ks.aliases().nextElement();

        // Se obtiene la llave privada
        PrivateKey pk = (PrivateKey) ks.getKey(alias, contenedorP12clave.toCharArray());

        // Se obtiene la cadena de certificados en base al nombre del certificado
        Certificate[] chain = ks.getCertificateChain(alias);

        // Se instancia un lector de PDF
        PdfReader reader = new PdfReader(pdfOrigen.getAbsolutePath());

        // Se crea un sello en la página de la firma
        PdfStamper stamper = PdfStamper.createSignature(reader, new FileOutputStream(pdfDestino.getAbsolutePath()), '\0');

        // Se obtiene el número total de páginas en el PDF
        int totalPaginas = reader.getNumberOfPages();

        // Se verifica si la página seleccionada está dentro del rango válido
        if (paginaFirma < 1 || paginaFirma > totalPaginas) {
            throw new IllegalArgumentException("Número de página de firma no válido.");
        }

        // Se obtiene la apariencia de la firma
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();

        // Establece la posición de la apariencia de firma en la página seleccionada
        Rectangle pageSize = reader.getPageSize(paginaFirma);

        // Calcula las coordenadas corregidas en función de la orientación de la página
        float llx, lly, urx, ury;
        if (pageSize.getRotation() == 90 || pageSize.getRotation() == 270) {
            llx = pageSize.getBottom() + posicionY;
            lly = pageSize.getLeft() - posicionX;
            urx = llx + 100; // Ancho de la firma
            ury = lly - 200; // Altura de la firma
        } else {
            llx = posicionX;
            lly = pageSize.getTop() - posicionY;
            urx = llx + 200; // Ancho de la firma
            ury = lly - 100; // Altura de la firma
        }

        appearance.setVisibleSignature(new Rectangle(llx, lly, urx, ury), paginaFirma, "Signature");

        // Se entrega la llave privada del certificado, el algoritmo de firma y el provider usado (bouncycastle)
        ExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
        ExternalDigest digest = new BouncyCastleDigest();

        // Se genera la firma y se almacena el pdf como se indicó en las líneas anteriores
        MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

        // Se cierran las instancias para liberar espacio
        stamper.close();
        reader.close();
    }
}