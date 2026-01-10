# Contributing Guidelines

Welcome to LiveApp! We appreciate your interest in contributing to our project. This document provides guidelines and best practices for contributing to the codebase.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Code Style and Standards](#code-style-and-standards)
- [Testing](#testing)
- [Documentation](#documentation)
- [Pull Request Process](#pull-request-process)
- [Commit Guidelines](#commit-guidelines)
- [Issue Reporting](#issue-reporting)

## Code of Conduct

We are committed to providing a welcoming and inclusive environment for all contributors. Please:

- Be respectful and inclusive in all interactions
- Focus on constructive feedback
- Help create a positive community
- Report any unacceptable behavior to the maintainers

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11 or higher
- Git
- Google Cloud Console account (for API setup)

### Setup

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/your-username/liveapp.git
   cd liveapp
   ```
3. Set up the project following the [README](README.md) instructions
4. Create a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Workflow

### Branching Strategy

We use a Git Flow-inspired branching strategy:

- `main`: Production-ready code
- `develop`: Integration branch for features
- `feature/*`: New features
- `bugfix/*`: Bug fixes
- `hotfix/*`: Critical fixes for production

### Feature Development

1. Create a feature branch from `develop`
2. Implement your changes
3. Write tests for new functionality
4. Update documentation if needed
5. Ensure all tests pass
6. Create a pull request

## Code Style and Standards

### Kotlin Style Guide

We follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) with some additional rules:

#### Naming Conventions

```kotlin
// Classes and Objects
class UserProfileViewModel

// Functions and Properties
fun getUserProfile(): User
val userProfile: User

// Constants
const val MAX_RETRY_ATTEMPTS = 3
val DEFAULT_TIMEOUT = 30_000L

// Private properties
private val _userState = MutableStateFlow<UserState>(UserState.Idle)
val userState: StateFlow<UserState> = _userState
```

#### Code Structure

```kotlin
// Use data classes for models
data class User(
    val id: String,
    val name: String,
    val email: String
)

// Use sealed classes for state management
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

### Android-Specific Guidelines

#### ViewModels

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login() {
        viewModelScope.launch {
            // Implementation
        }
    }
}
```

#### Compose Components

```kotlin
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    // Implementation
}
```

#### Dependency Injection

Use constructor injection with Hilt:

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val useCase: MyUseCase
) : ViewModel()
```

### Code Quality Tools

We use several tools to maintain code quality:

- **Detekt**: Static code analysis for Kotlin
- **Android Lint**: Android-specific code checks
- **Ktlint**: Kotlin code style checker

Run quality checks:

```bash
./gradlew detekt
./gradlew lint
./gradlew ktlintCheck
```

## Testing

### Testing Strategy

We follow a comprehensive testing strategy:

- **Unit Tests**: Test individual functions and classes
- **Integration Tests**: Test component interactions
- **UI Tests**: Test user interface flows
- **End-to-End Tests**: Test complete user journeys

### Unit Testing

```kotlin
class LoginUseCaseTest {

    private lateinit var useCase: LoginUseCase
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = LoginUseCase(repository)
    }

    @Test
    fun `login success should emit user`() = runTest {
        // Given
        val expectedUser = User(id = "1", name = "Test", email = "test@example.com")
        coEvery { repository.login() } returns flowOf(Result.success(expectedUser))

        // When
        val result = useCase().first()

        // Then
        assertEquals(Result.success(expectedUser), result)
    }
}
```

### UI Testing

```kotlin
@HiltAndroidTest
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginButtonClick_shouldTriggerLogin() {
        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule.onNodeWithText("Login").performClick()

        // Assert expected behavior
    }
}
```

## Documentation

### Code Documentation

All public APIs must be documented with KDoc:

```kotlin
/**
 * Authenticates a user with the provided credentials.
 *
 * @param email User's email address
 * @param password User's password
 * @return Flow emitting Result with User on success
 */
fun login(email: String, password: String): Flow<Result<User>>
```

### README Updates

Update documentation for any new features or changes:

- Update README.md for new features
- Add API documentation for new endpoints
- Update setup instructions if prerequisites change

## Pull Request Process

### Before Submitting

1. **Self-review**: Ensure your code follows the guidelines
2. **Tests**: All tests pass and new tests are added
3. **Documentation**: Code is documented and docs are updated
4. **Linting**: No linting errors or warnings
5. **Commits**: Follow commit message guidelines

### PR Template

Use this template for pull requests:

```markdown
## Description
Brief description of the changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] UI tests added/updated
- [ ] Manual testing completed

## Screenshots (if applicable)
Add screenshots for UI changes

## Checklist
- [ ] Code follows style guidelines
- [ ] Documentation updated
- [ ] Tests pass
- [ ] No linting errors
```

### Review Process

1. **Automated Checks**: CI must pass all checks
2. **Code Review**: At least one maintainer review required
3. **Testing**: Reviewer may request additional tests
4. **Approval**: PR approved by maintainer
5. **Merge**: Squash merge with descriptive commit message

## Commit Guidelines

### Commit Message Format

```
type(scope): description

[optional body]

[optional footer]
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

### Examples

```
feat(auth): add Google OAuth login

fix(streaming): resolve RTMP connection timeout

docs(api): update YouTube API setup guide

test(chat): add unit tests for message filtering
```

## Issue Reporting

### Bug Reports

When reporting bugs, please include:

- **Description**: Clear description of the issue
- **Steps to Reproduce**: Step-by-step instructions
- **Expected Behavior**: What should happen
- **Actual Behavior**: What actually happens
- **Environment**: Device, OS, app version
- **Logs**: Relevant log output
- **Screenshots**: If applicable

### Feature Requests

For feature requests, include:

- **Description**: What feature you want
- **Use Case**: Why you need this feature
- **Alternatives**: Considered alternatives
- **Additional Context**: Screenshots, mockups, etc.

## Recognition

Contributors will be recognized in:

- GitHub contributors list
- Release notes
- Project documentation

## Getting Help

- **Issues**: Use GitHub issues for bugs and features
- **Discussions**: Use GitHub discussions for questions
- **Documentation**: Check docs/ folder first
- **Team**: Contact maintainers for urgent issues

Thank you for contributing to LiveApp! Your efforts help make the project better for everyone.