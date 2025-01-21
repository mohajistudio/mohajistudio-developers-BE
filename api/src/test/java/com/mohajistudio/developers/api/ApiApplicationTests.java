package com.mohajistudio.developers.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

    String htmlContent = """
            <div>
                <p>여기에는 이미지가 포함된 게시글이 작성됩니다.</p>
                <img src="/media/temp/temp_image1.jpg" alt="Temporary Image1">
                <img src="/media/temp/temp_image2.jpg" alt="Temporary Image2">
            </div>""";

    @Test
    void testJsoup() {
        Document document = Jsoup.parse(htmlContent);
        Elements elements = document.select("img[src]");

        for (Element img : elements) {
            String src = img.attr("src");
            System.out.println("src = " + src);
            if (src.startsWith("/media/temp")) {
                String newPath = "/media/images/temp_image1.jpg";
                img.attr("src", newPath);
            }
        }

        System.out.println("document = " + document.html());
    }

}
