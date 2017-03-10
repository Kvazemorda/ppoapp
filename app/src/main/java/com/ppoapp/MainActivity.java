package com.ppoapp;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j256.ormlite.table.TableUtils;
import com.ppoapp.constant.Constans;
import com.ppoapp.controller.EndlessScrollListener;
import com.ppoapp.controller.ExpandableTextView;
import com.ppoapp.data.HelperFactory;
import com.ppoapp.entity.Content;
import com.ppoapp.service.AdapterContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    protected ArrayList<Content> contents;
    protected Long id = 1l;
    protected long previousTotalItemCount;
    protected int totalItems = 0;
    boolean loading;
    public ListView listViewContent;
    protected AdapterContent adapterContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HelperFactory.setHelper(getApplicationContext());
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        previousTotalItemCount = getCurrentDate();
        setContentView(R.layout.activity_main);
        listViewContent = (ListView) findViewById(R.id.tasksList);
        contents = new ArrayList<>();
        adapterContent = new AdapterContent(this, contents);
        new HttpRequestContent().execute();
        listViewContent.setAdapter(adapterContent);
        fillListView();
        loading = false;
    }

    public void fillListView(){
        listViewContent.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadNextDataFromApi(totalItemsCount);
                return true;
            }
        });
    }

    private void loadNextDataFromApi(int totalItemsCount){
        this.totalItems = totalItemsCount;
        new HttpRequestContent().execute();
    }

    /**
     * Get object from service
     */
    public class HttpRequestContent extends AsyncTask<Void, Void, Content[]> {

        @Override
        protected Content[] doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            URI url = UriComponentsBuilder.fromUriString(Constans.HOST)
                    .path(Constans.CONTENT_BY_TOTAL)
                    .queryParam("totalItems", totalItems)
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
            if (count > 5) {
                throw new Exception();
            } else {
                try {
                    Content[] rest = restTemplate.getForObject(url, Content[].class);
                    for(Content content : rest){
                        System.out.println(content.getTitle());
                    }
                    return rest;
                } catch (Exception e) {
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
                contents.addAll(HelperFactory.getHelper().getContentDAO().getLimitContent(totalItems));
                System.out.println("____________________________________________ " + totalItems);
                for(Content content : contents){
                    System.out.println();
                    System.out.println(content.getTitle());
                    System.out.println();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(contents.size() > 0){
                previousTotalItemCount = contents.get(contents.size()-1).getCreated().getTime();
                adapterContent.notifyDataSetChanged();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                System.out.println(sdf.format(new Date(previousTotalItemCount)));
            }
        }
    }


    protected long getCurrentDate(){
        return new Date().getTime();
    }

    @Override
    protected void onDestroy() {
        HelperFactory.releaseHelper();
        super.onDestroy();
    }
}
