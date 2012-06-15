package pde2cfdg;

import processing.core.*;

/**
 * 
 * @author tux
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
