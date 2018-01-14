package orm_test;

import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/*
 * Author: glaschenko
 * Created: 13.01.2018
 */
@Entity
@DiscriminatorValue(value = "BUS")
@Table(name = "tr_bus")
@ToString(callSuper = true)
public class Bus extends Transport {
    private int doorsCount = 1;
    private int passengerSeats = 15;

    public int getDoorsCount() {
        return doorsCount;
    }

    public void setDoorsCount(int doorsCount) {
        this.doorsCount = doorsCount;
    }

    public int getPassengerSeats() {
        return passengerSeats;
    }

    public void setPassengerSeats(int passengerSeats) {
        this.passengerSeats = passengerSeats;
    }
}
