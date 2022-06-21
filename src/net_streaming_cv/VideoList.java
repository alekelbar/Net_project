/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net_streaming_cv;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author INTEL
 */
public final class VideoList {

    private final ArrayList<String> available_videos;

    public VideoList() {
        this.available_videos = new ArrayList<>();
        setVideoList();
    }

    public ArrayList<String> getVideoList() {
        available_videos.clear(); // limpiar los registros
        setVideoList(); // actualizar
        return this.available_videos;
    }

    public ArrayList<String> setVideoList() {
        // Logica la leer el directorio y extraer los titulos...
        String carpAct = System.getProperty("user.dir");
        File carpet = new File(carpAct + "/videos");

        for (String dir : carpet.list()) {
            if (dir.endsWith(".mp4")) {
                available_videos.add(dir);
            }
        }

        System.out.println("Longitud: " + available_videos.size());

        return available_videos;
    }
}
