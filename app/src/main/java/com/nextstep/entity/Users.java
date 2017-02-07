package com.nextstep.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ppo1_users")
public class Users {
    @DatabaseField(generatedId = true) private long id;
    @DatabaseField private String name;

    public Users(String name) {
        this.name = name;
    }

    public Users() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}