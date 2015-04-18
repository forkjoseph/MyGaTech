package com.mygatech.webview;

import java.util.ArrayList;

import android.widget.AdapterView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mygatech.R;
import com.mygatech.map.MapPane;

public class StableAdapter extends ArrayAdapter<Restaurant> implements AdapterView.OnItemClickListener {
	private Context context;
	private ArrayList<Restaurant> objects;
	private int rowResourceId;
	
	public StableAdapter(Context context, int resource, ArrayList<Restaurant> objects) {
		super(context, resource, objects);
		this.context = context;
		this.objects = objects;
		this.rowResourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(rowResourceId, parent, false);
		ImageView imageView = (ImageView) rowView
				.findViewById(R.id.imageView);
		TextView textView = (TextView) rowView.findViewById(R.id.textView);
		textView.setTextColor(Color.BLACK);

		int imageFileR = objects.get(position).getImgSrcR(); 
		textView.setText(objects.get(position).toString());
//		Log.e(objects.get(position).getName(), objects.get(position).debug());
		try {
			// image 100x100 adjusting
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(context.getResources(), imageFileR,
					options);
			imageView.setImageBitmap(decodeSampledBitmapFromResource(
					context.getResources(), imageFileR, 100, 100));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowView;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
		if (!objects.get(position).getName().equalsIgnoreCase("closed")){
			view.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
				@Override
				public void run() {
					final double latitude = objects.get(position).getLat();
					final double longitude = objects.get(position).getLong();
					new AlertDialog.Builder(context).setTitle("Location")
							.setMessage(objects.get(position).getLocationString())
							.setPositiveButton("Map",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int which) {
										Intent intent = new Intent(context, MapPane.class);
										intent.putExtra("name", objects.get(position).getName());
										intent.putExtra("latitude", latitude);
										intent.putExtra("longitude", longitude);
										context.startActivity(intent);
										}
									})
							.setNegativeButton("Close",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int which) {
											// do nothing n Close
									}
								}
							).show();
					view.setAlpha(1);
				}
			});	
		}
	}


	public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2
			// and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}
	
	@Override
	public int getCount () {
		return objects.size();
	}
	
	public int getPosition (Restaurant item){
		return objects.indexOf(item);
	}
	
	@Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public Restaurant getItem (int position) {
        return objects.get(position);
    }

	public boolean hasStableIds() {
		return true;
	}

	
}