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
    private String category; // 新增 category 欄位

    public Sight(String name, String url) {
        this.name = name;
        this.url = url;
        this.zone = "未知";
        this.address = "無地址資訊";
        this.description = "無描述";
        this.category = "無類別"; // 預設為無類別
    }

    public void setAddress(String address) {
        this.address = (address != null) ? address : "無地址資訊";
    }

    public void setDescription(String description) {
        this.description = (description != null && !description.trim().isEmpty()) ? description : "無描述";
    }

    public void setZone(String zone) {
        this.zone = (zone != null) ? zone : "未知";
    }

    public void setCategory(String category) {
        this.category = (category != null && !category.trim().isEmpty()) ? category : "無類別";
    }

    public String getCategory() {
        return category;
    }

    public String getZone() {
        return zone;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return String.format("SightName: %s\nZone: %s\nCategory: %s\nPhotoURL:\n%s\nDescription: %s\nAddress: %s\n",
                name, zone, category, url, description, address); // 保留"地址："標籤
    }
}

public class test1 {
    private static final String BASE_URL = "https://www.travelking.com.tw";
    private static final String KEELUNG_URL = BASE_URL + "/tourguide/taiwan/keelungcity/";
    private static final String TARGET_ZONE = "七堵區";

    public static void main(String[] args) {
        try {
            List<Sight> sights = getItems();

            System.out.println("\n=====【七堵區 景點列表】=====");
            if (sights.isEmpty()) {
                System.out.println("找不到七堵區的景點！");
            } else {
                for (Sight sight : sights) {
                    System.out.println(sight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Sight> getItems() throws IOException {
        List<Sight> sights = new ArrayList<>();
        
        Document doc = Jsoup.connect(KEELUNG_URL).userAgent("Mozilla/5.0").get();
        Elements sightLinks = doc.select(".box a");

        for (Element link : sightLinks) {
            String sightName = link.text();
            String sightUrl = BASE_URL + link.attr("href");

            Sight sight = parseSightDetail(sightName, sightUrl);

            if ((sight.getZone() != null && sight.getZone().contains(TARGET_ZONE)) ||
                (sight.getAddress() != null && sight.getAddress().contains(TARGET_ZONE))) {
                sights.add(sight);
            }
        }
        return sights;
    }

    private static Sight parseSightDetail(String sightName, String sightUrl) {
        try {
            Document detailDoc = Jsoup.connect(sightUrl).userAgent("Mozilla/5.0").get();
            Sight sight = new Sight(sightName, sightUrl);

            // 取得區域資訊
            Elements zoneElements = detailDoc.select(".location");
            String zone = zoneElements.isEmpty() ? "" : zoneElements.text().trim();
            
            // 如果 `.location` 無法解析區域，嘗試從 `.address` 提取
            if (zone.isEmpty() || zone.equals("未知")) {
                Elements addressElements = detailDoc.select(".address");
                String address = addressElements.isEmpty() ? "" : addressElements.text().trim();
                if (address.contains(TARGET_ZONE)) {
                    zone = TARGET_ZONE;
                }
            }

            sight.setZone(zone.isEmpty() ? "未知" : zone);

            // 取得地址，並去除「地址：」標籤
            Elements addressElements = detailDoc.select("span[property=vcard:street-address]");
            String address = addressElements.isEmpty() ? "無地址資訊" : addressElements.text().trim();
            sight.setAddress(address);

            // 取得描述
            Elements descElements = detailDoc.select("div.text");
            String description = descElements.isEmpty() ? "無描述" : descElements.text().trim();
            sight.setDescription(description);

            // 取得類別，這裡可以根據具體網頁結構調整
            Elements categoryElements = detailDoc.select("span[property=rdfs:label]"); // 假設這裡是選擇類別的元素
            String category = categoryElements.isEmpty() ? "無類別" : categoryElements.text().trim();
            sight.setCategory(category);

            return sight;
        } catch (IOException e) {
            System.out.println("解析失敗：" + sightUrl);
            return new Sight(sightName, sightUrl);
        }
    }
}
