import junit.framework.TestCase;
import lori.jdbc.JDBCLoriProjectsLoader;

/*
 * Author: glaschenko
 * Created: 27.12.2017
 */
public class TestLoriLoaders extends TestCase {
    public void testJDBCLoader(){
        JDBCLoriProjectsLoader loader = new JDBCLoriProjectsLoader();
        loader.loadProjectsWithClients();
    }
}
