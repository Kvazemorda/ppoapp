package com.ppoapp;

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

import com.ppoapp.constant.Constans;
import com.ppoapp.entity.Content;
import com.ppoapp.service.AdapterContent;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected List<Content> contents = new ArrayList<>();
    protected Long id = 1l;
    protected long previousTotalItemCount;
    boolean loading;
    public ListView listViewContent;
    protected AdapterContent adapterContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        listViewContent.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount == totalItemCount){
                    loading = true;
                    new HttpRequestContent().execute();
                    //adapterContent.notifyDataSetChanged();
                }
            }
        });

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
                    .path(Constans.CONTENT)
                    .queryParam("date", previousTotalItemCount)
                    .build()
                    .toUri();
            System.out.println(url.toString());
            try{
                return getContent(url, restTemplate);
            }catch (Exception e){
                e.printStackTrace();
                return getContent(url, restTemplate);
            }
        }

        Content[] getContent(URI url, RestTemplate restTemplate){
            try {
                return restTemplate.getForObject(url, Content[].class);
            }catch (Exception e){
                return getContent(url, restTemplate);
            }
        }

        @Override
        protected void onPostExecute(Content[] contentsArray) {
            //Обязательно передать в встроенную базу данных и потом из нее брать данные для приложения
            contents.addAll(Arrays.asList(contentsArray));
            previousTotalItemCount = contents.get(contents.size()-1).getCreated().getTime();
            adapterContent.notifyDataSetChanged();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            System.out.println(sdf.format(new Date(previousTotalItemCount)));
        }
    }


    protected long getCurrentDate(){
        return new Date().getTime();
    }
}
