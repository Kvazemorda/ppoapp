package com.ppoapp.controller;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ppoapp.data.HelperFactory;
import com.ppoapp.data.dao.ContentDAO;
import com.ppoapp.entity.Content;
import com.ppoapp.entity.ImageJson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.ppoapp.constant.Constans.PPOSITE;

public class MyImageLoader {
    Context context;
    ImageView imageView;


    public MyImageLoader(Context context, ImageView imageView) {
        this.context = context;
        this.imageView = imageView;

        Executor downloadExecutor = Executors.newFixedThreadPool(5);
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memClass = am.getMemoryClass();
        final int memoryCacheSize = 1024 * 1024 * memClass / 8;

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(android.R.color.transparent)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .taskExecutor(downloadExecutor)
                .memoryCache(new UsingFreqLimitedMemoryCache(memoryCacheSize)) // 2 Mb
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public void loadImage(final Content content){
        String pathToFile = getPathToFile(content);
        if(pathToFile != null){
            ImageLoader.getInstance().loadImage(getPathToFile(content), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    File imageFileDir = new File(Environment.getExternalStorageDirectory() + "/ppo/data/"+ context.getPackageName() + "/Image");
                    if (! imageFileDir.exists()){
                        if (! imageFileDir.mkdirs()){
                            Log.d("TAG", "Картинка не скачалась");
                        }
                    }
                    File mediaFile = new File(imageFileDir.getPath() + File.separator + content.getId() + ".jpeg");

                    try {
                        FileOutputStream fos = new FileOutputStream(mediaFile);
                        loadedImage.compress(Bitmap.CompressFormat.PNG, 90, fos);
                        //loadedImage.createScaledBitmap(loadedImage, )
                        content.setLocalImage(imageFileDir.getPath() + File.separator + content.getId() + ".jpeg");
                        HelperFactory.getHelper().getContentDAO().update(content);
                        fos.close();
                        ImageLoader.getInstance().displayImage("file:///mnt/" + content.getLocalImage(), imageView);

                    } catch (SQLException e) {
                            e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        Log.d("TAG", "File not found: " + e.getMessage());
                    } catch (IOException e) {
                        Log.d("TAG", "Error accessing file: " + e.getMessage());
                    }
                }
            });
        }
    }

    private String getPathToFile(Content content){
        ObjectMapper mapper = new ObjectMapper();
        ImageJson imageJson = null;
        try {
            imageJson = mapper.readValue(content.getImages(), ImageJson.class);
            String htmlString = PPOSITE + imageJson.getImage_intro();
            return htmlString;
        } catch (IOException e) {
            Log.d("TAG", "File not found: " + e.getMessage());
            return null;
        }
    }
}
