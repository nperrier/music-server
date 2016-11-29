package com.perrier.music.api;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

// TODO: use https, not http
public class ServerAPI {

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {

			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return mapper.readValue(value, valueType);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			public String writeValue(Object value) {
				try {
					return mapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}
		});
		Unirest.setDefaultHeader("Accept", "application/json");
	}

	private final String host;
	private final Integer port;

	private String token;

	public ServerAPI(String host, Integer port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Authenticate with server
	 */
	public String authenticate(String username, String password) throws RestException {
		try {
			AuthRequest request = new AuthRequest(username, password);
			HttpResponse<String> response = Unirest.post("http://" + this.host + ":" + this.port + "/api/authentication")
					.header("Content-Type", "application/json")
					.body(request)
					.asString();

			checkStatus("Failed to authenticate with server", response);

			AuthResponse authResponse = mapper.readValue(response.getBody(), AuthResponse.class);
			this.token = authResponse.getToken();
			return this.token;

		} catch (UnirestException | IOException e) {
			throw new RestException("Error making authentication request with server", e);
		}
	}

	/**
	 * Fetch all the tracks meta data and hashes
	 */
	public LibraryMetaData getLibrary() throws RestException {
		try {
			HttpResponse<String> response = Unirest.get("http://" + this.host + ":" + this.port + "/api/library")
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + token)
					.asString();

			checkStatus("Unable to fetch library meta data", response);

			LibraryMetaData libraryMetaData = mapper.readValue(response.getBody(), LibraryMetaData.class);
			return libraryMetaData;

		} catch (UnirestException | IOException e) {
			throw new RestException("Unable to fetch library meta data", e);
		}
	}

	// Adds a new Track
	public TrackMetaData postTrack(TrackMetaData trackMetaData) throws RestException {
		try {
			HttpResponse<String> response = Unirest.post("http://" + this.host + ":" + this.port + "/api/track")
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + token)
					.body(trackMetaData)
					.asString();

			checkStatus("Unable to post track meta data", response);

			TrackMetaData uploadResponse = mapper.readValue(response.getBody(), TrackMetaData.class);
			return uploadResponse;

		} catch (UnirestException | IOException e) {
			throw new RestException("Unable to post track meta data", e);
		}
	}

	private void checkStatus(String message, HttpResponse<String> response) throws RestException, IOException {
		if (response.getStatus() < 200 || response.getStatus() >= 300) {
			String body = response.getBody();
			ErrorInfo errorInfo = mapper.readValue(body, ErrorInfo.class);
			throw new RestException(message + ", errorInfo=" + errorInfo);
		}
	}
}
