package muscular.man.tools.kanjinvk.model.storage.columns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import muscular.man.tools.kanjinvk.common.annotation.ColumnAnnotation;
import muscular.man.tools.kanjinvk.common.annotation.TableAnnotation;
import muscular.man.tools.kanjinvk.model.dto.KanjiTestDto;

/**
 * Created by KhanhNV10 on 2015/12/25.
 */

@TableAnnotation(name = "KANJI_TEST")
public class KanjiTestColumn implements Serializable {
    @ColumnAnnotation(name = "id", type = "TEXT PRIMARY KEY")
    public String id;// Ex:N50001,N40001,N30001,N20001,N10001

    @ColumnAnnotation(name = "input")
    public String input;

    @ColumnAnnotation(name = "question")
    public String question;

    @ColumnAnnotation(name = "a")
    public String a;

    @ColumnAnnotation(name = "b")
    public String b;

    @ColumnAnnotation(name = "c")
    public String c;

    @ColumnAnnotation(name = "d")
    public String d;

    @ColumnAnnotation(name = "aws")
    public String aws;

    public KanjiTestColumn() {
    }

    public KanjiTestColumn(KanjiTestDto dto) {
        id = dto.id;
        input = dto.input;
        question = dto.question;
        a = dto.a;
        b = dto.b;
        c = dto.c;
        d = dto.d;
        aws = String.valueOf(dto.aws);
    }

    public static List<KanjiTestColumn> getKanjiTestColums(List<KanjiTestDto> dtos) {
        List<KanjiTestColumn> columns = new ArrayList<>();
        for (KanjiTestDto dto : dtos) {
            columns.add(new KanjiTestColumn(dto));
        }

        return columns;
    }
}
