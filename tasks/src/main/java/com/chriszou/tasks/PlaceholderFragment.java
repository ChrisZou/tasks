/**
 * PlaceholderFragment.java
 *
 * Created by zouyong on Sep 9, 2014,2014
 */
package com.chriszou.tasks;

import android.app.Fragment;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chriszou.androidlibs.L;
import com.chriszou.androidlibs.OnEnterListener;
import com.chriszou.androidlibs.Toaster;
import com.chriszou.androidlibs.ViewBinderAdapter;
import com.chriszou.androidlibs.ViewBinderAdapter.ViewBinder;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zouyong
 *
 */
@EFragment(R.layout.fragment_main)
public class PlaceholderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    @ViewById(R.id.main_tags_list)
	ListView mTagList;

	@ViewById(R.id.main_tasks_list)
    SwipeListView mTaskList;

	@ViewById(R.id.main_add_edit)
	EditText mAddEdit;

//    @ViewById(R.id.swipe_container)
//    SwipeRefreshLayout mSwipeLayout;

	List<Task> mTasks;

	ViewBinderAdapter<String> mTagAdapter;
	ViewBinderAdapter<Task> mTaskAdapter;
	private int mCurrentTag = 0;

    ViewBinder<Task> mTaskViewBinder = new ViewBinder<Task>() {
        @Override
        public void bindView(final int position, View view, final Task item, ViewGroup parent) {
            TextView tView = (TextView) view.findViewById(R.id.task_item_title);
            tView.setText(item.title);

            ImageView deleteView = (ImageView) view.findViewById(R.id.task_item_delete);
            //Set this view's position in its tag, when clicking this view, fetch its position from its tag and delete it.
            deleteView.setTag(R.id.tag_item_position, position);
            deleteView.setOnClickListener(mDeleteListener);

        }
    };

	ViewBinder<String> mTagViewBinder = new ViewBinder<String>() {
		@Override
		public void bindView(int position, View view, String item, ViewGroup parent) {
			TextView tView = (TextView) view;
			tView.setText(item);
			int color = (position == mCurrentTag) ? Color.parseColor("#00B0E1") : Color.TRANSPARENT;
			tView.setBackgroundColor(color);
		}
	};

    private View.OnClickListener mDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer)v.getTag(R.id.tag_item_position);
            removeTask(position);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int swipeLeftOffset = mTaskList.getWidth() - (int)getResources().getDimension(R.dimen.delete_button_width);
                mTaskList.setOffsetLeft(swipeLeftOffset);
            }
        };
        mTaskList.post(runnable);
    }

    public PlaceholderFragment() {
	}

	@AfterViews
	void loadData() {
		init();
		reloadTasks();
	}

	private void init() {
        mTaskList.setSwipeListViewListener(new MySwipeListViewListener());
        mTagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    @Background
    void removeTask(int position) {
        try {
            boolean removeResult = Task.remove(mTaskAdapter.getItem(position));
            if (removeResult) {
                reloadTasks();
            } else {
                Toaster.s(getActivity(), "Remove failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toaster.s(getActivity(), "Network error happened during removing");
        }
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
	void reloadTasks() {
		try {
			mTasks = Task.all();
			updateTaskList(mTasks);
            List<String> tags = TagModel.getTagsFromTasks(mTasks);
			updateTagList(tags);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	@UiThread
	void updateTaskList(List<Task> tasks) {
        getTaskAdapter().removeAll();
        getTaskAdapter().addAll(tasks);
        //mSwipeLayout.setRefreshing(false);
        mTaskList.closeOpenedItems();

        showNotification();
	}

    private void showNotification() {
        if(mTasks!=null && mTasks.size()>0) {
            Utils.showTaskNotification(getActivity(), mTasks.get(0).title);
        }
    }

	@UiThread
	void updateTagList(List<String> tags) {
        getTagAdapter().removeAll();
        getTagAdapter().addAll(tags);
	}

    private ViewBinderAdapter<Task> getTaskAdapter() {
        if(mTaskAdapter==null) {
            mTaskAdapter = new ViewBinderAdapter<Task>(getActivity(), Collections.EMPTY_LIST, R.layout.task_item, mTaskViewBinder);
            mTaskList.setAdapter(mTaskAdapter);
        }

        return mTaskAdapter;
    }

    private ViewBinderAdapter<String> getTagAdapter() {
        if(mTagAdapter==null) {
            mTagAdapter = new ViewBinderAdapter<String>(getActivity(), Collections.EMPTY_LIST, mTagViewBinder);
            mTagList.setAdapter(mTagAdapter);
        }

        return mTagAdapter;
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



    @Override
    public void onRefresh() {
        reloadTasks();
    }

    private class MySwipeListViewListener extends BaseSwipeListViewListener {
        @Override
        public void onClickFrontView(int position) {
            super.onClickFrontView(position);
            L.l("click front view");
        }

        @Override
        public void onClickBackView(int position) {
            super.onClickBackView(position);
            L.l("click back view");
            mTaskList.closeOpenedItems();
        }

        @Override
        public void onStartOpen(int position, int action, boolean right) {
            super.onStartOpen(position, action, right);
            mTaskList.closeOpenedItems();
        }
    }

}