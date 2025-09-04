package com.example.thepizzamaniaproject.utils;



import android.view.View;
import androidx.databinding.BindingAdapter;

public class BindingUtils {

    @BindingAdapter("visibleIfNotNull")
    public static void setVisibilityNotNull(View view, Object object) {
        view.setVisibility(object != null ? View.GONE : View.VISIBLE);
    }

    @BindingAdapter("visibleIf")
    public static void setVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}