package com.mc0239.recyclertableview;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.mc0239.recyclertableview.annotation.RecyclerTableColumn;
import com.mc0239.recyclertableview.annotation.RecyclerTableRow;
import com.mc0239.recyclertableview.exception.NotCheckableException;
import com.mc0239.recyclertableview.exception.NotEditableException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class RecyclerTableViewAdapter extends RecyclerView.Adapter<RecyclerTableViewAdapter.ViewHolder> {

    private List<?> items;
    private HashMap<Integer, Field> rowFieldMap;
    private HashMap<Field, Method> fieldDisplayMap;

    @LayoutRes private int rowLayout;
    @IdRes private int checkboxId;
    @IdRes private int edittextId;

    boolean multipleCheckable;

    /**
     * Create a new adapter that gets row layout and column view ids from given class, which should
     * be properly annotated with {@link RecyclerTableRow} and given class fields annotated with
     * {@link RecyclerTableColumn}.
     * @param rowClass Class to use as table row data
     * @param items List of rows of type class
     */
    public RecyclerTableViewAdapter(Class rowClass, List<?> items) {
        this(rowClass, items, 0, 0, 0);
    }

    /**
     * Create a new adapter with explicitly given row layout. If rowLayout is not 0, annotation
     * {@link RecyclerTableRow} is ignored and parameters <i>rowLayout</i>, <i>checkboxId</i> and
     * <i>edittextId</i> are used instead.
     * Class is not required to be annotated with {@link RecyclerTableRow}, however, fields should
     * still be annotated with {@link RecyclerTableColumn}.
     * @param rowClass Class to use as table row data
     * @param items List of rows of type class
     * @param rowLayout Layout of the row to use
     * @param checkboxId Id of the checkbox (or radiobutton) view in rowLayout
     * @param edittextId Id of the edittext view in rowLayout
     */
    public RecyclerTableViewAdapter(Class rowClass, List<?> items, @LayoutRes int rowLayout, @IdRes int checkboxId, @IdRes int edittextId) {
        // get header resource layout
        // 1. from constructor arguments, if specified
        // 2. from annotation of the passed class
        if (rowLayout != 0) {
            this.rowLayout = rowLayout;
            this.checkboxId = checkboxId;
            this.edittextId = edittextId;
        } else {
            if (!rowClass.isAnnotationPresent(RecyclerTableRow.class)) {
                throw new RuntimeException("Class not annotated with RecyclerTableRow and no rowLayout specified");
            } else {
                RecyclerTableRow antRow = (RecyclerTableRow) rowClass.getAnnotation(RecyclerTableRow.class);
                this.rowLayout = antRow.value();
                this.checkboxId = antRow.checkboxViewId();
                this.edittextId = antRow.edittextViewId();
            }
        }

        // get column names (view IDs) and corresponding field names
        rowFieldMap = new HashMap<>();
        fieldDisplayMap = new HashMap<>();
        for (Field f : rowClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(RecyclerTableColumn.class)) {
                RecyclerTableColumn antColumn = f.getAnnotation(RecyclerTableColumn.class);
                int val = antColumn.value();
                if (val != 0) {
                    rowFieldMap.put(val, f);
                }
            }

            // get mapper if exists
            try {
                Method m = rowClass.getDeclaredMethod(f.getName() + "InView");
                fieldDisplayMap.put(f, m);
            } catch (NoSuchMethodException ignored) { }
        }

        if (items != null) {
            this.items = items;
        } else {
            this.items = new ArrayList<>();
        }
    }

    //

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position%2==1) holder.itemView.setBackgroundColor(Color.parseColor("#E0E0E0"));
        else              holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        Object row = items.get(position);

        for(Map.Entry<Integer, Field> entry : rowFieldMap.entrySet()) {
            View v = holder.itemView.findViewById(entry.getKey());

            if(v != null) {
                if (v instanceof TextView && !(v instanceof CompoundButton)) {
                    String value = null;
                    Method mapper = fieldDisplayMap.get(entry.getValue());
                    if (mapper != null) {
                        try {
                            value = (String) mapper.invoke(row);
                        } catch (IllegalAccessException ignored) { }
                        catch (InvocationTargetException ignored) { }
                    }

                    if (value == null) {
                        value = String.valueOf(getFieldValue(entry.getValue(), row));
                    }
                    ((TextView) v).setText(value);
                }
            }
        }

        if(checkboxId != 0) {
            CompoundButton c = holder.itemView.findViewById(checkboxId);
            c.setChecked((Boolean) getFieldValue(rowFieldMap.get(checkboxId), row));
        }

        if(edittextId != 0) {
            EditText e = holder.itemView.findViewById(edittextId);
            // reset tint coloring
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) e.setBackgroundTintList(null);
        }

        if(onRowBindListener != null) onRowBindListener.onRowBound(holder.itemView, items.get(position), position);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        if(onRowBindListener != null) onRowBindListener.onRowRecycled(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //

    private Object getFieldValue(Field f, Object o) {
        Object value = null;
        try {
            value = f.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "Field " + f + " does not exist in field map. Is there a view id without assigned field?\n" +
                    "Possible cause is missing field for checkboxId or edittextId.", e);
        }
        return value;
    }

    private void setFieldValue(Field f, Object o, Object v) {
        try {
            f.set(o, v);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //

    public int getRowLayout() {
        return rowLayout;
    }

    public int getCheckboxId() {
        return checkboxId;
    }

    public int getEdittextId() {
        return edittextId;
    }

    public List<?> getItems() {
        return items;
    }

    public int getCheckedItemCount() {
        if(checkboxId != 0) {
            int c = 0;
            for (Object row : items) {
                if ((Boolean) getFieldValue(rowFieldMap.get(checkboxId), row)) c++;
            }
            return c;
        } else throw new NotCheckableException();
    }

    public List<Object> getCheckedItems() {
        if(checkboxId != 0) {
            ArrayList<Object> ci = new ArrayList<>();
            for (Object row : items) {
                if ((Boolean) getFieldValue(rowFieldMap.get(checkboxId), row)) {
                    ci.add(row);
                }
            }
            return ci;
        } else throw new NotCheckableException();
    }

    //

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addItem(Object item) {
        // if it's checkable and no value for checkbox state is provided, default to false
        //if(checkboxId != 0 && row.get(checkboxId) == null) row.put(checkboxId, false);
        // items.add(item); TODO
        notifyDataSetChanged();
    }

    public void setItems(List<?> items) {
        this.items = items;
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
     *                TODO
     */
    /*public void setChecked(@NonNull SparseArray<Object> filter, boolean checked) {
        if(checkboxId != 0) {
            for (int i = 0; i < rows.size(); i++) {
                RecyclerTableRow row = rows.get(i);
                int fits = 0;
                for (int j = 0; j < filter.size(); j++) {
                    int key = filter.keyAt(j);
                    String c1 = String.valueOf(filter.get(key));
                    // String c2 = String.valueOf(row.get(key)); TODO
                    // if (c1.equals(c2)) fits++;
                }
                if (fits == filter.size()) {
                    setFieldValue(rowFieldMap.get(checkboxId), rows.get(i), checked);
                }
            }
            notifyItemRangeChanged(0, rows.size());
        } else throw new NotCheckableException();
    }*/

    /**
     * Sets all CheckBox fields in this adapter to given state and updates the RecyclerTableView.
     * @exception NotCheckableException Adapter does not have a CheckBox view ID set (setCheckable
     * was not called upon RecyclerTableView).
     * @param checked new state of CheckBoxes
     */
    public void setAllChecked(boolean checked) {
        if(checkboxId != 0) {
            for(Object row: items) {
                setFieldValue(rowFieldMap.get(checkboxId), row, checked);
            }
            notifyDataSetChanged();
        } else throw new NotCheckableException();
    }

    /**
     * Clears all EditText fields in this adapter and updates the RecyclerTableView.
     * @exception NotEditableException Adapter does not have an EditText view ID set (setEditable
     * was not called upon RecyclerTableView).
     */
    public void clearAllEdittexts() {
        if(edittextId != 0) {
            for(Object row: items) {
                setFieldValue(rowFieldMap.get(edittextId), row, "");
            }
            notifyDataSetChanged();
        } else throw new NotEditableException();
    }

    public boolean areAllChecked() {
        return getItemCount() > 0 && getCheckedItemCount() == getItemCount();
    }

    //

    private OnEditTextListener onEditTextListener = null;
    private OnRowClickListener onRowClickListener = null;
    private OnRowLongClickListener onRowLongClickListener = null;
    private OnRowBindListener onRowBindListener = null;

    public void setOnEditTextListener(OnEditTextListener l) { onEditTextListener = l; }
    public void setOnRowClickListener(OnRowClickListener l) { onRowClickListener = l; }
    public void setOnRowLongClickListener(OnRowLongClickListener l) { onRowLongClickListener = l; }
    public void setOnRowBindListener(OnRowBindListener l) { onRowBindListener = l; }

    //

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(checkboxId != 0) {
                        if(multipleCheckable) {
                            boolean checked = (boolean) getFieldValue(rowFieldMap.get(checkboxId), items.get(position));
                            setFieldValue(rowFieldMap.get(checkboxId), items.get(position), !checked);
                            notifyItemChanged(position);
                        } else {
                            setAllChecked(false);
                            setFieldValue(rowFieldMap.get(checkboxId), items.get(position), true);
                            notifyItemRangeChanged(0, items.size());
                        }
                    }
                    if(onRowClickListener != null) onRowClickListener.onRowClicked(v, items.get(position), position);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if(onRowLongClickListener != null) return onRowLongClickListener.onLongClicked(v, items.get(position), position);
                    return false;
                }
            });

            if(edittextId != 0) {
                final EditText e = itemView.findViewById(edittextId);
                e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if(hasFocus) {
                            int position = getAdapterPosition();
                            if(position >= 0)
                                if (onEditTextListener != null)
                                    onEditTextListener.onEditTextFocus((EditText) view, items.get(position));
                        }
                    }
                });
                e.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void afterTextChanged(Editable editable) {
                        setFieldValue(rowFieldMap.get(edittextId), items.get(getAdapterPosition()), editable.toString());
                        if(onEditTextListener != null) onEditTextListener.onEditTextChanged(e, editable.toString());
                    }
                });
                e.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                                if(onEditTextListener != null) return onEditTextListener.onEnterPressed((EditText) view, items.get(getAdapterPosition()));
                            }
                        }
                        return false;
                    }
                });
            }
        }

    }
}
