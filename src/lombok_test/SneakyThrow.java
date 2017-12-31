package lombok_test;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.log4j.Priority;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;

/*
 * Author: glaschenko
 * Created: 31.12.2017
 */
@Log
public class SneakyThrow {
    @SneakyThrows
    public SneakyThrow() {
        LogManager.getLogManager().readConfiguration(SneakyThrow.class.getResourceAsStream("logging.properties"));
    }

    @SneakyThrows
    public void sneakyIO(){
        log.log(Level.SEVERE, "Captain speaking");
        new FileInputStream("t.t");
    }
}
