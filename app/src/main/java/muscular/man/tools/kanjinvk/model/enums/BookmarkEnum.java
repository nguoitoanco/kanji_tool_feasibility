package muscular.man.tools.kanjinvk.model.enums;

/**
 * Created by khanhnv10 on 2016/02/25.
 */
public enum BookmarkEnum {
    IS_NOT_BOOKMARKED(0, "0"),
    IS_BOOKMARKED(1, "1");

    public int index;
    public String tag;
    BookmarkEnum(int id, String tag) {
        this.index = id;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}
