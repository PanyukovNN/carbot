package com.zylex.carbot.service.parser;

import com.zylex.carbot.model.Car;
import com.zylex.carbot.model.CarStatus;
import com.zylex.carbot.model.Filial;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class CallableFilialParser implements Callable<List<Car>> {

    private final Filial filial;

    CallableFilialParser(Filial filial) {
        this.filial = filial;
    }

    public List<Car> call() {
        try {
            return parseCars();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private List<Car> parseCars() throws IOException {
        Document document = navigateToFilial();
        Element carElement = document.selectFirst("div#12290960");
        String equipment = carElement.selectFirst("p.kompl_name").text();
        Element hasDealerElement = carElement.selectFirst("p.has_dealer");
        if (hasDealerElement == null) {
            return Collections.emptyList();
        }

        List<Car> parsedCars = new ArrayList<>();
        Elements colorElements = hasDealerElement.select("span.color_dealer");
        for (Element colorElement : colorElements) {
            String color = colorElement.attr("title");
            Car car = new Car(filial, equipment, color, CarStatus.NEW.toString());
            parsedCars.add(car);
        }
        return parsedCars;
    }

    private Document navigateToFilial() throws IOException {
        String url = "https://" + filial.getDealer().getLink() + "/ds/cars/vesta/sw-cross/prices.html";
        if (!filial.getCode().isEmpty()) {
            url += "?dealer=" + filial.getCode();
        }
        return connectToSite(url);
    }

    private static Document connectToSite(String link) throws IOException {
        return Jsoup.connect(link)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
    }
}
