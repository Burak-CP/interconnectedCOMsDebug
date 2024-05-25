package com.debug.db.record;

public class recordTable {

	private String Source;
	private String Destination;
	private String Message;
	private String Time;

	public recordTable(String source, String destination, String message, String time) {
		Time = time;
		Destination = destination;
		Message = message;
		Source = source;
	}

	public String getSource() {
		return Source;
	}

	public String getDestination() {
		return Destination;
	}

	public String getMessage() {
		return Message;
	}

	public String getTime() {
		return Time;
	}
}
