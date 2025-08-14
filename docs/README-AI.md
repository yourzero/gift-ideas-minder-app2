## AI Features Plan, Provider, and Costs

### Scope (from Project Design)
- **AI-generated gift suggestions (6.1)**: UI exists; fetch/dismiss not wired to backend.
- **AI gift picker by budget (6.2)**: not implemented.
- **Conversation scan → insights (6.3)**: SMS extraction exists; AI summarization is stubbed.

### Recommended provider
- **Google Vertex AI – Gemini Flash family**
  - Default: **Gemini 1.5 Flash (002)** for low cost, speed, and long context.
  - Escalate rarely to **Gemini 1.5 Pro** only when stronger reasoning is needed.
  - Alternative (newer preview): **Gemini 2.5 Flash** (slightly higher price; similar usage math below).

### Cost estimates (Gemini 1.5 Flash (002))
- Pricing used: **$0.13 / 1M input tokens**, **$0.38 / 1M output tokens**.
- Cost = inTokens/1e6 × 0.13 + outTokens/1e6 × 0.38.
- Assumptions per user/month:
  - **Suggestions refresh**: 4 calls, ~2k input + 1k output tokens each → ≈ $0.00064 per call.
  - **Budget picker**: 3 calls, ~1.5k input + 0.7k output tokens each → ≈ $0.00046 per call.
  - **SMS summarization**: 1 call, ~50k input + 2k output tokens → ≈ $0.00726 per call.

Estimated monthly totals:
- **Per user** (moderate usage): ≈ $0.0112 (~1.1¢)
- **100 users**: ≈ **$1.12 / month**
- Sensitivity (heavier SMS: 200k in + 3k out once/month):
  - Per user: ≈ $0.031
  - 100 users: ≈ **$3.10 / month**

Reference with Gemini 2.5 Flash (commonly cited): **$0.15 / 1M in**, **$0.60 / 1M out**
- Same workloads → 100 users, moderate: ≈ **$1.42 / month**; heavy SMS: ≈ **$3.73 / month**.

Notes:
- Token counts are approximations of typical prompts/responses; real costs vary with payload size.
- Prices can change by region/provider; verify on your Vertex AI billing page.

### Implementation plan (phased)
1) AI Suggestions (6.1)
- API contract: request includes recent gifts, people context, upcoming events, locale/currency; response returns suggestions with fields used by UI (`title`, `description`, `url`, `imageUrl?`, `estimatedPrice?`, `personId?`, `tags`, `reason`).
- Data: keep suggestions ephemeral in memory and persist only accepted suggestions as `gifts`. Track dismissals via a lightweight table keyed by a stable suggestion hash.
- ViewModel: implement `fetchSuggestions()` and `dismissSuggestion()`; add `isLoading`/`error` UI state.
- UI: reuse `SuggestionsCarousel`; add loading/error empty states; trigger fetch on screen entry.
- Person-centric prioritization: compute a priority per person from upcoming events, open gift count, time since last purchase, and relationship weight; request suggestions per top N persons and show the recipient on each card.

2) Gift Picker by Budget (6.2)
- API: `POST /suggestions/by-budget` (inputs: budget range, person/occasion, count, interests).
- UI: budget sliders/chips, optional person selector; accept/dismiss flow matches (6.1).

3) Conversation Scan → Insights (6.3)
- Source: use existing SMS extraction (opt‑in), optionally other sources.
- API: `POST /summarize/messages` → structured insights per person (interests, avoid list, sizes, dates, notes).
- UX: gated review list to accept/ignore each insight before persisting to the person profile.

4) Quality & Ops
- Price enrichment using `PriceService` for URLs/ASINs.
- Dedupe (by URL/title + fuzzy) against existing `gifts`.
- WorkManager: periodic refresh before upcoming events; backoff on failures.

### Data privacy & consent
- Explicit opt‑in before sending message data; allow opt‑out and deletion of derived insights.
- Minimize payloads (no phone numbers; mask names where feasible).
- Settings toggles gate all AI network calls.

### Code wiring points in this repo
- Network & DI: `app/src/main/java/com/giftideaminder/data/api/AIService.kt`, `app/src/main/java/com/giftideaminder/di/NetworkModule.kt`.
- ViewModels/UI: `GiftViewModel`, `GiftListScreen`, `SuggestionsCarousel` (already renders), `ImportViewModel` (SMS), `HomeDashboardScreen` (entry point for surfacing ideas later).
- DB: `AppDatabase` and optional dismissal table; existing `Suggestion` entity can be repurposed or replaced if you want persistent candidates.

### Milestones
- M1: Suggestions fetch/dismiss wired; accept inserts into `gifts`; manual refresh; basic tests.
- M2: Budget picker endpoint + screen; parity with accept/dismiss; tests.
- M3: Summarization review flow; persist accepted insights to people; suggestions improve via insights.
- M4: Background refresh + notifications; price enrichment; stability/perf pass.

