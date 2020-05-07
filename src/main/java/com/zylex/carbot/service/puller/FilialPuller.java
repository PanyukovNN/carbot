package com.zylex.carbot.service.puller;

import com.zylex.carbot.model.Dealer;
import com.zylex.carbot.model.Filial;
import com.zylex.carbot.repository.DealerRepository;
import com.zylex.carbot.repository.FilialRepository;
import com.zylex.carbot.service.driver.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class FilialPuller {

    private final DealerRepository dealerRepository;

    private final FilialRepository filialRepository;

    private final DriverManager driverManager;

    public FilialPuller(DealerRepository dealerRepository,
                        FilialRepository filialRepository,
                        DriverManager driverManager) {
        this.dealerRepository = dealerRepository;
        this.filialRepository = filialRepository;
        this.driverManager = driverManager;
    }

    @Transactional
    public void pull() {
        List<Dealer> dealers = dealerRepository.findAll();
        for (Dealer dealer : dealers) {
            String dealerLink = "https://" + dealer.getLink() + "/ds/cars/vesta/sw-cross/prices.html";
            driverManager.getDriver().navigate().to(dealerLink);

            if (driverManager.getDriver().getCurrentUrl().contains("www.lada.ru")) {
                continue;
            }

            boolean multipleFilials = true;
            if (driverManager.getDriver().getPageSource().contains("but-place-light")) {
                multipleFilials = false;
            }

            if (multipleFilials) {
                extractMultipleFilial(dealer);
            } else {
                WebElement addressElement = driverManager.waitElement(By::className, "but-place-light");
                Filial filial = new Filial(dealer, addressElement.getText(), "");
                if (filialRepository.findByAddress(filial.getAddress()) == null) {
                    filialRepository.save(filial);
                    System.out.println(filial);
                }
            }
        }
    }

    private boolean extractMultipleFilial(Dealer dealer) {
        try {
            driverManager.waitElement(By::className, "but-place").click();
        } catch (ElementClickInterceptedException ignore) {
        }
        try {
            driverManager.waitElement(By::id, "TopMenu_reg_city_name");
            Document document = Jsoup.parse(driverManager.getDriver().getPageSource());
            Elements filialElements = document.select("div#TopMenu_reg_city_name > ul > li");

            for (Element filialElement : filialElements) {
                String filialAddress = filialElement.text();
                String filialCode = filialElement.attr("value");
                Filial filial = new Filial(dealer, filialAddress, filialCode);
                if (filialRepository.findByAddress(filialAddress) == null) {
                    filialRepository.save(filial);
                    System.out.println(filial);
                }
            }
            if (!filialElements.isEmpty()) {
                return true;
            }
        } catch (TimeoutException ignore) {
        }
        return false;
    }
}
