package orm_test;

import javax.persistence.*;

/*
 * Author: glaschenko
 * Created: 13.01.2018
 */

@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "mammalType", discriminatorType = DiscriminatorType.STRING)
public abstract class Mammal {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;

    @Version
    private Integer version;


    private int weightKG;
    private int heightCM;

    @Enumerated(EnumType.STRING)
    private MammalType mammalType;

    public int getWeightKG() {
        return weightKG;
    }

    public void setWeightKG(int weightKG) {
        this.weightKG = weightKG;
    }

    public int getHeightCM() {
        return heightCM;
    }

    public void setHeightCM(int heightCM) {
        this.heightCM = heightCM;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
