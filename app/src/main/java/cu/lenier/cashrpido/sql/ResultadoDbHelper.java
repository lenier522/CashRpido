package cu.lenier.cashrpido.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ResultadoDbHelper extends SQLiteOpenHelper {
    // Definir la versión de la base de datos
    public static final int DATABASE_VERSION = 2;
    // Definir el nombre de la base de datos
    public static final String DATABASE_NAME = "Resultado.db";

    // Consulta SQL para crear la tabla
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ResultadoContract.ResultadoEntry.TABLE_NAME + " (" +
                    ResultadoContract.ResultadoEntry._ID + " INTEGER PRIMARY KEY," +
                    ResultadoContract.ResultadoEntry.COLUMN_RESULTADO + " TEXT," +
                    ResultadoContract.ResultadoEntry.COLUMN_CATEGORIA + " TEXT)";

    // Consulta SQL para eliminar la tabla
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ResultadoContract.ResultadoEntry.TABLE_NAME;

    public ResultadoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar la tabla existente
        db.execSQL(SQL_DELETE_ENTRIES);
        // Crear la nueva tabla
        onCreate(db);
    }

}

