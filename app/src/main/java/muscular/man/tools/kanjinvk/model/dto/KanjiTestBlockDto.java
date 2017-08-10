package muscular.man.tools.kanjinvk.model.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguoitoanco on 6/13/2016.
 */
public class KanjiTestBlockDto implements Parcelable {
    private List<KanjiTestDto> kanjiTestDtos;

    public KanjiTestBlockDto() {
        kanjiTestDtos = new ArrayList<>();
    }

    protected KanjiTestBlockDto(Parcel in) {
        kanjiTestDtos = in.createTypedArrayList(KanjiTestDto.CREATOR);
    }

    public List<KanjiTestDto> getKanjiTestDtos() {
        return new ArrayList<>(kanjiTestDtos);
    }

    public void setKanjiTestDtos(List<KanjiTestDto> kanjiTestDtos) {
        this.kanjiTestDtos = kanjiTestDtos;
    }

    public static final Creator<KanjiTestBlockDto> CREATOR = new Creator<KanjiTestBlockDto>() {
        @Override
        public KanjiTestBlockDto createFromParcel(Parcel in) {
            return new KanjiTestBlockDto(in);
        }

        @Override
        public KanjiTestBlockDto[] newArray(int size) {
            return new KanjiTestBlockDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(kanjiTestDtos);
    }
}
