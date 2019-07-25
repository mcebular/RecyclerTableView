package com.mc0239.recyclertableview;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
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

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
public class RecyclerTableViewAdapter<ItemType> extends RecyclerView.Adapter<RecyclerTableViewAdapter.ViewHolder> implements RecyclerTableViewAdapterInterface<ItemType> {

    private List<ItemType> items;
    private HashMap<Integer, Field> rowFieldMap;
    private HashMap<Field, Method> fieldDisplayMap;

    @LayoutRes private int rowLayout;
    @IdRes private int checkboxId;
    @IdRes private int edittextId;

    boolean multipleCheckable;

    //
    // CONSTRUCTORS
    //

    /**
     * Create a new adapter that gets row layout and column view ids from given class, which should
     * be properly annotated with {@link RecyclerTableRow} and given class fields annotated with
     * {@link RecyclerTableColumn}.
     * @param rowClass Class to use as table row data
     * @param items List of rows of type class
     */
    public RecyclerTableViewAdapter(Class<ItemType> rowClass, List<ItemType> items) {
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
    public RecyclerTableViewAdapter(Class<ItemType> rowClass, List<ItemType> items, @LayoutRes int rowLayout, @IdRes int checkboxId, @IdRes int edittextId) {

        if (rowLayout != 0) {
            // get layouts from constructor arguments if rowLayout is defined
            this.rowLayout = rowLayout;
            this.checkboxId = checkboxId;
            this.edittextId = edittextId;

        } else {
            // get layout IDs from given class' annotations
            if (!rowClass.isAnnotationPresent(RecyclerTableRow.class)) {
                throw new RuntimeException("Class not annotated with RecyclerTableRow and no rowLayout specified.");
            } else {
                RecyclerTableRow aRow = rowClass.getAnnotation(RecyclerTableRow.class);
                this.rowLayout = aRow.value();
                this.checkboxId = aRow.checkboxViewId();
                this.edittextId = aRow.edittextViewId();
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
    // GETTERS
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

    public boolean isMultipleCheckable() {
        return multipleCheckable;
    }


    //
    // ADAPTER OVERRIDES
    //

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerTableViewAdapter.ViewHolder holder, int position) {
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
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerTableViewAdapter.ViewHolder holder) {
        if(onRowBindListener != null) onRowBindListener.onRowRecycled(holder.itemView);
    }

    //
    // ADAPTER OVERRIDES
    //

    @Override
    public ItemType getItem(int index) {
        return items.get(index);
    }

    @Override
    public List<ItemType> getItems() {
        return items;
    }

    @Override
    public void addItem(ItemType item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    @Override
    public void addItemAt(int index, ItemType item) {
        items.add(index, item);
        notifyItemInserted(index);
    }

    @Override
    public void addItems(List<ItemType> items) {
        this.items.addAll(items);
        notifyItemRangeInserted(this.items.size(), items.size());
    }

    @Override
    public void addItemsAt(int index, List<ItemType> items) {
        this.items.addAll(index, items);
        notifyItemRangeInserted(index, items.size());
    }

    @Override
    public void removeItem(ItemType item) {
        items.remove(item);
        notifyItemRemoved(items.indexOf(item));
    }

    @Override
    public void removeItemAt(int index) {
        items.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public void removeItems(List<ItemType> items) {
        this.items.removeAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public void setItems(List<ItemType> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCheckedItemCount() {
        return getCheckedItems().size();
    }

    @Override
    public List<ItemType> getCheckedItems() {
        ArrayList<ItemType> checkedItems = new ArrayList<>();
        for (ItemType item : items) {
            if ((Boolean) getFieldValue(getCheckableField(), item)) {
                checkedItems.add(item);
            }
        }
        return checkedItems;
    }

    @Override
    public void setItemChecked(int index, boolean checked) {
        setFieldValue(getCheckableField(), items.get(index), checked);
        notifyItemChanged(index);
    }

    @Override
    public void setAllItemsChecked(boolean checked) {
        for(ItemType item : items) {
            setFieldValue(getCheckableField(), item, checked);
        }
        notifyDataSetChanged();
    }

    @Override
    public boolean areAllItemsChecked() {
        return getItemCount() > 0 && getCheckedItemCount() == getItemCount();
    }

    @Override
    public void setItemEditTextValue(int index, String value) {
        for(Object row: items) {
            setFieldValue(getEditableField(), row, value);
        }
        notifyItemChanged(index);
    }

    @Override
    public void setAllItemsEditTextValue(String value) {
        for(int i = 0; i < items.size(); i++) {
            setItemEditTextValue(i, value);
        }
    }

    //
    // LISTENERS
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
    // HELPER METHODS
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

    private @NonNull Field getCheckableField() {
        if (checkboxId == 0) throw new NotCheckableException();
        Field f = rowFieldMap.get(checkboxId);
        if (f == null) throw new NotCheckableException();
        return f;
    }

    private @NonNull Field getEditableField() {
        if (edittextId == 0) throw new NotEditableException();
        Field f = rowFieldMap.get(edittextId);
        if (f == null) throw new NotEditableException();
        return f;
    }

    //
    // VIEWHOLDER IMPLEMENTATION
    //

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setListeners(itemView);
        }

        private void setListeners(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(checkboxId != 0) {
                        if(multipleCheckable) {
                            boolean checked = (boolean) getFieldValue(rowFieldMap.get(checkboxId), items.get(position));
                            setFieldValue(getCheckableField(), items.get(position), !checked);
                            notifyItemChanged(position);
                        } else {
                            setAllItemsChecked(false);
                            setFieldValue(getCheckableField(), items.get(position), true);
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
                        setFieldValue(getEditableField(), items.get(getAdapterPosition()), editable.toString());
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

    /*

    //



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
}