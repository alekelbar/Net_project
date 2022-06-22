/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package net_streaming_cv;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author INTEL
 */
public class NET_Streaming_server extends JFrame {

    /**
     * @param args the command line arguments
     */
    // variables... GUI
    private final JLabel cameraScreen;
    private final JButton btnCapture;
    private boolean buttonClick = false;

    // variables... CV
    private VideoCapture video;
    private Mat img;

    public NET_Streaming_server() {
        setLocationRelativeTo(null);
        setLayout(null);

        // add components 
        cameraScreen = new JLabel();
        // Tamaño de la ventana que muestra el video
        cameraScreen.setBounds(0, 0, 1080, 720);
        setResizable(false);

        btnCapture = new JButton("Capture an image");
        btnCapture.setBounds(250, 500, 100, 50);
        add(btnCapture);
        add(cameraScreen);

        btnCapture.addActionListener((ActionEvent e) -> {
            System.out.println("button pressed");
            buttonClick = true;
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("Net Streaming server");
        // Tamaño de la ventana
        setSize(new Dimension(1080, 750));
    }

    //start camera service
    public void startCameraService() {
        // IMAGEN ORIGINAL
        video = new VideoCapture("videos/test.mp4");
        img = new Mat(); // la imagen como frame
        Mat dest = new Mat(); // LA FUTURA IMAGEN REDIMENSIONADA

//        byte[] imgData; // La imagen trasformada en bytes
        byte[] imgDataScaled; // La imagen trasformada en bytes
        ImageIcon icon; // icono del label
        Size size = new Size(1080, 720);
        while (true) {
            //conver image to matrix
            video.read(img);
            // finalizo el video...
            if (img.empty()) {
                break;
            }

            // operación de redefinición de escala
            Imgproc.resize(img, dest, size, 1, 1, Imgproc.INTER_AREA);

//            final MatOfByte buffer = new MatOfByte();
            // convert it to bytes
            final MatOfByte bufferScaled = new MatOfByte();
//            Imgcodecs.imencode(".jpg", img, buffer);
            Imgcodecs.imencode(".jpg", dest, bufferScaled);

//            imgData = buffer.toArray();
            imgDataScaled = bufferScaled.toArray();

            // Imagen que se mostrara en el label
            icon = new ImageIcon(imgDataScaled);
            this.cameraScreen.setIcon(icon);

//            add to Jlabel
            if (buttonClick) {
//                Prompt for the file image name
                String name = JOptionPane.showInputDialog("Please tell me your name: ");
                if (name == null || name.isEmpty()) {
                    // Si el nombre es invalido
                    name = new SimpleDateFormat("yyy-mm-dd-hh-mm-ss").format(new Date());
                }
                // write to file
                Imgcodecs.imwrite("frames/" + name + ".jpg", img);
                this.buttonClick = false;
            }

        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // NUNCA QUITAR
        EventQueue.invokeLater(() -> {
            NET_Streaming_server server = new NET_Streaming_server();
            new Thread(server::startCameraService).start();
        });

    }
}
