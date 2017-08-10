package muscular.man.tools.kanjinvk.model.enums;

import muscular.man.tools.kanjinvk.R;

/**
 * Created by nguoitoanco on 2/24/2016.
 */
public enum Tabs {
    HOME(0, R.string.home_tab_title, R.drawable.tab_home_style), // Home TAB
    BOOKMARK(1, R.string.bookmark_tab_title, R.drawable.tab_bookmark_style), // Bookmark TAB
    ABOUT(2, R.string.more_tab_title, R.drawable.tab_about_style); // About TAB
//    DETECT(2, R.string.detect_tab_title, R.mipmap.ic_settings), // Detect TAB
//    SETTING(2, R.string.setting_tab_title, android.R.drawable.ic_menu_manage); // Setting TAB

    private int index;
    private int title;
    private int icon;

    Tabs(int id, int title, int icon) {
        this.index = id;
        this.title = title;
        this.icon = icon;
    }

    public int getIndex() {
        return index;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public static int[] getIcons() {
        return new int[]{
                Tabs.HOME.getIcon(),
                Tabs.BOOKMARK.getIcon(),
                Tabs.ABOUT.getIcon()
        };
    }

    public static int[] getTitles() {
        return new int[]{
                Tabs.HOME.getTitle(),
                Tabs.BOOKMARK.getTitle(),
                Tabs.ABOUT.getTitle()
        };
    }

}
