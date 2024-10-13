package com.bitc.bitcclub.repository;

import com.bitc.bitcclub.model.BitcClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitcClassRepository extends JpaRepository<BitcClass, Integer> {

}
