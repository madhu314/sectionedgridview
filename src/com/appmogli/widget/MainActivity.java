package com.appmogli.widget;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	protected static final String TAG = "MainActivity";
	private ListView listView;
	private Dataset dataSet;
	private SectionedGridViewAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.listview);
		listView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						listView.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);

						// now check the width of the list view
						int width = listView.getWidth();

						// create data set

						dataSet = new Dataset();

						String sectionOne = "SectionOne";
						String sectionTwo = "SectionTwo";
						String sectionThree = "SectionThree";

						dataSet.addSection(sectionOne, 10);
						dataSet.addSection(sectionTwo, 5);
						dataSet.addSection(sectionThree, 18);

						LinkedHashMap<String, Cursor> cursorMap = dataSet
								.getSectionCursorMap();

						adapter = new SectionedGridViewAdapter(
								MainActivity.this, cursorMap, listView
										.getWidth(), listView.getHeight(),
								getResources().getDimensionPixelSize(
										R.dimen.grid_item_size));
						
						listView.setAdapter(adapter);
						
						listView.setDividerHeight(adapter.gapBetweenChildrenInRow());
						
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
