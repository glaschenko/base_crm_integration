package orm_test;

import javax.persistence.*;

/*
 * Author: glaschenko
 * Created: 13.01.2018
 */
@Table(name = "tr_airplane")
@Entity
@DiscriminatorValue(value = "AIRPLANE")
public class Airplane extends Transport{

    private int maxAltitude;

    @Column(name = "wings_span")
    private int wingsSpan;

    public int getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(int maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public int getWingsSpan() {
        return wingsSpan;
    }

    public void setWingsSpan(int wingsSpan) {
        this.wingsSpan = wingsSpan;
    }
}
