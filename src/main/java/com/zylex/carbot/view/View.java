package com.zylex.carbot.view;

import com.zylex.carbot.model.Car;
import com.zylex.carbot.model.CarStatus;
import com.zylex.carbot.model.Equipment;
import com.zylex.carbot.model.Filial;
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

    public void printOutput() {
        List<String> colors = Arrays.asList("Ледниковый", "Марс", "Сердолик", "Ангкор", "Карфаген", "Дайвинг", "Фантом", "Плутон", "Маэстро", "Платина");
        Equipment equipment = equipmentRepository.findById(2L).get();
        System.out.println("Комплектация: \"" + equipment.getName() + "\"");

        Set<Filial> totalFilials = new HashSet<>();
        for (String color : colors) {
            List<Car> cars = carRepository.findByColorContaining(color).stream()
                    .filter(car -> car.getEquipment().equals(equipment))
                    .filter(car -> car.getStatus().equals(CarStatus.NEW.toString()))
                    .collect(Collectors.toList());
            Set<Filial> filials = new LinkedHashSet<>();
            for (Car car : cars) {
                filials.add(car.getFilial());
            }
            totalFilials.addAll(filials);
            System.out.println("\nАвтомобили цвета \"" + color + "\":");
            int i = 0;
            for (Filial filial : filials) {
                System.out.println(++i + ") " + filial.getAddress());
                String url = "https://" + filial.getDealer().getLink() + equipment.getModel().getLinkPart();
                if (!filial.getCode().isEmpty()) {
                    url += "?dealer=" + filial.getCode();
                }
                System.out.println(url);
            }
            if (i == 0) {
                System.out.println("Нет доступных автомобилей");
            }
        }

        System.out.println("\nВсего автомобилей "
                + carRepository.findAll().stream()
                .filter(car -> car.getEquipment().equals(equipment))
                .filter(car -> car.getStatus().equals(CarStatus.NEW.toString()))
                .count()
                + " у "
                + totalFilials.size()
                + " дилеров.");
    }
}
