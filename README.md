# IncrementProductView

[![Made in SteelKiwi](https://github.com/steelkiwi/IncrementProductView/blob/master/assets/made_in_steelkiwi.png)](http://steelkiwi.com/blog/)
[ ![Download](https://api.bintray.com/packages/soulyaroslav/maven/increment-product-view/images/download.svg) ](https://bintray.com/soulyaroslav/maven/increment-product-view/_latestVersion)
# Description

Interesting concept of products incrementation inspired this [Design](https://dribbble.com/shots/1769468-Product-Animation)

# View

![Animation](https://github.com/steelkiwi/IncrementProductView/blob/master/assets/animation.gif)

# Download

For project API 21+.
For arc animation used this library components [ArcAnimator](https://github.com/asyl/ArcAnimator)

## Gradle

```gradle
compile 'com.steelkiwi:increment-product-view:1.2.0'
```

# Usage

Add IncrementProductView to your xml layout

```xml
<com.steelkiwi.library.IncrementProductView
    android:id="@+id/productView"
    android:layout_width="200dp"
    android:layout_height="200dp"
    android:background="@android:color/transparent"
    android:layout_centerInParent="true"
    app:ipv_middle_icon="@drawable/box"
    app:ipv_highlight_background_color="@color/highlight_background_color"
    app:ipv_default_background_color="@color/default_background_color"
    app:ipv_text_color="@android:color/white"
    app:ipv_text_size="@dimen/text_size"/>
```

You can customize view, through this attributes

    * app:ipv_middle_icon - main view icon
    * app:ipv_highlight_background_color - background color when view is expand
    * app:ipv_default_background_color - background color when view is idle
    * app:ipv_text_color - counter text color
    * app:ipv_text_size - counter text size
    * app:ipv_add_icon - icon for expand board view state
    * app:ipv_decrement_icon - icon for decrease view
    * app:ipv_increment_icon - icon for increase view
    * app:ipv_confirm_icon - icon for confirmation view state
    * app:ipv_counter_background_color - background color for board view

Inside your client code need to implement listener OnStateListener to handle view state.
IncrementProductView state:

    * onCountChange(int count) - call after each incrementation
    * onConfirm(int count) - call after click on confirmation view
    * onClose() - call after closing board view



# License

```
Copyright © 2017 SteelKiwi, http://steelkiwi.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
