# Gift Idea Minder – Project Design

## Epics & Stories

Legend: [✓] Complete · [~] Partial · [◻] Not implemented

### Epic 1: UI/UX Foundations
1. [✓] **1.1 Set up Jetpack Compose & theme**
2. [✓] **1.2 Design system & style guide**
3. [✓] **1.3 Base layout components (Scaffold, nav, FAB…)**

### Epic 2: Gift Idea Capture
1. [✓] **2.1 Manual entry screen**
2. [~] **2.2 Share-from-other-apps integration** (Manifest filter present; Activity does not navigate to `add_gift?sharedText=...`)
3. [✓] **2.3 OCR from screenshots**
4. [✓] **2.4 SMS-tap capture** (in-app SMS extraction)
5. [~] **2.5 File import (CSV/PDF/Doc/Spreadsheet)** (CSV import only; PDF/Doc/Spreadsheet not implemented)

### Epic 3: Giftee Management
1. [✓] **3.1 Manage Giftees screen**
2. [✓] **3.2 Add/Edit Giftee form**
3. [✓] **3.3 Import from Contacts**
4. [~] **3.4 AI message history scan for giftees** (opt-in SMS scan wired; AI summarization is stubbed)
5. [✓] **3.5 Relationship-first Add/Edit Giftee flow (wizard)**
   - Relationship → Details → Dates → Preferences → Review
   - Important dates prompts driven by `RelationshipType` flags
   - Typed dates with dropdown (Birthday/Anniversary/Graduation/First Met/Valentine's Day/Mother's Day/Father's Day/Custom), inline edit/remove

Screenshots (place under `docs/images/add-edit-giftee/`):
- `step1-relationship.png`
- `step2-details.png`
- `step3-dates-prompts.png`
- `step3-dates-custom.png`
- `step4-review.png`

### Epic 4: Gift Event Management
1. [✓] **4.1 Dashboard “Upcoming Gift Events”**
2. [◻] **4.2 Event Detail screen**
3. [◻] **4.3 Add/Edit Event form**

### Epic 5: Gift Browsing & Organization
1. [◻] **5.1 Browse by Occasion screen**
2. [◻] **5.2 Gift event planning & categorization UI**
3. [✓] **5.3 Budgeting & spend tracking**

### Epic 6: AI-Driven Features
1. [~] **6.1 AI-generated gift suggestions** (person-centric fetch/dismiss wired; backend prompt may need tuning)
2. [◻] **6.2 AI gift picker by budget**
3. [✓] **6.3 Scan existing conversations (opt-in)**

### Epic 7: Price Tracking & Comparison
1. [◻] **7.1 Sale alert detection**
2. [◻] **7.2 Price comparison UI**
3. [◻] **7.3 CamelCamelCamel integration for price history**

### Epic 8: Security & Settings
1. [◻] **8.1 Password protection (app or per-list)**
2. [◻] **8.2 Settings screen**

---

## Sprint Progress

### Completed Sprints
1. **Sprint 1**: ✅ Epic 1 complete; Epic 2 mostly complete (2.2/2.5 partial)
2. **Sprint 2**: ✅ Epic 3 mostly complete (3.4 partial)

### Next Prioritized Sprints
3. **Sprint 3**: Epic 4 + remaining Epic 5 items
4. **Sprint 4**: Epics 6 + 7 completion
5. **Sprint 5**: Epic 8 + polish

### Current Status
- **Completed**: UI foundations; manual gift entry; OCR import; SMS extraction; giftee management including relationship-first flow and contacts; budgeting; dashboard list of upcoming gift events.
- **In Progress**: Share intent routing to Add Gift; non-CSV file imports; AI suggestions backend; Event detail and Add/Edit Event screens; browse/categorization UI.
- **Next**: Finish Epic 4 screens; complete Epic 2/3/5 partials; implement AI picker by budget; wire price tracking; add settings/security UI.
