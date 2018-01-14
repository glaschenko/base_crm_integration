package orm_test;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
@Entity
@Cache(type = CacheType.FULL)
@DiscriminatorValue("HUMAN")
public class Person extends Mammal {

    private Date dob;
    private String name;
    private boolean male;

    private Family family;

    @Enumerated(EnumType.STRING)
    private Nationality nationality;

    @ManyToMany
    private Set<Person> friends;

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

    public Set<Person> getFriends() {
        return friends;
    }

    public void setFriends(Set<Person> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return name + ", v. " + getVersion() + ", family: " + family.getFamilyName();
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

}
