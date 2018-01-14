package lombok_test;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

import java.util.Date;
import java.util.List;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
@Data
@Builder
public class LBPerson {
    @NonNull
    private final Date dob;
    private String name;
    private boolean male;
    @Singular
    private List<LBPerson> friends;
}
