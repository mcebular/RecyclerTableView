package com.mc0239.recyclertableview;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.mc0239.recyclertableview.exception.NotCheckableException;
import com.mc0239.recyclertableview.exception.NotEditableException;

import java.util.ArrayList;

public class RecyclerTableViewAdapter extends RecyclerView.Adapter<RecyclerTableViewAdapter.ViewHolder> {
    private ArrayList<SparseArray<Object>> rows;
    @LayoutRes private int holderViewId;
    @LayoutRes private int[] columnNames;
    @IdRes private int checkboxId;
    @IdRes private int edittextId;
    boolean multipleCheckable;

    public RecyclerTableViewAdapter(@Nullable ArrayList<SparseArray<Object>> rows, @LayoutRes int viewId, @LayoutRes int[] columnNames) {
        this(rows, viewId, columnNames, 0, 0);
    }

    public RecyclerTableViewAdapter(@Nullable ArrayList<SparseArray<Object>> rows, @LayoutRes int viewId, @LayoutRes int[] columnNames, @IdRes int checkboxViewId) {
        this(rows, viewId, columnNames, checkboxViewId, 0);
    }

    public RecyclerTableViewAdapter(ArrayList<SparseArray<Object>> rows, @LayoutRes int viewId, @LayoutRes int[] columnNames, @IdRes int checkboxViewId, @IdRes int edittextViewId) {
        this.rows = rows;
        if(this.rows == null) this.rows = new ArrayList<>();
        this.holderViewId = viewId;
        this.columnNames = columnNames;
        this.checkboxId = checkboxViewId;
        multipleCheckable = true;
        this.edittextId = edittextViewId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(holderViewId, parent, false);
        //return new ViewHolder(itemLayoutView, itemLayoutView.getMeasuredWidth(), columnNames);
        return new ViewHolder(itemLayoutView, columnNames, checkboxId, edittextId);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position%2==1) holder.itemView.setBackgroundColor(Color.parseColor("#E0E0E0"));
        else              holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        SparseArray<Object> d = rows.get(position);

        for(int i=0; i<holder.cols.size(); i++) {
            View v = holder.cols.get(i);
            if(v instanceof TextView && !(v instanceof CompoundButton)) {
                ((TextView) v).setText(String.valueOf(d.get(v.getId())));
            }
        }

        if(checkboxId != 0) {
            CompoundButton c = (CompoundButton) holder.itemView.findViewById(checkboxId);
            c.setChecked((Boolean) d.get(checkboxId));
        }

        if(edittextId != 0) {
            EditText e = (EditText) holder.itemView.findViewById(edittextId);
            // reset tint coloring
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) e.setBackgroundTintList(null);
        }

        if(onRowBindListener != null) onRowBindListener.onRowBound(holder.itemView, rows.get(position), position);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if(onRowBindListener != null) onRowBindListener.onRowRecycled(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    public ArrayList<SparseArray<Object>> getItems() {
        return rows;
    }

    public int getCheckedItemCount() {
        if(checkboxId != 0) {
            int c = 0;
            for (SparseArray<Object> row : rows) {
                if ((Boolean) row.get(checkboxId)) c++;
            }
            return c;
        } else throw new NotCheckableException("This adapter does not have a checkbox view id set.");
    }

    public ArrayList<SparseArray<Object>> getCheckedItems() {
        ArrayList<SparseArray<Object>> ci = new ArrayList<>();
        for(SparseArray<Object> row : rows) {
            if((Boolean) row.get(checkboxId)) {ci.add(row);}
        }
        return ci;
    }


    public void removeAllRows() {
        rows.clear();
        notifyDataSetChanged();
    }

    /**
     * Adds a row to this adapter and updates the RecyclerTableView.
     * @param row SparseArray with columns that <b>must</b> match columns in defined row view
     *            (exception here is the CheckBox column, which defaults to false).
     */
    public void addRow(SparseArray<Object> row) {
        // if it's checkable and no value for checkbox state is provided, default to false
        if(checkboxId != 0 && row.get(checkboxId) == null) row.put(checkboxId, false);
        rows.add(row);
        notifyDataSetChanged();
    }

    public void setRows(ArrayList<SparseArray<Object>> rows) {
        for (SparseArray<Object> row : rows) {
            if(checkboxId != 0 && row.get(checkboxId) == null) row.put(checkboxId, false);
        }
        this.rows = rows;
        notifyDataSetChanged();
    }

    /**
     * Sets CheckBox fields in this adapter to given state, in rows that match the filter. Parameter
     * filter is a SparseArray that contains columns which should be compared. Filter can contain
     * any number of columns.
     * <br />
     * For example, if we want to check only a row with <i>someId</i>, filter would contain only one
     * column:
     * <pre>
     * {@code
     * SparseArray<Object> filter = new SparseArray<>(1);
     * filter.put(R.id.textViewID, someId);
     * }
     * </pre>
     * In this case, only the row with <i>someId</i> would be set as checked.
     * @exception NotCheckableException Adapter does not have a CheckBox view ID set (setCheckable
     * was not called upon RecyclerTableView).
     * @param filter SparseArray that is compared to each row
     * @param checked new state of CheckBoxes
     */
    public void setChecked(@NonNull SparseArray<Object> filter, boolean checked) {
        if(checkboxId != 0) {
            for (int i = 0; i < rows.size(); i++) {
                SparseArray<Object> row = rows.get(i);
                int fits = 0;
                for (int j = 0; j < filter.size(); j++) {
                    int key = filter.keyAt(j);
                    String c1 = String.valueOf(filter.get(key));
                    String c2 = String.valueOf(row.get(key));
                    if (c1.equals(c2)) fits++;
                }
                if (fits == filter.size()) {
                    rows.get(i).put(checkboxId, checked);
                }
            }
            notifyItemRangeChanged(0, rows.size());
        } else throw new NotCheckableException("This adapter does not have a checkbox view id set.");
    }

    /**
     * Sets all CheckBox fields in this adapter to given state and updates the RecyclerTableView.
     * @exception NotCheckableException Adapter does not have a CheckBox view ID set (setCheckable
     * was not called upon RecyclerTableView).
     * @param checked new state of CheckBoxes
     */
    public void setAllChecked(boolean checked) {
        if(checkboxId != 0) {
            for(SparseArray<Object> row: rows) {
                row.put(checkboxId, checked);
            }
            notifyDataSetChanged();
        } else throw new NotCheckableException("This adapter does not have a checkbox view id set.");
    }

    /**
     * Clears all EditText fields in this adapter and updates the RecyclerTableView.
     * @exception NotEditableException Adapter does not have an EditText view ID set (setEditable
     * was not called upon RecyclerTableView).
     */
    public void clearAllEditables() {
        if(edittextId != 0) {
            for(SparseArray<Object> row: rows) {
                row.put(edittextId, "");
            }
            notifyDataSetChanged();
        } else throw new NotEditableException("This adapter does not have an edittext view id set.");
    }

    public interface OnEditTextListener {
        void onEditTextFocus(EditText editText, SparseArray<Object> rowData);
        void onEditTextChanged(EditText editText, String text);
        boolean onEnterPressed(EditText editText, SparseArray<Object> rowData);
    }

    public interface OnRowClickListener {
        void onRowClicked(View rowView, SparseArray<Object> rowData, int position);
    }

    public interface OnRowLongClickListener {
        boolean onLongClicked(View rowView, SparseArray<Object> rowData, int position);
    }

    public interface OnRowBindListener {
        void onRowBound(View rowView, SparseArray<Object> rowData, int position);
        void onRowRecycled(View rowView);
    }

    private OnEditTextListener onEditTextListener = null;
    private OnRowClickListener onRowClickListener = null;
    private OnRowLongClickListener onRowLongClickListener = null;
    private OnRowBindListener onRowBindListener = null;

    public void setOnEditTextListener(OnEditTextListener l) { onEditTextListener = l; }
    public void setOnRowClickListener(OnRowClickListener l) { onRowClickListener = l; }
    public void setOnRowLongClickListener(OnRowLongClickListener l) { onRowLongClickListener = l; }
    public void setOnRowBindListener(OnRowBindListener l) { onRowBindListener = l; }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ArrayList<View> cols;

        public ViewHolder(View itemView, @LayoutRes int[] columnNames, @IdRes final int checkboxId, @IdRes final int edittextId) {
            super(itemView);
            cols = new ArrayList<>(columnNames.length);
            for (int columnName : columnNames) {
                cols.add(itemView.findViewById(columnName));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(checkboxId != 0) {
                        if(multipleCheckable) {
                            boolean checked = (boolean) rows.get(position).get(checkboxId);
                            rows.get(position).put(checkboxId, !checked);
                            notifyItemChanged(position);
                        } else {
                            setAllChecked(false);
                            rows.get(position).put(checkboxId, true);
                            notifyItemRangeChanged(0, rows.size());
                        }
                    }
                    if(onRowClickListener != null) onRowClickListener.onRowClicked(v, rows.get(position), position);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if(onRowLongClickListener != null) return onRowLongClickListener.onLongClicked(v, rows.get(position), position);
                    return false;
                }
            });

            if(edittextId != 0) {
                final EditText e = (EditText) itemView.findViewById(edittextId);
                e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if(hasFocus) {
                            int position = getAdapterPosition();
                            if(position >= 0)
                                if (onEditTextListener != null)
                                    onEditTextListener.onEditTextFocus((EditText) view, rows.get(position));
                        }
                    }
                });
                e.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        rows.get(getAdapterPosition()).put(edittextId, editable.toString());
                        if(onEditTextListener != null) onEditTextListener.onEditTextChanged(e, editable.toString());
                    }
                });
                e.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                                if(onEditTextListener != null) return onEditTextListener.onEnterPressed((EditText) view, rows.get(getAdapterPosition()));
                            }
                        }
                        return false;
                    }
                });
            }
        }

    }
}
