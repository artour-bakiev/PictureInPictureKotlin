# Task 1 - Legacy Support
Change the `minSdkVersion` to support API 21 (current 26). Make change so that the app can gracefully support the newer
features when possible with their API levels. You are welcome to make any changes to the UI as you see fit.
We are looking for some thought into UX and why you think some decisions you took might be better for the user. We
aren’t that interested in pixel perfect design, Material components are fine, just some thought to UX is best.

## Problem Description

Lowering `minSdkVersion` to API 21 introduces the following issues:

1. The "Picture-in-picture" (PIP) feature is not fully supported for versions prior to API 26.
2. Certain methods (such as `MediaPlayer::setDataSource(AssetFileDescriptor)` and
   `Icon::createWithResource(Context, int)`) are not accessible for versions prior to API 26.

### PIP Support

We essentially have three types of devices segregated by their PIP support, in accordance with
[the Android documentation](https://developer.android.com/develop/ui/views/picture-in-picture):

1. API 21-23: These devices do not support PIP.
2. API 24-25: They support PIP but lack support for PictureInPictureParams and RemoteAction.
3. API 26+: These devices offer full PIP support.

The segregation should consider both UI differences and runtime behavior.

#### UX Segregation Options

There are several strategies we can employ to handle differences in UX. We can separate the UX screens as follows:

1. By using `Compose`, ensuring that every type of device will have its own `Compose` screen.
2. By utilizing AndroidX `Fragment`s, providing each type of device with its own instance of `Fragment` class.
3. By creating different versions of XML layout files (e.g., `layout-v24` and `layout-v26`).
4. By using different product flavors.

#### Runtime Options

To address differences in runtime behavior, we essentially have the following options:

1. Use a single instance of the activity (both `MainActivity` and `MovieActivity.kt`) and employ runtime
   validation (using `Build.VERSION.SDK_INT`) to choose the execution path.
2. Use a single instance of the activity and a form of a delegate interface to encapsulate API differences inside a
   concrete class that implements the delegate interface.
3. Use different `Activity` classes to encapsulate API differences inside classes that are API-level aware (like
   `MainActivity24` and `MainActivity26`).
4. Employ different product flavors.

#### Solution

#### UX

The assessment has a time limitation, so we can opt for the straightforward solution: when certain UX elements are not
accessible for low API levels, they will not be shown. This includes the minimize button as well as the "Enter
Picture-in-Picture mode" button.

Another option could be employing the system alert window (SAW) to mimic "picture-in-picture" behavior. This way is not
recommended by the Android Google team, but it appears to be the only available option for API 23 and below (assuming we
have pressing requirements from the product management team). However, due to the time limitation, this solution is not
considered suitable.

#### Run-time

Among the various runtime options, it makes sense to choose the option with a delegate interface so that we can create
dedicated implementation classes to support a specific API level, like `PipController24` and `PipController26`. This
approach has its pros and cons but appears to align well with the assessment requirements:

**Pros:**

- It provides a clear codebase differentiation between different API levels.

**Cons:**

- The interface should be comprehensive, encompassing features supported by different API levels.

#### Summary

Another option we haven't mentioned yet is to use different flavors to deliver distinct APK versions to end-users.
However, this approach doesn't seem suitable in the long run, as it can impede distribution through Google Play.

Taking into account the previous sections ("UX" and "Run-time"), the solution will involve:

- Using a different activity for each supported device group.
- Creating various versions of XML layout files for each supported device group.
- For the device group that doesn't support PIP (API 23 and below), the UX elements related to the PIP feature will
  not be shown.

### Methods That Are Not Accessible For Pre-API 26

There are a couple of methods that are not supported in older APIs:

1. `MediaPlayer::setDataSource(AssetFileDescriptor)` - introduced in API 24
2. `Icon::createWithResource(Context, int)` - introduced in API 23

We can replace `MediaPlayer::setDataSource(AssetFileDescriptor)` with the platform code base since it's a trivial task.

We don't have to re-implement `Icon::createWithResource` since it will be used only in classes targeting API 24 and
above.
