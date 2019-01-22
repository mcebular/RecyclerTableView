package com.mc0239.recyclertableview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class RecyclerTableView extends FrameLayout {

    private HorizontalScrollView scrollView;
    private RecyclerView recyclerView;
    private LinearLayout headerRow;
    private RecyclerTableViewAdapter viewAdapter;
    @IdRes private int checkboxViewId;
    @IdRes private int edittextViewId;

    public RecyclerTableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.recyclertableview, this, true);

        scrollView = (HorizontalScrollView) v.findViewById(R.id.horizontalScrollView);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerTableView);

        int scrollbarSize = a.getLayoutDimension(R.styleable.RecyclerTableView_rtv_scrollbarSize, 5);
        scrollView.setScrollBarSize(scrollbarSize);
        recyclerView.setScrollBarSize(scrollbarSize);

        a.recycle();

        // fixes a problem with edittext focuses causing runtime crashes ???
        // https://stackoverflow.com/a/40659632/2907620
        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                ViewGroup v = (ViewGroup) holder.itemView;
                for(int i=0; i<v.getChildCount(); i++) {
                    if(v.getChildAt(i).hasFocus()) v.getChildAt(i).clearFocus();
                }
                v.clearFocus();
            }
        });

        // scrolling solution: https://stackoverflow.com/a/14256117/2907620
        recyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        scrollView.setOnTouchListener(new OnTouchListener() {
            private float mx, my, curX, curY;
            private boolean started = false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                curX = event.getX();
                curY = event.getY();
                int dx = (int) (mx - curX);
                int dy = (int) (my - curY);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (started) {
                            recyclerView.scrollBy(0, dy);
                            scrollView.scrollBy(dx, 0);
                        } else {
                            started = true;
                        }
                        mx = curX;
                        my = curY;
                        break;
                    case MotionEvent.ACTION_UP:
                        recyclerView.scrollBy(0, dy);
                        scrollView.scrollBy(dx, 0);
                        started = false;
                        break;
                }
                return true;
            }
        });
    }

    public void setHeaderResource(@LayoutRes int header, @ColorRes int backgroundColor, @ColorRes int textColor) {
        ViewStub stub = (ViewStub) findViewById(R.id.headerRow);
        stub.setLayoutResource(header);
        headerRow = (LinearLayout) stub.inflate();
        headerRow.setBackgroundColor(ContextCompat.getColor(getContext(), backgroundColor));
        for(int i=0; i<headerRow.getChildCount(); i++) {
            View v = headerRow.getChildAt(i);
            if(v instanceof TextView) ((TextView) headerRow.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), textColor));
            if(v instanceof EditText){
                v.setEnabled(false);
                v.setFocusable(false);
            }
        }
        headerRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // click listener for checkbox in header (check/uncheck all)
                if(checkboxViewId != 0) {
                    CompoundButton c = (CompoundButton) headerRow.findViewById(checkboxViewId);
                    if(c instanceof CheckBox) {
                        boolean areAllChecked = (viewAdapter.getCheckedItemCount() == viewAdapter.getItemCount());
                        viewAdapter.setAllChecked(!areAllChecked);
                        c.setChecked(!areAllChecked);
                    }
                }
            }
        });

        // disable edittext in header
        if(edittextViewId != 0) {
            EditText e = (EditText) headerRow.findViewById(edittextViewId);
            e.setEnabled(false);
            e.setFocusable(false);
        }
    }

    /**
     * Defines a column with CheckBox or RadioButton and enables "checkable" functionality in view
     * and adapter.
     * @param checkboxViewId resource ID of the CheckBox or RadioButton in row view.
     */
    public void setCheckable(@IdRes int checkboxViewId) {
        this.checkboxViewId = checkboxViewId;
    }

    /**
     * Defines a column with EditText and enables "editable" functionality in view and adapter.
     * @param edittextViewId resource ID of the EditText in row view.
     */
    public void setEditable(@IdRes int edittextViewId) {
        this.edittextViewId = edittextViewId;
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Sets an adapter for this RecyclerTableView. Please note that setCheckable(...) and
     * setEditable(...) methods for RecyclerTableView should be called <b>before</b> calling
     * setAdapter(...).
     * @param adapter adapter to set to this view, or null for no adapter.
     */
    public void setAdapter(RecyclerTableViewAdapter adapter) {
        viewAdapter = adapter;
        viewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() { checkHeader(); }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) { checkHeader(); }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) { checkHeader(); }
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) { checkHeader(); }

            private void checkHeader() {
                if(checkboxViewId != 0) {
                    CompoundButton c = (CompoundButton) headerRow.findViewById(checkboxViewId);
                    boolean areAllChecked = (viewAdapter.getItemCount() > 0 && viewAdapter.getCheckedItemCount() == viewAdapter.getItemCount());
                    c.setChecked(areAllChecked);
                }
            }
        });
        if(headerRow == null) {
            throw new RuntimeException("Header for RecyclerTableView must be set before calling setAdapter(...).");
        }
        CompoundButton cb = (CompoundButton) headerRow.findViewById(checkboxViewId);
        if(cb instanceof CheckBox) viewAdapter.multipleCheckable = true;
        else if(cb instanceof RadioButton) viewAdapter.multipleCheckable = false;
        recyclerView.setAdapter(adapter);
    }

}
