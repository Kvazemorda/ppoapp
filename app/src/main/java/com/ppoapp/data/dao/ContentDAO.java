package com.ppoapp.data.dao;


import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.ppoapp.entity.Content;
import java.sql.SQLException;
import java.util.List;

public class ContentDAO extends BaseDaoImpl<Content, Integer> {

    public ContentDAO(ConnectionSource connectionSource, Class<Content> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Content> getAllContent() throws SQLException {
            return this.queryForAll();
    }

    public List<Content> getLimitContent(int totalItems){
        QueryBuilder<Content, Integer> queryBuilder = this.queryBuilder();
        try {
            queryBuilder.offset((long)totalItems).limit(10l);
            queryBuilder.orderBy("created", false);
            return this.query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}
