package com.zylex.carbot.service.parser;

import com.zylex.carbot.controller.logger.ParseProcessorConsoleLogger;
import com.zylex.carbot.model.*;
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

    private final Model model;

    private final List<Equipment> equipments;

    CallableFilialParser(Filial filial,
                         Model model,
                         List<Equipment> equipments) {
        this.filial = filial;
        this.model = model;
        this.equipments = equipments;
    }

    public List<Car> call() {
        try {
            ParseProcessorConsoleLogger.logFilial();
            return parseCars();
        } catch (IOException e) {
            ParseProcessorConsoleLogger.logFilialError();
            return Collections.emptyList();
        }
    }

    private List<Car> parseCars() throws IOException {
        Document document = navigateToFilial();
        List<Car> parsedCars = new ArrayList<>();
        for (Equipment equipment : equipments) {
            Element carElement = document.selectFirst("div#" + equipment.getCode());
            if (carElement == null) {
                continue;
            }
            Element hasDealerElement = carElement.selectFirst("p.has_dealer");
            if (hasDealerElement == null) {
                continue;
            }

            Elements colorElements = hasDealerElement.select("span.color_dealer");
            for (Element colorElement : colorElements) {
                String color = colorElement.attr("title");
                Car car = new Car(filial, equipment, color);
                parsedCars.add(car);
            }
        }
        return parsedCars;
    }

    private Document navigateToFilial() throws IOException {
        String url = "https://" + filial.getDealer().getLink() + model.getLinkPart();
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
