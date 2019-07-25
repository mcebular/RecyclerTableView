package com.mc0239.recyclertableview;

import java.util.List;

public interface RecyclerTableViewAdapterInterface<T> {
    T getItem(int index);
    List<T> getItems();

    int getItemCount();

    void addItem(T item);
    void addItemAt(int index, T item);
    void addItems(List<T> items);
    void addItemsAt(int index, List<T> items);

    void removeItem(T item);
    void removeItemAt(int index);
    void removeItems(List<T> items);

    void clearItems();
    void setItems(List<T> items);

    // operations on checkable rows

    int getCheckedItemCount();
    List<T> getCheckedItems();

    void setItemChecked(int index, boolean checked);
    void setAllItemsChecked(boolean checked);

    boolean areAllItemsChecked();

    // operations on rows with edittext

    void setItemEditTextValue(int index, String value);
    void setAllItemsEditTextValue(String value);
}
