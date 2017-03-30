package com.ppoapp.service.cache;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * in - set key objects received from data base (memory)
 * out - set key objects received from in when sizeIn = maxSizeIn (file system)
 * main - set key objects received from out when user try get key
 * @param <K> key
 * @param <V> value
 */
public class Cache<K, V> {

    private HashMap<K,V> wordsToMemory;
    private LinkedHashSet<K> in;
    private LinkedHashSet<K> out;
    private LinkedHashSet<K> main;
    public int maxSizeMain, maxSizeOut, maxSizeIn, sizeCatch;
    public int sizeMain, sizeOut, sizeIn;
    private double shareIn;
    private double shareOut;
    private double shareMain;
    public int countIn, countOut, countMain;
    private String pathToCacheFiles;

    public Cache(int sizeCatch){
        if(sizeCatch <= 0){
            throw new IllegalArgumentException("the catch size less or equals then zero");
        }
        this.sizeCatch = sizeCatch;
        wordsToMemory = new HashMap<>();
        in = new LinkedHashSet<>();
        out = new LinkedHashSet<>();
        main = new LinkedHashSet<>();

        shareIn = getMaxSize("in");
        shareOut = getMaxSize("out");
        shareMain = getMaxSize("main");
        pathToCacheFiles = getPathToCacheFiles();

        sizeIn = 0;
        sizeOut = 0;
        sizeMain = 0;
        countIn = 0;
        countMain = 0;
        countOut = 0;

        if(shareIn + shareOut + shareMain != 1.0){
            maxSizeIn = (int) (sizeCatch * 0.1);
            maxSizeOut = (int) (sizeCatch * 0.7);
            maxSizeMain = (int) (sizeCatch * 0.2);
        }else{
            maxSizeIn = (int) (sizeCatch * shareIn);
            maxSizeOut = (int) (sizeCatch * shareOut);
            maxSizeMain = (int) (sizeCatch * shareMain);
        }
    }

    public V getV(K key){
        if (key == null){
            throw new NullPointerException("Key is null");
        }
        V value = null;
        synchronized (this){
            if(in.contains(key)) {
                value = wordsToMemory.get(key);

                countIn++;
                return value;

            }else if(main.contains(key)){
                value = wordsToMemory.get(key);
                main.remove(key);
                main.add(key);

                countMain++;
                return value;

            }else if(out.contains(key)){
                value = getValueFromFile(key);
                moveValueFromFileToMemory(key, value);

                countOut++;
                return value;
            }else {
                return null;
            }
        }
    }

    public void putValueFormDB(K key, V value){
        if (value == null) {
            throw new NullPointerException("you want put to cache value which is null");
        }
        synchronized (this) {
            if (maxSizeIn > sizeIn) {
                putValueToMemory(key, value, in);
                sizeIn++;
            } else {
                // супер медленный способ найти последний эелемент
                K oldKey = (K) in.toArray()[in.size() - 1];
                if (oldKey != null) {
                    addFileToSystem(oldKey);
                    in.remove(oldKey);
                    wordsToMemory.remove(oldKey);
                    sizeIn--;
                    putValueToMemory(key, value, in);
                    sizeIn++;
                }
            }
        }
    }

    public String getPathToCacheFiles(){
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File("src/main/resources/cache.prop")));
            return properties.getProperty("path");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private double getMaxSize(String nameMemory){
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File("src/main/resources/cache.prop")));
            return Double.parseDouble(properties.getProperty(nameMemory));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void putValueToMemory(K key, V value, LinkedHashSet<K> set){
        wordsToMemory.put(key, value);
        set.add(key);
    }

    private void moveValueFromFileToMemory(K key, V value){
        synchronized (this) {
            main.add(key);
            if (maxSizeMain > sizeMain) {
                putValueToMemory(key, value, main);
                sizeMain++;
                removeFileFromSystem(key);
            } else {
                // супер медленный способ найти последний эелемент
                K oldKey = (K) main.toArray()[main.size() - 1];
                if (oldKey != null) {
                    main.remove(oldKey);
                    wordsToMemory.remove(oldKey);
                    sizeMain--;
                    putValueToMemory(key, value, main);
                    sizeMain++;
                    removeFileFromSystem(key);
                }
            }
        }
    }

    /**
     * remove file from files system
     * @param key
     */
    private void removeFileFromSystem(K key){
        try {
            new File(pathToCacheFiles + key).delete();
            out.remove(key);
            sizeOut--;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * save file in files system
     * @param key
     */
    private void addFileToSystem(K key){
        synchronized (this){
            if(maxSizeOut > sizeOut){
                serializable(key);
            }else {
                // супер медленный способ найти последний эелемент
                K oldKey = (K) out.toArray()[out.size() - 1];
                removeFileFromSystem(oldKey);
                serializable(key);
            }
        }
    }
    public void serializable(K key){
        File newFile = new File(pathToCacheFiles + key);
        try (FileOutputStream fos = new FileOutputStream(newFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(wordsToMemory.get(key));
            out.add(key);
            sizeOut++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get file from files system
     * @param key
     * @return V
     */
    private V getValueFromFile(K key){
        File file = new File(pathToCacheFiles + key);
        V value = null;
        try(FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis)){
            value = (V) ois.readObject();
        }catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }


}
