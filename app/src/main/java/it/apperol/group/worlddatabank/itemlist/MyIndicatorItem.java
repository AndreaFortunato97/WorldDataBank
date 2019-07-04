package it.apperol.group.worlddatabank.itemlist;

public class MyIndicatorItem {
    private String indicatorName;
    private String indicatorID;

    public MyIndicatorItem(String indicatorName, String indicatorID) {
        this.indicatorName = indicatorName;
        this.indicatorID = indicatorID;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public String getIndicatorID() {
        return indicatorID;
    }
}
