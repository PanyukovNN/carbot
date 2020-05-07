package com.zylex.carbot.service.parser;

import com.zylex.carbot.model.Car;
import com.zylex.carbot.model.Filial;
import com.zylex.carbot.repository.CarRepository;
import com.zylex.carbot.repository.FilialRepository;
import com.zylex.carbot.service.driver.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParseProcessor {

    private final FilialRepository filialRepository;

    private final CarRepository carRepository;

    private final DriverManager driverManager;

    @Autowired
    public ParseProcessor(FilialRepository filialRepository,
                          CarRepository carRepository,
                          DriverManager driverManager) {
        this.filialRepository = filialRepository;
        this.carRepository = carRepository;
        this.driverManager = driverManager;
    }

    public void parse() {
        List<Filial> filials = filialRepository.findAll();
        for (Filial filial : filials) {
            String url = "https://" + filial.getDealer().getLink() + "/ds/cars/vesta/sw-cross/prices.html";
            if (!filial.getCode().isEmpty()) {
                url += "?dealer=" + filial.getCode();
            }
            driverManager.getDriver().navigate().to(url);
            driverManager.waitElement(By::id, "configurator");

            Document document = Jsoup.parse(driverManager.getDriver().getPageSource());
            Element carElement = document.selectFirst("div#12290960");
            String equipment = carElement.selectFirst("p.kompl_name").text();
            Element hasDealerElement = carElement.selectFirst("p.has_dealer");
            if (hasDealerElement == null) {
                continue;
            }
            Elements colorElements = hasDealerElement.select("span.color_dealer");
            for (Element colorElement : colorElements) {
                String color = colorElement.attr("title");
                Car car = new Car(filial, equipment, color);
                if (carRepository.findByFilialAndEquipmentAndColor(filial, equipment, color) == null) {
                    carRepository.save(car);
                    System.out.println(car);
                }
            }
        }
    }
}
