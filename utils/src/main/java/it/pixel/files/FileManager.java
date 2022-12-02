package it.pixel.files;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type File manager.
 *
 * @param <T> the type parameter
 */
public class FileManager<T> {

    /**
     * The type Append object output stream.
     */
    private static class AppendObjectOutputStream extends ObjectOutputStream {

        /**
         * Instantiates a new Append object output stream.
         *
         * @param file the file
         * @throws IOException the io exception
         */
        public AppendObjectOutputStream(OutputStream file) throws IOException {
            super(file);
        }


        /**
         * Write stream header.
         *
         * @throws IOException the io exception
         */
        @Override
        protected void writeStreamHeader() throws IOException {
            // do not write a header
            reset();
        }
    }

    private final String filename;

    /**
     * Instantiates a new File manager.
     *
     * @param filePath the file path
     */
    public FileManager(String filePath){
        this.filename = filePath;
    }

    /**
     * Is first time boolean.
     *
     * @param file the file
     * @return the boolean
     */
    private static boolean isFirstTime(String file) {
        return new File(file).length() == 0;
    }

    /**
     * Gets file reader.
     *
     * @param file the file
     * @return the file reader
     * @throws IOException the io exception
     */
    private static ObjectInputStream getFileReader(String file) throws IOException {
        return new ObjectInputStream(new FileInputStream(file));
    }


    /**
     * Gets file writer.
     *
     * @param file the file
     * @return the file writer
     * @throws IOException the io exception
     */
    private static ObjectOutputStream getFileWriter(String file) throws IOException {
        if (isFirstTime(file)) {
            return new ObjectOutputStream(new FileOutputStream(file, true));
        } else {
            return new AppendObjectOutputStream(new FileOutputStream(file, true));
        }
    }

    /**
     * Save to log file the line
     *
     * @param obj the line to log to file
     * @throws IOException the io exception
     */
    public void write(T obj) throws IOException {
        ObjectOutputStream writer = getFileWriter(filename);
        writer.writeObject(obj);
        writer.close();
    }


    /**
     * Read file list.
     *
     * @return the list
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
    public List<T> readFile() throws Exception {
        if (!isFirstTime(filename)) {
            ObjectInputStream reader = getFileReader(filename);
            List<T> objects = new ArrayList<>();
            boolean go = true;
            do {
                try {
                    objects.add((T) reader.readObject());
                } catch (Exception e) {
                    go = false;
                }
            } while (go);
            reader.close();
            return objects;
        } else {
            return new ArrayList<>();
        }
    }


}
