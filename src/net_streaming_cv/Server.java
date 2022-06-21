/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net_streaming_cv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author INTEL
 */
public class Server extends Thread {

    public static ArrayList<Flujo> flujos = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<String> stock;
    public static VideoList video_list;

    public static void main(String[] args) throws IOException {
        cargarUsuarios();
        video_list = new VideoList();

        ServerSocket server = null;
        try {
            server = new ServerSocket(8000); // INICIO EL SERVIDOR....

            System.out.println("Servidor escuchando en la IP: " + server.getInetAddress() + " y puerto " + 8000);
        } catch (IOException ioe) {
            System.out.println("Comunicación rechazada." + ioe);
            System.exit(1);
        }

        while (true) {
            try {
                // Voy a recibir la información de autenticación del usuario...
                /*Socket del cliente*/
                System.out.println("Escuchando por conexiones...");
                Socket clientSocket = server.accept(); // Aceptando la conexión

                // Crear el flujo de atención a este usuario...
                Flujo flujo = new Flujo(clientSocket);

                flujo.start(); // iniciar el hilo de este flujo

            } catch (IOException ioe) {
                System.out.println("Error: " + ioe);
            }

        }

    }

    public static void cargarUsuarios() {
        // Limpiar el array de usuarios...
        synchronized (Server.users) {
            users.clear();

            File file = new File("usuarios/usuarios.txt");

            if (!file.exists()) {
                System.out.println("No hay usuarios....");
                return;
            }
            FileReader reader;
            BufferedReader buffer;

            String string;

            try {
                synchronized (file) {
                    reader = new FileReader(file);
                    buffer = new BufferedReader(reader);
                    string = "";

                    while (string != null) {
                        try {
                            string = buffer.readLine();
                            if (string != null) {
                                construirUsuario(string);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(Flujo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        buffer.close();
                        reader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Flujo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Flujo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Hay " + users.size() + " Usuarios");
    }

    public static void construirUsuario(String userInfo) {
        String[] userArrayInfo = userInfo.split(":");
        String username = userArrayInfo[0];
        String password = userArrayInfo[1];

        User user = new User(username, password);
        synchronized (users) {
            Server.users.add(user);
        }
    }
}
