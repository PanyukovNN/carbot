package com.zylex.carbot.repository;

import com.zylex.carbot.model.Dealer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {

    Dealer findByLink(String link);
}
