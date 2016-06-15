package mover;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

public class Merger {

	private static final String TIMESTAMP_BLOCK_CORNER = "+";
	private static final String TIMESTAMP_BLOCK_HORIZONTAL = "-";
	private static final String TIMESTAMP_BLOCK_VERTICAL = "|";

	private String src;
	private String dst;

	public Merger(String src, String dst) {
		this.src = src;
		this.dst = dst;
	}

	public void mergeIntoDest() throws IOException {
		mergeToFile(dst);
		new File(src).delete();
	}

	public void mergeToFile(String dest) throws IOException {
		PrintWriter out = new PrintWriter(dest);
		out.print(merge());
		out.close();
	}

	public String merge() throws IOException {
		StringBuilder result = new StringBuilder();
		Map<Date, TimeBlock> srcMap = getFileAsTimeBlock(src);
		Map<Date, TimeBlock> dstMap = getFileAsTimeBlock(dst);
		for (Date date : srcMap.keySet()) {
			if (dstMap.containsKey(date)) {
				dstMap.replace(date, srcMap.get(date));
			} else {
				dstMap.put(date, srcMap.get(date));
			}
		}
		for (Date date : dstMap.keySet()) {
			result.append(dstMap.get(date));
		}
		return result.toString();
	}

	// do this in a map<date, timeblock> instead, it will be so much easier! Put
	// it in a treemap, then add the metadata to date 0 and boom.
	private Map<Date, TimeBlock> getFileAsTimeBlock(String file) throws IOException {
		Map<Date, TimeBlock> result = new TreeMap<Date, TimeBlock>();
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
						&& line.substring(1, line.length() - 1).matches("["+TIMESTAMP_BLOCK_HORIZONTAL+"]*")) {
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
								if (nextLine.length() > 1 && nextLine.startsWith(TIMESTAMP_BLOCK_VERTICAL)
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
									if (currentBlock != null) {
										result.put(currentBlock.getDate(), currentBlock);
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
					if (line.startsWith("[__") && line.endsWith("__]")) {
						currentBlock = new TimeBlock(line + "\n");
						result.put(new Date(0), currentBlock);
						currentBlock = null;
					} else {
						currentBlockContent.append(line + "\n");
					}
				}
			}
			if (currentBlock != null) {
				currentBlock.setContent(currentBlockContent.toString());
				result.put(currentBlock.getDate(), currentBlock);
			}
		}
		return result;
	}

}
