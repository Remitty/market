package me.riddhimanadib.library;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Helper class to handle bottom navigation UI and click events
 *
 * Created by Adib on 13-Apr-17.
 */

public class BottomNavigationBar implements View.OnClickListener {

    public static final int MENU_BAR_1 = 0;
    public static final int MENU_BAR_2 = 1;
    public static final int MENU_BAR_3 = 2;
    public static final int MENU_BAR_4 = 3;

    private List<NavigationPage> mNavigationPageList = new ArrayList<>();

    private Context mContext;
    private AppCompatActivity mActivity;
    private BottomNavigationMenuClickListener mListener;

    private LinearLayout mLLBar1, mLLBar2, mLLBar3, mLLBar4;
    private View mViewBar1, mViewBar2, mViewBar3, mViewBar4;
    private ImageView mImageViewBar1, mImageViewBar2, mImageViewBar3, mImageViewBar4;
    private TextView mTextViewBar1, mTextViewBar2, mTextViewBar3, mTextViewBar4;

    public BottomNavigationBar(AppCompatActivity context, List<NavigationPage> pages, BottomNavigationMenuClickListener listener) {

        // initialize variables
        mContext = context;
        mActivity = (AppCompatActivity) mContext;
        mListener = listener;
        mNavigationPageList = pages;

        // getting reference to bar linear layout viewgroups
        mLLBar1 = (LinearLayout) mActivity.findViewById(R.id.linearLayoutBar1);
        mLLBar2 = (LinearLayout) context.findViewById(R.id.linearLayoutBar2);
        mLLBar3 = (LinearLayout) context.findViewById(R.id.linearLayoutBar3);
        mLLBar4 = (LinearLayout) context.findViewById(R.id.linearLayoutBar4);

        // getting reference to bar upper highlight
        mViewBar1 = (View) context.findViewById(R.id.viewBar1);
        mViewBar2 = (View) context.findViewById(R.id.viewBar2);
        mViewBar3 = (View) context.findViewById(R.id.viewBar3);
        mViewBar4 = (View) context.findViewById(R.id.viewBar4);

        // getting reference to bar titles
        mTextViewBar1 =  context.findViewById(R.id.textViewBar1);
        mTextViewBar2 =  context.findViewById(R.id.textViewBar2);
        mTextViewBar3 =  context.findViewById(R.id.textViewBar3);
        mTextViewBar4 =  context.findViewById(R.id.textViewBar4);

        NavigationPage temp = mNavigationPageList.get(0);
        String title = temp.getTitle();

        // 2etting the titles
        mTextViewBar1.setText(title);
        mTextViewBar2.setText(mNavigationPageList.get(1).getTitle());
        mTextViewBar3.setText(mNavigationPageList.get(2).getTitle());
        mTextViewBar4.setText(mNavigationPageList.get(3).getTitle());

        // getting reference to bar icons
        mImageViewBar1 =  context.findViewById(R.id.imageViewBar1);
        mImageViewBar2 =  context.findViewById(R.id.imageViewBar2);
        mImageViewBar3 =  context.findViewById(R.id.imageViewBar3);
        mImageViewBar4 =  context.findViewById(R.id.imageViewBar4);

        // setting the icons

        mImageViewBar1.setImageDrawable(mNavigationPageList.get(0).getIcon());
        mImageViewBar2.setImageDrawable(mNavigationPageList.get(1).getIcon());
        mImageViewBar3.setImageDrawable(mNavigationPageList.get(2).getIcon());
        mImageViewBar4.setImageDrawable(mNavigationPageList.get(3).getIcon());

        // setting click listeners
        mLLBar1.setOnClickListener(this);
        mLLBar2.setOnClickListener(this);
        mLLBar3.setOnClickListener(this);
        mLLBar4.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        // setting clicked bar as highlighted view
        setView(view);

        // triggering click listeners
        if (view.getId() == R.id.linearLayoutBar1) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_1);
            return;
        } else if (view.getId() == R.id.linearLayoutBar2) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_2);
            return;
        } else if (view.getId() == R.id.linearLayoutBar3) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_3);
            return;
        } else if (view.getId() == R.id.linearLayoutBar4) {
            mListener.onClickedOnBottomNavigationMenu(MENU_BAR_4);
            return;
        } else {
            return;
        }

    }

    /**
     * sets the clicked view as selected, resets other views
     * @param view clicked view
     */
    private void setView(View view) {

        // seting all highlight bar as invisible
        mViewBar1.setVisibility(View.INVISIBLE);
        mViewBar2.setVisibility(View.INVISIBLE);
        mViewBar3.setVisibility(View.INVISIBLE);
        mViewBar4.setVisibility(View.INVISIBLE);

        // resetting colors of all icons
        mImageViewBar1.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mImageViewBar2.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mImageViewBar3.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mImageViewBar4.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));

        // resetting colors of all titles
        mTextViewBar1.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mTextViewBar2.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mTextViewBar3.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));
        mTextViewBar4.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentUnselected));

        // selectively colorizing the marked view
        if (view.getId() == R.id.linearLayoutBar1) {
            mViewBar1.setVisibility(View.VISIBLE);
            mImageViewBar1.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            mTextViewBar1.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            return;
        } else if (view.getId() == R.id.linearLayoutBar2) {
            mViewBar2.setVisibility(View.VISIBLE);
            mImageViewBar2.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            mTextViewBar2.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            return;
        } else if (view.getId() == R.id.linearLayoutBar3) {
            mViewBar3.setVisibility(View.VISIBLE);
            mImageViewBar3.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            mTextViewBar3.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            return;
        } else if (view.getId() == R.id.linearLayoutBar4) {
            mViewBar4.setVisibility(View.VISIBLE);
            mImageViewBar4.setColorFilter(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            mTextViewBar4.setTextColor(ContextCompat.getColor(mContext, R.color.colorNavAccentSelected));
            return;
        } else {
            return;
        }

    }

    public interface BottomNavigationMenuClickListener {
        void onClickedOnBottomNavigationMenu(int menuType);
    }

}
