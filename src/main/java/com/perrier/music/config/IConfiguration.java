package com.perrier.music.config;

public interface IConfiguration {

	Integer getOptionalInteger(OptionalProperty<Integer> op);

	String getOptionalString(OptionalProperty<String> op);

	String getRequiredString(Property<String> p) throws ConfigException;

	Integer getRequiredInteger(Property<Integer> p) throws ConfigException;

	Boolean getOptionalBoolean(OptionalProperty<Boolean> showSql);

}
