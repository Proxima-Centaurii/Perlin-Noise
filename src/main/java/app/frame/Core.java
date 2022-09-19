package app.frame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JPanel;

import app.generator.classic.PerlinNoise;
import app.utility.MathUtil;

public class Core extends JPanel implements Runnable{

    Thread t1;
    public volatile boolean isRunning= false;
    private BufferedImage bfimg;
    public int[] pixels;


    public Core(){
        setBackground(Color.GREEN);
        setSize(Frame.W_SIZE, Frame.W_SIZE);
        setFocusable(true);
        requestFocus();

        //set up the buffered image to be deplayed
        bfimg = new BufferedImage(Frame.W_SIZE,Frame.W_SIZE,BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)bfimg.getRaster().getDataBuffer()).getData();
    }

    public void addNotify(){
        super.addNotify();
        startThread();
    }

    public void startThread(){
        try{
            t1 = new Thread(this);
            t1.start();
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            isRunning = true;
            Frame.log("Thread is running!\n");
        }
    }
    public void stopThread(){
        isRunning = false;
        Frame.log("Stopping thread...\n");
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        int thread_lifespan = 2,thread_life = 0;
        //solve problem

        while(isRunning){
            //if thread life span has reached it's limit kill the thread
            if(thread_life >= thread_lifespan){isRunning = false;break;}
            thread_life++;
            //draw
            Frame.log("update "+thread_life+"\n");

            if(thread_life == 1)
                render();

            paintScreen();
            //thread sleeping to avoid busy waiting
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Frame.log("Thread exited loop.\n");

    }

    private void paintScreen(){
        Graphics g;
        try{
            g = this.getGraphics();
            if(g != null)
                g.drawImage(bfimg, 0, 0, null);
            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void render(){
        //Initialize generator
        int SIZE = 9;
        int ISIZE = 1 << 9;
        PerlinNoise pn = new PerlinNoise();

        //Generate noise map
        //pn.generatePerlinNoise(7);
        float map[][] = pn.generateNoiseMap(SIZE,7);

        //Color results to obtain "terrain map"
        System.out.print("Rendering noise map.\n");
        for(int j=0;j< ISIZE; j++){
            for(int i=0;i< ISIZE;i++){
                int color = terrainColor(map[j][i]);
                pixels[j*ISIZE+i] = color;
            }
        }
    }

    /*  [0.0-0.2) -> DEEP OCEAN
        [0.2,0.35) -> OCEAN/RIVER
        [0.35,0.4) -> SAND
        [0.4,0.55) -> GRASS
        [0.55,0.7) -> HIGHLAND
        [0.7,0.85) -> STONE
        [0.85,1.0) -> SNOW */
    /**Used to colour a height map (noise map) in order to make it look like a terrain map.
     * @param value A height value.
     * @return An integer representing a colour.*/
    public int terrainColor(float value){
        if(value < 0.2f)
            return toRBG(0,0, MathUtil.interpolate(value,0,.2f,.2f,.45f));
        else if(value < 0.35f)
            return toRBG(0,0, MathUtil.interpolate(value,.2f,.35f,.5f,.7f));
        else if(value < 0.4f)
            return toRBG(1.0f,1.0f,.2f);
        else if(value < .55f)
            return toRBG(.0f, MathUtil.interpolate(value,.4f,.55f,.75f,.5f),.0f);
        else if(value < .7f)
            return toRBG(MathUtil.interpolate(value,.55f,.7f,.51f,.72f), MathUtil.interpolate(value,.55f,.7f,.29f,.54f), MathUtil.interpolate(value,.55f,.7f,.0f,.305f));
        else if(value < .85f)
            return toRBG(value,value,value);
        else
            return toRBG(1.0f,1.0f,1.0f);
    }

    /**Used to get an INT that represents a color value.
     * @param R A float value in the range [0,1] representing colour RED
     * @param G A float value in the range [0,1] representing colour GREEN
     * @param B A float value in the range [0,1] representing colour BLUE
     * @return Returns an integer (primitive).
     * */
    public int toRBG(float R, float G, float B){
        int INTr = (int)(255 * R);
        int INTg = (int)(255 * G);
        int INTb = (int)(255 * B);
        return( (INTr << 16) + (INTg << 8) + INTb);
    }

    /**Takes a float and converts it to a shade of gray.
     * @param value A float value in the range [0,1] that is mapped to a shade of gray. 0f = BLACK; 1f = WHITE
     * @return  An integer value representing a shade of gray.*/
    public int toRBG(float value){
        int INTvalue = (int)(255 * value);
        return( (INTvalue << 16) + (INTvalue << 8) + INTvalue);
    }

}//end of class
