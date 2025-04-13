package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// 景點資料類別
class Sight {
    private String name;
    private String address;
    private String description;
    private String zone;
    private String category;
    private String photoUrl;

    public Sight(String name) {
        this.name = name;
        this.zone = "未知";
        this.address = "無地址資訊";
        this.description = "無描述";
        this.category = "無類別";
        this.photoUrl = "無圖片";
    }

    public void setAddress(String address) {
        this.address = (address != null) ? address : "無地址資訊";
        setZoneFromAddress();
    }

    public void setDescription(String description) {
        this.description = (description != null && !description.trim().isEmpty()) ? description : "無描述";
    }

    public void setZone(String zone) {
        if (zone != null && !zone.trim().isEmpty() && !zone.equals("未知")) {
            this.zone = zone;
        } else {
            setZoneFromAddress();
        }
    }

    public void setCategory(String category) {
        this.category = (category != null && !category.trim().isEmpty()) ? category : "無類別";
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = (photoUrl != null && !photoUrl.trim().isEmpty()) ? photoUrl : "無圖片";
    }

    public String getCategory() { return category; }
    public String getZone() { return zone; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getPhotoUrl() { return photoUrl; }

    private void setZoneFromAddress() {
        if (this.zone.equals("未知") && this.address.contains("區")) {
            this.zone = this.address.split("區")[0] + "區";
        }
    }

    @Override
    public String toString() {
        return String.format("SightName: %s\nZone: %s\nCategory: %s\nPhotoURL: %s\nDescription: %s\nAddress: %s\n",
                name, zone, category, photoUrl, description, address);
    }
}

// 主爬蟲程式
public class KeelungSightsCrawler {
    private static final String BASE_URL = "https://www.travelking.com.tw";
    private static final String KEELUNG_URL = BASE_URL + "/tourguide/taiwan/keelungcity/";

    public static void main(String[] args) {
        String targetZone = "七堵"; // 可修改查詢區域
        KeelungSightsCrawler crawler = new KeelungSightsCrawler();
        Sight[] sights = crawler.getItems(targetZone);

        System.out.printf("\n=====【%s區 景點列表】=====\n", targetZone);
        if (sights.length == 0) {
            System.out.printf("找不到 %s區 的景點！\n", targetZone);
        } else {
            for (Sight sight : sights) {
                System.out.println(sight);
            }
        }
    }

    public Sight[] getItems(String targetZone) {
        List<Sight> sights = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(KEELUNG_URL).userAgent("Mozilla/5.0").get();
            Elements sightLinks = doc.select(".box a");

            for (Element link : sightLinks) {
                String sightName = link.text();
                String sightUrl = BASE_URL + link.attr("href");
                Sight sight = parseSightDetail(sightName, sightUrl);

                if (sight.getZone().equals(targetZone + "區") || sight.getAddress().contains(targetZone)) {
                    sights.add(sight);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sights.toArray(new Sight[0]);
    }

    private Sight parseSightDetail(String sightName, String sightUrl) {
        try {
            Document detailDoc = Jsoup.connect(sightUrl).userAgent("Mozilla/5.0").get();
            Sight sight = new Sight(sightName);

            // 地區
            Elements zoneElements = detailDoc.select(".bc_last");
            String zone = zoneElements.isEmpty() ? "未知" : zoneElements.text().trim();
            sight.setZone(zone);

            // 地址
            Elements addressElements = detailDoc.select("span[property=vcard:street-address]");
            String address = addressElements.isEmpty() ? "無地址資訊" : addressElements.text().trim();
            sight.setAddress(address);

            // 描述
            Elements descElements = detailDoc.select("div.text");
            String description = descElements.isEmpty() ? "無描述" : descElements.text().trim();
            sight.setDescription(description);

            // 分類
            Elements categoryElements = detailDoc.select("span[property=rdfs:label]");
            String category = categoryElements.isEmpty() ? "無類別" : categoryElements.text().trim();
            sight.setCategory(category);

            // 圖片網址
            Elements imgElements = detailDoc.select("meta[itemprop=image]");
            String photoUrl = imgElements.isEmpty() ? "無圖片" : imgElements.attr("abs:content");
            sight.setPhotoUrl(photoUrl);

            return sight;

        } catch (IOException e) {
            System.out.println("解析失敗：" + sightUrl);
            return new Sight(sightName);
        }
    }
}
