package com.zylex.carbot.view;

import com.zylex.carbot.model.*;
import com.zylex.carbot.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class View {

    private final CarRepository carRepository;

    private Map<String, String> colors = new LinkedHashMap<>();

    {
        colors.put("Ледниковый", "Белый");
        colors.put("Марс", "Оранжевый");
        colors.put("Сердолик", "Красный");
        colors.put("Ангкор", "Коричневый");
        colors.put("Карфаген", "Серо-бежевый");
        colors.put("Дайвинг", "Ярко-синий");
        colors.put("Фантом", "Серо-голубой");
        colors.put("Плутон", "Серый");
        colors.put("Маэстро", "Черный");
        colors.put("Платина", "Серебристый");
    }

    public View(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String process(Equipment equipment) {
        return buildOutput(equipment);
    }

    private String buildOutput(Equipment equipment) {
        String output = "";
        output += "\nКомплектация: \"" + equipment.getName() + "\"\n";

        Set<Filial> totalFilials = new HashSet<>();
        for (String color : colors.keySet()) {
            output += findByColor(equipment, totalFilials, color);
        }

        output += "\nВсего автомобилей "
                + carRepository.findAll().stream()
                .filter(car -> car.getEquipment().equals(equipment))
                .count()
                + " у "
                + totalFilials.size()
                + " дилеров.";

        return output;
    }

    private String findByColor(Equipment equipment, Set<Filial> totalFilials, String color) {
        String output = "";
        List<Car> cars = carRepository.findByColorContaining(color).stream()
                .filter(car -> car.getEquipment().equals(equipment))
                .collect(Collectors.toList());
        Set<Filial> filials = new LinkedHashSet<>();
        for (Car car : cars) {
            filials.add(car.getFilial());
        }
        totalFilials.addAll(filials);
        output += "\nАвтомобили цвета \"" + color + "\" (" + colors.get(color) + "):\n";
        int i = 0;
        for (Filial filial : filials) {
            output += ++i + ") " + filial.getAddress() + "\n";
            String url = filial.getDealer().getLink() + equipment.getModel().getLinkPart();
            if (!filial.getCode().isEmpty()) {
                url += "?dealer=" + filial.getCode();
            }
            output += url + "\n";
        }
        if (i == 0) {
            output += "Нет доступных автомобилей\n";
        }
        return output;
    }

    public String buildColorOutput(Equipment equipment, String color) {
        String output = "";
        output += "\nКомплектация: \"" + equipment.getName() + "\"\n";

        Set<Filial> totalFilials = new HashSet<>();
        output += findByColor(equipment, totalFilials, color);

        return output;
    }

    public Map<String, String> getColors() {
        return colors;
    }
}
