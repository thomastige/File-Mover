package jsonbuilder;

public class JSONBug {

	
	private String bugNumber;
	private String date;
	private String worked;
	private String billed;
	private String description;
	private String role;
	public String getBugNumber() {
		return bugNumber;
	}
	public void setBugNumber(String bugNumber) {
		this.bugNumber = bugNumber;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
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


	public String toString(){
		return "{\"bugNumber\" : \"" + bugNumber + "\", \"date\":\"" + date + "\", \"worked\" : \""+ worked +"h\", \"billed\":\"" + billed + "h\", \"description\" : \"" + description + "\", \"Role\" : \"" + role + "\"}";
	}
}
