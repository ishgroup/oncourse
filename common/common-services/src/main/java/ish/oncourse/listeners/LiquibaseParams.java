package ish.oncourse.listeners;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiquibaseParams {

    private List<String> machineIncludes = new ArrayList<>();
    private List<String> machineExcludes = new ArrayList<>();
    private boolean failOnError;
    private List<DataSource> dataSourcesParam = new ArrayList<>();
    private List<String> changeLogFilesParam = new ArrayList<>();
    private String contexts;
    private String defaultSchema;
    private Map<String, String> liquibaseParameters = new HashMap<>();

    public List<String> getMachineIncludes() {
        return machineIncludes;
    }

    public void addMachineIncludes(String machineIncludes) {
        this.machineIncludes.add(machineIncludes);
    }

    public List<String> getMachineExcludes() {
        return machineExcludes;
    }

    public void addMachineExcludes(String machineExcludes) {
        this.machineExcludes.add(machineExcludes);
    }

    public boolean getFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public List<DataSource> getDataSources() {
        return dataSourcesParam;
    }

    public void addDataSourcesParam(DataSource dataSource) {
        this.dataSourcesParam.add(dataSource);
    }

    public List<String> getChangeLogFilesParam() {
        return changeLogFilesParam;
    }

    public void addChangeLogFiles(String changeLogFilesParam) {
        this.changeLogFilesParam.add(changeLogFilesParam);
    }

    public String getContexts() {
        return contexts;
    }

    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }

    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public Map<String, String> getLiquibaseParameters() {return liquibaseParameters; }

    public void addLiquibaseParameter(String paramName, String paramValue) { this.liquibaseParameters.put(paramName, paramValue); }
}
