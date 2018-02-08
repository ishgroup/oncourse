package ish.oncourse.listeners;

import javax.sql.DataSource;

public class LiquibaseParamBuilder {

    private LiquibaseParams params = new LiquibaseParams();

    public LiquibaseParamBuilder addChangeLogFile(String param) {
        params.addChangeLogFiles(param);
        return this;
    }

    public LiquibaseParamBuilder addDataSource(DataSource param){
        params.addDataSourcesParam(param);
        return this;
    }

    public LiquibaseParamBuilder addContexts(String param) {
        params.setContexts(param);
        return this;
    }

    public LiquibaseParamBuilder addDefaultSchema(String param) {
        params.setDefaultSchema(param);
        return this;
    }

    public LiquibaseParamBuilder setFailOnError(boolean param) {
        params.setFailOnError(param);
        return this;
    }

    public LiquibaseParamBuilder addMachineExcludes(String param) {
        params.addMachineExcludes(param);
        return this;
    }

    public LiquibaseParamBuilder addMachineIncludes(String param) {
        params.addMachineIncludes(param);
        return this;
    }

    public LiquibaseParamBuilder addLiquibaseeParamerter(String key, String value) {
        params.addLiquibaseParameter(key, value);
        return this;
    }

    public LiquibaseParams build() {
        return params;
    }
}
