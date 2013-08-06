package com.example.orthopedicdb;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageDetailFragment extends Fragment {
	private static final String IMAGE_DATA_EXTRA = "resId";
	private static final String IMAGE_MODEL_EXTRA = "modId";
    private String mPATH;
    private String modID;
    private ImageView mImageView;
    TextView tv;
    DB db;
    Cursor cursor;

    static ImageDetailFragment newInstance(String imageSRC, String modelID) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageSRC);
        args.putString(IMAGE_MODEL_EXTRA, modelID);
        f.setArguments(args);
        return f;
    }

    // Empty constructor, required as per Fragment docs
    public ImageDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPATH = getArguments().getString(IMAGE_DATA_EXTRA);
        modID = getArguments().getString(IMAGE_MODEL_EXTRA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // image_detail_fragment.xml contains just an ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        tv = (TextView) v.findViewById(R.id.model_id);
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        
        tv.setText(modID);
        if (ImageDetailActivity.class.isInstance(getActivity())) {
            // Call out to ImageDetailActivity to load the bitmap in a background thread
            ((ImageDetailActivity) getActivity()).loadBitmap(mPATH, mImageView, width, height);
        }
        if (OnClickListener.class.isInstance(getActivity())) {
            mImageView.setOnClickListener((OnClickListener) getActivity());
        }
    }
}
