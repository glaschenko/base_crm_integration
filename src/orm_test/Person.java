package orm_test;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
@Entity
@Cache(type = CacheType.FULL)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    private Date dob;
    private String name;
    private boolean male;

    private Family family;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    @Override
    public String toString() {
        return name;
    }
}
