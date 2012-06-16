/**
 * 
 * The purpose of this library is to allow the pixellation of images in
 * processing Copyright (C) 2012 Martin Prout This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * Obtain a copy of the license at http://www.gnu.org/licenses/lgpl-2.1.html
 */

package pde2cfdg;

import processing.core.*;

/**
 * 
 * @author Martin Prout
 */
public class cfdg extends PApplet {

    ProcessingToCF cfdg;
    String input;

    /**
     * 
     */
    @Override
    public void setup() {
        size(1280, 1024);
        background(255);
        colorMode(HSB, 1.0f);
        cfdg = new ProcessingToCF(this);
        cfdg.setDotSize(2);
        cfdg.setPathToCFDG("/home/tux/CF3/cfdg");        
        cfdg.getInput();
    }

    /**
     * 
     */
    @Override
    public void draw() {
        if (cfdg.finished()) {            
            PImage img = loadImage(cfdg.outFile);
            image(img, 0, 0, img.width, img.height);
            // background(img); // preferred blows up if image isn't correct size
            noLoop();
        }
    }

    /**
     * 
     * @param args
     */
    public static void main(String args[]) {
        PApplet.main(new String[]{"pde2cfdg.cfdg"});
    }
}
