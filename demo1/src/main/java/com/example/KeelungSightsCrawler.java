package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Sight {
    private String name;
    private String url;
    private String address;
    private String description;
    private String zone;
    private String category;

    public Sight(String name, String url) {
        this.name = name;
        this.url = url;
        this.zone = "未知";
        this.address = "無地址資訊";
        this.description = "無描述";
        this.category = "無類別";
    }

    public void setAddress(String address) {
        this.address = (address != null) ? address : "無地址資訊";
        // 若 Zone 仍為「未知」且 Address 包含「七堵」，則修正 Zone
        if (this.zone.equals("未知") && this.address.contains("七堵")) {
            this.zone = "七堵區";
        }
    }

    public void setDescription(String description) {
        this.description = (description != null && !description.trim().isEmpty()) ? description : "無描述";
    }

    public void setZone(String zone) {
        if (zone != null && !zone.trim().isEmpty() && !zone.equals("未知")) {
            this.zone = zone;
        } else if (this.address.contains("七堵")) {
            this.zone = "七堵區";
        }
    }

    public void setCategory(String category) {
        this.category = (category != null && !category.trim().isEmpty()) ? category : "無類別";
    }

    public String getCategory() { return category; }
    public String getZone() { return zone; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return String.format("SightName: %s\nZone: %s\nCategory: %s\nPhotoURL:\n%s\nDescription: %s\nAddress: %s\n",
                name, zone, category, url, description, address);
    }
}

public class KeelungSightsCrawler {
    private static final String BASE_URL = "https://www.travelking.com.tw";
    private static final String KEELUNG_URL = BASE_URL + "/tourguide/taiwan/keelungcity/";

    public static void main(String[] args) {
        KeelungSightsCrawler crawler = new KeelungSightsCrawler();
        Sight[] sights = crawler.getItems("七堵");

        System.out.println("\n=====【七堵區 景點列表】=====");
        if (sights.length == 0) {
            System.out.println("找不到七堵區的景點！");
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

                // 如果 Zone 是 "七堵區" 或 Address 包含 "七堵" 就加入列表
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
            Sight sight = new Sight(sightName, sightUrl);

            // 取得區域資訊
            Elements zoneElements = detailDoc.select(".location");
            String zone = zoneElements.isEmpty() ? "未知" : zoneElements.text().trim();
            sight.setZone(zone);

            // 取得地址
            Elements addressElements = detailDoc.select(".address");
            String address = addressElements.isEmpty() ? "無地址資訊" : addressElements.text().trim();
            sight.setAddress(address);

            // 取得描述
            Elements descElements = detailDoc.select("div.text");
            String description = descElements.isEmpty() ? "無描述" : descElements.text().trim();
            sight.setDescription(description);

            // 取得類別
            Elements categoryElements = detailDoc.select("span[property=rdfs:label]");
            String category = categoryElements.isEmpty() ? "無類別" : categoryElements.text().trim();
            sight.setCategory(category);

            return sight;
        } catch (IOException e) {
            System.out.println("解析失敗：" + sightUrl);
            return new Sight(sightName, sightUrl);
        }
    }
}
