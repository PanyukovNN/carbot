package com.zylex.carbot.repository;

import com.zylex.carbot.model.ParsingTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParsingTimeRepository extends JpaRepository<ParsingTime, Long> {

    ParsingTime findFirstByOrderByDateTimeDesc();
}
