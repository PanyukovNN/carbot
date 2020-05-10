package com.zylex.carbot.repository;

import com.zylex.carbot.model.Equipment;
import com.zylex.carbot.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByModel(Model model);
}
