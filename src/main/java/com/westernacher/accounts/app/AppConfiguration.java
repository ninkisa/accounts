package com.westernacher.accounts.app;

public class AppConfiguration {
	private static final AppConfiguration instance = new AppConfiguration();
	private String jdbcUrl;
	private String jdbcUser;
	private String jdbcPassword;
    
	private AppConfiguration() {
	}
	
	public static AppConfiguration getInstance() {
		return instance;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getJdbcUser() {
		return jdbcUser;
	}

	void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}
	
}
