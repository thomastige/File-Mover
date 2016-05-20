package table;

public class Cell {

	private String content = "";
	private Alignment alignment;

	public String getPaddedContent(int size) {
		StringBuilder contentBuilder = new StringBuilder();

		for (int i = 0; i < (size - content.length()); ++i) {
			contentBuilder.append(" ");
		}
		if (Alignment.LEFT == alignment) {
			contentBuilder.insert(0, content);
		} else if (Alignment.CENTER == alignment) {
			contentBuilder.insert((contentBuilder.length() / 2), content);
		} else if (Alignment.RIGHT == alignment) {
			contentBuilder.append(content);
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

}
