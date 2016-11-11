package in.zollet.abhilash.dictionary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

@Database(version = DictionaryDatabase.VERSION)
public class DictionaryDatabase {
    private DictionaryDatabase() {
    }
    static final int VERSION = 1;

    @Table(DictionaryColumns.class)
    public static final String DICTIONARY = "Products";

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {

    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {


    }

}
