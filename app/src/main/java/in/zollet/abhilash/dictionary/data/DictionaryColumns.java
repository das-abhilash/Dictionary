package in.zollet.abhilash.dictionary.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public class DictionaryColumns {
    @DataType(DataType.Type.INTEGER)
    @AutoIncrement  @PrimaryKey
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)  @NotNull
    public static final String WORD = "word";

    @DataType(DataType.Type.INTEGER)
    public static final String FIGURE_OF_SPEECH = "figure_of_speech";

    @DataType(DataType.Type.TEXT)
    public static final String MEANING = "meaning";

    @DataType(DataType.Type.TEXT)
    public static final String STARTING_ALPHABATE = "starting_alphabte";

}
