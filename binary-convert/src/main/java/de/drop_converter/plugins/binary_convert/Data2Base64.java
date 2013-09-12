/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.drop_converter.plugins.binary_convert;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

import javax.swing.TransferHandler.TransferSupport;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.IOUtils;

import de.drop_converter.plugin.annotations.ConverterPluginDetails;
import de.drop_converter.plugin.exception.ConverterException;

/**
 * Convert data to base64.
 * 
 * @author Thomas Chojecki
 */
@ConverterPluginDetails(authorEmail = "info@rayman2200.de", authorName = "Thomas Chojecki", pluginDescription = "Convert data into base64", pluginName = "Data2Base64", pluginVersion = "0.0.1")
public class Data2Base64 extends AbstractDataPlugin
{

  @Override
  public boolean importData(TransferSupport support) throws ConverterException
  {
    try {
      if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
        List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        for (File file : files) {
          FileInputStream fis = null;
          Base64InputStream base64IS = null;
          OutputStream out = null;
          try {
            fis = new FileInputStream(file);
            base64IS = new Base64InputStream(fis, true);
            out = getOutputStream(file, ".base64");
            IOUtils.copy(base64IS, out);
          } catch (Exception e) {
            throw new ConverterException(e);
          } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(base64IS);
            IOUtils.closeQuietly(fis);
          }
        }
        return true;
      } else if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
        OutputStream out = null;
        try {
          byte[] encode = new Base64().encode(data.getBytes());
          out = getOutputStream(null, ".base64");
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
