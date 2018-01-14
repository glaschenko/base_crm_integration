package orm_test;

import lombok.Data;
import org.eclipse.persistence.annotations.Index;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
@Entity
@Data
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String summary;
    @Index(name = "description_idx", unique = true)
    private String description;
}
