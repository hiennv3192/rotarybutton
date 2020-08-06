# RotaryButton
Custom view for user who want to have a rotary button like volume button or a seekbar that is round.

![preview](./demo.png)

Installation
-------

Add jitpack.io repository to your root build.gradle:
```groovy
allprojects {
 repositories {
    google()
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```
Add the dependency to your module build.gradle:

`implementation 'com.github.hiennv3192:rotarybutton:1.0.5'`

API
-------

Method | Xml | Description
--- | --- | ---
`setEnabled` | android:enabled | make view enable to interact
`isEnabled` |  | Check view is enable or not
`setProgressBgImgRes` | app:rotary_progressBackgroundDrawable | Set the image for progress's background
`setProgressFgImgRes` | app:rotary_progressForegroundDrawable | Set the image for progress's foreground
`setButtonBgImgRes` | app:rotary_buttonBackgroundDrawable | Set the image for button's background
`setButtonFgImgRes` | app:rotary_buttonForegroundDrawable | Set the image for button's foreground
`setProgressMax` | app:rotary_progressMax | Set max for progress
`getProgressMax` |  | Get max progress
`setProgress` | app:rotary_progress | Set progress
`getProgress` |  | Get current progress
`setMaxRotateDegrees` | app:rotary_maxRotateDegrees | Set the max rotation degrees of button
`setProgressStartDegrees` | app:rotary_progressStartDegrees | Starting angle (in degrees) where the progress begins
`setButtonStartDegrees` | app:rotary_buttonStartDegrees | Set the start point in degrees of button foreground.
`setProgressPadding` | app:rotary_progressPadding | Set padding for progress foreground and progress background
`setButtonBgPadding` | app:rotary_buttonBackgroundPadding | Set padding for button background
`setButtonFgPadding` | app:rotary_buttonForegroundPadding | Set padding for button foreground
`setOnSeekBarChangeListener` |  | Add a listener that will be invoked when the user interacts(start touch, rotate, stop touch) with the view
`setOnClickListener` |  | Add a listener that will be invoked when the user interacts(touch only) with the view

License
-------

    Copyright 2020 Nguyen Van Hien

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.