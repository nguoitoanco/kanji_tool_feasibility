package muscular.man.tools.kanjinvk.model.enums;

import muscular.man.tools.kanjinvk.R;

/**
 * Created by nguoitoanco on 2/24/2016.
 */
public enum JlptTabs {
    N1(1, R.string.jlpt_n1_tab_text),
    N2(2, R.string.jlpt_n2_tab_text),
    N3(3, R.string.jlpt_n3_tab_text),
    N4(4, R.string.jlpt_n4_tab_text),
    N5(5, R.string.jlpt_n5_tab_text);

    private int index;
    private int title;

    JlptTabs(int id, int title) {
        this.index = id;
        this.title = title;
    }

    public int getIndex() {
        return index;
    }

    public int getTitle() {
        return title;
    }


    public static int[] getTitles() {
        return new int[]{
            JlptTabs.N5.getTitle(),
            JlptTabs.N4.getTitle(),
            JlptTabs.N3.getTitle(),
            JlptTabs.N2.getTitle(),
            JlptTabs.N1.getTitle(),
        };
    }

}
