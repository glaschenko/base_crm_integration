package lori.jdbc;

import lori.LoriMain;
import lori.model.LoriProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/*
 * Author: glaschenko
 * Created: 26.12.2017
 */
public class JDBCLoriProjectsLoader {


    public List<LoriProject> loadProjectsWithClients() {
        Connection connection = null;
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(LoriMain.DB_URL, LoriMain.USERNAME, LoriMain.PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet projectsRS = statement.executeQuery(
                    "SELECT * FROM ts_project limit 10");
            projectsRS.next();
            System.out.println(projectsRS.getString("name"));
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        return null;
    }
}
