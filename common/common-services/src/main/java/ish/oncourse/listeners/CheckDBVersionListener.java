package ish.oncourse.listeners;

import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.exception.LiquibaseException;

import java.util.List;

public class CheckDBVersionListener extends ALiquibaseServletListener {

    @Override
    protected void executeLiquibase(Liquibase liquibase, String contexts) throws LiquibaseException {
        List<ChangeSet> changeSets = liquibase.listUnrunChangeSets(contexts);
        if (changeSets.size() > 0)
            throw new IllegalArgumentException(String.format(
                    "The version of the application cannot be started because DB version is old. \n" +
                    "The following changeSets are not applied: \n %s", changeSets));
    }
}
