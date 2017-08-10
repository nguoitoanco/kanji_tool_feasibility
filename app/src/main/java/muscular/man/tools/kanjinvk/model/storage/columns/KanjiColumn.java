package muscular.man.tools.kanjinvk.model.storage.columns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.common.annotation.ColumnAnnotation;
import muscular.man.tools.kanjinvk.common.annotation.TableAnnotation;
import muscular.man.tools.kanjinvk.model.dto.KanjiDto;

/**
 * Created by KhanhNV10 on 2015/12/25.
 */

@TableAnnotation(name = "KANJI")
public class KanjiColumn implements Serializable {
    @ColumnAnnotation(name = "id", type = "TEXT PRIMARY KEY")
    public String kid;// Ex:N50001,N40001,N30001,N20001,N10001

    @ColumnAnnotation(name = "word")
    public String word;

    @ColumnAnnotation(name = "onyomi")
    public String onyomi;

    @ColumnAnnotation(name = "kuniomi")
    public String kuniomi;

    @ColumnAnnotation(name = "en_onyomi")
    public String enOnyomi;

    @ColumnAnnotation(name = "en_kuniomi")
    public String enKuniomi;

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

    @ColumnAnnotation(name = "is_bookmarked")
    public String isBookmarked = "0";

    public KanjiColumn() {
    }

    public KanjiColumn(KanjiDto dto) {
        kid = dto.kid;
        word = dto.word;
        enOnyomi = dto.enOnyomi;
        enKuniomi = dto.enKuniomi;
        onyomi = dto.onyomi;
        kuniomi = dto.kuniomi;
        enMean = dto.enMean;
        vnMean = dto.vnMean;
        enCompound = dto.enCompound;
        vnCompound = dto.vnCompound;
        enHistory = dto.enHistory;
        vnHistory = dto.vnHistory;
        basicSets = dto.basicSets;
        if (dto.isBookmarked) {
            isBookmarked = "1";
        } else {
            isBookmarked = "0";
        }

    }

    public static List<KanjiColumn> getKanjiColums(List<KanjiDto> dtos) {
        List<KanjiColumn> columns = new ArrayList<>();
        for (KanjiDto dto : dtos) {
            columns.add(new KanjiColumn(dto));
        }

        return columns;
    }
}
