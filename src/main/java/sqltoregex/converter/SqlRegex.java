package sqltoregex.converter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlRegex {
    private String sql = "";
    private String regex = "";

    public SqlRegex(String sql){
        this.sql = sql;
    }

    public SqlRegex(){}

    public String getRegex() {
        return regex;
    }

    public String getSql() {
        return sql;
    }

    public void setRegex(String regex) {
        if(regex.length() != 0){
            this.regex = regex;
        }
    }

    public void setSql(String sql) {
        if(sql.length() != 0){
            this.sql = sql;
        } else throw new NullPointerException("SQL-Input-String should have more characters than null.");
    }

    public void convert(){
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.INFO);
        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        logger.addHandler(handler);
        try{
            //TODO: some converting stuff here
            this.setRegex("test - converting coming soon");
        } catch(Exception e){
            this.setRegex("Something went wrong. Try again!");
            logger.severe(e.toString());
        }
    }

    @Override
    public String toString(){
        return "{\"sql\":\"" + this.getSql() + "\", \"regex\":\"" + this.getRegex() + "\"}";
    }
}