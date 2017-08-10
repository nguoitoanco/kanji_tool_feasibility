package muscular.man.tools.kanjinvk.model.enums;

/**
 * Created by nguoitoanco on 2/29/2016.
 */
public enum  ViewDetailMode {
    DEFAULT(0),
    CLEAR(1),
    WRITING(2),
    PRACTICING(3),
    FULL(4),
    SLIDE(5);

    private int type;
    ViewDetailMode(int type) {
        this.type = type;
    }

    public int toInt() {
        return type;
    }

    public ViewDetailMode toViewDetailMode(int newType) {
        if (newType == DEFAULT.toInt()) {
            return ViewDetailMode.DEFAULT;
        } else {
            return ViewDetailMode.FULL;
        }
    }

    public boolean isDefault() {
        return this.type == ViewDetailMode.DEFAULT.toInt();
    }

    public boolean isClearMode() {
        return this.type == ViewDetailMode.CLEAR.toInt();
    }

}
