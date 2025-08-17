# Key Features and Flows

## Person Management
- Multi-step Add/Edit Person flow driven by `PersonFlowViewModel`
- Relationship-based date prompting (birthday for friends, anniversary for spouses)
- Custom date support with labels
- Contact import integration with SMS scanning capability

## Gift Management  
- Gift tracking with price history via `PriceRecord` entities
- AI-powered suggestions through `AIService` integration
- OCR import from screenshots using ML Kit Text Recognition
- Budget tracking and spending alerts

## Integration Points
- **AI Service**: Gemini API for gift suggestions (requires `GEMINI_API_KEY` in local.properties)
- **Price Tracking**: CamelCamelCamel API integration for price history
- **ML Kit**: Text recognition for OCR import functionality
- **Contacts**: Device contact integration with permission handling