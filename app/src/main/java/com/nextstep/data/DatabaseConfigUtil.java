package com.nextstep.data;


import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static void main(String[] args) {
        try {
            writeConfigFile("ormlite_config.txt");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
