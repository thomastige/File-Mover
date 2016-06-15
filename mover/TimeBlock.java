package mover;

import java.util.Date;

public class TimeBlock {

	private String timeStampBlock;
	private String content;

	private Date date;
	private String override;

	public TimeBlock(String content) {
		this.content = content;
	}

	public TimeBlock(String timeStampBlock, Date date) {
		this.timeStampBlock = timeStampBlock;
		this.date = date;
	}

	public String getTimeStampBlock() {
		return timeStampBlock;
	}

	public void setTimeStampBlock(String timeStampBlock) {
		this.timeStampBlock = timeStampBlock;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		if (date == null) {
			date = new Date(1);
		}
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getOverride() {
		return override;
	}

	public void setOverride(String override) {
		this.override = override;
	}

	public String toString() {
		return (timeStampBlock == null ? "" : timeStampBlock) + content;
	}

}
