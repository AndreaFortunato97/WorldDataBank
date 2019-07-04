package it.apperol.group.worlddatabank.itemlist;

public class MyTopicItem {
    private String topicName;
    private Integer topicID;

    public MyTopicItem(String topicName, Integer topicID) {
        this.topicName = topicName;
        this.topicID = topicID;
    }

    public String getTopicName() {
        return topicName;
    }

    public Integer getTopicID() {
        return topicID;
    }
}
