package com.perrier.music.config;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ApplicationConfig implements IConfiguration {

	@SuppressWarnings("rawtypes")
	private final Map properties;

	public ApplicationConfig(@SuppressWarnings("rawtypes")
	Map props) {
		this.properties = props;
	}

	private Double getDouble(String key) {
		if (this.properties.containsKey(key)) {
			Object p = this.properties.get(key);
			if (BigDecimal.class.equals(p.getClass())) {
				BigDecimal dec = (BigDecimal) p;
				return Double.valueOf(dec.doubleValue());
			}
		}
		return null;
	}

	private Date getDate(String key) {
		return this.getForType(key, Date.class);
	}

	private List<?> getList(String key) {
		if (this.properties.containsKey(key)) {
			Object p = this.properties.get(key);
			if (List.class.isAssignableFrom(p.getClass())) {
				return (List<?>) p;
			}
		}
		return null;
	}

	private <T> T getForType(String key, Class<T> type) {

		if (this.properties.containsKey(key)) {
			Object p = this.properties.get(key);
			if (p.getClass().equals(type)) {
				return type.cast(p);
			}
		}

		return null;
	}

	private <T> T getRequiredForType(String key, Class<T> type) throws ConfigException {
		T prop = this.getForType(key, type);
		if (prop == null) {
			throw new ConfigException("Missing required property: " + key);
		}
		return prop;
	}

	private <T> T getOptionalForType(String key, T keyDefault, Class<T> type) {
		T value = this.getForType(key, type);
		return value != null ? value : keyDefault;
	}

	private String getRequiredString(String key) throws ConfigException {
		return this.getRequiredForType(key, String.class);
	}

	@Override
	public Boolean getOptionalBoolean(OptionalProperty<Boolean> op) {
		return this.getOptionalBoolean(op.getKey(), op.getDefaultValue());
	}

	private Boolean getOptionalBoolean(String key, Boolean keyDefault) {
		return this.getOptionalForType(key, keyDefault, Boolean.class);
	}

	private String getOptionalString(String key, String keyDefault) {
		return this.getOptionalForType(key, keyDefault, String.class);
	}

	@Override
	public Integer getOptionalInteger(OptionalProperty<Integer> op) {
		return this.getOptionalInteger(op.getKey(), op.getDefaultValue());
	}

	private Integer getOptionalInteger(String key, Integer defaultValue) {
		return defaultValue;
	}

	public Integer getRequiredInteger(String key) throws ConfigException {
		return this.getRequiredForType(key, Integer.class);
	}

	@Override
	public String getOptionalString(OptionalProperty<String> op) {
		return this.getOptionalString(op.getKey(), op.getDefaultValue());
	}

	@Override
	public String getRequiredString(Property<String> p) throws ConfigException {
		return this.getRequiredString(p.getKey());
	}

	@Override
	public Integer getRequiredInteger(Property<Integer> p) throws ConfigException {
		return this.getRequiredInteger(p.getKey());
	}

}
