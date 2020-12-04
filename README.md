# prototyping-android

This app is to be used in conjunction with the 
[prototyping-server](https://github.com/romrell4/prototyping-server) backend.
This app is a thin client that allows the user to register a widget from 
the Firebase Firestore, and will display a UI that can send events to the server
and receive events from the server. See the server's README for details about 
the system as a whole.

## Setup

The only piece of setup necessary is to download the google_services.json file
and place it in the `app` folder to allow the app to interact with Firebase. 
This can be downloaded from the [firebase console](https://console.firebase.google.com/u/0/project/prototyping-a7600/settings/general/android:com.romrell4.prototyping).
If you aren't able to view the project, please reach out to Eric Romrell or 
Mike Jones, who should be able to add you as a member 
[here](https://console.firebase.google.com/u/0/project/prototyping-a7600/settings/iam).

## Example Usage

![usage](./readme_resources/android_only.gif)

## Widget Types

### Currently Supported Types

Currently, the app was built to support four different types of widgets, each 
type supporting certain incoming and outgoing events:
* **button**: This widget provides a single simple button
    * Inbound events:
        * `UPDATE_BUTTON_TEXT`: when this event is received, the text of the 
        button will update to reflect whatever text was sent in the *message*
        of the event.
    * Outbound events:
        * `BUTTON_TAPPED`: this event is fired when the button is tapped. No
        *message* is sent with the event.
* **slider**: This widget provides a progress slider
    * Inbound events:
        * `UPDATE_PROGRESS`: when this event is received, the progress of the 
        slider will update to reflect whatever integer is sent in the *message*
        of the event.
    * Outbound events:
        * `PROGRESS_UPDATED`: this event is fired whenever the slider progress
        is changed.
* **speaker**: This widget simply can use it's speaker to announce a message
    * Inbound events:
        * `SPEAK`: when this event is received, the device will use a 
        text-to-voice system to speak whatever text was sent in the *message*
        of the event.
    * Outbound events: None
* **text**: This widget provides an editable text field
    * Inbound events:
        * `UPDATE_TEXT`: when this event is received, the text field will 
        be overwritten by whatever *message* is sent in the event.
    * Outbound events:
        * `TEXT_UPDATED`: this event is fired whenever the text is updated.
        However, a threshold is set so that it will only send an update after
        3/4 of a second have passed without new changes.

### Creating a New Widget Type

Creating widget types is meant to be as simple as possible. However, there are
obviously additions that need to be made in order to support a new type.

Within the Android code, you will need to create a new Fragment that extends
the `BaseFragment` class, overriding the necessary values. Within that fragment,
you will do whatever logic is necessary to both handle and raise events. In order
to handle an event, place whatever code is necessary into the overridden 
`handleEvent` function. To raise an event, simply call the `sendEvent` function.

Within the server's code, you'll need to create a new template with stubs that
can handle any outbound events the widget may raise to the server. For instance,
if I were to create a new *Microphone* widget that could raise an 
`AUDIO_TEXT_RECORDED` event, I would need to create a `templates/microphone.py`
file to stub that event.
```
def audio_text_recorded(widgets, state, recorded_text):
    pass
``` 

By creating this stub, the server GUI should allow the system designer to select
this new widget type when creating a widget. Once the server has created the widget,
the Android app should allow you to register as that widget.

