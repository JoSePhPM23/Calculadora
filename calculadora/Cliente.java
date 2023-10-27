import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Clase principal del cliente que se conecta al servidor y abre la ventana de la calculadora.
 */
public class Cliente {
    static int max = 9990;
    static int min = 9000;
    static int puerto = (int) (Math.random() * (max - min)) + min; // Número del puerto del cliente.

    public static void main(String[] args) throws IOException {
        System.out.println("Hola");
        Socket socket = new Socket("192.168.1.6", 9999);
        datos paquete = new datos();
        paquete.setPort(puerto);
        paquete.setExpresion(null);
        ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
        salida.writeObject(paquete);
        salida.close();
        Ventana ventana1 = new Ventana();
        ventana1.setVisible(true);
    }
}

/**
 * Clase de la ventana que contiene el panel con los elementos de la calculadora.
 */
class Ventana extends JFrame {
    public Ventana() {
        setSize(250, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocation(150, 50);
        setTitle("Calculadora");
        panel panel1 = new panel();
        add(panel1);
    }
}

/**
 * Clase del panel que contiene todos los elementos de la calculadora.
 */
class panel extends JPanel implements Runnable {
    JTextField txt = new JTextField(15); // Espacio para escribir la expresión matemática.
    JTextArea txt_area = new JTextArea(3, 15); // Área de texto donde se mostrará el resultado de la operación.
    JButton[] buttons = new JButton[20]; // Botones numéricos y de operaciones.

    public panel() {
        setLayout(new BorderLayout());
        add(txt, BorderLayout.NORTH);
        txt_area.setEditable(false);
        txt_area.setLineWrap(true);
        add(new JScrollPane(txt_area), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4));
        String[] buttonLabels = {
            "7", "8", "9", "+",
            "4", "5", "6", "-",
            "1", "2", "3", "*",
            "(", "0", ")", "/",
            "C", "=", "&", "|", "^", "~"
        };
        for (int i = 0; i < 20; i++) {
            buttons[i] = new JButton(buttonLabels[i]);
            buttonPanel.add(buttons[i]);
            buttons[i].addActionListener(new Envia());
        }
        add(buttonPanel, BorderLayout.SOUTH);

        Thread hilo1 = new Thread(this);
        hilo1.start();
    }

    /**
     * Función ejecutada al presionar los botones para enviar la expresión al servidor.
     */
    private class Envia implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (button.getText().equals("=")) {
                try {
                    Socket misocket = new Socket("192.168.1.6", 9999);
                    datos mensaje = new datos();
                    mensaje.setPort(Cliente.puerto);
                    mensaje.setExpresion(txt.getText());
                    ObjectOutputStream salida = new ObjectOutputStream(misocket.getOutputStream());
                    salida.writeObject(mensaje);
                    salida.close();
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                }
            } else if (button.getText().equals("C")) {
                txt.setText("");
                txt_area.setText(""); // Borra también el resultado
            } else {
                txt.setText(txt.getText() + button.getText());
            }
        }
    }

    /**
     * Hilo que se mantiene a la espera de la respuesta del servidor.
     */
    @Override
    public void run() {
        try {
            ServerSocket receptor = new ServerSocket(Cliente.puerto);
            System.out.println("Conectado");
            while (true) {
                datos mensaje;
                Socket misocket = receptor.accept();
                ObjectInputStream entrada = new ObjectInputStream(misocket.getInputStream());
                mensaje = (datos) entrada.readObject();
                txt_area.setText(mensaje.getExpresion());
                misocket.close();
            }
        } catch (IOException | ClassNotFoundException e1) {
            throw new RuntimeException(e1);
        }
    }
}

/**
 * Clase que funciona como un paquete de datos que contiene el puerto del cliente y la expresión matemática para ser enviado al servidor.
 */
class datos implements Serializable {
    private String expresion;
    private int port;

    public String getExpresion() {
        return expresion;
    }

    public void setExpresion(String ip) {
        this.expresion = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}



