/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.plugins.binary_convert;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.swing.TransferHandler.TransferSupport;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import de.drop_converter.plugin.annotations.ConverterPluginDetails;
import de.drop_converter.plugin.exception.ConverterException;

/**
 * Convert data to hex.
 * 
 * @author Thomas Chojecki
 */
@ConverterPluginDetails(authorEmail = "info@rayman2200.de", authorName = "Thomas Chojecki", pluginDescription = "Convert data into hex", pluginName = "Data2Hex", pluginVersion = "0.0.1")
public class Data2Hex extends AbstractDataPlugin
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
                out.write(Hex.encodeHexString(buffer).getBytes("UTF-8"));
              } else {
                byte[] tmp = Arrays.copyOf(Hex.encodeHexString(buffer).getBytes("UTF-8"), count);
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
          byte[] encode = Hex.encodeHexString(data.getBytes()).getBytes();
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

  public static class HexInputStream extends FilterInputStream
  {

    protected HexInputStream(InputStream in)
    {
      super(in);
    }

  }
}
