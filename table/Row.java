package table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Row {
	private List<Cell> cells;
	private List<Integer> columnWidths;

	public Row() {
		cells = new ArrayList<Cell>();
		columnWidths = new ArrayList<Integer>();
	}

	public void addCell(String content) {
		addCell(content, Alignment.LEFT);
	}

	public void addCell(String content, Alignment alignment) {
		Cell cell = new Cell(content, alignment);
		cells.add(cell);
		columnWidths.add(cell.getContent().length());
	}

	public void addCell(String content, int position, Alignment alignment) {
		Cell cell = new Cell(content, alignment);
		cells.set(position, cell);
		while (columnWidths.size() <= position){
			columnWidths.add(0);
		}
		columnWidths.set(position, cell.getContent().length());
	}
	
	public String render(Map<Integer, List<Integer>> widthsMap, int tableSize) {
		StringBuilder renderBuilder = new StringBuilder();
		int height = getRowHeight();
		List<Integer> widths = widthsMap.get(cells.size());
		for (int i = 0; i < height; ++i) {
			renderBuilder.append(Table.VERTICAL);
			Queue<Integer> pads = getNormalizationPaddings(tableSize - calculateWidth(widthsMap.get(cells.size())));
			Iterator<Integer> it = widths.iterator();
			int counter = 0;
			while (it.hasNext()) {
				Integer width = it.next();
				if (cells.size() >= counter) {
					renderBuilder.append(cells.get(counter++).getPaddedContent(width + pads.remove(), i));
					renderBuilder.append(Table.VERTICAL);
				}
			}
			renderBuilder.append("\n");
		}
		return renderBuilder.toString();
	}

	public List<Cell> getCells() {
		return cells;
	}

	public int getRowHeight() {
		int result = 0;
		Iterator<Cell> it = cells.iterator();
		while (it.hasNext()) {
			Cell cell = it.next();
			int height = cell.getCellHeight();
			if (height > result) {
				result = height;
			}
		}
		return result;
	}

	public void normalizeWidth(Map<Integer, List<Integer>> maxWidth) {
		List<Integer> list = maxWidth.get(cells.size());
		if (list == null) {
			maxWidth.put(cells.size(), columnWidths);
		} else {
			for (int i = 0; i < list.size() && i < columnWidths.size(); ++i) {
				if (list.get(i) < columnWidths.get(i)) {
					list.set(i, columnWidths.get(i));
				}
			}
		}
	}

	private int calculateWidth(List<Integer> normalizedWidths) {
		int result = 0;
		Iterator<Integer> it = normalizedWidths.iterator();
		while (it.hasNext()) {
			result += it.next();
		}
		return result;
	}

	private Queue<Integer> getNormalizationPaddings(int tableSize) {
		if (cells.size() == 0) {
			Integer[] resultForZero = new Integer[1];
			resultForZero[0] = tableSize;
			return new LinkedList<Integer>(Arrays.asList(resultForZero));
		}
		Integer[] paddings = new Integer[columnWidths.size()];
		for (int i = 0; i < paddings.length; ++i) {
			paddings[i] = 0;
		}
		for (int i = 0; i < tableSize; ++i) {
			++paddings[i % paddings.length];
		}
		return new LinkedList<Integer>(Arrays.asList(paddings));
	}

}
