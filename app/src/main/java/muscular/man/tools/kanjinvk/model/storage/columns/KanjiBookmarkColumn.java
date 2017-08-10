package muscular.man.tools.kanjinvk.model.storage.columns;

import java.io.Serializable;

import muscular.man.tools.kanjinvk.common.annotation.ColumnAnnotation;
import muscular.man.tools.kanjinvk.common.annotation.TableAnnotation;

/**
 * Created by KhanhNV10 on 2015/12/25.
 */

@TableAnnotation(name = "KANJI_BOOKMARK")
public class KanjiBookmarkColumn implements Serializable {
    @ColumnAnnotation(name = "id", type = "TEXT PRIMARY KEY")
    public String kid;// Ex:N50001,N40001,N30001,N20001,N10001

    @ColumnAnnotation(name = "word", type = "TEXT PRIMARY KEY")
    public String word;

    @ColumnAnnotation(name = "onyomi")
    public String onyomi;

    @ColumnAnnotation(name = "kuniomi")
    public String kuniomi;

    @ColumnAnnotation(name = "en_mean")
    public String enMean;

    @ColumnAnnotation(name = "vn_mean")
    public String vnMean;

    @ColumnAnnotation(name = "en_compound")
    public String enCompound;

    @ColumnAnnotation(name = "vn_compound")
    public String vnCompound;

    @ColumnAnnotation(name = "en_history")
    public String enHistory;

    @ColumnAnnotation(name = "vn_history")
    public String vnHistory;

    @ColumnAnnotation(name = "basic_set")
    public String basicSets;

    public KanjiBookmarkColumn() {
    }

//    public KanjiColumn(Shop shop) {
//        this.shopId = shop.id;
//        this.shopName = shop.name;
//        this.genreName = shop.genre.name;
//        this.shopIcon = shop.photo.mobilePhoto.s;
//        this.shopAccess = shop.access;
//        this.shopCatch = shop.strCatch;
//        this.shopOpen = shop.open;
//    }
//
//    public KanjiColumn(ShopDTO dto) {
//        this.shopId = dto.id;
//        this.shopName = dto.name;
//        this.genreName = dto.genre;
//        this.shopIcon = dto.mobilePhoto;
//        this.shopAccess = dto.access;
//        this.shopCatch = dto.strCatch;
//        this.shopOpen = dto.open;
//    }
}
