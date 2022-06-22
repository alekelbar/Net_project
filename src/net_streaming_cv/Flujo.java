/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net_streaming_cv;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.ImageIcon;
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
public class Flujo extends Thread {

    public Socket client;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    VideoCapture video;
    Mat img;

    public Flujo(Socket client) {
        // DEFINIENDO: socket del cliente, y flujos de salida y entrada.
        this.client = client;
        try {
            // texto...
            this.inputStream = new DataInputStream(client.getInputStream());
            this.outputStream = new DataOutputStream(client.getOutputStream());
        } catch (IOException ioe) {
            System.out.println("Al iniciar el flujo de usuarios: " + ioe);
        }
    }

    public void persistirUsuario(String user) {
        File usuarios = new File("usuarios/usuarios.txt");
        FileWriter write;
        PrintWriter writeLine;
        if (!usuarios.exists()) {
            try {
                usuarios.createNewFile();

                write = new FileWriter(usuarios, true);
                writeLine = new PrintWriter(write);
                writeLine.println(user);

                writeLine.close();
                write.close();
            } catch (IOException ex) {
                System.out.println("Al persistir usuarios..." + ex.getMessage());
            }
        } else {
            try {
                write = new FileWriter(usuarios, true);
                writeLine = new PrintWriter(write);
                writeLine.println(user);

                writeLine.close();
                write.close();
            } catch (IOException ex) {
                System.out.println("Al persistir usuarios..." + ex.getMessage());
            }

        }
        Server.cargarUsuarios();
    }

    @Override
    public void run() {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // NUNCA QUITAR
        System.out.println("Un usuario nuevo conectado, esperando instrucciones");
        Server.conectados.add(this);
        while (true) {
            try {
                // Leo la instrucción y ejecuto una acción.
                System.out.println("Conectados : " + Server.conectados.size());
                System.out.print("Previo a leer la instruccion: ...");
                String instrucction = this.inputStream.readUTF();
                System.out.println(": " + instrucction);

                if (instrucction.contains("start")) {
                    System.out.println("INICIO DE OPERACION: Conexion");

                    outputStream.writeUTF("activo");
                    outputStream.flush(); // limpiar la salida...

                } else if (instrucction.contains("upload:")) {
                    System.out.println("INICIO DE OPERACIÓN: Envio");

                    //RECIBIR UN VIDEO....
                    // objetos serializables...
                    ObjectInputStream objInputStream = new ObjectInputStream(client.getInputStream());
                    // Extraer el nombre del video primero...
                    // Debería llegar en este formato: upload:nameVideo
                    String videoFileName = instrucction.split(":")[1];
                    FileOutputStream OpenVideoFile = new FileOutputStream("videos/" + videoFileName);
                    /*Operacion para extraer los bytes*/
                    TakeVideoMessage video_recived = null;
                    /*Mensaje aux, solo para el casting*/
                    Object video_aux = null;
                    do {
                        try {
                            video_aux = objInputStream.readObject();
                            /*Objeto recibido*/
                        } catch (ClassNotFoundException ex) {
                            System.out.println("Ocurrio un error al leer el mensaje");
                        }

                        // Verificar que la instancia es correcta
                        if (video_aux instanceof TakeVideoMessage) {
                            video_recived = (TakeVideoMessage) video_aux; // Casteamos el objeto

                            // Escribir el contenido del fichero...
                            // los parametros corresponden a contenido, inicio y final....
                            OpenVideoFile.write(video_recived.FileContent, 0, video_recived.ValidBytes);
                        } else {
                            System.err.println("Mensaje no esperado "
                                    + video_aux.getClass().getName());
                            break;
                        }
                    } while (!video_recived.LastMessage);
                    // El video termino de procesarse
                    System.out.println("Flujo de entrada: Video RECIBIDO");

                } else if (instrucction.equals("auth")) {
                    // AUTENTICAR UN USUARIO....
                    System.out.println("INICIO DE OPERACION: Autenticacion");
                    boolean auth_success = true;
                    outputStream.writeUTF("continue");
                    outputStream.flush();

                    String user = inputStream.readUTF();
                    String[] array = user.split(":");
                    String name = array[0];
                    String pass = array[1];

                    for (User el : Server.users) {
                        if (el.getName().equals(name) && el.getPassword().equals(pass)) {
                            auth_success = false;
                            outputStream.writeUTF("Exist");
                            outputStream.flush();
                        }
                    }

                    if (auth_success) {
                        outputStream.writeUTF("Sucess");
                        outputStream.flush();
                        persistirUsuario(user);
                    }

                } else if (instrucction.equals("login")) {
                    // Verificar si el usuario esta registrado...
                    System.out.println("INICIO DE OPERACION: Login");

                    boolean rejected = true;
                    outputStream.writeUTF("continue");
                    outputStream.flush();

                    String user = inputStream.readUTF();
                    String[] userInfo = user.split(":");

                    for (User element : Server.users) {
                        if (element.getName().equals(userInfo[0]) && element.getPassword().equals(userInfo[1])) {
                            outputStream.writeUTF("Acepted");
                            outputStream.flush();
                            rejected = false;
                        }
                    }

                    if (rejected) {
                        outputStream.writeUTF("Rejected");
                        outputStream.flush();
                    }

                } else if (instrucction.contains("watch:")) {

                    System.out.println("INICIO DE OPERACION: Visualización");

                    String[] string = instrucction.split(":");

                    File testingExist = new File("videos/" + string[1]);
                    if (testingExist.exists()) {
                        outputStream.writeUTF("acepted");
                        outputStream.flush();
                    } else {
                        outputStream.writeUTF("rejected");
                        outputStream.flush();
                    }
                    // flujo de salida de objetos...
                    ObjectOutputStream object_send = new ObjectOutputStream(client.getOutputStream());

                    video = new VideoCapture("videos/" + string[1]); // cargando el video del sistema de ficheros...

                    img = new Mat(); // la imagen como frame
                    Mat dest = new Mat(); // LA FUTURA IMAGEN REDIMENSIONADA

                    byte[] imgDataScaled; // La imagen trasformada en bytes
                    ImageIcon icon = new ImageIcon(); // icono del label
                    Size size = new Size(637, 381);

                    GetVideoMessage message = new GetVideoMessage();
                    message.video_name = string[1];

                    MatOfByte bufferScaled;

                    while (true) {
                        //conver image to matrix
                        video.read(img);
                        // finalizo el video...
                        if (!img.empty()) {
                            message.lastFrame = false;
                            System.out.println("Continuamos...");
                        } else {
                            message.lastFrame = true;
                            System.out.println("NO Continuamos...");
                            break;
                        }

                        Imgproc.resize(img, dest, size, 0.1, 0.1, Imgproc.INTER_AREA);

                        bufferScaled = new MatOfByte();
                        // Imgcodecs.imencode(".jpg", img, buffer);
                        Imgcodecs.imencode(".jpg", dest, bufferScaled);

                        // imgData = buffer.toArray();
                        imgDataScaled = bufferScaled.toArray();

                        // Imagen que se mostrara en el label
                        icon = new ImageIcon(imgDataScaled);

                        message.imgDataScaled = icon;
                        object_send.writeObject(message);
                        break;
                    }
                    System.out.println("Termino el streaming...");

                } else if (instrucction.equals("stock")) {
                    ArrayList<String> stock = Server.video_list.getVideoList();

                    String send = "";
                    for (String string : stock) {
                        send += string + ":";
                    }

                    outputStream.writeUTF(send);
                    outputStream.flush();
                }

            } catch (IOException ex) {
                Server.conectados.remove(this);
                System.out.println("Un usuario se desconecto: " + Server.conectados.size() + " disponibles");
                break;
            }
        }
        System.out.println("Se salio del bucle D:!");
    }
}
