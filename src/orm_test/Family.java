package orm_test;

import lombok.Data;
import org.eclipse.persistence.annotations.*;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.util.List;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
@Entity
@Data
@Cacheable
@NamedQuery(name = "familyByName", query = "select f from Family f where f.familyName like :name")
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    private String familyName;

    @OneToMany(mappedBy = "family", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//    @JoinFetch(JoinFetchType.OUTER)
//    @BatchFetch(BatchFetchType.JOIN)
    private List<Person> members;


}
