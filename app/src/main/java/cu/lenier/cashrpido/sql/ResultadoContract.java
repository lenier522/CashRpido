package cu.lenier.cashrpido.sql;

import android.provider.BaseColumns;

public class ResultadoContract {
    private ResultadoContract() {}

    public static class ResultadoEntry implements BaseColumns {
        public static final String TABLE_NAME = "resultado";
        public static final String COLUMN_RESULTADO = "resultado";
        public static final String COLUMN_CATEGORIA = "categoria";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ResultadoEntry.TABLE_NAME + " (" +
                    ResultadoEntry._ID + " INTEGER PRIMARY KEY," +
                    ResultadoEntry.COLUMN_RESULTADO + " TEXT," +
                    ResultadoEntry.COLUMN_CATEGORIA + " TEXT)";
}


