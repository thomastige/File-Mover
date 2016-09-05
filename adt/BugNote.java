package adt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import constants.Constants;

public class BugNote {

	Map<String, String> metadata;
	Map<Date, TimeBlock> timeBlockMap;
	String metaDataString;

	public BugNote() {
		metadata = new HashMap<String, String>();
		timeBlockMap = new TreeMap<Date, TimeBlock>();
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public Map<Date, TimeBlock> getTimeBlockMap() {
		return timeBlockMap;
	}

	public void setTimeBlockMap(Map<Date, TimeBlock> timeBlockMap) {
		this.timeBlockMap = timeBlockMap;
	}

	public void addEntry(TimeBlock block) {
		timeBlockMap.put(block.getDate(), block);
	}

	public void addMetadataEntry(String key, String value) {
		metadata.put(key, value);
	}

	public void setMetaData(String metadata) {
		metaDataString = metadata;
		metadata = metadata.replace(Constants.METADATA_START, "").replace(Constants.METADATA_END, "");
		String[] splitMetadata = metadata.split(Constants.METADATA_SEPARATOR);
		for (int i = 0; i < splitMetadata.length; ++i) {
			if (splitMetadata[i].contains(":")) {
				String[] nameValue = splitMetadata[i].split(Constants.METADATA_NAME_SEPARATOR);
				this.metadata.put(nameValue[0], mergeValue(nameValue));
			} else {
				if (!"".equals(splitMetadata[i].trim())) {
					this.metadata.put("" + i, splitMetadata[i]);
				}
			}
		}
	}

	private String mergeValue(String[] splitArray) {
		StringBuilder result = new StringBuilder();
		if (splitArray.length > 1) {
			for (int i = 1; i < splitArray.length; ++i) {
				if (i > 1) {
					result.append(Constants.METADATA_NAME_SEPARATOR);
				}
				result.append(splitArray[i]);
			}
		}
		return result.toString();
	}

	public String getMetaDataString() {
		return metaDataString;
	}

	public void setMetaDataString(String metaDataString) {
		this.metaDataString = metaDataString;
	}

}
