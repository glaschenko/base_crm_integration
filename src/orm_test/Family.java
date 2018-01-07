package orm_test;

import lombok.Data;
import org.eclipse.persistence.annotations.*;
import org.eclipse.persistence.annotations.Cache;

import javax.persistence.*;
import java.util.List;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
@Entity
@Data
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    private String familyName;

    @OneToMany(mappedBy = "family")
//    @Noncacheable
    private List<Person> members;


}
