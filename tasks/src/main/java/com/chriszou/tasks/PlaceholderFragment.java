/**
 * PlaceholderFragment.java
 *
 * Created by zouyong on Sep 9, 2014,2014
 */
package com.chriszou.tasks;

import android.app.Fragment;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.chriszou.androidlibs.L;
import com.chriszou.androidlibs.OnEnterListener;
import com.chriszou.androidlibs.Toaster;
import com.chriszou.androidlibs.ViewBinderAdapter;
import com.chriszou.androidlibs.ViewBinderAdapter.ViewBinder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

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

	@ViewById(R.id.main_add_edit)
	EditText mAddEdit;

	List<Task> mTasks;

	ViewBinderAdapter<String> mTagAdapter;
	ViewBinderAdapter<Task> mTaskAdapter;
	private int mCurrentTag = 0;

	ViewBinder<String> mTagViewBinder = new ViewBinder<String>() {
		@Override
		public void bindView(int position, View view, String item, ViewGroup parent) {
			TextView tView = (TextView) view;
			tView.setText(item);
			int color = (position == mCurrentTag) ? Color.parseColor("#00B0E1") : Color.TRANSPARENT;
			tView.setBackgroundColor(color);
		}
	};

	ViewBinder<Task> mTaskViewBinder = new ViewBinder<Task>() {
		@Override
		public void bindView(int position, View view, Task item, ViewGroup parent) {
			TextView tView = (TextView) view;
			tView.setText(item.title);
		}
	};

	public PlaceholderFragment() {
	}

	@AfterViews
	void loadData() {
		init();
		getTasks();
	}

	private void init() {
		mAddEdit.setOnKeyListener(new OnEnterListener() {
			@Override
			public void onEnter() {
				String title = mAddEdit.getText().toString().trim();
				if (title.length() > 0) {
					mAddEdit.setEnabled(false);
					addTask(title);
				}
			}
		});
	}

	@Background
	void addTask(String title) {
		Task task = new Task(title).save();
		onAddingResult(task);
	}


	/**
	 * @param task
	 *            The saved task
	 */
	@UiThread
	void onAddingResult(Task task) {
		mAddEdit.setEnabled(true);
		if (task != null) {
			mTasks.add(0, task);
			updateTaskList(mTasks);
			mAddEdit.setText("");
		} else {
			notifyError("Error during adding task");
		}
	}

	public void notifyError(String errorMsg) {
		Toaster.s(getActivity(), errorMsg);
	}

	@Background
	void getTasks() {
		try {
			mTasks = Task.all();
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
		mTaskAdapter = new ViewBinderAdapter<Task>(getActivity(), tasks, mTaskViewBinder);
		mTaskList.setAdapter(mTaskAdapter);

        showNotification();
	}

    private void showNotification() {
        if(mTasks!=null && mTasks.size()>0) {
            Utils.showTaskNotification(getActivity(), mTasks.get(0).title);
        }
    }

	@UiThread
	void updateTagList(List<Task> tasks) {
		List<String> tags = new ArrayList<String>();
		tags.add("All");
		for (Task task : tasks) {
			List<String> subTags = getTagsFromString(task.title);
			for (String tag : subTags) {
				if (!tags.contains(tag)) {
					tags.add(tag);
				}
			}
		}

		mTagAdapter = new ViewBinderAdapter<String>(getActivity(), tags, mTagViewBinder);
		mTagList.setAdapter(mTagAdapter);
		mTagList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					updateTaskList(mTasks);
				} else {
					String tag = mTagAdapter.getItem(position);
					filterTasks(tag);
				}
				mCurrentTag = position;
				mTagAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * @param tag
	 */
	protected void filterTasks(String tag) {
		List<Task> tasks = new ArrayList<Task>();
		for (Task task : mTasks) {
			if (task.title.contains(tag)) {
				tasks.add(task);
			}
		}

		updateTaskList(tasks);
	}

	private List<String> getTagsFromString(String str) {
		List<String> tags = new ArrayList<String>();
		int start = str.indexOf("#");
		while (start != -1) {
			int end = str.indexOf(" ", start);
			if (end == -1) {
				end = str.length();
			}
			L.l("start: " + start + ", end: " + end);
			String tag = str.substring(start, end);
			tags.add(tag);
			start = str.indexOf("#", end);
		}

		return tags;
	}
}