package com.microservice.pricingservice.repository;

import com.microservice.pricingservice.entity.Price;
import org.springframework.data.repository.CrudRepository;

public interface PriceRepository extends CrudRepository<Price, Long> {

}
