package com.zylex.carbot.repository;

import com.zylex.carbot.model.Car;
import com.zylex.carbot.model.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Car findByFilialAndEquipmentAndColor(Filial filial, String equipment, String color);
}
