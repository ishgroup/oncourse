package ish.oncourse.services.environment;

public interface IEnvironmentService {

	String getBuildServerID();

	String getApplicationName();

	String getScmVersion();

	boolean isTransientEnvironment();
	
	String getCiVersion();
}
