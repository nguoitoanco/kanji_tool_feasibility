package muscular.man.tools.kanjinvk.model.enums;

import muscular.man.tools.kanjinvk.R;

/**
 * Created by nguoitoanco on 3/3/2016.
 */
public enum Blocks {
    BLOCK_10(0, 10),
    BLOCK_15(1, 15),
    BLOCK_20(2, 20);
    public int index;
    public int elementOfLine;

    Blocks(int index, int elementOfLine) {
        this.index = index;
        this.elementOfLine = elementOfLine;
    }
}
