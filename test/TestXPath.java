import junit.framework.TestCase;
import org.w3c.dom.NodeList;
import xpath.EmployeesParser;

/*
 * Author: glaschenko
 * Created: 07.01.2018
 */
public class TestXPath extends TestCase {
    public void testParse(){
        EmployeesParser parser = new EmployeesParser();
        parser.parse("employees.xml");
        System.out.println(parser.getXmlDocument());
    }

    public void testGetEmailById(){
        EmployeesParser parser = new EmployeesParser();
        parser.parse("employees.xml");
        String email = parser.getEmailByEmployeeId(2222);
        System.out.println(email);
    }

    public void testGetEmployeesByType(){
        EmployeesParser parser = new EmployeesParser();
        parser.parse("employees.xml");
        NodeList admins = parser.getEmployeesByType("admin");
        parser.printEmployees(admins);
    }

    public void testOldEmployees(){
        EmployeesParser parser = new EmployeesParser();
        parser.parse("employees.xml");
        NodeList oldFarts = parser.getOldEmployees(40);
        parser.printEmployees(oldFarts);
    }

    public void testPrintEmails(){
        EmployeesParser parser = new EmployeesParser();
        parser.parse("employees.xml");
        parser.printEmails();
    }

    public void testNameById(){
        EmployeesParser parser = new EmployeesParser();
        parser.parse("employees.xml");
        String name = parser.getNameSurnameByEmployeeId(3333);
        System.out.println(name);
    }
}
