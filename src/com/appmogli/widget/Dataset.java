package com.appmogli.widget;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.database.Cursor;
import android.database.MatrixCursor;

public class Dataset {

	private LinkedHashMap<String, Integer> sectionItems = new LinkedHashMap<String, Integer>();

	public static final String DATA_COLUMN = "data";

	public static final int TYPE_DATA = 1;

	public static final String ITEM_PREFIX = "data-";

	public static final String[] COLUMNS = new String[] { DATA_COLUMN, "_id" };
	
	private static volatile int INDEX = 1;
	
	private LinkedHashMap<String, Cursor> sectionCursors = new LinkedHashMap<String, Cursor>();

	public void addSection(String sectionName, int numberOfItems) {
		sectionItems.put(sectionName, numberOfItems);
	}

	public Cursor getSectionCursor(String sectionName) {
		MatrixCursor cursor = (MatrixCursor) sectionCursors.get(sectionName);
		if( cursor == null) {
			cursor = new MatrixCursor(COLUMNS);
			int items = sectionItems.get(sectionName);
			cursor.addRow(new Object[] {sectionName, INDEX++ });

			// now add item rows
			for (int i = 1; i < items; i++) {
				cursor.addRow(new Object[] { ITEM_PREFIX + i, INDEX++ });
			}
			
			sectionCursors.put(sectionName, cursor);

		}
		
		return cursor;
	}
	
	public LinkedHashMap<String, Cursor> getSectionCursorMap() {
		if(sectionCursors.isEmpty()) {
			 for(String sectionName : sectionItems.keySet()) {
				 getSectionCursor(sectionName);
			 }
		}
		
		
		return sectionCursors;
		
	}

}
