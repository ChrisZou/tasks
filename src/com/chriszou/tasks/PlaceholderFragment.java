/**
 * PlaceholderFragment.java
 * 
 * Created by zouyong on Sep 9, 2014,2014
 */
package com.chriszou.tasks;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.chriszou.androidlibs.BaseViewBinderAdapter;
import com.chriszou.androidlibs.BaseViewBinderAdapter.ViewBinder;
import com.chriszou.androidlibs.L;

/**
 * @author zouyong
 *
 */
@EFragment(R.layout.fragment_main)
public class PlaceholderFragment extends Fragment {

	@ViewById(R.id.main_tags_list)
	ListView mTagList;

	@ViewById(R.id.main_tasks_list)
	ListView mTaskList;

	List<Task> mTasks;

	BaseViewBinderAdapter<String> mTagAdapter;

	ViewBinder<String> mTagViewBinder = new ViewBinder<String>() {
		@Override
		public void bindView(int position, View view, String item, ViewGroup parent) {
			TextView tView = (TextView)view;
			tView.setText(item);
		}
	};

	ViewBinder<Task> mTaskViewBinder = new ViewBinder<Task>() {
		@Override
		public void bindView(int position, View view, Task item, ViewGroup parent) {
			TextView tView = (TextView)view;
			tView.setText(item.title);
		}
	};

	public PlaceholderFragment() {
	}

	@AfterViews
	void loadData() {
		getTasks();
	}

	@Background
	void getTasks() {
		try {
			mTasks= Task.all();
			updateTaskList(mTasks);
			updateTagList(mTasks);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@UiThread
	void updateTaskList(List<Task> tasks) {
		BaseViewBinderAdapter<Task> taskAdapter = new BaseViewBinderAdapter<Task>(getActivity(), tasks, mTaskViewBinder);
		mTaskList.setAdapter(taskAdapter);
	}

	@UiThread
	void updateTagList(List<Task>tasks) {
		List<String> tags = new ArrayList<String>();
		for(Task task: tasks) {
			List<String> subTags = getTagsFromString(task.title);
			for(String tag: subTags) {
				if(!tags.contains(tag)) {
					tags.add(tag);
				}
			}
		}


		mTagAdapter = new BaseViewBinderAdapter<String>(getActivity(), tags, mTagViewBinder);
		mTagList.setAdapter(mTagAdapter);
		mTagList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String tag = mTagAdapter.getItem(position);
				filterTasks(tag);
			}
		});
	}

	/**
	 * @param tag
	 */
	protected void filterTasks(String tag) {
		List<Task> tasks = new ArrayList<Task>();
		for(Task task : mTasks) {
			if(task.title.contains(tag)) {
				tasks.add(task);
			}
		}

		updateTaskList(tasks);
	}

	private List<String> getTagsFromString(String str){
		List<String> tags = new ArrayList<String>();
		int start = str.indexOf("#");
		while(start!=-1) {
			int end = str.indexOf(" ", start);
			if(end==-1) {
				end = str.length();
			}
			L.l("start: "+start+", end: "+end);
			String tag = str.substring(start, end);
			tags.add(tag);
			start = str.indexOf("#", end);
		}

		return tags;
	}
}