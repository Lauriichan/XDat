package me.lauriichan.data.xdat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class XDatFileManager extends XDatManager {

    private final File directory;
    private final boolean compressed;

    private final ConcurrentHashMap<String, File> fileCache = new ConcurrentHashMap<>();

    public XDatFileManager(final File directory) {
        this(directory, false);
    }

    public XDatFileManager(final XDatTable table, final File directory) {
        this(table, directory, false);
    }

    public XDatFileManager(final File directory, final boolean compressed) {
        this.directory = directory;
        this.compressed = compressed;
    }

    public XDatFileManager(final XDatTable table, final File directory, final boolean compressed) {
        super(table);
        this.directory = directory;
        this.compressed = compressed;
    }

    public final boolean isCompressed() {
        return compressed;
    }

    public final File getDirectory() {
        return directory;
    }
    
    @Override
    protected void log(String message, Exception exception) {}

    private File createFile(String dataKey) {
        return new File(directory, dataKey + ".xdat");
    }

    @Override
    protected boolean exists(String dataKey) throws IOException {
        return fileCache.computeIfAbsent(dataKey, this::createFile).exists();
    }

    @Override
    protected void delete(String dataKey) throws IOException {
        File file = fileCache.computeIfAbsent(dataKey, this::createFile);
        if (!file.exists()) {
            return;
        }
        file.delete();
    }

    @Override
    protected InputStream openInput(String dataKey) throws IOException {
        FileInputStream fileInput = new FileInputStream(fileCache.computeIfAbsent(dataKey, this::createFile));
        if (compressed) {
            return new GZIPInputStream(fileInput);
        }
        return fileInput;
    }

    @Override
    protected OutputStream openOutput(String dataKey) throws IOException {
        File file = fileCache.computeIfAbsent(dataKey, this::createFile);
        if (!file.exists()) {
            if (!directory.exists()) {
                directory.mkdirs();
            }
            file.createNewFile();
        }
        FileOutputStream fileOutput = new FileOutputStream(file, true);
        if (compressed) {
            return new GZIPOutputStream(fileOutput);
        }
        return fileOutput;
    }

}
