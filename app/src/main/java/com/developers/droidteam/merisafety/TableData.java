package com.developers.droidteam.merisafety;

import android.provider.BaseColumns;

/**
 * Created by siddharth on 6/20/2017.
 */

public class TableData {

    public TableData()
    {

    }

    public static abstract class TableInfo implements BaseColumns
    {
        /** KEYSSSS  */
        public static final String USER_NAME = "user_name";
        public static final String USER_PASS = "user_pass";
        public static final String DATABASE_NAME = "user2_info";
        public static final String TABLE_NAME = "reg1_info";
        public static final String TABLE_NAME_GAUR = "reg2_info";

        public static final String GAUR_NAME = "gaur_name";
        public static final String GAUR_NUM = "gaur_num";
        public static final String GAUR_EMAIL = "gaur_email";


    }
}
