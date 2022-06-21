/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net_streaming_cv;

import java.io.Serializable;

/**
 *
 * @author INTEL
 */
public class TakeVideoMessage implements Serializable{

    /**
     * File name
     */
    public String FileName = "";

    /**
     * is the last part?
     */
    public boolean LastMessage = true;

    /**
     * Valid bytes count
     */
    public int ValidBytes = 0;

    /**
     * File content
     */
    public byte[] FileContent = new byte[MAX_SIZE];

    /**
     * Can I read 4000 bytes at time
     */
    public final static int MAX_SIZE = 4000;
}
