package it.apperol.group.worlddatabank.itemlist;

public class MyCountryItem {
    private String countryName;
    private String capitalName;
    private String imageUrl;
    private String countryIso2Code;

    public MyCountryItem(String countryName, String capitalName, String imageUrl, String countryIso2Code) {
        this.countryName = countryName;
        this.capitalName = capitalName;
        this.imageUrl = imageUrl;
        this.countryIso2Code = countryIso2Code;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCapitalName() {
        return capitalName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCountryIso2Code() {
        return countryIso2Code;
    }
}
