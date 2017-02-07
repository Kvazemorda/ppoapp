package com.ppoapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.ppoapp.constant.Constans;
import com.ppoapp.data.HelperFactory;
import com.ppoapp.entity.Content;
import com.ppoapp.service.AdapterTasks;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected List<Content> contents = new ArrayList<>();
    Content content;
    protected Long id = 1l;
    private Long currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createPerson();
        new HttpRequestTasks().execute();
    }

    public void createPerson(){
        try {
            System.out.println(HelperFactory.getHelper().getContentDAO().getAllContent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fillListView(){
        ListView listView = (ListView) findViewById(R.id.tasksList);
        AdapterTasks adapterTasks = new AdapterTasks(this, contents);
        listView.setAdapter(adapterTasks);
    }

    /**
     * Get object from service
     */
    public class HttpRequestTasks extends AsyncTask<Void, Void, Content[]> {
        @Override
        protected Content[] doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            URI url = UriComponentsBuilder.fromUriString(Constans.HOST)
                    .path(Constans.CONTENT)
                    .queryParam("date", getCurrentDate())
                    .build()
                    .toUri();
            System.out.println(url.toString());
            Content[] contentsArray = new Content[2]; //restTemplate.getForObject(url, Content[].class);
            Content content = new Content();
            content.setIntrotext("intro text");
            content.setTitle("TITLE");
            content.setImages("http://ppovankorneft.ru/images/news/2016/ElkaSFU/ban400.jpg");
            Content content1 = new Content();
            content1.setIntrotext("intro text1");
            content1.setTitle("TITLE1");
            content1.setImages("http://ppovankorneft.ru/images/news/2016/newYear/newYear400.jpg");

            contentsArray[0] = content;
            contentsArray[1] = content1;
            return contentsArray;
        }

        @Override
        protected void onPostExecute(Content[] contetsArray) {
            //Обязательно передать в встроенную базу данных и потом из нее брать данные для приложения
            contents = Arrays.asList(contetsArray);
            fillListView();
        }
    }

    protected long getCurrentDate(){
        return new Date().getTime();
    }
}
