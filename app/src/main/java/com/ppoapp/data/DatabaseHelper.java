package com.ppoapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.ppoapp.data.dao.ContentDAO;
import com.ppoapp.data.dao.VisitDAO;
import com.ppoapp.entity.Content;
import com.ppoapp.entity.Visit;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    //имя файла базы данных который будет храниться в /data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME ="ppo.db";
    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 14;
    //ссылки на DAO соответсвующие сущностям, хранимым в БД
    private ContentDAO contentDAO = null;
    private VisitDAO visitDAO = null;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, Content.class);
            TableUtils.createTable(connectionSource, Visit.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
        try{
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, Content.class, true);
            TableUtils.dropTable(connectionSource, Visit.class, true);
            onCreate(db, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+DATABASE_NAME+"from ver "+oldVer);
            throw new RuntimeException(e);
        }
    }

    //синглтон для GoalDAO
    public ContentDAO getContentDAO() throws SQLException{
        if(contentDAO == null){
            contentDAO = new ContentDAO(getConnectionSource(), Content.class);
        }
        return contentDAO;
    }

    public VisitDAO getVisitDAO() throws SQLException{
        if(visitDAO == null){
            visitDAO = new VisitDAO(getConnectionSource(), Visit.class);
        }
        return visitDAO;
    }

    //выполняется при закрытии приложения
    @Override
    public void close(){
        super.close();
        contentDAO = null;
    }
}
