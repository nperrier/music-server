package com.perrier.music.api;

public class ErrorInfo {

	private int status;
	private String message;

	public ErrorInfo() {
	}

	public ErrorInfo(String message, int status) {
		this.message = message;
		this.status = status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public int getStatus() {
		return this.status;
	}
}
