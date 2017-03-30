package com.ppoapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Comparator;
import java.util.Date;

@JsonIgnoreProperties()
@DatabaseTable(tableName = "visit")
public class Visit implements Comparable{

    @DatabaseField(id = true) private long id;
    @JsonFormat(pattern = "MMM dd, yyyy HH:mm:ss a")
    @DatabaseField private Date dateOfVisit;

    public Visit() {
    }

    public Visit(Date dateOfVisit) {
        this.dateOfVisit = dateOfVisit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDateOfVisit() {
        return dateOfVisit;
    }

    public void setDateOfVisit(Date dateOfVisit) {
        this.dateOfVisit = dateOfVisit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visit visit = (Visit) o;

        if (id != visit.id) return false;
        return dateOfVisit != null ? dateOfVisit.equals(visit.dateOfVisit) : visit.dateOfVisit == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (dateOfVisit != null ? dateOfVisit.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {
        Visit visit = (Visit) o;
        if(this.id > visit.getId()){
            return 1;
        }else if(this.id == visit.getId()) {
            return 0;
        }else return -1;
    }
}
