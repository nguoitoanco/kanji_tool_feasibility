package muscular.man.tools.kanjinvk.model.enums;

import muscular.man.tools.kanjinvk.R;
import muscular.man.tools.kanjinvk.fragment.BookmarkFragment;

/**
 * Created by khanhnv10 on 2016/02/25.
 */
public enum FragmentEnum {
    HOME(0, "HomeFragment"),
    SEARCH_RESULT(1, "KanjiSearchResultFragment"),
    ABOUT(2, "AboutFragment"),
    SETTING(3, "SettingFragment"),
    BOOKMARK(4, "BookmarkFragment"),
    BASIC_SET(5, "BasicSetFragment");

    public int index;
    public String tag;
    FragmentEnum(int id, String tag) {
        this.index = id;
        this.tag = tag;
    }
}
