* gifts screen
  - don't do suggestions for everyone (at least not yet)
  - UI sections:
    - upcoming gifts
    - gifts by person
      - this screen will have suggestions
      - gift suggestions:
        - make sure image is displayed
        - provide a link to the gift (ensure it opens in a new window / doesn't lose current app screen state)
  - changes:
    - "add gift flow"
      - don't allow clicking of next button until person is selected
      - Pick Date button doesn't work
      - can't type into gift idea field
      - change "create gift" button to "save"
      - align "back" and "save" buttons to the same layout as add/edit recipient flow
* all add/edit screens
  - clicking on one of the main (bottom) nav bar icons will take the user out of the current screen, after prompting if they want to cancel
  - each time the add/edit screen is reopened (from the + button, not from being popped off the back stack), it should clear all selections. This might be inherently fixed by the previous change.
    -- above should have been implemented now

  - gift suggestions screen:
    - ensure image loading works
    - have each gift item link to the URL of the gift
    - for each gift item, change the accept and decline buttons to be graphical - use whatever style is appropriate to the current style of this app
    - upon hitting the accept button, the UI of the gift item should change (allow decline, but show that accept has been hit)
  - gifts list screen
    - clicking on a gift idea crashes (I assume it's not wired up to the edit screen)


new:
- gift suggestions screen:
  - while the gift suggestions are loading from the ui, add pseudo work text - like when an app's loading screen scrolls a bunch of messages that make it sound like it's doing work, but they're probably made up. Except in this case, include the real gift ideas being sent, as in "searching for gift ideas for: x, y, z"; but also include other pseudo-work messages, and just scroll through them in the loading area until it's done. Also add a "ding" sound when it's done. Also this pseudo-work text should be small and grey-ish (to make it look like it's in the background)
    - add a setting that will switch between the pseudo-work text described, and displaying the actual prompt being sent to the AI (and make it look and scroll like the pseudo-work text). I'll use the prompt for debugging

- add a description/instructions to important areas
  - such as: gift idea (in recipient edit)
    - this description text would be something like "enter as many things that this recipient likes, to help generate gift ideas"
  - use whatever is currently the standard (appropriate to the app's style) way of showing help text - whether it's in smaller text in the same box, or a question mark button, or something else
- recipients screen
  - each recipient card says "no birthday set" - most recipients currently have a birthday, so this is incorrect
  - change each card button from text to icons - use a lightbulb or similar for the "ideas" button
- on all screens, if i click on a button on the main bottom nav bar, it should reset (or forgot) the state of the screen it was in, so that when i come back to that screen via the nav bar, it shows the main screen it would usually show by default. For example, if I click on recipients->edit one, then i click on the home nav button, then i click on the recipients button again, it currently shows where i was (editing a person) - instead if want it to display the recipients screen like it does by default 
- 