/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.plugins.binary_convert;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.swing.TransferHandler.TransferSupport;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import de.drop_converter.plugin.annotations.ConverterPluginDetails;
import de.drop_converter.plugin.exception.ConverterException;

/**
 * Convert hex to data.
 * 
 * @author Thomas Chojecki
 */
@ConverterPluginDetails(authorEmail = "info@rayman2200.de", authorName = "Thomas Chojecki", pluginDescription = "Convert hex to data", pluginName = "Hex2Data", pluginVersion = "1.0.0")
public class Hex2Data extends AbstractDataPlugin
{

  @Override
  public boolean importData(TransferSupport support) throws ConverterException
  {
    try {
      if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        for (File file : files) {
          FileInputStream fis = null;
          OutputStream out = null;
          try {
            out = getOutputStream(file, ".hex");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[bufferSize];
            int count = 0;
            while (-1 != (count = fis.read(buffer))) {
              if (count == bufferSize) {
                out.write(Hex.decodeHex(new String(buffer).toCharArray()));
              } else {
                byte[] tmp = Arrays.copyOf(Hex.decodeHex(new String(buffer).toCharArray()), count);
                out.write(tmp);
              }
            }
          } catch (Exception e) {
            throw new ConverterException(e);
          } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fis);
          }
        }
        return true;
      } else if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
        OutputStream out = null;
        try {
          byte[] encode = Hex.decodeHex(data.toCharArray());
          out = getOutputStream(null, ".hex");
          out.write(encode);
        } catch (Exception e) {
          throw new ConverterException(e);
        } finally {
          IOUtils.closeQuietly(out);
        }
      }
    } catch (Exception e) {
      throw new ConverterException(e);
    }

    return false;
  }
}
