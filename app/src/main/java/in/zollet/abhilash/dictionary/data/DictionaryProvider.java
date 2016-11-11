package in.zollet.abhilash.dictionary.data;


import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


@ContentProvider(authority = DictionaryProvider.AUTHORITY, database = DictionaryDatabase.class)
public class DictionaryProvider {
    public static final String AUTHORITY = "in.zollet.abhilash.dictionary.data";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String Dictionary= "dictionary";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = DictionaryDatabase.DICTIONARY)
    public static class Dictionary {
        @ContentUri(
                path = Path.Dictionary,
                type = "vnd.android.cursor.dir/dictionary"
        )
        public static final Uri CONTENT_URI = buildUri(Path.Dictionary);

        @InexactContentUri(
                name = "Dictionary_ID",
                path = Path.Dictionary + "/*",
                type = "vnd.android.cursor.item/dictionary",
                whereColumn = DictionaryColumns._ID,
                pathSegment = 1
        )
        public static Uri ID(String ID) {
            return buildUri(Path.Dictionary, ID);
        }
    }
}

