package com.appmogli.widget;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SectionedGridViewAdapter extends BaseAdapter {

	private static final String TAG = "SectionedGridViewAdapter";
	private SparseBooleanArray idAnimations = new SparseBooleanArray();
	private int listItemRowWidth = -1;
	private int gridItemSize = -1;
	private int listViewHeight = -1;

	private int numberOfChildrenInRow = -1;

	private int[] childrenSpacing = null;

	private int childSpacing = -1;

	private LinkedHashMap<String, Cursor> sectionCursors = null;

	private LinkedHashMap<String, Integer> sectionRowsCount = new LinkedHashMap<String, Integer>();

	private Context mContext = null;

	public static final int VIEW_TYPE_HEADER = 0;

	public static final int VIEW_TYPE_ROW = 1;

	public static final int MIN_SPACING = 10;

	public SectionedGridViewAdapter(Context context,
			LinkedHashMap<String, Cursor> sectionCursors, int listItemRowSize,
			int listViewHeight, int gridItemSquareSize) {

		this.sectionCursors = sectionCursors;

		this.listItemRowWidth = listItemRowSize;
		this.gridItemSize = gridItemSquareSize;
		this.listViewHeight = listViewHeight;

		// griditem size is always less that list item size

		if (gridItemSize > this.listItemRowWidth) {
			throw new IllegalArgumentException(
					"Griditem size cannot be greater that list item row size");
		}
		// calculate items number of items that can fit into row size

		numberOfChildrenInRow = listItemRowWidth / gridItemSize;

		int reminder = listItemRowWidth % gridItemSize;

		if (reminder == 0) {
			numberOfChildrenInRow = numberOfChildrenInRow - 1;
			reminder = gridItemSize;
		}

		int numberOfGaps = 0;
		int toReduce = 0;
		while (childSpacing < MIN_SPACING) {
			numberOfChildrenInRow = numberOfChildrenInRow - toReduce;
			reminder += toReduce * gridItemSize;
			numberOfGaps = numberOfChildrenInRow - 1;
			childSpacing = reminder / numberOfGaps;
			toReduce++;
		}

		int spacingReminder = reminder % numberOfGaps;

		// distribute spacing gap equally first
		childrenSpacing = new int[numberOfGaps];

		for (int i = 0; i < numberOfGaps; i++) {
			childrenSpacing[i] = childSpacing;
		}

		// extra reminder distribute from beginning
		for (int i = 0; i < spacingReminder; i++) {
			childrenSpacing[i]++;
		}

		this.mContext = context;
		Log.d(TAG, "Computed number of children in row");

	}

	// @Override
	// public void bindView(View view, Context context, Cursor cursor) {
	// int type = cursor.getInt(0);
	// String data = cursor.getString(1);
	// ViewHolder holder = (ViewHolder) view.getTag();
	// holder.dataView.setVisibility(View.GONE);
	// holder.sectionHeader.setVisibility(View.GONE);
	//
	// if (type == Dataset.TYPE_SECTION) {
	// holder.sectionHeader.setVisibility(View.VISIBLE);
	// holder.sectionHeader.setText(data);
	// } else {
	// holder.dataView.setVisibility(View.VISIBLE);
	// }
	// holder.position = cursor.getPosition();
	//
	// // now do new view item animations
	//
	// boolean animated = idAnimations.get(holder.position);
	// if (!animated) {
	// Animation existingAnim = view.getAnimation();
	// idAnimations.put(holder.position, true);
	//
	// if (existingAnim != null && existingAnim.hasStarted()
	// && !existingAnim.hasEnded()) {
	// // already animating leave it
	// existingAnim.cancel();
	// } else {
	// Animation a = AnimationUtils.loadAnimation(context,
	// R.anim.slide_up_left);
	// view.startAnimation(a);
	// }
	//
	// }
	//
	// }
	//
	// @Override
	// public View newView(Context context, Cursor cursor, ViewGroup parent) {
	// LayoutInflater inflater = (LayoutInflater) context
	// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// View v = null;
	// v = inflater.inflate(R.layout.list_item, null);
	// ViewHolder holder = new ViewHolder();
	// holder.dataView = (LinearLayout) v.findViewById(R.id.data_item);
	// holder.sectionHeader = (TextView) v.findViewById(R.id.section_item);
	// holder.position = cursor.getPosition();
	// v.setTag(holder);
	// return v;
	// }

	@Override
	public int getCount() {

		sectionRowsCount.clear();

		// count is cursors count + sections count
		int sections = sectionCursors.size();

		int count = sections;
		// count items in each section
		for (String sectionName : sectionCursors.keySet()) {
			int sectionCount = sectionCursors.get(sectionName).getCount();
			int numberOfRows = sectionCount / numberOfChildrenInRow;
			if (sectionCount % numberOfChildrenInRow != 0) {
				numberOfRows++;
			}

			sectionRowsCount.put(sectionName, numberOfRows);
			count += numberOfRows;
		}

		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = null;
		boolean isSectionheader = isSectionHeader(position);
		LinearLayout rowPanel = null;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (isSectionheader) {
				v = inflater.inflate(R.layout.section_header, null);
			} else {
				LinearLayout ll = (LinearLayout) inflater.inflate(
						R.layout.list_row, null);
				// add childrenCount to this
				for (int i = 0; i < numberOfChildrenInRow; i++) {
					// add a child
					View child = inflater.inflate(R.layout.data_item, null);
					ll.addView(child, new LinearLayout.LayoutParams(
							gridItemSize, gridItemSize));

					if (i < numberOfChildrenInRow - 1) {
						// now add space view
						View spaceItem = new View(mContext);
						ll.addView(spaceItem, new LinearLayout.LayoutParams(
								childrenSpacing[i], ll.getHeight()));
					}
				}
				v = ll;
				rowPanel = ll;
			}

		} else {
			v = convertView;
		}

		String sectionName = whichSection(position);

		if (isSectionheader) {
			TextView tv = (TextView) v;
			tv.setText(sectionName);
		} else {
			rowPanel = (LinearLayout) v;
			//set all children visible first
			for (int i = 0; i < 2*numberOfChildrenInRow - 1 ; i++) {
				// we need to hide grid item and gap
				View child = rowPanel.getChildAt(i);
				child.setVisibility(View.VISIBLE);
			}
			
			// check if this position corresponds to last row
			boolean isLastRowInSection = isLastRowInSection(position);

			if (isLastRowInSection) {
				// check how many items needs to be hidden in last row
				int sectionCount = sectionCursors.get(sectionName).getCount();

				int childrenInLastRow = sectionCount % numberOfChildrenInRow;
				
				if (childrenInLastRow > 0) {
					int gaps = childrenInLastRow - 1;

					for (int i = childrenInLastRow + gaps; i < rowPanel
							.getChildCount(); i++) {
						// we need to hide grid item and gap
						View child = rowPanel.getChildAt(i);
						child.setVisibility(View.INVISIBLE);
					}

				}
			}

		}

//		boolean animated = idAnimations.get(position);
//		if (!animated) {
//			Animation existingAnim = v.getAnimation();
//			idAnimations.put(position, true);
//
//			if (existingAnim != null && existingAnim.hasStarted()
//					&& !existingAnim.hasEnded()) {
//				// already animating leave it
//				existingAnim.cancel();
//			} else {
//				Animation a = AnimationUtils.loadAnimation(mContext,
//						android.R.anim.fade_in);
//				v.startAnimation(a);
//			}
//		}
		return v;
	}

	private boolean isLastRowInSection(int position) {

		for (String key : sectionCursors.keySet()) {
			int size = sectionRowsCount.get(key) + 1;

			if (position == size - 1)
				return true;

			position -= size;
		}

		return false;
	}

	private boolean isSectionHeader(int position) {

		for (String key : sectionCursors.keySet()) {
			int size = sectionRowsCount.get(key) + 1;

			if (position == 0)
				return true;

			position -= size;
		}

		return false;

	}

	private String whichSection(int position) {

		for (String key : sectionCursors.keySet()) {
			int size = sectionRowsCount.get(key) + 1;

			if (position < size) {
				return key;
			}

			position -= size;
		}

		return null;

	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (isSectionHeader(position)) {
			return VIEW_TYPE_HEADER;
		}

		return VIEW_TYPE_ROW;
	}

	@Override
	public boolean isEnabled(int position) {
		if (isSectionHeader(position)) {
			return false;
		}

		return true;

	}

	public int gapBetweenChildrenInRow() {
		return childSpacing;
	}

}
