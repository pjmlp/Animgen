/*
 * DropHandler.java
 * Processes the droping of files into a Swing component.
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * The class manages the processing of drop messages
 * into a Swing component.
 * Only allows the drop of file objects that have a
 * .jpg/.jpeg extension.
 *
 * @author ppinto
 */
public class DropHandler extends TransferHandler {
    /**
     * Validates if the data that is being dropped is valid.
     * @param comp Where the drop is performed.
     * @param transferFlavors Type of objects being dropped.
     * @return True if the drop is acceptable.
     */
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (int i = 0; i < transferFlavors.length; i++) {
            if (!transferFlavors[i].isFlavorJavaFileListType()) {
                return false;
            }
        }
        
        // If we reach here all drops are acceptable
        return true;
    }
    
    
    /**
     * Process the objects being dropped.
     * @param comp The component that will receive the data. Must use DefaultListModel as a model.
     * @param t Data being transfered.
     * @returns true if the data was sucessfuly imported.
     */
    @Override
    public boolean importData(JComponent comp, Transferable t) {
        try {
            JList list = (JList) comp;
            DefaultListModel model = (DefaultListModel) list.getModel();
            
            List files= (List) t.getTransferData(DataFlavor.javaFileListFlavor);
            Iterator iter = files.iterator();
            while (iter.hasNext()) {
                File fd = (File) iter.next();
                String pathname = fd.getPath();
                if (isValidFileType(pathname)) {
                    model.addElement(pathname);
                }
            }
            
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Tells if this is a valid type of file to process.
     * @param filename The complete path to the filename.
     * @returns true if this is a jpeg based image.
     */
    private boolean isValidFileType(String filename) {
        String lowercase = filename.toLowerCase();
        return lowercase.endsWith(".jpg") || lowercase.endsWith(".jpeg");
    }
}
