# LiveApp

LiveApp is a comprehensive Android application designed for live streaming enthusiasts. It integrates seamlessly with YouTube's live streaming platform, providing users with tools to manage streams, engage with audiences through real-time chat, and customize their broadcasting experience.

## Features

- **Authentication**: Secure login and profile management using Google OAuth
- **Live Streaming**: Full integration with YouTube Live API for stream management, including scheduling, presets, and real-time monitoring
- **Real-time Chat**: Interactive chat overlay with moderation tools, response templates, and user blocking capabilities
- **Settings**: Customizable app preferences, privacy policies, and user configurations
- **Performance Monitoring**: Built-in tools for network stats, stream quality tracking, and performance optimization
- **Offline Support**: Local data storage for stream history, presets, and scheduled streams

## Architecture

The app follows Clean Architecture principles with MVVM pattern, organized into modular components:

```
app/
├── core/          # Shared utilities and common components
├── domain/        # Business logic layer (use cases, entities, repositories)
├── data/          # Data access layer (repositories, data sources, database)
└── features/      # Feature modules
    ├── auth/      # Authentication feature
    ├── streaming/ # Live streaming feature
    ├── chat/      # Real-time chat feature
    └── settings/  # User settings feature
```

### Architecture Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │     Domain      │    │      Data       │
│   (UI Layer)    │◄───│  (Business Logic)│◄───│  (Data Layer)   │
│                 │    │                 │    │                 │
│ • Activities    │    │ • Use Cases     │    │ • Repositories  │
│ • Fragments     │    │ • Entities      │    │ • Data Sources  │
│ • ViewModels    │    │ • Repositories  │    │ • DTOs          │
│ • Compose UI    │    │   Interfaces    │    │ • Mappers       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Dependency    │
                    │   Injection     │
                    │   (Hilt)        │
                    └─────────────────┘
```

## Prerequisites

- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK API level 21+ (minimum)
- Google Cloud Console account for API setup

## Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/liveapp.git
   cd liveapp
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. **Configure API Keys:**
   - Follow the [API Setup Guide](docs/api-setup.md) to configure YouTube Data API v3 and Google OAuth

4. **Build the project:**
   ```bash
   ./gradlew build
   ```

5. **Run the app:**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## API Setup

For detailed instructions on setting up the required APIs, see the [API Setup Guide](docs/api-setup.md).

## Build and Deployment

- **Build Scripts**: Refer to [Build Configuration](docs/build-scripts.md) for Gradle configurations
- **CI/CD**: Automated testing and building via [GitHub Actions](.github/workflows/ci.yml)
- **Deployment**: See the [Deployment Guide](docs/deployment.md) for Google Play Store submission

## Documentation

- [API Setup Guide](docs/api-setup.md)
- [Build Scripts](docs/build-scripts.md)
- [Deployment Guide](docs/deployment.md)
- [Troubleshooting](docs/troubleshooting.md)
- [Contributing Guidelines](docs/contributing.md)

## Contributing

We welcome contributions! Please see our [Contributing Guidelines](docs/contributing.md) for details on how to get started.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, please open an issue on GitHub or contact the development team.