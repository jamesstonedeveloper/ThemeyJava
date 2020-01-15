package com.jamesstonedeveloper.themeyjava;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatDelegate;

import static com.jamesstonedeveloper.themeyjava.Themey.CircleAnimation.NONE;

public class Themey {

    private static Themey themey;
    private Context context;
    private static String PREFS_NAME = "THEME_CHANGE_PREFS";
    private static String THEME_KEY = "THEME";
    private Bitmap oldThemeSnapshot;
    private Bitmap newThemeSnapshot;
    private Boolean isAnimating = false;
    private ImageView newThemeImageView;
    private ImageView oldThemeImageView;
    private ViewGroup themeLayout;
    private Integer circleAnimation = NONE;
    private boolean shouldKeepTheme;
    private int centerX = 0;
    private int centerY = 0;
    private float elevation = 10;
    private int animationDuration = 1000;
    private int defaultTheme;
    private int currentTheme = 0;

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public void setDefaultTheme(int defaultTheme) {
        this.defaultTheme = defaultTheme;
    }

    public static Themey getInstance() {
        if (themey == null) {
            themey = new Themey();
        }
        return themey;
    }

    public void init(Context context, ViewGroup rootLayout, boolean shouldKeepTheme) {
        this.context = context;
        this.shouldKeepTheme = shouldKeepTheme;

        if (oldThemeSnapshot != null) {
            oldThemeImageView = createImageViewWithBitmap(oldThemeSnapshot);
            oldThemeImageView.setVisibility(View.GONE);
        }
        setRootLayout(rootLayout);
    }

    public void delayedInit(Context context, boolean shouldKeepTheme) {
        this.context = context;
        this.shouldKeepTheme = shouldKeepTheme;

        if (oldThemeSnapshot != null) {
            oldThemeImageView = createImageViewWithBitmap(oldThemeSnapshot);
            oldThemeImageView.setVisibility(View.GONE);
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        currentTheme = sharedPreferences.getInt(THEME_KEY, -1);
        context.setTheme(currentTheme);
    }

    public void setRootLayout(ViewGroup rootLayout) {
        this.themeLayout = rootLayout;
        if (oldThemeSnapshot != null) {
            themeLayout.addView(oldThemeImageView);
            oldThemeImageView.bringToFront();
        }
        initThemeLayout();
    }

    private void initThemeLayout() {
        themeLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                initTheme();
                themeLayout.removeOnLayoutChangeListener(this);
            }
        });
    }

    protected void initTheme() {
        if (oldThemeSnapshot != null) {
            if (circleAnimation == null) {
                circleAnimation = NONE;
            }

            switch (circleAnimation) {
                case -1:
                    //NONE
                    break;
                case 0:
                    //OUTWARD
                    drawAnimationOutwards();
                    break;
                case 1:
                    //INWARD
                    drawAnimationInwards();
                    break;
                default:
            }

        } else {
            if (shouldKeepTheme) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
                int keptTheme = sharedPreferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                if (isDayNightTheme(keptTheme)) {
                    AppCompatDelegate.setDefaultNightMode(keptTheme);
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
    }

    public void toggleDayNight() {
        changeTheme(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
    }

    public void toggleDayNight(int circleAnimation) {
        changeTheme(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES, centerX, centerY, circleAnimation);
    }

    public void toggleDayNight(int circleAnimation, int centerX, int centerY) {
        changeTheme(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES, circleAnimation, centerX, centerY);
    }

    public void toggleDayNight(int centerX, int centerY) {
        changeTheme(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES, centerX, centerY);
    }

    public void changeTheme(int theme) {
        if (!isAnimating) {
            changeTheme(theme, circleAnimation);
        }
    }

    public void changeTheme(int theme, int circleAnimation) {
        if (!isAnimating) {
            changeTheme(theme, circleAnimation, 0, 0);
        }
    }

    public void changeTheme(int theme, int centerX, int centerY) {
        if (!isAnimating) {
            changeTheme(theme, circleAnimation, centerX, centerY);
        }
    }

    public void changeTheme(int theme, int circleAnimation, int centerX, int centerY) {
        if (!isAnimating) {
            if (isDayNightTheme(theme)) {
                if (isCurrentDayNightTheme(theme) && isCurrentCustomTheme(defaultTheme)) {
                    return;
                }
            } else {
                if (isCurrentCustomTheme(theme)) {
                    return;
                }
            }
            this.circleAnimation = circleAnimation;
            this.centerX = centerX;
            this.centerY = centerY;
            applyTheme(theme);
        }
    }

    private void applyTheme(int theme) {
        oldThemeSnapshot = returnScreenshot();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        sharedPreferences.edit().putInt(THEME_KEY, theme).apply();
        currentTheme = theme;

        if (isDayNightTheme(theme)) {
            if (AppCompatDelegate.getDefaultNightMode() == theme) {
                changeTheme(defaultTheme, circleAnimation, centerX, centerY);
                return;
            }
            AppCompatDelegate.setDefaultNightMode(theme);
            if (defaultTheme != -1) {
                changeTheme(defaultTheme, circleAnimation, centerX, centerY);
            }
        } else {
            if (context instanceof Activity) {
                ((Activity) context).recreate();
            }
        }
    }

    private boolean isDayNightTheme(int theme) {
        return theme == AppCompatDelegate.MODE_NIGHT_YES ||
                theme == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY ||
                theme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM ||
                theme == AppCompatDelegate.MODE_NIGHT_NO;
    }

    private Bitmap returnScreenshot() {
        themeLayout.setDrawingCacheEnabled(true);
        themeLayout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        themeLayout.buildDrawingCache();

        if (themeLayout.getDrawingCache() == null) return null;

        Bitmap snapshot = Bitmap.createBitmap(themeLayout.getDrawingCache());
        themeLayout.setDrawingCacheEnabled(false);
        themeLayout.destroyDrawingCache();

        return snapshot;
    }

    private void drawAnimationInwards() {
        oldThemeImageView.setVisibility(View.VISIBLE);
        oldThemeImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Animator animator = ViewAnimationUtils.createCircularReveal(oldThemeImageView, centerX, centerY, getCircleRadius(), 0);
                animator.setDuration(animationDuration);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        themeLayout.removeView(oldThemeImageView);
                        isAnimating = false;
                        resetVariables();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                animator.start();
            }
        });
    }

    private void drawAnimationOutwards() {
        newThemeSnapshot = returnScreenshot();
        oldThemeImageView.setVisibility(View.VISIBLE);
        newThemeImageView = createImageViewWithBitmap(newThemeSnapshot);
        newThemeImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Animator animator = ViewAnimationUtils.createCircularReveal(newThemeImageView, centerX, centerY, 0, getCircleRadius());
                animator.setDuration(animationDuration);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        themeLayout.removeView(newThemeImageView);
                        themeLayout.removeView(oldThemeImageView);
                        isAnimating = false;
                        resetVariables();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                animator.start();
            }
        });
        themeLayout.addView(newThemeImageView);
        newThemeImageView.bringToFront();
    }

    private ImageView createImageViewWithBitmap(Bitmap bitmap) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setElevation(elevation);
        return imageView;
    }

    private float getCircleRadius() {
        double longestRadius = 0.0;
        Pair<Integer, Integer>[] cornerCoordinates = new Pair[]{new Pair(0, 0), new Pair(themeLayout.getWidth(), 0), new Pair(0, themeLayout.getHeight()), new Pair(themeLayout.getWidth(), themeLayout.getHeight())};

        for (Pair<Integer, Integer> pair : cornerCoordinates) {
            int xDifference = pair.first - centerX;
            int yDifference = pair.second - centerY;
            xDifference = Math.abs(xDifference);
            yDifference = Math.abs(yDifference);
            double radius = Math.hypot(xDifference, yDifference);
            if (radius > longestRadius) {
                longestRadius = radius;
            }
        }
        return (float) longestRadius;
    }

    private boolean isCurrentCustomTheme(int theme) {
        return currentTheme == theme;
    }

    private boolean isCurrentDayNightTheme(int theme) {
        return AppCompatDelegate.getDefaultNightMode() == theme;
    }

    private void resetVariables() {
        oldThemeImageView = null;
        newThemeImageView = null;
        newThemeSnapshot = null;
        oldThemeSnapshot = null;
        circleAnimation = NONE;
        centerY = 0;
        centerX = 0;
    }

    public enum CircleAnimation {
        ;
        public static int NONE = -1;
        public static int OUTWARD = 0;
        public static int INWARD = 1;
    }
}