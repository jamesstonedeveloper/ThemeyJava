# ThemeyJava

A simple library to easily switch between themes or day/night modes in your app. Circle reveal animations included for that extra bit of style. [Click here for the kotlin version](https://github.com/jamesstonedeveloper/ThemeyKotlin "ThemeyKotlin")

<img src="https://github.com/jamesstonedeveloper/ThemeyJavaDemo/blob/master/themeydemo.gif" height="600" />

## Gradle

[![](https://jitpack.io/v/jamesstonedeveloper/ThemeyJava.svg)](https://jitpack.io/#jamesstonedeveloper/ThemeyJava)

[Click here for the demo app](https://github.com/jamesstonedeveloper/ThemeyJavaDemo "Themey java demo")

Add jitpack to your project gradle file
``` 
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Then in your app gradle file

```
dependencies {
    ...
    implementation 'com.github.jamesstonedeveloper:ThemeyJava:1.0.5'

}
```

## Requirements

* API level 21
* To prevent your screen going black as the themes change you'll need to add this to your activity's theme:
`<item name="android:windowAnimationStyle">@style/ThemeyAnimationTransition</item>`

## Set up

### For just day/night changing

In your activity's onCreate method, after setting the content view, initialise the Themey class by providing context, your activity's root layout and whether you want the app to remember theme changes or not (these are stored in shared preferences)
```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.mainLayout); //your activity's root layout
        Themey.getInstance().init(this, mainLayout, true); //initialise 
    }
```

### For using cutom themes
Note: you can interchangably switch between custom themes and day/night mode if set up this way

In your activity's onCreate method, BEFORE setting the content view, you need to run a `delayedInit()` on the Themey class by providing context and a boolean for if you want the app to remember theme changes
Then AFTER setting the content view, set your activity's root layout with `setRootLayout()`. Finally, provide your app's default theme with `setDefaultTheme()`

```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themey.getInstance().delayedInit(this, true); //DELAYED init BEFORE setContentView()
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.mainLayout); 
        Themey.getInstance().setRootLayout(mainLayout);  // set root layout AFTER setContentView()
        Themey.getInstance().setDefaultTheme(R.style.AppTheme);
    }
```


## Usage

There are 2 main methods to use: `changeTheme()` and `toggleDayNight()`. `changeTheme()` is used to specify the theme to change to whereas `toggleDayNight()` will always switch to the opposite day/night theme.
You can use these methods with a few parameters, although `toggleTheme()` doesn't need any parameters. These methods take:
* `theme` as an int. This can be a day/night theme or a custom style (e.g `AppCompatDelegate.MODE_NIGHT_YES` or `R.style.GreenTheme`) - This is only used by `changeTheme()` methods
* `circleAnimation` as an int, enums have been provided (`INWARD`, `OUTWARD` and `NONE`)
* `centerx` and `centerY` as two ints. These are used to determine where the circle animation will start or end on the screen

You can provide any combination of those parameters, although `changeTheme()` requires a `theme` to be passed. If not provided, circle animation will default to `NONE` and `centerX` and `centerY` will default to 0 and 0

You can also change the animation speed by using `Themey.getInstance().setAnimationDuration()`;

## Examples

Just toggle the day/night theme:
```java 
Themey.getInstance().toggleDayNight();
```

Toggle day/night theme with an outwards animation:
```java
Themey.getInstance().toggleDayNight(OUTWARDS);
```

Toggle day/night theme with an outwards animation at the center of the screen:
```java
int x = getView().getWidth();
int y = getView().getWidth();
Themey.getInstance().toggleDayNight(OUTWARDS, x / 2, y / 2);
```

Change the theme to your custom style:
```java
Themey.getInstance().changeTheme(R.style.GreenTheme);
```

Change theme to you custom style with an inwards animation starting at the middle of the screen:
```java
int x = getView().getWidth();
int y = getView().getWidth();
Themey.getInstance().changeTheme(R.style.GreenTheme, INWARD, x / 2, y / 2);
```

## Limitations

* An image is placed over the activity to allow for the circle animation. This image has an elevation of 10 by default, but if your view has anything with an elevation higher than 10 you'll need to use the setElevation() method provided to increase the images' elevation
* Does not work well with android ActionBars/Toolbars
