# RecyclerTableView

RecyclerTableView allows you to have table-like RecyclerView. Due to how view recycling works, it is
difficult to know how wide the rows should be, therefore requiring usage of fixed width row layouts.

RecyclerTableView's features:

- scrollable both vertically and horizontally (if rows are wider than the available view),
- sticky header row,
- using CheckBoxes or RadioButtons for row selection (clicking header row can (de)select all rows),
- using EditTexts in rows.

Please see [samples folder](https://github.com/mc0239/RecyclerTableView/tree/master/app/src/main/java/com/mc0239/recyclertableviewexample/samples) for example usages of RecyclerTableView. To shortly describe usage, you'll need to:

1. Create a row layout which is basically a LinearLayout (oriented horizontally) with TextViews of fixed layout width. You can also include a CheckBox or a RadioButton. See [row layout samples](https://github.com/mc0239/RecyclerTableView/tree/master/app/src/main/res/layout).
2. Prepare the data for the table: Make an `ArrayList<SparseArray<Object>>`, filling SparseArray with indexes matching the view IDs. For example, if you have a row with id `R.id.myCoolRow`, use `s.put(R.id.myCoolRow, "row content");` (where `s` is a SparseArray that you add to `ArrayList<>`.
3. Instantiate a new RecyclerTableViewAdapter, passing it the layout resource ID and IDs of the TextViews on the given layout.
4. Then, get a reference to RecyclerTableView view and set the layout of the header row by calling `.setHeaderResource()`, set layout manager by calling `.setLayoutManager()` and finally calling `setAdapter()`, passing it the adapter created in previous step.
5. Again, see [samples](https://github.com/mc0239/RecyclerTableView/tree/master/app/src/main/java/com/mc0239/recyclertableviewexample/samples).
