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
 