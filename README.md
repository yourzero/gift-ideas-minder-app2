# Gift Idea Minder Android App

A mobile application for managing and planning gift ideas, featuring persistence, reminders, integrations, and AI‑powered suggestions.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Epics & User Stories](#epics--user-stories)

    * [Epic: Gift Management](#epic-gift-management)
    * [Epic: Person Management](#epic-person-management)
    * [Epic: Reminders & Notifications](#epic-reminders--notifications)
    * [Epic: Integrations & Import](#epic-integrations--import)
    * [Epic: Price Tracking & Budgeting](#epic-price-tracking--budgeting)
    * [Epic: Security & Privacy](#epic-security--privacy)
    * [Epic: AI‑Driven Suggestions](#epic-ai-driven-suggestions)
3. [Architecture & Modules](#architecture--modules)
4. [Getting Started](#getting-started)
5. [Future Backlog](#future-backlog)

---

## Project Overview

`Gift Idea Minder` helps users capture, organize, and track gift ideas for friends and family. Core features include:

* **Persistent storage** of gifts and recipients via Room
* **Fast entry UI** with form validation
* **OCR import** from screenshots or camera
* **Event reminders** and notifications
* **Price tracking** and sale alerts (CamelCamelCamel integration)
* **Budgeting** and event categorization
* **Security** via password or biometric lock
* **AI‑powered suggestions** based on past ideas and external data (now person‑centric; prioritized by upcoming events and recipient context)

---

## Epics & User Stories

### Epic: Gift Management

As a user, I want to add, view, and delete gift ideas so I can keep track of potential presents.

* **Story GM-1**: Add a new gift with title, description, URL, price, date, and assigned person.
* **Story GM-2**: Display a list of saved gifts sorted by event date.
* **Story GM-3**: View details of a gift, including link and notes.
* **Story GM-4**: Delete or edit an existing gift.

### Epic: Person Management

As a user, I want to manage my gift recipients so I can assign ideas to the right people.

* **Story PM-1**: Add a new person via a 5‑step flow (Relationship → Details → Dates → Preferences → Review), with typed dates and Gift Inspirations.
* **Story PM-2**: Select or change a gift’s assigned person from a dropdown.
* **Story PM-3**: View all persons and their upcoming birthdays.

### Epic: Reminders & Notifications

As a user, I want to be notified before important gift events so I don’t forget to purchase.

* **Story RN-1**: Configure default reminder offsets (e.g., 7 days before).
* **Story RN-2**: Schedule local notifications via WorkManager.
* **Story RN-3**: View upcoming reminders in-app.

### Epic: Integrations & Import

As a user, I want to quickly capture gift ideas from other sources.

* **Story II-1**: Use OCR (ML Kit or Tesseract) to import text from screenshots.
* **Story II-2**: Import from CSV or spreadsheet files.
* **Story II-3**: Extract gift suggestions from SMS or chat logs (user‑selected).
* **Story II-4**: Import wishlist items via selectable UI checkboxes.

### Epic: Price Tracking & Budgeting

As a user, I want to track prices and manage budgets so I can optimize spending.

* **Story PB-1**: Integrate CamelCamelCamel API to fetch price history.
* **Story PB-2**: Show current price vs. saved price and highlight deals.
* **Story PB-3**: Tag gifts with budgets or events and track total spent.
* **Story PB-4**: Alert when spending approaches a budget limit.

### Epic: Security & Privacy

As a user, I want to protect my gift data from unauthorized access.

* **Story SP-1**: Add password lock for the app or selected lists.
* **Story SP-2**: Integrate biometric authentication (fingerprint, face).
* **Story SP-3**: Store sensitive data encrypted (Jetpack Security).

### Epic: AI‑Driven Suggestions

As a user, I want personalized gift recommendations so I can discover new ideas.

* **Story AI-1**: Send user’s gift/person data to backend AI endpoint for suggestions.
* **Story AI-2**: Display a “Today’s Suggestions” carousel on Dashboard.
* **Story AI-3**: Allow users to accept or dismiss AI suggestions.

---

## Architecture & Modules

* **`data/model/`**: Room entities (`Gift`, `Person`), DAOs (`IGiftDao`, `IPersonDao`), `AppDatabase`
* **`data/repository/`**: `GiftRepository`, `PersonRepository`
* **`viewmodel/`**: `GiftViewModel`, `PersonViewModel`
* **`di/`**: Hilt modules (`DatabaseModule`, `RepositoryModule`)
* **`ui/screens/`**: Composables for Dashboard, AddGift, AddPerson (future)
* **`ui/navigation/`**: NavHost and route definitions
* **`ui/components/`**: Reusable UI pieces (cards, dialogs)

---

## Getting Started

1. **Clone** this repo and open the `android/` folder in Android Studio.
2. Ensure you have a **root-level** `settings.gradle.kts` and `build.gradle.kts`.
3. **Sync** Gradle → **Run** the app on an emulator or device.
4. **Add** your own API keys or configure backend URLs in `local.properties` or `strings.xml` as needed.

---

## Future Backlog

Consider moving large feature lists (epics, stories) into a separate `BACKLOG.md` when the list grows.

* Wishlist sharing / social features
* Calendar integration (Google Calendar sync)
* Dark mode theming
* Multi‑language support
* Unit & instrumentation tests for all ViewModels and DAOs

---

*This README provides a developer‑focused overview, feature breakdown, and roadmap to help both human and AI collaborators understand and contribute to the project.* 