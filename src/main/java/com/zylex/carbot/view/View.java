package com.zylex.carbot.view;

import com.zylex.carbot.model.*;
import com.zylex.carbot.repository.CarRepository;
import com.zylex.carbot.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class View {

    private final CarRepository carRepository;

    private final EquipmentRepository equipmentRepository;

    public View(CarRepository carRepository,
                EquipmentRepository equipmentRepository) {
        this.carRepository = carRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public String process(Model model) {
        List<String> colors = Arrays.asList("Ледниковый", "Марс", "Сердолик", "Ангкор", "Карфаген", "Дайвинг", "Фантом", "Плутон", "Маэстро", "Платина");
        List<Equipment> equipments = equipmentRepository.findByModel(model);
//        for (Equipment equipment : equipments) {
//            printEquipment(colors, equipment);
//        }
        return buildOutput(colors, equipments.get(0));
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
                String url = "https://" + filial.getDealer().getLink() + equipment.getModel().getLinkPart();
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
