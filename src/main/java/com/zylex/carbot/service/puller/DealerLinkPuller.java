package com.zylex.carbot.service.puller;

import com.zylex.carbot.exception.ParseProcessorException;
import com.zylex.carbot.model.Dealer;
import com.zylex.carbot.repository.DealerRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DealerLinkPuller {

    private final DealerRepository dealerRepository;

    @Autowired
    public DealerLinkPuller(DealerRepository dealerRepository) {
        this.dealerRepository = dealerRepository;
    }

    public void pull() {
        try {
            Document document = connectToSite("https://bezrulya.ru/dealers/list/lada/");
            Elements linkElements = document.select("div.dlc-item > p > a");
            List<String> dealerPages = new ArrayList<>();
            for (Element linkElement : linkElements) {
                dealerPages.add(linkElement.attr("href"));
            }

            for (String dealerPage : dealerPages) {
                Document dealerDocument = connectToSite("https://bezrulya.ru" + dealerPage);
                String dealerDocumentHtml = dealerDocument.html();
                int startIndex = dealerDocumentHtml.indexOf("dealer-link=\"") + 13;
                int endIndex = dealerDocumentHtml.substring(startIndex).indexOf("\"");
                String dealerLink = dealerDocumentHtml.substring(startIndex, startIndex + endIndex);
                if (dealerLink.endsWith(".lada.ru")) {
                    dealerLink = dealerLink.replace("http://", "");
                    System.out.println(dealerLink);
                    Dealer dealer = new Dealer(dealerLink);
                    if (dealerRepository.findByLink(dealerLink) == null) {
                        dealerRepository.save(dealer);
                    }
                }
            }
        } catch (IOException e) {
            throw new ParseProcessorException(e.getMessage(), e);
        }
    }

    private Document connectToSite(String link) throws IOException {
        return Jsoup.connect(link)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
    }
}
