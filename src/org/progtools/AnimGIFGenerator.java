/*
 * AnimGIFGenerator.java
 * An image processor to generate frammed images with copyright messages
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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

/**
 * A GIF generator from Sony's Multi-Burst image files.
 * @author Paulo
 */
public class AnimGIFGenerator {
    private int imagesPerRow;
    private int imagesPerCol;
    
    
    /**
     * Prepares the class instance to process images of a given size.
     *
     * @param colCount The number of frames per column on the image.
     * @param rowCount The number of frames per row on the image.
     */
    public AnimGIFGenerator(int colCount, int rowCount) {
        imagesPerCol = colCount;
        imagesPerRow = rowCount;
    }
    
    /**
     * Given a pathname to a jpg image, processes it and generates
     * an animated gif in the same directory as the given image.
     * @param pathnameToImage The pathname to the jpg image file.
     * @throws IOException If there is an error reading or writing the image data.
     */
    public void generateGIF(String pathnameToImage) throws IOException{
        // Prepare the amout of frames to generate
        BufferedImage[] frames = getMovieFrames(new File(pathnameToImage));
        
        // Now write the image
        File movieFile = convertFilename(pathnameToImage);
        saveAnimation(movieFile, frames);
    }

    /**
     * Reads the jpg image file and splits the frames into individual image
     * objects.
     *
     * @param filename The pathname to the jpg image file.
     * @returns An array containing the animation frames.
     * @throws IOException If there is an error reading or writing the image data.
     */
    private BufferedImage[] getMovieFrames(File filename) throws IOException {
        // Load the images
        BufferedImage originalImg = ImageIO.read(filename);
        
        // Get the width/height per frame
        int frameWidth = originalImg.getWidth() / imagesPerRow;
        int frameHeight = originalImg.getHeight() / imagesPerCol;
        int frameCount = imagesPerCol * imagesPerRow;
        
        // Prepare the amout of frames to generate
        BufferedImage[] frames = new BufferedImage[frameCount];
        for (int i = 0; i < frames.length; i++) {
            BufferedImage destImg = new BufferedImage(frameWidth, frameHeight, originalImg.getType());
            
            // Now copy the frame into the image
            Graphics2D g = (Graphics2D) destImg.getGraphics();
            
            int x = frameWidth * (i % imagesPerRow);
            int y = frameHeight * (i / imagesPerCol);
            g.drawImage(originalImg, 0, 0, frameWidth, frameHeight, x, y, x + frameWidth, y + frameHeight,null);
            frames[i] = destImg;
        }
        
        return frames;
    }
    
    /**
     * Converts the filename into a file object targeting
     * a file with the same path as the given argument but
     * with a .gif extension.
     *
     * @param filename The filename to convert. Must have an extension
     * @return An instance of File pointing to the new name.
     */
    private File convertFilename(String filename) {
        String newName = null;
        int pos = filename.lastIndexOf('.');
        if (pos > 0) {
            newName = filename.substring(0, pos) + ".gif";
        }
        return new File(newName);
    }
    
    /**
     * Saves a sequence of images as an animated gif.
     *
     * @param filename The name of the file where to write to. It is advisable to end in ".gif".
     * @param frames The images to write to the file as animation frames.
     * @throws IOException If there is an error writing to the destination file.
     */
    private void saveAnimation(File filename, BufferedImage[] frames) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("gif");
        ImageWriter writer = writers.next(); // Always assume GIF is available

        // prepare the sequence writer
        writer.setOutput(new FileImageOutputStream(filename));
        writer.prepareWriteSequence(null);
        
        // write the sequence
        for (int i = 0; i < frames.length; i++) {
            IIOImage img = new IIOImage(frames[i], null, null);
            writer.writeToSequence(img, null);
            
        }
        
        // terminate the sequence writer
        writer.endWriteSequence();
    }
}
