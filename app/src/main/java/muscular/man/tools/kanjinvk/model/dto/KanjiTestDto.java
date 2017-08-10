package muscular.man.tools.kanjinvk.model.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import muscular.man.tools.kanjinvk.model.storage.columns.KanjiTestColumn;

/**
 * Created by KhanhNV10 on 10/06/2016.
 */
public class KanjiTestDto implements Parcelable, Serializable {
    public String id;
    public String input;
    public String question;
    public String a;
    public String b;
    public String c;
    public String d;
    public String aws;
    public int selectedItem = -1;

    public KanjiTestDto(KanjiTestColumn cl) {
        id = cl.id;
        input = cl.input;
        question = cl.question;
        a = cl.a;
        b = cl.b;
        c = cl.c;
        d = cl.d;
        aws = cl.aws;
    }

    public KanjiTestDto(String line) {
        String[] elements = line.split(";");
        id = elements[0].trim();
        input = elements[1];
        question = elements[2];
        a = elements[3];
        b = elements[4];
        c = elements[5];
        d = elements[6];
        aws = elements[7];
    }


    protected KanjiTestDto(Parcel in) {
        id = in.readString();
        input = in.readString();
        question = in.readString();
        a = in.readString();
        b = in.readString();
        c = in.readString();
        d = in.readString();
        aws = in.readString();
        selectedItem = in.readInt();
    }

    public static final Creator<KanjiTestDto> CREATOR = new Creator<KanjiTestDto>() {
        @Override
        public KanjiTestDto createFromParcel(Parcel in) {
            return new KanjiTestDto(in);
        }

        @Override
        public KanjiTestDto[] newArray(int size) {
            return new KanjiTestDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(input);
        dest.writeString(question);
        dest.writeString(a);
        dest.writeString(b);
        dest.writeString(c);
        dest.writeString(d);
        dest.writeString(aws);
        dest.writeInt(selectedItem);
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
}
