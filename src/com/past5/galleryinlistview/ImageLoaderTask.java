//Author: Vsevolod Geraskin (past5)

package com.past5.galleryinlistview;

import com.past5.galleryinlistview.MainActivity.GalleryAdapter;
import com.past5.galleryinlistview.MainActivity.ViewHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoaderTask extends AsyncTask<Integer, String, Bitmap> {
	Integer m_position;
	ViewHolder m_holder;
	ImageView m_photo;
	String m_photoPath;
	GalleryAdapter m_adapter;

	public ImageLoaderTask(ImageView photo, String photoPath,
			GalleryAdapter adapter, int position, ViewHolder holder) {
		SyncCounter.inc();

		m_photo = photo;
		m_photoPath = photoPath;
		m_adapter = adapter;
		m_position = position;
		m_holder = holder;
	}

	@Override
	protected Bitmap doInBackground(Integer... params) {
		// re-sample sample image in the background to 200x200
		Bitmap bitmap = decodeSampledBitmapFromString(200, 200);

		return bitmap;
	}

	// set photoView and holder
	protected void onPostExecute(Bitmap bitmap) {
		if (bitmap != null) {
			BitmapCache.addBitmapToMemoryCache(m_position, bitmap);

			if (m_holder == null) {
				m_photo.setImageBitmap(bitmap);
			} else {
				if (m_holder.position == m_position) {
					if (m_holder.picture != null) {
						m_holder.picture.setImageBitmap(bitmap);
					}

				}
			}

			Log.i("GalleryInListView", "Bitmap loaded, notifying list adapter");
			m_adapter.notifyDataSetChanged();
		} else {
			Log.e("GalleryInListView", "Error: Could not sample bitmap");
		}

		SyncCounter.dec();
	}

	// resample Bitmap to prevent out-of-memory crashes
	private Bitmap decodeSampledBitmapFromString(int reqWidth, int reqHeight) {
		Bitmap bitmap;

		// decode File and set inSampleSize
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(m_photoPath, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// decode File with inSampleSize set
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(m_photoPath, options);

		return bitmap;
	}

	// calculate bitmap sample sizes
	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
}
