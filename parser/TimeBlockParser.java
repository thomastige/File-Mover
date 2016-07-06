package parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import adt.BugNote;
import adt.TimeBlock;
import constants.Constants;

public class TimeBlockParser {

	private static final String TIMESTAMP_BLOCK_CORNER = "+";
	private static final String TIMESTAMP_BLOCK_HORIZONTAL = "-";
	private static final String TIMESTAMP_BLOCK_VERTICAL = "|";

	String file;

	public TimeBlockParser(String file) {
		this.file = file;
	}

	public BugNote getFileAsBugNote() throws IOException {
		BugNote result = new BugNote();
		if (new File(file).exists()) {
			List<String> lines = Files.readAllLines(Paths.get(file));
			Iterator<String> it = lines.iterator();

			StringBuilder currentBlockContent = new StringBuilder();
			TimeBlock currentBlock = null;
			while (it.hasNext()) {

				String line = it.next();
				// check if timestamp block header
				if (line.length() > 1 && line.startsWith(TIMESTAMP_BLOCK_CORNER)
						&& line.endsWith(TIMESTAMP_BLOCK_CORNER)
						&& line.substring(1, line.length() - 1).matches("[" + TIMESTAMP_BLOCK_HORIZONTAL + "]*")) {
					StringBuilder potentialTimeBlock = new StringBuilder();
					potentialTimeBlock.append(line + "\n");
					if (it.hasNext()) {
						String secondLine = it.next();
						// check if timestamp date
						if (secondLine.length() > 1 && secondLine.startsWith(TIMESTAMP_BLOCK_VERTICAL)
								&& secondLine.endsWith(TIMESTAMP_BLOCK_VERTICAL)) {
							String trimmedLine = secondLine.substring(1, secondLine.length() - 1).trim();
							DateFormat df = new SimpleDateFormat("dd MMM yyyy");
							Date date = null;
							try {
								date = df.parse(trimmedLine);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (date != null) {
								potentialTimeBlock.append(secondLine + "\n");
								String nextLine = it.next();
								// check if time override
								while (nextLine.length() > 1 && nextLine.startsWith(TIMESTAMP_BLOCK_VERTICAL)
										&& secondLine.endsWith(TIMESTAMP_BLOCK_VERTICAL)) {
									potentialTimeBlock.append(nextLine + "\n");
									nextLine = it.next();
								}
								// Check if end of timestamp
								if (nextLine.equals(line)) {
									potentialTimeBlock.append(line + "\n");
									// close current block (generate new time
									// block and append current to result, then
									// append timeblock to current and restart
									// loop.
									if (currentBlock != null) {
										currentBlock.setContent(currentBlockContent.toString());
									} else {
										if (!"".equals(currentBlockContent.toString())) {
											currentBlock = new TimeBlock(currentBlockContent.toString());
										}
									}
									currentBlockContent = new StringBuilder();
									if (currentBlock != null && currentBlock.getDate() != null) {
										result.addEntry(currentBlock);
									}
									currentBlock = new TimeBlock(potentialTimeBlock.toString(), date);

								} else {
									currentBlockContent.append(potentialTimeBlock.toString() + "\n");
								}

							} else {
								currentBlockContent.append(potentialTimeBlock.toString() + "\n");
							}
						} else {
							currentBlockContent.append(potentialTimeBlock.toString() + "\n");
						}
					} else {
						currentBlockContent.append(line + "\n");
					}
				} else {
					if (line.startsWith(Constants.METADATA_START) && line.endsWith(Constants.METADATA_END)) {
						result.setMetaData(line);
						currentBlock = null;
					} else {
						currentBlockContent.append(line + "\n");
					}
				}
			}
			if (currentBlock != null) {
				currentBlock.setContent(currentBlockContent.toString());
				result.addEntry(currentBlock);
			}
		}
		return result;
	}

}
