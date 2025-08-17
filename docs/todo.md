# Done
## Gifts
- gifts screen:
  - add gift:
    - "pick date" should prompt the user with the dates that have been set for the recipient, not prompt for a date itself (at least not initially)
      - the user can also just enter a date directly, along with a label/occasion, and this will be added to the recipient's saved dates
- gift ideas generation:
  - this doesn't seem to work - it only displays the first log (pseudo-work) line and never does anything else.
  - add debug logging to the communication with gemini functionality
- gift details screen:
  - along with the gift suggestions carousel, the link to the gift either isn't saved or goes to an image. The link (clicking anywhere on the gift card besides the buttons) should take them to the web page the gift is on (the product page)
  - there need to be a back arrow/button like other screens
  - layout/style the screen much better, considering the existing style/layout of the app and other screens
- gift suggestion card
  - change the link to be accessible via a globe (www) icon, instead of having the whole card be clickable

# TODO
## Gifts
- gifts screen:
  - add gift:
    - change the add gift flow screen to clearly indicate when one of the saved dates is selected (the button itself); 
    - in fact, change the whole "pick custom date" and "add & use date" to one "add date" button. this button will popup a dialog similar to the current "add & use date", but with a date picker too. also include a checkbox to save the date to the person's profile (unless the data model is structured so that a date can't be associated with a gift unless it's saved - if that's the case, then ignore the checkbox and always save it)
      - after this custom date is added, ensure it is automatically selected on the screen
