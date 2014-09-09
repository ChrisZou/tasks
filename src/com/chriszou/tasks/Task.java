/**
 * Task.java
 * 
 * Created by zouyong on Sep 9, 2014,2014
 */
package com.chriszou.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chriszou.androidlibs.L;
import com.chriszou.androidlibs.UrlContentLoader;

/**
 * @author zouyong
 *
 */
public class Task {
	public static final String SERVER_URL = "http://112.124.121.155/tasks.json";
	public String id;
	public String title;
	public String note;

	public static List<Task> all() throws IOException, JSONException {
		L.l("getting task");
		UrlContentLoader loader = new UrlContentLoader(SERVER_URL);
		String tasksString = loader.executeSync();
		L.l("tasks: "+tasksString);
		List<Task>tasks = jsonArrayToList(tasksString);
		return tasks;
	}

	public void save() {

	}

	public void destroy() {

	}

	private static List<Task> jsonArrayToList(String jsonArray) throws JSONException {
		List<Task>tasks = new ArrayList<Task>();
		JSONArray array = new JSONArray(jsonArray);
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.optJSONObject(i);
			Task task = new Task();
			task.id = json.optString("id");
			task.title = json.optString("title");
			task.note = json.optString("note");
			tasks.add(task);
		}

		return tasks;
	}
}
