package com.ppoapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Comparator;
import java.util.Date;

@JsonIgnoreProperties()
@DatabaseTable(tableName = "ppo1_content")
public class Content implements Comparable {

    @DatabaseField(id = true) private long id;
    @DatabaseField private String title;
    @DatabaseField private String introtext;
    @DatabaseField private String fulltext;
    @DatabaseField private String images;
    @JsonFormat(pattern = "MMM dd, yyyy")
    @DatabaseField private Date created;
    @DatabaseField private int state;
    @JsonIgnore
    @DatabaseField private String localImage;

    public Content() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntrotext() {
        return introtext;
    }

    public void setIntrotext(String introtext) {
        this.introtext = introtext;
    }

    public String getFulltext() {
        return fulltext;
    }

    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getLocalImage() {
        return localImage;
    }

    public void setLocalImage(String localImage) {
        this.localImage = localImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Content content = (Content) o;

        if (id != content.id) return false;
        if (title != null ? !title.equals(content.title) : content.title != null) return false;
        if (introtext != null ? !introtext.equals(content.introtext) : content.introtext != null) return false;
        if (fulltext != null ? !fulltext.equals(content.fulltext) : content.fulltext != null) return false;
        if (images != null ? !images.equals(content.images) : content.images != null) return false;
        return created != null ? created.equals(content.created) : content.created == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (introtext != null ? introtext.hashCode() : 0);
        result = 31 * result + (fulltext != null ? fulltext.hashCode() : 0);
        result = 31 * result + (images != null ? images.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContentDAO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", introtext='" + introtext + '\'' +
                ", fulltext='" + fulltext + '\'' +
                ", images='" + images + '\'' +
                ", createdBy='" + created + '\'' +
                '}';
    }


    @Override
    public int compareTo(Object o) {
        Content content = (Content) o;
        if(this.created.after(content.created)){
            return 1;
        }else if(this.created.before(content.created)) {
            return -1;
        }else {
            return 0;
        }
    }
}
