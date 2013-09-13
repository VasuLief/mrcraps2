package de.thm.mrcraps.views;

import java.util.ArrayList;

import de.thm.mrcraps.R;
import de.thm.mrcraps.controllers.DatabaseHandler;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HighscoreActivity extends Activity {

	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);
		updateList();
	}

	protected void onResume() {
		super.onResume();
		updateList();
		//test
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_highscore, menu);
		return true;
	}

	private void updateList() {
		boolean isEmpty = false;
		lv = (ListView) findViewById(R.id.highscoreList);
		ArrayList<String> scores = new DatabaseHandler(this).getAllData();
		if (scores.size() == 0) {
			scores = new ArrayList<String>();
			scores.add("Aktuell sind keine Einträge vorhanden!");
			isEmpty = true;
		}
		int listSize = scores.size();
		String sumLine = "";

		if (!isEmpty) {
			switch (listSize) {
			case 1:
				sumLine = "Bisher wurde " + listSize + " Singleplayer-Spiel gespielt.";
				break;
			default:
				sumLine = "Bisher wurden " + listSize + " Singleplayer-Spiele gespielt.";
				break;
			}
			if (sumLine != null) {
				scores.add(sumLine);
			}
		}
		
		ArrayList<String> twenty = new ArrayList<String>();
		if(scores.size() > 21)
		{
			for(int i = 0; i < 21; i++)
			{
				twenty.add(scores.get(i));
			}
			twenty.add(scores.get(scores.size()-1));
		}
		else
		{
			twenty = scores;
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, twenty);
		lv.setAdapter(arrayAdapter);
	
		}

}
