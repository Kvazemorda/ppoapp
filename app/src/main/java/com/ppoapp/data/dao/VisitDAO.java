package com.ppoapp.data.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.ppoapp.data.DatabaseHelper;
import com.ppoapp.entity.Visit;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class VisitDAO extends BaseDaoImpl<Visit, Integer> {
    private static final String TAG = VisitDAO.class.getSimpleName();
    private static final String DATABASE_NAME ="ppo.db";

    public VisitDAO(ConnectionSource connectionSource, Class<Visit> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public Visit getLastVisit(){
        List<Visit> list = null;
        try {
            list = this.queryForAll();
            System.out.println(list.size() + "===================================================");
            if(list.size() > 0){
                return list.get(list.size() - 1);
            }
        } catch (SQLException e) {
            Log.e(TAG,"error get all visit " + e);
            e.printStackTrace();
        }
        return null;
    }
}
