# RecyclerTableView

[![](https://jitpack.io/v/mc0239/RecyclerTableView.svg)](https://jitpack.io/#mc0239/RecyclerTableView)

![Sample screenshots](https://raw.githubusercontent.com/mc0239/RecyclerTableView/2.x/screenshot.jpg)

RecyclerTableView allows you to have table-like RecyclerView. Due to how view recycling works, it is
difficult to know how wide the rows should be, therefore requiring usage of fixed width row layouts.

Note: with release of 2.0.0, library uses AndroidX dependencies.

RecyclerTableView's features:

- scrollable both vertically and horizontally (if rows are wider than the available view),
- works with Lists or LiveData,
- sticky header row,
- using CheckBoxes or RadioButtons for row selection (clicking header row can (de)select all rows),
- using EditTexts in rows.

Please see [samples folder](https://github.com/mc0239/RecyclerTableView/tree/master/app/src/main/java/com/mc0239/recyclertableviewexample/samples) for example usages of RecyclerTableView. To shortly describe usage, you'll need to:

1. Create a row layout which is basically a LinearLayout (oriented horizontally) with TextViews of fixed layout width. You can also include a CheckBox or a RadioButton. See [row layout samples](https://github.com/mc0239/RecyclerTableView/tree/2.x/app/src/main/res/layout).
2. Make a row "entity": create a class with `@RecyclerTableRow` and `@RecyclerTableColumn(R.id.column_ID)` annotations. See [row object samples](https://github.com/mc0239/RecyclerTableView/tree/2.x/app/src/main/java/com/mc0239/recyclertableviewexample/rows). You can easily use your existing Room entities ([sample here](https://github.com/mc0239/RecyclerTableView/blob/2.x/app/src/main/java/com/mc0239/recyclertableviewexample/database/User.java))!
3. Instantiate a new `RecyclerTableViewAdapter`.
4. Then, get a reference to `RecyclerTableView` view and set the adapter by calling `setAdapter()`, passing it the adapter created in previous step.
5. Again, see [samples](https://github.com/mc0239/RecyclerTableView/tree/2.x/app/src/main/java/com/mc0239/recyclertableviewexample/samples).
