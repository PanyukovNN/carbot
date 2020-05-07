package com.zylex.carbot.repository;

import com.zylex.carbot.model.Dealer;
import com.zylex.carbot.model.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilialRepository extends JpaRepository<Filial, Long> {

    Filial findByDealerAndCode(Dealer dealer, String code);

    Filial findByAddress(String address);
}
