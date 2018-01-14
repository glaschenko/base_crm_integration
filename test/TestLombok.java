import junit.framework.TestCase;
import lombok.SneakyThrows;
import lombok_test.Order;
import lombok_test.LBPerson;
import lombok_test.SneakyThrow;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
public class TestLombok extends TestCase {
    public void testData(){
        Order order = new Order(15, new BigDecimal(50.0));
        order.setGoodsDescription("Goods description");
        System.out.println(order);
    }

    public void testSneaky(){
        try {
            new SneakyThrow().sneakyIO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void testBuilder(){
        DateFormat f = SimpleDateFormat.getDateInstance();
        LBPerson arina = LBPerson.builder().name("Arina").dob(f.parse("18.05.1982")).build();
        LBPerson kostya = LBPerson.builder().name("Kostya").dob(f.parse("17.05.1982")).build();
        LBPerson andrey = LBPerson.builder().dob(f.parse("19.05.1982")).male(true).name("Andrey Glaschenko")
                .friend(arina).friend(kostya).build();
        System.out.println(andrey);
    }
}
