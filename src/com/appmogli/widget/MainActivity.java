package com.appmogli.widget;

import java.util.LinkedHashMap;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListView;
import android.widget.Toast;

import com.appmogli.widget.SectionedGridViewAdapter.OnGridItemClickListener;

public class MainActivity extends Activity implements OnGridItemClickListener {

	protected static final String TAG = "MainActivity";
	private ListView listView;
	private Dataset dataSet;
	private SectionedGridViewAdapter adapter = null;
	private LinkedHashMap<String, Cursor> cursorMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// create data set

		dataSet = new Dataset();

		String sectionOne = "SectionOne";
		String sectionTwo = "SectionTwo";
		String sectionThree = "SectionThree";

		dataSet.addSection(sectionOne, 5);
		dataSet.addSection(sectionTwo, 7);
		dataSet.addSection(sectionThree, 18);

		cursorMap = dataSet.getSectionCursorMap();

		listView = (ListView) findViewById(R.id.listview);
		listView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						listView.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);

						// now check the width of the list view
						int width = listView.getWidth();

						adapter = new SectionedGridViewAdapter(
								MainActivity.this, cursorMap, listView
										.getWidth(), listView.getHeight(),
								getResources().getDimensionPixelSize(
										R.dimen.grid_item_size));
						
						adapter.setListener(MainActivity.this);
						listView.setAdapter(adapter);

						listView.setDividerHeight(adapter
								.gapBetweenChildrenInRow());

					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onGridItemClicked(String sectionName, int position, View v) {
		Cursor sectionCursor = cursorMap.get(sectionName);
		if(sectionCursor.moveToPosition(position)) {
			String data = sectionCursor.getString(0);
			String msg = "Item clicked is:" + data;
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			Log.d(TAG, msg);
		}
	}

}
