package muscular.man.tools.kanjinvk.model.enums;

/**
 * Created by nguoitoanco on 2/27/2016.
 */
public enum SizeEnum {
    FULL_WIDTH(0),
    RATE_ONE(1),
    RATE_TWO(2),
    RATE_THREE(3),
    RATE_FOUR(4),
    RATE_FIVE(5),
    RATE_SIX(6),
    RATE_TEN(10);

    public int rate;
    SizeEnum(int rate) {
        this.rate = rate;
    }
}
