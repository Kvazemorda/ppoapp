package com.nextstep.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties()
public class ImageJson {
    private String image_intro;
    private String image_fulltext;

    public ImageJson() {
    }

    public String getImage_intro() {
        return image_intro;
    }

    public void setImage_intro(String image_intro) {
        this.image_intro = image_intro;
    }

    public String getImage_fulltext() {
        return image_fulltext;
    }

    public void setImage_fulltext(String image_fulltext) {
        this.image_fulltext = image_fulltext;
    }
}
