package lombok_test;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
@Data
public class Order {
    private final int id;
    private final BigDecimal amount;
    private Date date;
    private String goodsDescription;
}
