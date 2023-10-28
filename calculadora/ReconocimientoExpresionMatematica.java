import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfRect;

import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * Esta clase realiza el reconocimiento de expresiones matemáticas impresas utilizando una cámara web y Tesseract OCR.
 */
public class ReconocimientoExpresionMatematica {
    /**
     * Método principal que inicia la aplicación de reconocimiento de expresiones matemáticas.
     *
     * @param args Los argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Carga la biblioteca OpenCV

        VideoCapture camera = new VideoCapture(0); // Abre la cámara web

        if (!camera.isOpened()) {
            System.out.println("Error: No se pudo abrir la cámara.");
            return;
        }

        JFrame frame = new JFrame("Reconocimiento de expresión matemática");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JLabel label = new JLabel();
        frame.add(label);
        frame.setVisible(true);

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("path/to/tesseract/data"); // Reemplazar con la ruta correcta a los datos de entrenamiento de Tesseract

        while (true) {
            Mat frameMat = new Mat();
            camera.read(frameMat);

            // Preprocesamiento de la imagen (ajuste de contraste, escala de grises, etc.)
            Imgproc.cvtColor(frameMat, frameMat, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(frameMat, frameMat, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

            MatOfRect boxes = new MatOfRect();
            Imgproc.putText(frameMat, "Captura una expresion matematica", new org.opencv.core.Point(10, 30), Core.FONT_HERSHEY_SIMPLEX, 2.0, new Scalar(255, 255, 255), 3);


            tesseract.setTessVariable("tessedit_char_whitelist", "0123456789()+-*/%=&|^~ ") ; // Caracteres permitidos

            tesseract.setPageSegMode(6); // Modo de segmentación de página PSM_SINGLE_LINE

            // Realiza el reconocimiento de texto utilizando Tesseract
            try {
                tesseract.doOCR(Imgcodecs.imencode(".png", frameMat, new Mat()));
                String result = tesseract.getUTF8Text();
                System.out.println("Expresión matemática reconocida: " + result);
            } catch (TesseractException e) {
                e.printStackTrace();
            }

            // Muestra la imagen en la ventana
            BufferedImage image = Mat2BufferedImage(frameMat);
            label.setIcon(new ImageIcon(image));

            frame.repaint();
        }
    }
    /**
     * Convierte una matriz OpenCV a una imagen BufferedImage.
     *
     * @param m La matriz OpenCV a convertir en imagen.
     * @return La imagen BufferedImage resultante.
     */
    public static BufferedImage Mat2BufferedImage(Mat m) {
        // Convierte una matriz OpenCV a una imagen BufferedImage
        int type = BufferedImage.TYPE_BYTE_GRAY; // Tipo predeterminado de la imagen, en escala de grises.

        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR; // Si la matriz tiene más de un canal (es a color), se utiliza el tipo 3BYTE_BGR.
        }
        
        int bufferSize = m.channels() * m.cols() * m.rows(); // Tamaño del búfer para almacenar los bytes de la matriz.
        byte[] b = new byte[bufferSize]; // Se crea un arreglo de bytes para almacenar los datos de la matriz.
        
        m.get(0, 0, b); // Se copian los datos de la matriz en el arreglo de bytes.
        
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type); // Se crea una nueva imagen con el tipo y dimensiones adecuadas.
        final byte[] targetPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData(); // Se obtiene un arreglo de bytes de los píxeles de la imagen.
        
        System.arraycopy(b, 0, targetPixels, 0, b.length); // Se copian los datos desde el arreglo de bytes de la matriz al arreglo de bytes de la imagen.
        
        return image; // Se retorna la imagen convertida.
    }
}



