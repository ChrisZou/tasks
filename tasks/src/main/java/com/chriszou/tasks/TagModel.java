package com.chriszou.tasks;

import com.chriszou.androidlibs.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zouyong on 10/30/14.
 */
public class TagModel {
    /**
     *
     */
    public static List<String> getTagsFromTasks(List<Task> tasks) {
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

        return tags;
    }

    private static List<String> getTagsFromString(String str) {
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
