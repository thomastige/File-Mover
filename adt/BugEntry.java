package adt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class BugEntry {

	private String bugNumber;
	private Date date;
	private String worked;
	private String billed;
	private String description;
	private String role;
	private boolean override = false;
	
	private String fileName;
	private String sprint;
	private Map<String, String> metadata;
	
	public String getBugNumber() {
		return bugNumber;
	}

	public void setBugNumber(String bugNumber) {
		this.bugNumber = bugNumber;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getWorked() {
		return worked;
	}

	public void setWorked(String worked) {
		this.worked = worked;
	}

	public String getBilled() {
		return billed;
	}

	public void setBilled(String billed) {
		this.billed = billed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean getOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSprint() {
		return sprint;
	}
	
	public void setSprint(String sprint) {
		this.sprint = sprint;
	}
	
	public Map<String, String> getMetadata() {
		return metadata;
	}
	
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public String toString() {
		DateFormat df = new SimpleDateFormat("dd/MMM/yy");
		return "{\"bugNumber\" : \"" + bugNumber + "\", \"date\":\"" + df.format(date) + "\", \"worked\" : \"" + worked
				+ "h\", \"billed\":\"" + billed + "h\", \"description\" : \"" + description + "\", \"Role\" : \"" + role
				+ "\", \"Override\" : \"" + override + "\"}";
	}
}
