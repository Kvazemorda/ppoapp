package com.nextstep.data.dao;


import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.nextstep.entity.Content;
import java.sql.SQLException;
import java.util.List;

public class ContentDAO extends BaseDaoImpl<Content, Integer> {

    public ContentDAO(ConnectionSource connectionSource, Class<Content> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Content> getAllContent() throws SQLException {
            return this.queryForAll();
    }
}
