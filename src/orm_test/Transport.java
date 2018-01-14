package orm_test;

import lombok.ToString;

import javax.persistence.*;

/*
 * Author: glaschenko
 * Created: 13.01.2018
 */
@Table(name = "tr_transport")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@ToString
public abstract class Transport {
    @GeneratedValue
    @Id
    private int id;

    @Column(name = "max_speed")
    private int maxSpeed;

    private int capacity;

    private String model;

    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
