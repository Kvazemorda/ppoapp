package com.ppoapp.service;


import android.app.IntentService;
import android.content.Intent;
import android.icu.util.TimeUnit;

import com.ppoapp.constant.Constans;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;

public class MyForeground extends IntentService {
    int msOfSec = 1000;
    int secOfMin = 60;

    public MyForeground(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int time = intent.getIntExtra("lastNew", (int)new Date().getTime());
        while (true){
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            URI url = UriComponentsBuilder.fromUriString(Constans.HOST)
                    .path(Constans.CHECK_NEW)
                    .queryParam("date", time)
                    .build()
                    .toUri();
            System.out.println(url.toString());

        }
    }

    protected int getOneMinut(){
        return msOfSec * secOfMin;
    }
}
