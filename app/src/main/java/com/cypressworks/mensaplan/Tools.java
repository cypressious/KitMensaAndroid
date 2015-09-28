package com.cypressworks.mensaplan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Kirill Rakhman
 */
public class Tools {
    private Tools() {
    }

    public static void deleteFolder(final File folder) {
        final File[] files = folder.listFiles();
        if (files != null) { // some JVMs return null for empty dirs
            for (final File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void writeObject(
            final Serializable object, final File target) throws IOException {

        if (target.exists()) {
            target.delete();
        }

        final FileOutputStream fos = new FileOutputStream(target);
        final ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(object);
        oos.flush();
        oos.close();

    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T readObject(
            final File file) throws IOException, ClassNotFoundException {

        final FileInputStream fis = new FileInputStream(file);
        final ObjectInputStream ois = new ObjectInputStream(fis);
        final Object o = ois.readObject();
        ois.close();

        return (T) o;
    }

}
