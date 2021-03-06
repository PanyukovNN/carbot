package com.zylex.carbot.repository;

import com.zylex.carbot.model.Car;
import com.zylex.carbot.model.Equipment;
import com.zylex.carbot.model.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Car findByFilialAndEquipmentAndColor(Filial filial, Equipment equipment, String color);

    List<Car> findByEquipment(Equipment equipment);

    List<Car> findByColorContaining(String color);
}
