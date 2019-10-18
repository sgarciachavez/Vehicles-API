package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final MapsClient maps;
    private final PriceClient pricing;

    public CarService(CarRepository repository, PriceClient pc, MapsClient mc) {

        this.maps = mc;
        this.pricing  = pc;
        this.repository = repository;
    }
    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {

        List<Car> list = repository.findAll();

        for (Car c : list) {
            findById(c.getId()); //before returning the list add location & price
        }

        return list;
        //return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        Optional<Car> car = repository.findById(id);
        if(!car.isPresent()){
            throw new CarNotFoundException();
        }

        Car aCar = car.get();

        if(aCar.getPrice() == null){
            String price = pricing.getPrice(id); //Use pricing service
            if(price != null){
                aCar.setPrice(price);
            }
        }

        if(aCar.getLocation().getAddress() == null){
            Location location = maps.getAddress(aCar.getLocation());  //Use Location service
            aCar.setLocation(location);
        }

        return aCar;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {

        Optional<Car> car = repository.findById(id);
        if(!car.isPresent()){
            throw new CarNotFoundException();
        }

        Car aCar = car.get();
        repository.delete(aCar);
    }
}
