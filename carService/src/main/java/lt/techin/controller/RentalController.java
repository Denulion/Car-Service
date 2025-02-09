package lt.techin.controller;

import lt.techin.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public  RentalController(RentalService rentalService){
        this.rentalService = rentalService;
    }
}
