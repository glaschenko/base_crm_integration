package xpath;

import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

/*
 * Author: glaschenko
 * Created: 07.01.2018
 */
public class EmployeesParser {
    private Document xmlDocument;

    @SneakyThrows
    public void parse(String path) {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        try(InputStream is = this.getClass().getResourceAsStream(path)){
            xmlDocument = builder.parse(is);
        }
    }

    @SneakyThrows
    public String getEmailByEmployeeId(int id) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/Employees/Employee[@emplid='" + id + "']/email";

        //read an xml node using xpath
//        Node node = (Node) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
//        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        //read a string value
        return xPath.compile(expression).evaluate(xmlDocument);
    }

    @SneakyThrows
    public String getNameSurnameByEmployeeId(int id) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/Employees/Employee[@emplid='" + id + "']/firstname/node() | " +
                "/Employees/Employee[@emplid='" + id + "']/lastname/node()";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        String res = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            if(i > 0) res += " ";
            Node each = nodeList.item(i);
            res += each.getNodeValue();
        }

        //read a string value
        return res;
    }


    @SneakyThrows
    public NodeList getEmployeesByType(String type) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//Employee[@type='" + type + "']";
        return (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
    }

    @SneakyThrows
    public NodeList getOldEmployees(int age) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//Employee[age > '" + age + "']";
        return (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
    }

    @SneakyThrows
    public void printEmails(){
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/Employees/Employee/email/node()";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node each = nodeList.item(i);
            System.out.println(each.getNodeValue());
        }
    }

    public void printEmployees(NodeList employees) {
        for (int i = 0; i < employees.getLength(); i++) {
            printEmployee(employees.item(i));
        }
    }

    public void printEmployee(Node node) {
        System.out.println("-----------------------");
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node each = nodeList.item(i);
            if (each.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println(nodeList.item(i).getNodeName() + " : " + each.getFirstChild().getNodeValue());
            }
        }
    }

    public Document getXmlDocument() {
        return xmlDocument;
    }


}
