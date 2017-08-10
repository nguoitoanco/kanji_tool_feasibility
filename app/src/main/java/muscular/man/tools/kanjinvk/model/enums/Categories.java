package muscular.man.tools.kanjinvk.model.enums;

import muscular.man.tools.kanjinvk.R;

/**
 * Created by khanhnv10 on 2016/02/25.
 */
public enum Categories {
    KEYWORD_SEARCH(0, R.string.btn_search_text, ""),
    JLPT_N1(1, R.string.jlpt_n1_text, "N1%"),
    JLPT_N2(2, R.string.jlpt_n2_text, "N2%"),
    JLPT_N3(3, R.string.jlpt_n3_text, "N3%"),
    JLPT_N4(4, R.string.jlpt_n4_text, "N4%"),
    JLPT_N5(5, R.string.jlpt_n5_text, "N5%"),
    BASIC_SET(6, R.string.kanji_basic_set_text, "B%"),
    GAME(7, R.string.kanji_game_text, ""),
    TEST(8, R.string.kanji_test_text, "kanji/data_jlpt_test_full.txt"),
    ALL(9, R.string.btn_search_text, "kanji/data_jlpt_full.txt");

    public int index;
    public int title;
    public String dataUrl;
    Categories(int id, int title, String url) {
        this.index = id;
        this.title = title;
        dataUrl = url;
    }

    public static int[] getCates() {
        return new int[] {
                Categories.KEYWORD_SEARCH.index,
                Categories.JLPT_N1.index,
                Categories.JLPT_N2.index,
                Categories.JLPT_N3.index,
                Categories.JLPT_N4.index,
                Categories.JLPT_N5.index,
                Categories.BASIC_SET.index,
                Categories.GAME.index,
                Categories.TEST.index,
        };
    }
}
