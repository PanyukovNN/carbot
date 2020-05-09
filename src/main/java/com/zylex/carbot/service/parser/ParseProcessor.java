package com.zylex.carbot.service.parser;

import com.zylex.carbot.exception.ParseProcessorException;
import com.zylex.carbot.model.Car;
import com.zylex.carbot.model.CarStatus;
import com.zylex.carbot.model.Filial;
import com.zylex.carbot.repository.CarRepository;
import com.zylex.carbot.repository.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ParseProcessor {

    private final FilialRepository filialRepository;

    private final CarRepository carRepository;

    @Autowired
    public ParseProcessor(FilialRepository filialRepository,
                          CarRepository carRepository) {
        this.filialRepository = filialRepository;
        this.carRepository = carRepository;
    }

    public void parse() {
        try {
            List<Car> totalParsedCar = parseFilials();
            System.out.println("Parsing finished");
            for (Car parsedCar : totalParsedCar) {
                Car repositoryCar = carRepository.findByFilialAndEquipmentAndColor(parsedCar.getFilial(), parsedCar.getEquipment(), parsedCar.getColor());
                if (repositoryCar == null) {
                    carRepository.save(parsedCar);
                    System.out.println(parsedCar);
                } else {
                    if (repositoryCar.getStatus().equals(CarStatus.REMOVED.toString())) {
                        repositoryCar.setStatus("NEW");
                        carRepository.save(parsedCar);
                        System.out.println("Car status changed " + repositoryCar);
                    }
                }
            }

            List<Car> existedCars = carRepository.findAll();
            for (Car existedCar : existedCars) {
                if (!totalParsedCar.contains(existedCar)) {
                    existedCar.setStatus(CarStatus.REMOVED.toString());
                    System.out.println("Car removed: " + existedCar);
                    carRepository.save(existedCar);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ParseProcessorException(e.getMessage(), e);
        }
    }

    private List<Car> parseFilials() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newWorkStealingPool();
        try {
            List<CallableFilialParser> callableFilialParsers = new ArrayList<>();
            for (Filial filial : filialRepository.findAll()) {
                callableFilialParsers.add(new CallableFilialParser(filial));
            }
            List<Future<List<Car>>> futureFilialParsers = service.invokeAll(callableFilialParsers);

            List<Car> totalParsedCars = new ArrayList<>();
            for (Future<List<Car>> carList : futureFilialParsers) {
                totalParsedCars.addAll(carList.get());
            }
            return totalParsedCars;
        } finally {
            service.shutdown();
        }
    }
}
