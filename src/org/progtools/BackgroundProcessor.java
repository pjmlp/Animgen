/*
 * BackgroundProcessor.java
 * Application window for the generator class.
 * Copyright (C) 2007  Paulo Pinto
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.progtools;

import java.io.IOException;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * Swing worker thread to process the image generation in the background.
 * @author Paulo Pinto
 */
public class BackgroundProcessor implements Runnable  {
    private JProgressBar progressBar;
    private Enumeration<?> images;
    private AnimGIFGenerator generator;
    private JFrame owner;
            
    /**
     * Creates a new instance of BackgroundProcessor.
     * @param owner The window that owns the background task.
     * @param progressBar The progress bar to update asynchronously.
     * @param images The set of images to process.
     * @param generator The generator that converts the images to the desired format.
     */
    public BackgroundProcessor(JFrame owner, JProgressBar progressBar, Enumeration<?> images, AnimGIFGenerator generator) {
        this.owner = owner;
        this.progressBar = progressBar;
        this.images = images;
        this.generator = generator;
    }

    /**
     * Implements the thread action
     */
    @Override
    public void run() {
        int count = 1;
        while (images.hasMoreElements()) {
            try {
                updateProgressBar(count++);
                generator.generateGIF((String)images.nextElement());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(owner, e, "Error while processing file", JOptionPane.ERROR_MESSAGE);
            }
        }
        resetState();
    }
    
    /**
     * Updates the user interface progress bar in a thread safe way
     * @param value The current value to be setted on the progress bar.
     */
    private void updateProgressBar(final int value) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    progressBar.setValue(value);
                }
            });
        } catch (Exception ex) {
            // Just ignore it for the time being
        }
    }

    /**
     * Sets the cursor and progress bar again to a normal state
     */
    private void resetState() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    progressBar.setValue(0);
                    owner.setCursor(null);
                }
            });
        } catch (Exception ex) {
            // Just ignore it for the time being
        }
    }
    
}
