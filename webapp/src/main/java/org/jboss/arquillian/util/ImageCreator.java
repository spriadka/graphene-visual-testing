/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.arquillian.util.Base64;

/**
 *
 * @author spriadka
 */
public class ImageCreator {

    public static File createImageFromBase64String(String base64String, String fileName) {
        String[] splitData = base64String.split(";");
        if (!splitData[0].contains("data:image")) {
            return null;
        } else {
            String fileSuffix = splitData[0].split("/")[1];
            File toCreate = new File(fileName + "." + fileSuffix);
            byte[] image = Base64.decodeFast(splitData[1].split(",")[1]);
            boolean wasCreated = false;
            try {
                wasCreated = ImageIO.write(ImageIO.read(new ByteArrayInputStream(image)), fileSuffix, toCreate);
            } catch (IOException ex) {
                Logger.getLogger(ImageCreator.class).error(ex);
            } finally {
                if (wasCreated) {
                    return toCreate;
                } else {
                    return null;
                }
            }
        }
    }

    public static byte[] getByteArrayFromImageFile(File fromFile) {
        try {
            return IOUtils.toByteArray(new FileInputStream(fromFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImageCreator.class).error(ex);
        } catch (IOException ex) {
            Logger.getLogger(ImageCreator.class).error(ex);
        }
        return null;
    }
}
