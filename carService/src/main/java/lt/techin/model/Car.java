package lt.techin.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('AVAILABLE', 'RENTED') DEFAULT 'AVAILABLE'")
    private CarStatus status = CarStatus.AVAILABLE;
    private BigDecimal dailyRentPrice;

    @OneToMany(mappedBy = "car")
    private List<Rental> rentals = new ArrayList<>();

    public Car(String brand, String model, int year, CarStatus status, List<Rental> rentals, BigDecimal dailyRentPrice) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.status = (status != null) ? status : CarStatus.AVAILABLE;
        this.rentals = rentals != null ? rentals : new ArrayList<>();
        this.dailyRentPrice = dailyRentPrice;
    }

    public Car() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public BigDecimal getDailyRentPrice() {
        return dailyRentPrice;
    }

    public void setDailyRentPrice(BigDecimal dailyRentPrice) {
        this.dailyRentPrice = dailyRentPrice;
    }
}
