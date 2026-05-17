# BiteFinder Design System

Direction: Sapphire Food-Tech.

BiteFinder keeps its blue identity, but the product should feel more premium than a default admin/app template: deep navy atmosphere, bright sapphire actions, clean white product surfaces, strong dish imagery, and restrained motion.

## Principles

- Dish-first: food imagery leads discovery and detail screens.
- Blue premium: navy depth plus sapphire CTAs, not generic corporate blue.
- Data for decisions: price, rating, distance, reviews and availability stay visible.
- One product: app and web share the same tokens, radii, motion and component anatomy.
- Restrained motion: no playful bounce; use quick, precise easing.

## Tokens

Canonical token file: `design-tokens/bitefinder-blue.json`.

Core colors:

- Navy 900: `#061121`
- Navy 800: `#0A1628`
- Navy 700: `#102844`
- BiteFinder Blue: `#1B6EC2`
- Deep Blue: `#103E7D`
- Bright Blue: `#5AA7F2`
- Pale Blue: `#D6E8F8`
- App Surface: `#F4F7FB`
- Raised Surface: `#FFFFFF`
- Text: `#12213D`
- Muted Text: `#5F7190`
- Rating: `#FFC107`
- Success: `#34C67A`
- Danger: `#E85C5C`

## Component Rules

- Buttons use sapphire blue, 16-24px radius, subtle elevation and short pressed states.
- Cards use white or deep navy surfaces, 24px radius, image-led composition and clear metadata.
- Ratings use yellow only for stars/scores, never as a primary CTA.
- Forms use pale blue borders, blue focus rings and human error copy.
- Navigation uses deep navy surfaces and a soft active blue pill.

## Motion

- Fast: 150ms.
- Standard: 220ms.
- Page/sheet: 360ms.
- Easing: `cubic-bezier(.2,.8,.2,1)`.
- Pressed states scale no lower than `0.97`.
- Avoid bounce, shake, decorative loops and excessive shimmer.
