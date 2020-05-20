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

    public View(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String process(Equipment equipment) {
        List<String> colors = Arrays.asList("Ледниковый", "Марс", "Сердолик", "Ангкор", "Карфаген", "Дайвинг", "Фантом", "Плутон", "Маэстро", "Платина");
        return buildOutput(colors, equipment);
    }

    private String buildOutput(List<String> colors, Equipment equipment) {
        String output = "";
        output += "\nКомплектация: \"" + equipment.getName() + "\"\n";

        Set<Filial> totalFilials = new HashSet<>();
        for (String color : colors) {
            List<Car> cars = carRepository.findByColorContaining(color).stream()
                    .filter(car -> car.getEquipment().equals(equipment))
                    .collect(Collectors.toList());
            Set<Filial> filials = new LinkedHashSet<>();
            for (Car car : cars) {
                filials.add(car.getFilial());
            }
            totalFilials.addAll(filials);
            output += "\nАвтомобили цвета \"" + color + "\":\n";
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
}
