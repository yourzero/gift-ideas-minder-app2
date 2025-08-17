# Gift Idea Minder Android Project Overview

**Purpose**: Gift Idea Minder is an Android app that helps users capture, organize, and track gift ideas for friends and family with AI-powered suggestions, price tracking, OCR import, and person/relationship management.

**Tech Stack**:
- Kotlin with Jetpack Compose for UI
- Room for persistence 
- Hilt for dependency injection
- MVVM architecture pattern
- ML Kit for OCR text recognition
- Gemini AI API for gift suggestions
- CamelCamelCamel API for price tracking
- Material 3 design system

**Core Features**:
- Person management with 5-step flow (relationship → details → dates → preferences → review)
- Gift management with price tracking and AI suggestions
- OCR import from screenshots/camera
- Event reminders and notifications
- Security with password/biometric lock
- Multi-platform imports (CSV, SMS, wishlists)

**Key Architecture Components**:
- `data/model/`: Room entities (Gift, Person), DAOs, AppDatabase
- `data/repository/`: Repository pattern for data access
- `viewmodel/`: ViewModels for UI state management
- `ui/screens/`: Composable screens
- `ui/components/`: Reusable UI components
- `di/`: Hilt dependency injection modules