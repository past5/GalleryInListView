//Author: Vsevolod Geraskin (past5)

package com.past5.galleryinlistview;

//static class to count number of concurrent asynctasks
public final class SyncCounter {
	private static int i = 0;

	public static synchronized void inc() {
		i++;
	}

	public static synchronized void dec() {
		i--;
	}

	public static synchronized int current() {
		return i;
	}
}
