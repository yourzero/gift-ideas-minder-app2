# Project Roadmap

Based on the project design, the following features are planned for future implementation:

## Epic 4: Gift Event Management
- Event Detail screen for viewing specific gift events
- Add/Edit Event form for creating and modifying gift events

## Epic 5: Gift Browsing & Organization
- Browse by Occasion screen for filtering gifts by event type
- Enhanced gift event planning and categorization UI

## Epic 6: AI-Driven Features (Remaining)
- AI gift picker by budget for budget-constrained suggestions

## Epic 7: Price Tracking & Comparison
- Sale alert detection for monitored gifts
- Price comparison UI for cross-platform price viewing
- Full CamelCamelCamel integration for comprehensive price history

## Epic 8: Security & Settings
- Password protection (app-level or per-list)
- Comprehensive settings screen for app configuration

## Known Limitations

The following features are partially implemented and may need completion:

### Gift Capture
- **Share intent integration**: Manifest filter exists but Activity doesn't navigate to `add_gift?sharedText=...`
- **File import**: Only CSV import implemented; PDF/Doc/Spreadsheet import not yet available

### Person Management
- **AI message history scan**: Opt-in SMS scan is wired but AI summarization is stubbed

## Future Integrations

The following external services and APIs are planned for integration:

### Enhanced AI Services
- Improved Gemini API prompts for better gift suggestions
- AI-powered budget-based gift recommendations

### Price Tracking APIs
- Full CamelCamelCamel API integration for price history tracking
- Additional price comparison services for comprehensive coverage

### File Processing
- Extended file import capabilities (PDF, Word documents, Excel spreadsheets)
- Enhanced OCR processing for complex document layouts

## Security Considerations

Future security implementations will include:

### App Protection
- Biometric authentication support
- PIN/password protection options
- Per-list security settings for sensitive gift information

### Data Privacy
- Secure storage of personal information
- Privacy controls for AI features and external service integration
- Optional data encryption for sensitive gift and person data