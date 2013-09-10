/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.plugins.binary_convert;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.TransferHandler.TransferSupport;

import de.drop_converter.plugin.ConverterPluginAdapter;
import de.drop_converter.plugin.exception.ConverterException;

/**
 * Abstract plugin class that provide some common functions. This is a good base for creating many plugins inside one
 * jar.
 * 
 * @author Thomas Chojecki
 */
public abstract class AbstractDataPlugin extends ConverterPluginAdapter
{

  @Override
  public boolean canImport(TransferSupport support)
  {
    return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
        || support.isDataFlavorSupported(DataFlavor.stringFlavor);
  }

  /**
   * Try to determinate the output file. The drop-converter does not provide any hint, so we try to guess where the
   * output should be put.
   * 
   * @param original should be the original file that was converted. Can be null, so we use the desktop as output the
   *          result.
   * @return the ouputfile
   */
  public File getOutputFile(File original)
  {
    String filename;
    if (original == null) {
      filename = "converted";
    } else {
      filename = original.getName();
    }

    int lastIndex = filename.lastIndexOf(".");
    if (lastIndex == -1) {
      lastIndex = filename.length();
    }
    filename = filename.substring(0, lastIndex).concat(".hex");

    File dir;
    if (original == null) {
      dir = new File(System.getProperty("user.home"), "Desktop");
    } else {
      dir = original.getParentFile();
    }
    return new File(dir, filename);
  }

  /**
   * Try to determinate the output file as OutputStream. The drop-converter does not provide any hint, so we try to
   * guess where the output should be put.
   * 
   * @param original should be the original file that was converted. Can be null, so we use the desktop as output the
   *          result.
   * @return an OutputStream pointing to the result file.
   * @throws ConverterException
   * @see {@link #getOutputFile(File)}
   */
  public OutputStream getOutputStream(File original) throws ConverterException
  {
    try {
      return new FileOutputStream(getOutputFile(original));
    } catch (FileNotFoundException e) {
      throw new ConverterException(e);
    }
  }

}
