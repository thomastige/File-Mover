package table;

public class Cell {

	private String content = "";
	private Alignment alignment;

	public String getPaddedContent(int size, int lineNumber) {
		StringBuilder contentBuilder = new StringBuilder();
		String[] lines = content.split("\n");
		if (lineNumber < lines.length) {
			String line = lines[lineNumber];
			for (int i = 0; i < (size - line.length()); ++i) {
				contentBuilder.append(" ");
			}
			if (Alignment.LEFT == alignment) {
				contentBuilder.insert(0, line);
			} else if (Alignment.CENTER == alignment) {
				contentBuilder.insert((contentBuilder.length() / 2), line);
			} else if (Alignment.RIGHT == alignment) {
				contentBuilder.append(line);
			}
		} else {
			for (int i = 0; i < size; ++i) {
				contentBuilder.append(" ");
			}
		}
		return contentBuilder.toString();

	}

	public Cell(String content) {
		this(content, Alignment.LEFT);
	}

	public Cell(String content, Alignment alignment) {
		this.content = content;
		this.alignment = alignment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCellHeight() {
		return content.split("\n").length;
	}

}
