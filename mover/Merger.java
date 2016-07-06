package mover;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import adt.BugNote;
import adt.TimeBlock;
import parser.TimeBlockParser;

public class Merger {



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
		BugNote source =  new TimeBlockParser(src).getFileAsBugNote();
		Map<Date, TimeBlock> srcMap = source.getTimeBlockMap();
		Map<Date, TimeBlock> dstMap = new TimeBlockParser(dst).getFileAsBugNote().getTimeBlockMap();
		
		for (Date date : srcMap.keySet()) {
			if (dstMap.containsKey(date)) {
				dstMap.replace(date, srcMap.get(date));
			} else {
				dstMap.put(date, srcMap.get(date));
			}
		}
		result.append(source.getMetaDataString() + "\n");
		for (Date date : dstMap.keySet()) {
			result.append(dstMap.get(date));
		}
		return result.toString();
	}

	

}
