package com.zylex.carbot.service.parser;

import com.zylex.carbot.controller.logger.ParseProcessorConsoleLogger;
import com.zylex.carbot.exception.ParseProcessorException;
import com.zylex.carbot.model.*;
import com.zylex.carbot.repository.CarRepository;
import com.zylex.carbot.repository.EquipmentRepository;
import com.zylex.carbot.repository.FilialRepository;
import com.zylex.carbot.repository.ModelRepository;
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

    private final ModelRepository modelRepository;

    private final EquipmentRepository equipmentRepository;

    private final CarRepository carRepository;

    @Autowired
    public ParseProcessor(FilialRepository filialRepository,
                          CarRepository carRepository,
                          ModelRepository modelRepository,
                          EquipmentRepository equipmentRepository) {
        this.filialRepository = filialRepository;
        this.carRepository = carRepository;
        this.modelRepository = modelRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public void parse() {
        try {
            Model model = modelRepository.findByName("VESTA SW CROSS");
            List<Car> parsedCars = parseFilials(model);

            System.out.println("\nParsing finished");

            for (Car parsedCar : parsedCars) {
                Car repositoryCar = carRepository.findByFilialAndEquipmentAndColor(parsedCar.getFilial(), parsedCar.getEquipment(), parsedCar.getColor());
                if (repositoryCar == null) {
                    carRepository.save(parsedCar);
                    System.out.println(parsedCar);
                } else {
                    if (!repositoryCar.getStatus().equals(CarStatus.NEW.toString())) {
                        repositoryCar.setStatus("NEW");
                        carRepository.save(parsedCar);
                    }
                }
            }

            for (Equipment modelEquipment : model.getEquipments()) {
                List<Car> existedEquipmentCars = carRepository.findByEquipment(modelEquipment);
                for (Car existedCar : existedEquipmentCars) {
                    if (!parsedCars.contains(existedCar)) {
                        existedCar.setStatus(CarStatus.REMOVED.toString());
                        carRepository.save(existedCar);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ParseProcessorException(e.getMessage(), e);
        }
    }

    private List<Car> parseFilials(Model model) throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newWorkStealingPool();
        try {
            List<CallableFilialParser> callableFilialParsers = new ArrayList<>();
            List<Equipment> equipments = equipmentRepository.findByModel(model);
            List<Filial> filials = filialRepository.findAll();
            ParseProcessorConsoleLogger.startLog(filials.size());
            for (Filial filial : filials) {
                callableFilialParsers.add(new CallableFilialParser(filial, model, equipments));
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
