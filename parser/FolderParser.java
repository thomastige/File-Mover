package parser;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import adt.BugEntry;
import adt.BugNote;
import adt.TimeBlock;
import constants.Constants;

public class FolderParser {

	private File fileToParse;
	private String commentPosition;
	private String defaultComment;

	private String role;

	private Date fromDate;
	private Date toDate;

	List<BugEntry> bugs;

	public FolderParser() {
		this(System.getProperty("system.dir"));
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setFromDate(String fromDate) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date date;
		try {
			date = df.parse(fromDate);
			this.fromDate = date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void setToDate(String toDate) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date date;
		try {
			date = df.parse(toDate);
			this.toDate = date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public FolderParser(String dir) {
		fileToParse = new File(dir);
		bugs = new ArrayList<BugEntry>();
		fromDate = new Date(1);
		toDate = new Date();
	}

	public List<BugEntry> parseToList() throws IOException {
		extractBugs();
		try {
			sortList();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bugs;
	}

	private void extractBugs() throws IOException {
		extractBugs(fileToParse);
	}

	private void extractBugs(File dir) throws IOException {
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; ++i) {
			if (files[i].isDirectory()) {
				extractBugs(files[i]);
			} else {
				if (!isException(files[i].getName())) {
					extractBugsFromFile(files[i]);
				}
			}
		}
	}

	private void extractBugsFromFile(File file) throws IOException {
		// TODO: replace the timeblockmap with a Bug class
		BugNote bugNote = new TimeBlockParser(file.getPath()).getFileAsBugNote();
		Iterator<Date> it = bugNote.getTimeBlockMap().keySet().iterator();
		Map<String, String> metadata = bugNote.getMetadata();
		while (it.hasNext()) {
			Date date = it.next();
			if (!date.after(toDate) && !date.before(fromDate)) {
				Map<String, String> overrides = getOverrides(bugNote.getTimeBlockMap().get(date));
				BugEntry entry = new BugEntry();
				entry.setFileName(file.getName());
				entry.setDate(date);
				entry.setBugNumber(file.getName().split(" - ")[0]);
				entry.setBilled(Constants.DEFAULT_BILLED);
				entry.setSprint(file.getParentFile().getName());
				if (overrides.get(Constants.OVERRIDE_WORKED) != null) {
					entry.setWorked(overrides.get(Constants.OVERRIDE_WORKED));
					entry.setOverride(true);
				}
				entry.setMetadata(metadata);
				if (overrides.get(Constants.OVERRIDE_COMMENT) != null) {
					entry.setDescription(overrides.get(Constants.OVERRIDE_COMMENT));
				} else {
					entry.setDescription(getDefaultComment(metadata));
				}
				entry.setRole(role);
				bugs.add(entry);
			}
		}
	}

	private String getDefaultComment(Map<String, String> metadata) {
		String result = null;
		if (metadata != null) {
			result = metadata.get(commentPosition);
		}
		if (result == null) {
			result = defaultComment;
		}
		return result;
	}

	private Map<String, String> getOverrides(TimeBlock timeBlock) {
		Map<String, String> result = new HashMap<String, String>();
		String timeStampBlock = timeBlock.getTimeStampBlock();
		if (timeStampBlock != null) {
			String[] lines = timeStampBlock.split("\n");
			for (int i = 0; i < lines.length; ++i) {
				String line = lines[i].substring(1, lines[i].length() - 2);
				String[] splitLine = line.split(":");
				if (splitLine.length == 2) {
					result.put(splitLine[0].trim().toUpperCase(), splitLine[1].trim());
				}
			}
		}
		return result;
	}

	private boolean isException(String fileName) {
		return Constants.CALENDAR_LOG.equals(fileName) || Constants.METRICS_LOG.equals(fileName) || Constants.HOURS_LOG.equals(fileName) || Constants.JSON_TEMPO.equals(fileName);
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	// TODO: write an actual comparator
	public void sortList() throws ParseException {
		List<BugEntry> result = new ArrayList<BugEntry>();
		Map<Date, List<BugEntry>> sortingMap = new TreeMap<Date, List<BugEntry>>();
		Iterator<BugEntry> it = bugs.iterator();
		while (it.hasNext()) {
			BugEntry entry = it.next();
			Date date = entry.getDate();
			if (sortingMap.get(date) == null) {
				sortingMap.put(date, new ArrayList<BugEntry>());
			}
			sortingMap.get(date).add(entry);
		}
		Iterator<Date> it2 = sortingMap.keySet().iterator();
		while (it2.hasNext()) {
			result.addAll(sortingMap.get(it2.next()));
		}
		bugs = result;
	}

	public String getCommentPosition() {
		return commentPosition;
	}

	public void setCommentPosition(String commentPosition) {
		this.commentPosition = commentPosition;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDefaultComment() {
		return defaultComment;
	}

	public void setDefaultComment(String defaultComment) {
		this.defaultComment = defaultComment;
	}
}
