package muscular.man.tools.kanjinvk.model.enums;

/**
 * Created by khanhnv10 on 2016/02/25.
 */
public enum ThemesEnum {
    HOME(0, "DEFAULT_THEMES"),
    SEARCH_RESULT(1, "BLACK_THEMES");

    public int index;
    public String tag;
    ThemesEnum(int id, String tag) {
        this.index = id;
        this.tag = tag;
    }
}
