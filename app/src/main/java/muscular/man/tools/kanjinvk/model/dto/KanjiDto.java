package muscular.man.tools.kanjinvk.model.dto;

import muscular.man.tools.kanjinvk.model.storage.columns.KanjiColumn;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by khanhnv10 on 2016/02/26.
 */
public class KanjiDto implements Parcelable {
    private static final String TAG = KanjiDto.class.getSimpleName();
    public String kid;// Ex:N50001,N40001,N30001,N20001,N10001
    public String word;
    public String enOnyomi;
    public String enKuniomi;
    public String onyomi;
    public String kuniomi;
    public String enMean;
    public String vnMean;
    public String enCompound;
    public String vnCompound;
    public String enHistory;
    public String vnHistory;
    public String basicSets;
    public boolean isBookmarked = false;

    public KanjiDto(KanjiColumn cl) {
        kid = cl.kid;
        word = cl.word;
        enOnyomi = cl.enOnyomi;
        enKuniomi = cl.enKuniomi;
        onyomi = cl.onyomi;
        kuniomi = cl.kuniomi;
        enMean = cl.enMean;
        vnMean = cl.vnMean;
        enCompound = cl.enCompound;
        vnCompound = cl.vnCompound;
        enHistory = cl.enHistory;
        vnHistory = cl.vnHistory;
        basicSets = cl.basicSets;
        isBookmarked = "1".equals(cl.isBookmarked);
    }

    public KanjiDto(String line) {
        String[] elements = line.split(";");
        kid = elements[0].trim();
        word = elements[1].trim();
        enOnyomi = elements[2].trim();
        enKuniomi = elements[3].trim();
        onyomi = elements[4].trim();
        kuniomi = elements[5].trim();
        enMean = elements[6].trim();
        vnMean = elements[7].trim();
        enCompound = elements[8].trim();
        vnCompound = elements[9].trim();
        enHistory = elements[10].trim();
        vnHistory = elements[11].trim();
//        basicSets = elements[12];
    }

    protected KanjiDto(Parcel in) {
        kid = in.readString();
        word = in.readString();
        enOnyomi = in.readString();
        enKuniomi = in.readString();
        onyomi = in.readString();
        kuniomi = in.readString();
        enMean = in.readString();
        vnMean = in.readString();
        enCompound = in.readString();
        vnCompound = in.readString();
        enHistory = in.readString();
        vnHistory = in.readString();
        basicSets = in.readString();
        isBookmarked = in.readByte() != 0;
    }

    public static final Creator<KanjiDto> CREATOR = new Creator<KanjiDto>() {
        @Override
        public KanjiDto createFromParcel(Parcel in) {
            return new KanjiDto(in);
        }

        @Override
        public KanjiDto[] newArray(int size) {
            return new KanjiDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kid);
        dest.writeString(word);
        dest.writeString(enOnyomi);
        dest.writeString(enKuniomi);
        dest.writeString(onyomi);
        dest.writeString(kuniomi);
        dest.writeString(enMean);
        dest.writeString(vnMean);
        dest.writeString(enCompound);
        dest.writeString(vnCompound);
        dest.writeString(enHistory);
        dest.writeString(vnHistory);
        dest.writeString(basicSets);
        dest.writeByte((byte) (isBookmarked ? 1 : 0));
    }
}
