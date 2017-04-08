package com.ppoapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ppoapp.constant.Constans;
import com.ppoapp.controller.EndlessScrollListener;
import com.ppoapp.controller.imageLoader.ImageLoaderInit;
import com.ppoapp.data.HelperFactory;
import com.ppoapp.entity.Content;
import com.ppoapp.service.AdapterContent;
import com.ppoapp.service.MyForeground;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    protected ArrayList<Content> contents;
    protected Long id = 1l;
    protected int totalItems = 0;
    boolean loading;
    public ListView listViewContent;
    protected AdapterContent adapterContent;
    protected int limitQueryToServer = 5;
    private static final long FIRST_VISIT = 1388509200000l;
    private long lastVisit = FIRST_VISIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperFactory.setHelper(getApplicationContext());
        //инитциализация лоадера
        ImageLoaderInit imageLoaderInit = new ImageLoaderInit(this);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        listViewContent = (ListView) findViewById(R.id.tasksList);
        listViewContent.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
        listViewContent.setAlwaysDrawnWithCacheEnabled(true);
        contents = new ArrayList<>();
        adapterContent = new AdapterContent(this, contents);
        loadNextDataFromApi(totalItems);
        listViewContent.setAdapter(adapterContent);
        fillListView();
        loading = false;

        Intent intent = new Intent(this, MyForeground.class);
//        startService(super.getIntent().putExtra("time", 1));
    }

    public void fillListView(){
        listViewContent.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadNextDataFromApi(totalItemsCount);
                return true;
            }

            @Override
            public void checkNewsForLoad() {
                new HttpRequestForCheckNews().execute();
            }
        });
    }


    private void loadNextDataFromApi(int totalItemsCount){
        this.totalItems = totalItemsCount;
        new RequestToLocalDataBase().execute();

    }

    public class RequestToLocalDataBase extends AsyncTask<Void, Void, List<Content>>{
        @Override
        protected List<Content> doInBackground(Void... params) {
            try {
                return HelperFactory.getHelper().getContentDAO().getLimitContent(totalItems);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Content> contentsFromDB) {
            if(contentsFromDB.size() > 0){
                addNewContent(contentsFromDB);
                adapterContent.notifyDataSetChanged();
                //проверка свежих новостей
                //new HttpRequestForCheckNews().execute();
            }else {
                new HttpRequestContent().execute(totalItems);
            }
        }
    }

    /**
     * Get object from server
     */
    public class HttpRequestContent extends AsyncTask<Integer, Void, Content[]> {

        @Override
        protected Content[] doInBackground(Integer... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            URI url = UriComponentsBuilder.fromUriString(Constans.HOST)
                    .path(Constans.CONTENT_BY_TOTAL)
                    .queryParam("totalItems", params[0])
                    .build()
                    .toUri();
            System.out.println(url.toString());

            int count = 0;
            try {
                return getContent(url, restTemplate, count);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        Content[] getContent(URI url, RestTemplate restTemplate, int count) throws Exception {
            count++;
            if (count > limitQueryToServer) {
                throw new Exception();
            } else {
                try {
                    Content[] rest = restTemplate.getForObject(url, Content[].class);
                    return rest;
                } catch (Exception e) {
                    e.printStackTrace();
                    return getContent(url, restTemplate, count);
                }
            }
        }

        @Override
        protected void onPostExecute(Content[] contentsArray) {
            //Обязательно передать в встроенную базу данных и потом из нее брать данные для приложения
            try {

                for(Content content: contentsArray){
                    HelperFactory.getHelper().getContentDAO().createOrUpdate(content);
                }
                List<Content> list = HelperFactory.getHelper().getContentDAO().getLimitContent(totalItems);
                //обновляю последний визит
                if(list.size() > 0){
                    lastVisit = list.get(0).getModified().getTime();
                }
                addNewContent(list);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check new on server
     */
    public class HttpRequestForCheckNews extends AsyncTask<Long, Void, Integer> {

        @Override
        protected Integer doInBackground(Long... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            URI url = UriComponentsBuilder.fromUriString(Constans.HOST)
                    .path(Constans.CHECK_NEW)
                    .queryParam("date", getDateLastVisit())
                    .build()
                    .toUri();
            System.out.println(url.toString());
            System.out.println(new Date(getDateLastVisit()));

            int count = 0;
            try {
                return getQuantityLastNews(url, restTemplate, count);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected long getDateLastVisit(){
            try {
                if(lastVisit != FIRST_VISIT){
                    return lastVisit;
                }else {
                    Content content = HelperFactory.getHelper().getContentDAO().getLastContent();
                    return content.getModified().getTime();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return FIRST_VISIT;
            }
        }

        Integer getQuantityLastNews(URI url, RestTemplate restTemplate, int count) throws Exception {
            count++;
            if (count > limitQueryToServer) {
                throw new Exception();
            } else {
                try {
                    Integer rest = restTemplate.getForObject(url, Integer.class);
                    return rest;
                } catch (Exception e) {
                    return getQuantityLastNews(url, restTemplate, count);
                }
            }
        }

        @Override
        protected void onPostExecute(Integer quantityLastNews) {
            if(quantityLastNews > 0){
                new HttpRequestContent().execute(0);
            }else {
                checkContentsOnOldNew();
            }
        }
    }

    @Override
    protected void onDestroy() {
        HelperFactory.releaseHelper();
        super.onDestroy();
    }

    protected synchronized void checkContentsOnOldNew(){
        ArrayList<Content> copyContents = new ArrayList<>(contents);
        for(Content content: copyContents){
            if(content.getState() != 1){
                contents.remove(content);
            }
            adapterContent.notifyDataSetChanged();
        }
    }

    // сделать добавление нового контента асинхронно
    //понять почему не грузиться дальше лист.. почемуто loading не переходит в false

    protected void addNewContent(List<Content> list){
        for(Content content: list){
            if(content.getState() == 1 && !contents.contains(content)){
                contents.add(content);
            }else if(content.getState() == 1 && contents.contains(content)){
                contents.remove(content);
                contents.add(content);
            }
        }
        checkContentsOnOldNew();
        adapterContent.notifyDataSetChanged();
    }

}
