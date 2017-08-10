package muscular.man.tools.kanjinvk.model.enums;

/**
 * Created by khanhnv10 on 2016/02/26.
 */
public enum ViewListType {
    BLOCK(0, "Blocks"),
    DETAIL(1, "Details"),
    GRID(2, "Grid");

    public int index;
    public String title;
    ViewListType(int id, String title) {
        this.index = id;
        this.title = title;
    }

    public boolean isBlockType() {
        return ViewListType.BLOCK.index == index;
    }

    public boolean isDetailType() {
        return ViewListType.DETAIL.index == index;
    }

    public boolean isGridType() {
        return ViewListType.GRID.index == index;
    }
}
