"# homework1" 
(第三次git上船更新測試用)
此為大四生研究所的學期作業第一題
使用jsoup抓取網頁的資料
(1) 網頁爬蟲練習
Java 版網頁爬蟲程式:使用 jsoup 剖析網頁,並將其轉換為 Java 的自訂物件
Sight 之陣列。
Sight 類別應是你自行創建之類別,應包含 sightName, zone, category, photoURL,
address 等屬性,並有相對應之 get/set 方法;Sight 類別應是一個 JavaBean。
⚫ Jsoup 函式庫:http://jsoup.org/
⚫ JavaBean 說明:https://zh.wikipedia.org/wiki/JavaBeans (只是一種 Java 資料類別的
規範)
⚫ 剖析之網頁:https://www.travelking.com.tw/tourguide/taiwan/keelungcity/
要剖析的區塊在下半部,且要深入到第二層連結之網頁進行剖析。

⚫ 此作業必須包含的程式片段:
KeelungSightsCrawler crawler = new KeelungSightsCrawler();
Sight [] sights = crawler.getItems("七堵");
for (Sight s: sights) {
System.out.println(s);
}
⚫ 執行結果範例:
SightName: 泰安瀑布
Zone: 七堵區
Category: 風景區
PhotoURL:
https://photo.network.com.tw/scenery/D6B7575B-E509-4FEA-9701-7CE532A9
650D_d.jpg
Description: 泰安瀑布位於七堵南方的草濫山區,又稱草濫山瀑布,因過

去交通不便,反而保留了純樸自然的景色,景致優美清麗,規畫有烤肉
區露營區、戲水區及環山步道,為一大眾化的露營、踏青去處。自瀑布
入口進入後,分成二條林蔭步道,左側步道沿溪闢建,略具原始叢林的
面貌,循行可抵露營區及烤肉區;露營區因乏人使用,已顯荒蕪。 若
自入口循右側步道上行可抵泰安瀑布;瀑高約 20 公尺,自山壁傾瀉而
下,聚成一泓清潭,於此戲水,偶見水鳥佇立瀑旁岩石,非常可愛;沿
著瀑布旁的環山步道繞行草濫山一周,還可做一次有身心健康的森林浴,
享受茂林修竹的自然清新,實為躲避塵囂的理想去處。
Address: 基隆市七堵區泰安路
SightName: 七堵鐵道紀念公園(舊七堵火車前站)
Zone: 七堵區
Category: 歷史古蹟
PhotoURL:
https://photo.network.com.tw/scenery/C8FACA19-DC22-46D8-9220-11A774E
8A6EF_d.jpg
Description:
Address: 基隆市七堵區光明路 23 號