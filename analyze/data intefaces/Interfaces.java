
// logic interfaces

public interface ISapContext {
	
	List<IAccessLogEntry> queryAccessLog(ISapConnectionData credentials);
	
}

public interface ILocalDbContext {
	
	// init connection
	boolean initContext(ILocalDbUser user);
	
	// CRUD configs
	List<IAuthPattern> queryConfigs();
	
	// CRUD whitelists
	List<IWhitelist> queryWhitelists();
	
	// pattern analysis
	List<IAccessLogEntry> runPatternAnalysisAndGetResults();
	void savePatternAnalysisResults(List<IAccessLogEntry>);
	
}

// data objects

public interface ILocalDbUser {
	
	String getUsername();
	String getPassword();
	
}

public interface ISapConnectionData {
	
	
	
}

public interface IAccessLogEntry {
	
	
	
}

public interface IPatternAnalysisResult {
	
	
	
}

public interface IWhitelist {
	
	
	
}