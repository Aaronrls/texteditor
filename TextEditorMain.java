/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texteditor;

import javax.swing.JFrame;

/**
 *
 * @author taylorbrown
 */
public class TextEditorMain {
    public static void main(String [] args){
        TextEditorFrame frame = new TextEditorFrame();
        //exit on close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //center
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
