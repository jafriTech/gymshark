# Gymshark Products — Android

My submission for the Gymshark Mobile Engineering Challenge. It's a small Android app that loads products from the provided endpoint, shows them in a grid, and opens a detail page when you tap one.

Built with Kotlin, Jetpack Compose, MVVM, Hilt, Retrofit, kotlinx.serialization and Coil.

<img width="1440" height="3040" alt="Screenshot_20260930-190629_GymShark" src="https://github.com/user-attachments/assets/07c19500-ecec-40c4-8bd0-e5f0af019a55" />
<img width="1440" height="3040" alt="Screenshot_20260930-190711_GymShark" src="https://github.com/user-attachments/assets/a253d511-02c2-44a8-8e37-f37830910068" />


## Running it

Open the project in Android Studio and run the `app` configuration.

To run the tests:

bash
./gradlew testDebugUnitTest


## What it does

- Shows products in a grid with image, title, colour and price
- Shows a chip for product labels like "Going Fast", and an "Out of stock" chip where needed
- Handles missing or broken images with a placeholder while loading and a fallback icon if they fail
- Detail page with swipeable images, available sizes (sold-out ones are greyed out) and the product description rendered from HTML
- Loading, empty and error screens with a retry button
- Pull to refresh. If a refresh fails, the list stays on screen and a snackbar explains what went wrong
- Supports dark mode and TalkBack

## A few decisions

- **The API data is treated as untrusted.** Every field is optional when parsing, and the mapper cleans things up: it drops products with no id or title, rejects bad image URLs, and strips the junk out of the HTML descriptions.
- **Products are cached in memory.** The detail page looks the product up from the list that's already loaded, instead of downloading everything again. Errors aren't cached, so retry always tries the network again.
- **No use cases.** Each screen only makes one simple repository call, so a use case layer would just be empty wrappers. I'd add one if there was real logic like filtering or sorting.
- **Only the product id is passed to the detail screen.** That way it can reload itself if Android kills the app in the background.

## Testing

I wrote the tests parallel for the mapper, repository and ViewModels, not 100% TDD approach of writing the test first. They cover:

- Parsing messy data and the mapper edge cases
- The API setup, using MockWebServer
- Repository caching, refresh and error handling
- Loading, content, empty, error, retry and refresh states in both ViewModels
- Price formatting

I used simple hand-written fakes instead of a mocking library.

## Assumptions

- Prices are in pounds, so '1000' shows as £1,000.
- 'going-fast' is the only label in the data. Any other label is still shown, just formatted nicely (e.g. 'limited-edition' becomes 'Limited Edition').
- Sizes are display-only, since there's no basket.
- Nothing is saved between app launches, because persistence wasn't part of the brief.

## If I had more time

- Offline support with Room
- Compose UI tests
- Filtering and sorting
- A GitHub Actions workflow to run the tests on every push

## How I used AI

I used an AI assistant outside the IDE, mainly to get through the boilerplate faster: the DTOs, the mapper, the data layer, test setup and Compose UI code. That gave me more time to focus on how the app behaves and how it's structured.

It was useful for spotting edge cases in the real JSON, like null labels and the messy HTML, and for speeding up Compose details like image loading states and pull to refresh.

I made the main decisions myself. I chose the order to build things in, decided to skip use cases, and questioned a couple of its suggestions. For example, I compared the in-memory cache with using OkHttp's cache before deciding to keep the in-memory one. I also asked why the result type had no loading state, and agreed it belonged in the UI state instead. I reviewed and ran everything before committing, and I'm happy to talk through any part of the code.

I split the work into logical commits at the end rather than committing as I went.
