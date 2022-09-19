package app.frame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class Frame extends JFrame{

    //Size in pixels of the window (square shape)
    public static final int W_SIZE = 512;
    Core panel;

    public Frame(){
        panel = new Core();

        this.setTitle("Noise generator");
        this.setSize(W_SIZE,W_SIZE);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                panel.stopThread();
                super.windowClosing(e);
            }
        });
        this.setVisible(true);
    }



    public static void main(String args[]){
        new Frame();
    }

    public static void log(String s){System.out.print(s);}
}//end of class
