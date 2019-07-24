package com.mc0239.recyclertableview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
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

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerTableView extends FrameLayout {

    private HorizontalScrollView scrollView;
    private RecyclerView recyclerView;
    private LinearLayout headerRow;
    private RecyclerTableViewAdapter viewAdapter;

    @IdRes private int checkboxViewId;

    @SuppressLint("ClickableViewAccessibility")
    public RecyclerTableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.recyclertableview, this, true);

        scrollView = v.findViewById(R.id.horizontalScrollView);
        recyclerView = v.findViewById(R.id.recyclerView);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerTableView);

        int scrollbarSize = a.getLayoutDimension(R.styleable.RecyclerTableView_rtv_scrollbarSize, 5);
        scrollView.setScrollBarSize(scrollbarSize);
        recyclerView.setScrollBarSize(scrollbarSize);

        a.recycle();

        // fixes a problem with edittext focuses causing runtime crashes ???
        // https://stackoverflow.com/a/40659632/2907620
        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
                ViewGroup v = (ViewGroup) holder.itemView;
                for(int i=0; i<v.getChildCount(); i++) {
                    if(v.getChildAt(i).hasFocus()) v.getChildAt(i).clearFocus();
                }
                v.clearFocus();
            }
        });

        // scrolling solution:
        // https://stackoverflow.com/a/14256117/2907620
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

    /**
     * Sets an adapter for this RecyclerTableView.
     * @param adapter adapter to set to this view, or null for no adapter.
     */
    public void setAdapter(@Nullable RecyclerTableViewAdapter adapter) {
        if (adapter == null) {
            recyclerView.setAdapter(null);
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.checkboxViewId = adapter.getCheckboxId();

        setHeaderResource(adapter.getRowLayout(), R.color.colorPrimary, R.color.colorWhite);

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
                    CompoundButton c = headerRow.findViewById(checkboxViewId);
                    c.setChecked(viewAdapter.areAllChecked());
                }
            }
        });

        CompoundButton cb = headerRow.findViewById(checkboxViewId);
        if(cb instanceof CheckBox) viewAdapter.multipleCheckable = true;
        else if(cb instanceof RadioButton) viewAdapter.multipleCheckable = false;

        recyclerView.setAdapter(adapter);
    }

    private void setHeaderResource(@LayoutRes int header, @ColorRes int backgroundColor, @ColorRes int textColor) {
        // Replace Stub with given header row
        ViewStub stub = findViewById(R.id.headerRow);
        stub.setLayoutResource(header);
        headerRow = (LinearLayout) stub.inflate();
        headerRow.setBackgroundColor(ContextCompat.getColor(getContext(), backgroundColor));
        for(int i=0; i<headerRow.getChildCount(); i++) {
            View v = headerRow.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView) headerRow.getChildAt(i)).setTextColor(ContextCompat.getColor(getContext(), textColor));
            }
            if(v instanceof EditText){
                // disable Edittexts in header
                v.setEnabled(false);
                v.setFocusable(false);
            }
        }

        headerRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // click listener for checkbox in header (check/uncheck all)
                if(checkboxViewId != 0) {
                    CompoundButton c = headerRow.findViewById(checkboxViewId);
                    if(c instanceof CheckBox) {
                        boolean allChecked = viewAdapter.areAllChecked();
                        viewAdapter.setAllChecked(!allChecked);
                        c.setChecked(!allChecked);
                    }
                }
            }
        });
    }

}
