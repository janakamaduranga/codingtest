package com.wiley.test.portablecache.interfaces.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiley.test.portablecache.exception.CacheException;
import com.wiley.test.portablecache.exception.ErrorCode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class FileUtil<K, V> {

    protected static final String folderPath;
    protected static final String cacheMetaFilePath;
    protected static final String delemeter;
    protected static final String cacheMetaContent;
    protected static final String cacheMetaTempFilePath;
    protected static final String DELETE = "DELETE";
    protected static final String GET = "GET";
    protected static final String ADD = "ADD";

    static {
        //can be read from properties for more customization
        folderPath = "C:" + File.separator + "cache";
        cacheMetaFilePath = folderPath + File.separator + "cache-meta.txt";
        delemeter = "=";
        cacheMetaContent = "cacheSize";

        cacheMetaTempFilePath = folderPath + File.separator + "tmp-cache-meta.txt";

        File directory = new File(folderPath);
        File cacheMetaFile = new File(cacheMetaFilePath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (!(cacheMetaFile.exists())) {
            try {
                if (cacheMetaFile.createNewFile()) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(cacheMetaFile))) {
                        writer.append(new StringBuilder(cacheMetaContent).append(delemeter).append("0").append(System.lineSeparator()).toString());
                    }
                }
            } catch (IOException e) {
                throw new CacheException(ErrorCode.ERROR_CREATING_CACHE_META_FILE);
            }
        }

    }

    protected String cacheFileName;
    protected String cacheType;
    protected int cacheSize;
    protected Class<V> type;

    public FileUtil(String cacheType, int cacheSize, Class<V> type) {
        this.cacheType = cacheType;
        this.cacheSize = cacheSize;
        this.type = type;

        cacheFileName = folderPath + File.separator + cacheType + ".txt";
        File cacheFile = new File(cacheFileName);
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                throw new CacheException(ErrorCode.ERROR_CREATING_CACHE_FILE);
            }
        }
    }

    public abstract V addNewEntryToCache(K key, V value) throws IOException;

    public abstract V getEntry(K key) throws IOException;

    public abstract V deleteEntry(K key) throws IOException;

    protected void moveFile(File file, String name) throws IOException {
        Path source = Paths.get(file.getAbsolutePath());
        Files.move(source, source.resolveSibling(name), StandardCopyOption.REPLACE_EXISTING);
    }

    public boolean isCacheFull() throws IOException {
        File cacheMetaFile = new File(cacheMetaFilePath);

        String currentLine;
        try (BufferedReader metaFileReader = new BufferedReader(new FileReader(cacheMetaFile));) {
            while ((currentLine = metaFileReader.readLine()) != null) {
                String[] metaEntries = currentLine.split(delemeter);
                if (metaEntries[0].equalsIgnoreCase(cacheMetaContent)) {
                    Integer cacheSizeInFile = Integer.parseInt(metaEntries[1]);
                    return cacheSizeInFile >= cacheSize;
                }
            }

        }
        return false;
    }

    public boolean updateCacheMeta(String action) throws IOException {
        File cacheMetaFile = new File(cacheMetaFilePath);
        File cacheMetaTempFile = new File(cacheMetaTempFilePath);

        String currentLine;
        try (BufferedReader metaFileReader = new BufferedReader(new FileReader(cacheMetaFile));
             BufferedWriter metaFilewriter = new BufferedWriter(new FileWriter(cacheMetaTempFile));) {
            while ((currentLine = metaFileReader.readLine()) != null) {
                String[] metaEntries = currentLine.split(delemeter);
                if (metaEntries[0].equalsIgnoreCase(cacheMetaContent)) {
                    Integer cacheSizeInFile = Integer.parseInt(metaEntries[1]);
                    if (!DELETE.equals(action) && cacheSizeInFile >= cacheSize) {
                        throw new CacheException(ErrorCode.CACHE_MAX_SIZE_REACHED);
                    }
                    if (!DELETE.equals(action)) {
                        metaFilewriter.write(new StringBuilder(cacheMetaContent).append(delemeter)
                                .append(++cacheSizeInFile).append(System.lineSeparator()).toString());
                    } else {
                        metaFilewriter.write(new StringBuilder(cacheMetaContent).append(delemeter)
                                .append(--cacheSizeInFile).append(System.lineSeparator()).toString());
                    }

                }
                break;
            }

        }

        moveFile(cacheMetaTempFile, cacheMetaFilePath);

        return true;

    }

    public V updateCache(K key, String action) throws IOException {

        String cacheTempFileName = folderPath + File.separator + "tmp-" + cacheType + ".txt";
        File cacheTempFile = new File(cacheTempFileName);
        File cacheFile = new File(cacheFileName);
        ObjectMapper mapper = new ObjectMapper();
        String value = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(cacheTempFile));) {
            String currentLine;
            String keyJson = mapper.writeValueAsString(key);
            while ((currentLine = reader.readLine()) != null) {
                // remove if the entry is already there and added it to the end
                if (!currentLine.split(delemeter)[0].equals(keyJson)) {
                    writer.write(currentLine + System.lineSeparator());
                } else {
                    value = currentLine;
                }

            }
            if (GET.equals(action)) {
                // add new entry
                writer.write(value + System.lineSeparator());
            } else if (DELETE.equals(action)) {
                updateCacheMeta(action);
            }

        }
        moveFile(cacheTempFile, cacheFile.getAbsolutePath());
        return value != null ? mapper.readValue(value.split(delemeter)[1], type) : null;
    }

    public void clearCache() throws IOException {
        File cacheFile = new File(cacheFileName);
        File cacheMetaFile = new File(cacheMetaFilePath);
        File cacheMetaTempFile = new File(cacheMetaTempFilePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile));
             BufferedReader metaFileReader = new BufferedReader(new FileReader(cacheMetaFile));
             BufferedWriter metaFilewriter = new BufferedWriter(new FileWriter(cacheMetaTempFile));) {
            String currentLine;
            int lineCount = 0;
            while ((currentLine = reader.readLine()) != null) {
                ++lineCount;
            }

            while ((currentLine = metaFileReader.readLine()) != null) {
                String[] metaEntries = currentLine.split(delemeter);
                if (metaEntries[0].equalsIgnoreCase(cacheMetaContent)) {
                    Integer cacheSizeInFile = Integer.parseInt(metaEntries[1]);
                    cacheSizeInFile = cacheSizeInFile - lineCount;
                    metaFilewriter.write(new StringBuilder(cacheMetaContent).append(delemeter)
                            .append(cacheSizeInFile > 0 ? cacheSizeInFile : 0).append(System.lineSeparator())
                            .toString());
                    break;
                }
            }
        }
        moveFile(cacheMetaTempFile, cacheMetaFilePath);
        Path path = Paths.get(cacheFile.getAbsolutePath());
        Files.delete(path);
    }

    public V queryForKey(K key) throws IOException {
        File cacheFile = new File(cacheFileName);
        ObjectMapper mapper = new ObjectMapper();
        String value = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile));) {
            String currentLine;
            String keyJson = mapper.writeValueAsString(key);
            while ((currentLine = reader.readLine()) != null) {
                // remove if the entry is already there and added it to the end
                if (currentLine.split(delemeter)[0].equals(keyJson)) {
                    value = currentLine;
                    break;
                }

            }
        }
        return value == null ? null : mapper.readValue(value.split(delemeter)[1], type);
    }
}
