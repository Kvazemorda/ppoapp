package com.ppoapp.controller.imageLoader;


import android.app.Activity;
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
import com.ppoapp.R;
import com.ppoapp.data.HelperFactory;
import com.ppoapp.data.dao.ContentDAO;
import com.ppoapp.entity.Content;
import com.ppoapp.entity.ImageJson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.Telephony.Mms.Part.FILENAME;
import static com.ppoapp.constant.Constans.PPOSITE;

public class MyImageLoader extends Activity {
    Context context;
    ImageView imageView;
    Content content;
    DisplayImageOptions options;
    private static String PATH = "/ppo/data/";

    public MyImageLoader(Context context, ImageView imageView, Content content) {
        this.context = context;
        this.imageView = imageView;
        this.content = content;
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.logo_ppo)
                .showImageForEmptyUri(R.drawable.logo_ppo)
                .build();
    }

    public void loadImage(){
        String pathToFile = getPathToFile(content);
        if(pathToFile != null){
            ImageLoader.getInstance().loadImage(getPathToFile(content), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if(content.getLocalImage() == null){
                        //проверяю доступ к СД карте
                        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                            Log.d("TAG", "SD-карта не доступна: " + Environment.getExternalStorageState());
                            //записываю файл с название id контента
                            File fileInternal = new File(context.getFilesDir(), content.getId() + ".jpeg");
                            writeFile(fileInternal, loadedImage);
                            //сохраняю пусть к файлу
                            content.setLocalImage(fileInternal.getAbsolutePath());
                         //   System.out.println("!!!!!!!!!!!!!!!!!!! грузим картинку из БД");
                            ImageLoader.getInstance().displayImage("file:///" + content.getLocalImage(), imageView);
                            Log.d("TAG", "Картинка загрузилась");
                        }else{
                            File imageFileDir = new File(Environment.getExternalStorageDirectory() + PATH + context.getPackageName() + "/Image");
                            if (! imageFileDir.exists()){
                                if (! imageFileDir.mkdirs()){
                                    Log.d("TAG", "Картинка не скачалась");
                                }
                            }
                            File mediaFile = new File(imageFileDir.getPath() + File.separator + content.getId() + ".jpeg");
                            try {
                                FileOutputStream fos = new FileOutputStream(mediaFile);
                                loadedImage.compress(Bitmap.CompressFormat.JPEG, 20, fos);
                                content.setLocalImage(mediaFile.getAbsolutePath());
                                HelperFactory.getHelper().getContentDAO().update(content);
                                fos.close();
                                ImageLoader.getInstance().displayImage("file:///" + content.getLocalImage(), imageView);

                            } catch (SQLException e) {
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Log.d("TAG", "File not found: " + e.getMessage());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("TAG", "Error accessing file: " + e.getMessage());
                            }
                        }
                    }else {
                        ImageLoader.getInstance().displayImage("file:///" + content.getLocalImage(), imageView);
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
            e.printStackTrace();
            Log.d("TAG", "File not found: " + e.getMessage());
            return null;
        }
    }

    void writeFile(File file, Bitmap loadedImage) {
        try {
            // отрываем поток для записи
            FileOutputStream fos = new FileOutputStream(file);
            loadedImage.compress(Bitmap.CompressFormat.JPEG, 20, fos);
            HelperFactory.getHelper().getContentDAO().update(content);
            fos.close();
            Log.d("TAG", "Файл записан");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
