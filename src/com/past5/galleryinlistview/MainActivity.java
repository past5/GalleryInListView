//Author: Vsevolod Geraskin (past5)

package com.past5.galleryinlistview;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity {
	private final int MAX_TASKS = 50;

	private ListView m_listView;
	private static ArrayList<Uri> m_uris;
	private GalleryAdapter m_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// point cursor to images in external content directory
		final Cursor cursor = this.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
				null);

		try {
			cursor.moveToFirst();

			Log.i("GalleryInListView", "image count= " + cursor.getCount());
			m_uris = new ArrayList<Uri>(cursor.getCount());

			// loop to put all image uris into our arraylist
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);

				m_uris.add(Uri.parse(cursor.getString(1)));
				Log.i("GalleryInListView", "m_uris[" + i + "]=" + m_uris.get(i));
			}
		} catch (Exception e) {
			Log.e("GalleryInListView", "onCreate error: " + e.toString());
		}

		// initialize bitmap cache
		BitmapCache.InitBitmapCache();

		m_adapter = new GalleryAdapter(this);
		m_listView = (ListView) this.findViewById(R.id.lstGallery);
		m_listView.setAdapter(m_adapter);
	}

	// ViewHolder class
	public static class ViewHolder {
		public ImageView picture;
		public int position;
	}

	public class GalleryAdapter extends BaseAdapter {
		private final Context context;

		public GalleryAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return m_uris.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ImageView photo = null;
			ViewHolder holder = null;

			// implement ViewHolder pattern to help recycle views
			if (view == null) {
				view = LayoutInflater.from(context).inflate(R.layout.list_item,
						parent, false);
				holder = new ViewHolder();
				holder.position = position;
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			photo = (ImageView) view.findViewById(R.id.list_item_photo);

			Log.i("GalleryInListView", "position[" + position + "] = "
					+ m_uris.get(position).getPath());

			loadBitmap(photo, position, holder);

			return view;
		}

		// load Bitmap either from our cache or asynchronously
		public void loadBitmap(ImageView photo, Integer position,
				ViewHolder holder) {
			Bitmap bitmap = null;
			bitmap = BitmapCache.getBitmapFromMemCache(position);

			if (bitmap != null) {
				photo.setImageBitmap(bitmap);
				Log.i("GalleryInListView", "setting imageview from CACHE");
			} else {
				Log.i("GalleryInListView", "SyncCounter.current = "
						+ SyncCounter.current());

				// only start MAX_TASKS asynctasks at a time to prevent
				// out-of-memory errors and to stay under android limit
				if (SyncCounter.current() < MAX_TASKS) {
					new ImageLoaderTask(photo, m_uris.get(position).getPath(),
							m_adapter, position, holder).executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, (Integer[]) null);
				}
			}
		}
	}
}
