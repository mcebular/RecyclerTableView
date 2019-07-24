package com.mc0239.recyclertableview.annotation;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RecyclerTableRow {
    /**
     * @return Layout resource for the row
     */
    @LayoutRes int value();
    @IdRes int checkboxViewId() default 0;
    @IdRes int edittextViewId() default 0;
}
