/*******************************************************************************
 * Copyright 2017 Johannes Boczek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package de.sogomn.engine.util;


import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This class holds several useful methods related to files and IO-streams.
 * Uses the ("old") Java IO model. I don't like NIO.
 * @author Sogomn
 *
 */
public final class FileUtils {

    private static final int BUFFER_SIZE = 1024;

    private FileUtils() {
        //...
    }

    private static byte[] readData(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[BUFFER_SIZE];

        int bytesRead = 0;

        while ((bytesRead = in.read(buffer)) != 1) {
            out.write(buffer, 0, bytesRead);
        }

        return out.toByteArray();
    }

    private static String[] readLines(final InputStream in) throws IOException {
        final ArrayList<String> lines = new ArrayList<String>();
        final InputStreamReader reader = new InputStreamReader(in);
        final BufferedReader bufferedReader = new BufferedReader(reader);

        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }

        final String[] lineArray = lines.stream().toArray(String[]::new);

        return lineArray;
    }

    /**
     * Reads all data from the given file (classpath!).
     * @param path The path to the file
     * @return The data or null in case of failure
     */
    public static byte[] readInternalData(final String path) {
        final InputStream in = FileUtils.class.getResourceAsStream(path);

        try {
            return readData(in);
        } catch (final Exception ex) {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * Reads all data from the given external file.
     * @param path The path to the file
     * @return The data or null in case of failure
     */
    public static byte[] readExternalData(final String path) {
        try {
            final FileInputStream in = new FileInputStream(path);

            return readData(in);
        } catch (final Exception ex) {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * Reads all lines from the given file (classpath!).
     * @param path The path to the file
     * @return The lines or null in case of failure
     */
    public static String[] readInternalLines(final String path) {
        final InputStream in = FileUtils.class.getResourceAsStream(path);

        try {
            return readLines(in);
        } catch (final Exception ex) {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * Reads all lines from the given external file.
     * @param path The path to the file
     * @return The lines or null in case of failure
     */
    public static String[] readExternalLines(final String path) {
        try {
            final FileInputStream in = new FileInputStream(path);

            return readLines(in);
        } catch (final Exception ex) {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * Writes data to the given path.
     * @param path The path
     * @param data The data
     * @return True on success; false otherwise
     */
    public static boolean writeData(final String path, final byte[] data) {
        try {
            final FileOutputStream out = new FileOutputStream(path);

            out.write(data);
            out.flush();
            out.close();

            return true;
        } catch (final Exception ex) {
            ex.printStackTrace();

            return false;
        }
    }

    /**
     * Writes lines of text to the given path.
     * Appends a line break to every string.
     * @param path The path
     * @param lines The lines
     * @return True on success; false otherwise
     */
    public static boolean writeLines(final String path, final String... lines) {
        try {
            final FileOutputStream out = new FileOutputStream(path);
            final OutputStreamWriter writer = new OutputStreamWriter(out);
            final BufferedWriter bufferedWriter = new BufferedWriter(writer);

            for (final String line : lines) {
                bufferedWriter.write(line + "\r\n");
            }

            bufferedWriter.flush();
            bufferedWriter.close();

            return true;
        } catch (final Exception ex) {
            ex.printStackTrace();

            return false;
        }
    }

    /**
     * Creates a new file at the given path.
     * Also creates all nonexistent parent directories.
     * If the file already exists, nothing will happen.
     * @param file The file to be created
     * @return True on success; false otherwise
     */
    public static boolean createFile(final File file) {
        final File parent = file.getParentFile();

        try {
            createDirectory(parent);

            return file.createNewFile();
        } catch (final Exception ex) {
            ex.printStackTrace();

            return false;
        }
    }

    /**
     * Creates a new file at the given path.
     * Also creates all nonexistent parent directories.
     * If the file already exists, nothing will happen.
     * @param path The path
     * @return True on success; false otherwise
     */
    public static boolean createFile(final String path) {
        final File file = new File(path);

        return createFile(file);
    }

    /**
     * Deletes the specified file.
     * @param file The file to be deleted
     * @return True on success; false otherwise
     */
    public static boolean deleteFile(final File file) {
        try {
            return file.delete();
        } catch (final Exception ex) {
            ex.printStackTrace();

            return false;
        }
    }

    /**
     * Deletes the specified file.
     * @param path The path to the file
     * @return True on success; false otherwise
     */
    public static boolean deleteFile(final String path) {
        final File file = new File(path);

        return deleteFile(file);
    }

    /**
     * Creates a new folder and all necessary parent folders.
     * If the folder already exists, nothing will happen.
     * @param file The target directory
     * @return True if the directories were created; false otherwise
     */
    public static boolean createDirectory(final File file) {
        try {
            return file.mkdirs();
        } catch (final Exception ex) {
            ex.printStackTrace();

            return false;
        }
    }

    /**
     * Creates a new folder and all necessary parent folders.
     * If the folder already exists, nothing will happen.
     * @param path The path to the target directory
     * @return True if the directories were created; false otherwise
     */
    public static boolean createDirectory(final String path) {
        final File file = new File(path);

        return createDirectory(file);
    }

    /**
     * Executes the given file.
     * @param file The file to be executed
     * @return True on success; false otherwise
     */
    public static boolean executeFile(final File file) {
        final boolean desktopSupported = Desktop.isDesktopSupported();

        if (desktopSupported && file.exists()) {
            final Desktop desktop = Desktop.getDesktop();
            final boolean canOpen = desktop.isSupported(Action.OPEN);

            if (canOpen) {
                try {
                    desktop.open(file);

                    return true;
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return false;
    }

    /**
     * Executes the given file.
     * @param path The path to the file
     * @return True on success; false otherwise
     */
    public static boolean executeFile(final String path) {
        final File file = new File(path);

        return executeFile(file);
    }
    public static void copyFile(Path file, Path destination) {
        Path sourceFile = Paths.get(String.valueOf(file));
        Path targetDir = Paths.get(String.valueOf(destination));
        Path targetFile = targetDir.resolve(sourceFile.getFileName());

        try {

            Files.copy(sourceFile, targetFile);

        } catch (FileAlreadyExistsException ex) {
            System.err.format("File %s already exists.", targetFile);
        } catch (IOException ex) {
            System.err.format("I/O Error when copying file");
        }

    }

}