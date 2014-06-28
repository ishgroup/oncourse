package ish.oncourse.listeners;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

//import javax.servlet.ServletContextListener;

public class TheLiquibaseServletListener extends ALiquibaseServletListener {

    @Override
    protected void executeLiquibase(Liquibase liquibase, String contexts) throws LiquibaseException {
        liquibase.update(contexts);
    }
}
