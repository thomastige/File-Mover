package table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Table {

	public static final String HORIZONTAL = "-";
	public static final String VERTICAL = "|";
	public static final String CORNER = "+";
	public static final String BLANK = " ";

	private Columns columns;
	private List<Row> rows;
	private Row currentRow;
	private Row headers;
	Map<Integer, List<Integer>> normalizedWidths;

	public Table(Columns columns) {
		this.columns = columns;
		rows = new ArrayList<Row>();
		currentRow = new Row();
		normalizedWidths = new HashMap<Integer, List<Integer>>();
	}

	public void addHeader(String content) {
		if (headers == null) {
			headers = new Row();
		}
		headers.addCell(content.toUpperCase(), Alignment.CENTER);
	}

	public Table() {
		this(Columns.FREE);
	}

	public void newRow() {
		if (currentRow.getCells().size() > 0) {
			rows.add(currentRow);
			currentRow = new Row();
		}
	}

	public void addCell(String content) {
		addCell(content, Alignment.LEFT);
	}

	public void addCell(String content, Alignment alignment) {
		currentRow.addCell(content, alignment);
	}
	
	public void addCell(String content, String column, Alignment alignment){
		int position = -1;
		if (headers == null){
			headers = new Row();
		}
		
		for (int i=0; i<headers.getCells().size(); ++i){
			String header = headers.getCells().get(i).getContent();
			if (header != null && header.equals(column.toUpperCase())){
				position = i;
				break;
			}
		}
		if (position == -1){
			addHeader(column);
			position = headers.getCells().size() -1;
		}
		while (currentRow.getCells().size() <= position){
			currentRow.addCell("");
		}
		currentRow.addCell(content, position, alignment);
	}

	public void closeTable() {
		if (currentRow.getCells().size() > 0) {
			rows.add(currentRow);
		}
	}

	public String render() {
		boolean hasHeaders = (headers != null);
		if (hasHeaders) {
			rows.add(0, headers);
		}
		normalizeCells();
		StringBuilder renderBuilder = new StringBuilder();
		Iterator<Row> it = rows.iterator();
		int biggestRow = normalizeRowWidth();
		int tableWidth = calculateTableWidth();
		String buffer = null;

		while (it.hasNext()) {
			Row row = it.next();
			String rowRepresentation = row.render(normalizedWidths, tableWidth + biggestRow - row.getCells().size());
			if (buffer == null) {
				buffer = rowRepresentation;
			}
			renderBuilder.append(createDelimiter(buffer, rowRepresentation, tableWidth + biggestRow));
			renderBuilder.append(rowRepresentation);
			buffer = rowRepresentation;
		}
		renderBuilder.append(createDelimiter(buffer, buffer, tableWidth + biggestRow));
		if (hasHeaders) {
			rows.remove(0);
		}
		return renderBuilder.toString();
	}

	private String createDelimiter(String previous, String row, int size) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i <= size; ++i) {
			if (VERTICAL.equals(previous.charAt(i) + "") || VERTICAL.equals(row.charAt(i) + "")) {
				result.append(CORNER);
			} else {
				result.append(HORIZONTAL);
			}
		}
		result.append("\n");
		return result.toString();
	}

	// return highest key (highest amount of cells in a row)
	private int normalizeRowWidth() {
		int result = 0;
		Iterator<Row> it = rows.iterator();

		while (it.hasNext()) {
			Row row = it.next();
			row.normalizeWidth(normalizedWidths);
			if (!it.hasNext()) {
				result = row.getCells().size();
			}
		}
		return result;
	}

	private int calculateTableWidth() {
		Iterator<Integer> keyIt = normalizedWidths.keySet().iterator();
		int result = 0;
		while (keyIt.hasNext()) {
			Integer key = keyIt.next();
			List<Integer> widths = normalizedWidths.get(key);
			Iterator<Integer> it = widths.iterator();
			int currWidth = key;
			while (it.hasNext()) {
				currWidth += it.next();
			}
			if (currWidth > result) {
				result = currWidth;
			}
		}
		return result;
	}

	private void normalizeCells() {
		if (columns == Columns.FIXED) {
			Iterator<Row> it = rows.iterator();
			int cellLength = 0;
			while (it.hasNext()) {
				Row row = it.next();
				int numberOfCells = row.getCells().size();
				if (numberOfCells > cellLength) {
					cellLength = numberOfCells;
				}
			}
			it = rows.iterator();
			while (it.hasNext()) {
				Row row = it.next();
				while (row.getCells().size() < cellLength) {
					row.addCell("");
				}
			}
		}
	}
}
