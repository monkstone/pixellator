/**
 * *
 * The purpose of this library is to allow the pixellation of images in
 * processing Copyright (C) 2012 Martin Prout This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Obtain a copy of the license at http://www.gnu.org/licenses/lgpl-2.1.html
 */
package pde2cfdg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;
import processing.core.PImage;

/**
 *
 * @author Martin Prout
 */
public class ProcessingToCF {

    private final String VERSION = "0.2";
    private Event event;
    private PApplet parent;
    private PImage source;
    private int distance = 3;
    private String filename;
    private String name;
    private String pathToCFDG;
    private String cfdgFile;
    private String dataFile;
    /**
     *
     */
    public String outFile;
    private Process proc = null;

    /**
     *
     * @param parent
     */
    public ProcessingToCF(PApplet parent) {
        this.parent = parent;
        this.parent.registerDispose(this);
        this.outFile = parent.sketchPath("out.png");
        this.dataFile = parent.sketchPath("data.cfdg");
        this.event = Event.START;
        System.out.println(event);
    }

    /**
     *
     * @param path
     */
    public void setPathToCFDG(String path) {
        this.pathToCFDG = path;
    }

    /**
     *
     * @param d
     */
    public void setDotSize(int d) {
        this.distance = d;
    }

    /**
     *
     * @param img
     */
    public void setImage(File img) {
        this.filename = img.getAbsolutePath();
        this.name = img.getName();
        event = Event.LOADING;
        this.source = parent.loadImage(this.filename);
        this.source.loadPixels();
        System.out.println(event);
    }

    /**
     *
     */
    public void getInput() {
        String input = parent.selectInput();
        if (input != null) {
            event = Event.SELECTED;
            System.out.println(event);
        }
        setImage(new File(input));
        //writeCFDG();
    }

    /**
     *
     * @return
     */
    public boolean finished() {
        return (event == Event.DISPLAY);
    }

    /**
     *
     */
    public void writeCFDG() {
        String rule;
        if (name.endsWith("png")) {
            cfdgFile = parent.sketchPath(name.replace("png", "cfdg"));
            rule = name.replace(".png", "");
        } else if (name.endsWith("jpg")) {
            cfdgFile = parent.sketchPath(name.replace("jpg", "cfdg"));
            rule = name.replace(".jpg", "");
        } else {
            cfdgFile = parent.sketchPath("out.cfdg");
            rule = "pde";
        }
        if (event == Event.SELECTED || event == Event.DISPLAY) {
            event = Event.WRITING;
            System.out.println(event);
        }
        try {
            PrintWriter cfdg = new PrintWriter(new BufferedWriter(new FileWriter(cfdgFile)));
            cfdg.println("CF::Background = [b -1]\n");
            cfdg.println(String.format("startshape %s\n", rule));
            cfdg.println("shape dot{CIRCLE[]}\n");
            cfdg.println("import data.cfdg");
            cfdg.flush();
            cfdg.close();
            cfdg = new PrintWriter(new BufferedWriter(new FileWriter(dataFile)));
            cfdg.println(String.format("\n\nshape %s{", rule));
            for (int x = 0; x < source.width; x += distance) {
                for (int y = 0; y < source.height; y += distance) {
                    int pix = source.pixels[x + y * source.width];
                    float sat = parent.saturation(pix);
                    float hu = parent.hue(pix);
                    float sz = parent.brightness(pix) * distance;
                    if (sz > 0.03) {
                        cfdg.println(String.format(
                                "\tdot[x %d y %d s %.2f hue %d sat %.3f b 1]", 
                                x, -y, sz, Math.round(hu * 360), sat));
                    }
                }
            }
            cfdg.println("}\n");
            cfdg.flush();
        } catch (IOException ex) {
            Logger.getLogger(ProcessingToCF.class.getName()).log(Level.SEVERE, null, ex);
        }
        event = Event.PROCESSING;
        process(cfdgFile);
    }

    /**
     *
     * @param name
     */
    public void process(String name) {
        try {
            //String width = String.format("%d", parent.width);
            //String height = String.format("%d", parent.height);
            String size = String.format("%d", (int) PApplet.max(parent.width, parent.height));
            String[] commands;

            commands = new String[]{this.pathToCFDG, "-s", size, "-c", name, "-o", this.outFile};
            //commands = new String[]{this.pathToCFDG, "-w", width, "-h", height, name, "out.png"};
            if (this.event == Event.PROCESSING) {
                System.out.println(event);
                proc = Runtime.getRuntime().exec(commands);
            }
            if (proc.waitFor() == 0) {
                this.event = Event.DISPLAY;
                System.out.println(event);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProcessingToCF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Processing libraries require this, doesn't do much here
     */
    public final void dispose() {
    }

    /**
     * Returns library version no, processing libraries require this
     *
     * @return
     */
    public final String version() {
        return VERSION;
    }
}
