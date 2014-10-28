/**
 * Task.java
 *
 * Created by zouyong on Sep 9, 2014,2014
 */
package com.chriszou.tasks;

import com.chriszou.androidlibs.HttpUtils;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		String tasksString = HttpUtils.getContent(SERVER_URL);
		List<Task>tasks = jsonArrayToList(tasksString);
		return tasks;
	}

	public Task() {
		id = "";
		title = "";
		note = "";
	}

	public Task(String title) {
		this.title = title;
		id = "";
		note = "";
	}

	/**
	 * Save the Task and return the saved task instance;
	 *
	 * @return
	 */
	public Task save() {
		String json = new Gson().toJson(this);
		try {
			HttpResponse response = HttpUtils.postJson(SERVER_URL, json);
			if (response.getStatusLine().getStatusCode() == 201) {
				String content = EntityUtils.toString(response.getEntity(), "UTF-8");
				Task t = new Gson().fromJson(content, Task.class);
				return t;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
