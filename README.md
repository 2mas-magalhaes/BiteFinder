# BiteFinder

![Android](https://img.shields.io/badge/Android-Kotlin%20%2B%20Compose-3DDC84) ![Backend](https://img.shields.io/badge/Backend-PHP%20%2B%20Azure%20SQL-777BB4) ![Auth](https://img.shields.io/badge/Auth-JWT-orange)

BiteFinder is a mobile app for discovering, comparing and reviewing dishes from restaurants around you. Instead of searching for a restaurant, you search for the dish: check the price, the rating, the distance, and then decide where to eat.

The project has two parts:

- **ByteFinder** — Android app (Kotlin + Jetpack Compose)
- **bitefinder-api** — PHP backend with Azure SQL and a web portal for restaurants

## Demo

[![Watch the demo](https://img.youtube.com/vi/6YliaOlt-C8/maxresdefault.jpg)](https://youtu.be/6YliaOlt-C8)

## Features

### For customers

- **Dish discovery** — search by name, filter by category and sort by the most relevant results.
- **Nearby** — dishes close to you, based on your location and a configurable radius, shown on a map.
- **Dish detail** — price, photo, description, restaurant and average rating.
- **Comparison** — put two dishes side by side to decide faster.
- **Reviews** — leave your rating and comment, and browse your review history.
- **Login** — email/password authentication with JWT, plus social login (Google, Facebook, Microsoft, Apple).

### For restaurants

- **Partner sign-up** — create a restaurant account with tax-number validation (via nif.pt).
- **Menu management** — create, edit, feature and remove dishes.
- **Review replies** — respond directly to customer comments.
- **Web portal** — a management dashboard accessible from the browser, in addition to the app.

## Stack

**Android app**
- Kotlin + Jetpack Compose (Material 3)
- MVVM architecture (ViewModel + StateFlow)
- Retrofit + OkHttp for the networking layer
- Coil for image loading
- Google Maps Compose + Play Services Location
- *Mock* mode with automatic fallback when the API is unavailable

**Backend**
- PHP (REST endpoints under `bitefinder-api/api`)
- Azure SQL (PDO)
- JWT authentication (HS256) with timing-attack protection
- Tax-number validation through the nif.pt API
- HTML web portal for restaurant management

## Project structure

```
BiteFinder/
├── ByteFinder/          Android app (Kotlin + Compose)
├── bitefinder-api/      PHP backend + web portal
│   └── api/
│       ├── auth/        JWT login, social login, restaurant sign-up
│       ├── pratos/      Dish CRUD, search and "nearby"
│       ├── avaliacoes/  Create, list and reply to reviews
│       └── middleware/  JWT auth, CSRF and security
└── design-tokens/       Shared design tokens (colors, spacing, etc.)
```

## Getting started

### Backend

1. Copy `bitefinder-api/.env.example` to `.env` and fill in the credentials (Azure SQL, JWT, social login keys).
2. Start the PHP server from `bitefinder-api`:

   ```powershell
   php -S 127.0.0.1:8000
   ```

   - Web portal: `http://127.0.0.1:8000/`
   - Login API (JWT): `http://127.0.0.1:8000/api/auth/login_jwt.php`

> Note: endpoints that rely on Azure SQL need the `pdo_sqlsrv` driver installed in PHP. Without it, some flows run in mock/fallback mode.

### Android app

1. Set the keys in `ByteFinder/local.properties` (see `local.properties.example`):

   ```properties
   MAPS_API_KEY=...
   API_BASE_URL=http://10.0.2.2:8000/
   ```

   (`10.0.2.2` is the host machine as seen from the Android emulator.)

2. Open the `ByteFinder` folder in Android Studio and run the app, or:

   ```powershell
   .\gradlew installDebug
   ```

### Quick start (Windows)

The script below builds the app and starts the emulator and the server:

```powershell
powershell -ExecutionPolicy Bypass -File .\run-bytefinder.ps1
```

## Test accounts

| Type       | Email                   | Password |
|------------|-------------------------|----------|
| Customer   | `goncalo@teste.com`     | `123456` |
| Restaurant | `restaurante@teste.com` | `123456` |

These accounts exist in the app's mock mode and also work for testing the login endpoint.

## Design

BiteFinder follows its own design system — *Sapphire Food-Tech* — with a blue identity, dish imagery front and center, and restrained motion. The tokens live in [design-tokens/](design-tokens/) and the documentation in [BITEFINDER_DESIGN_SYSTEM.md](BITEFINDER_DESIGN_SYSTEM.md).
