package muscular.man.tools.kanjinvk.model.storage.entity;

import java.util.List;

/**
 * Created by khanhnv10 on 2016/02/23.
 */
public class Kanji {
    public String kid;// Ex:N50001,N40001,N30001,N20001,N10001
    public String word;
    public String enOnyomi;
    public String enKuniomi;
    public String onyomi;
    public String kuniomi;
    public String english;
    public String vietnamese;
    public String compound;
    public String history;
    public List<BasicSet> basicSets;
}
