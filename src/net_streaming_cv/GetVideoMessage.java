/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net_streaming_cv;

import java.awt.Image;
import java.io.Serializable;
import javax.swing.ImageIcon;
import org.opencv.core.Mat;

/**
 *
 * @author INTEL
 */
public class GetVideoMessage implements Serializable {

    /**
     * Video file name
     */
    public String video_name = "";

    public boolean lastFrame = false;

    public ImageIcon imgDataScaled = new ImageIcon();

}
