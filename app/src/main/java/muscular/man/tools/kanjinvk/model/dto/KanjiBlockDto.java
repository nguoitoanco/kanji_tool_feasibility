package muscular.man.tools.kanjinvk.model.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguoitoanco on 3/3/2016.
 */
public class KanjiBlockDto implements Parcelable {
    private List<KanjiDto> kanjiDtos;

    public KanjiBlockDto(){
        kanjiDtos = new ArrayList<>();
    };
    public KanjiBlockDto(Parcel in) {
        kanjiDtos = in.createTypedArrayList(KanjiDto.CREATOR);
    }

    public static final Creator<KanjiBlockDto> CREATOR = new Creator<KanjiBlockDto>() {
        @Override
        public KanjiBlockDto createFromParcel(Parcel in) {
            return new KanjiBlockDto(in);
        }

        @Override
        public KanjiBlockDto[] newArray(int size) {
            return new KanjiBlockDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(kanjiDtos);
    }

    public List<KanjiDto> getKanjiDtos() {
        return kanjiDtos;
    }

    public void setKanjiDtos(List<KanjiDto> kanjiDtos) {
        this.kanjiDtos = new ArrayList<>(kanjiDtos);
    }

    public static Creator<KanjiBlockDto> getCREATOR() {
        return CREATOR;
    }
}
