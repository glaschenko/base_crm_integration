package orm_test;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/*
 * Author: glaschenko
 * Created: 13.01.2018
 */
@Entity
@DiscriminatorValue("HUMAN")
public class Dog extends Mammal{
    private String name;
    private int tailLength;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTailLength() {
        return tailLength;
    }

    public void setTailLength(int tailLength) {
        this.tailLength = tailLength;
    }
}
