# Abe's Community Car Dealership

## Table of Contents
1. [Overview](#overview)
2. [Product Spec](#product-spec)
3. [Algorithms](#algorithms)
4. [Wireframes](#wireframes)
5. [Schema](#schema)

## Overview
### Description
Users can find cars that other users are selling in their area. Users can also sign in/create account to unlock other features including Adding listings to their favorites, communicating with the seller through chat, and posting their cars for sale on the app.

### App Evaluation
- **Category:** Marketplace / Sales
- **Mobile:** App is primarily meant to be mobile but its features aren't entirely dependent on being mobile. Meaning that a web version of the app could also be made at a later point.
- **Story:** Finds a list of cars for sale depending on the user's location and filter preferences. Users can find the cars they like and message the sellers if they are interested. The app is primarily consumption-based but users can also create their sellings on the app and be contacted by others with offers.
- **Market:** App is primarily targeted to those who are considering buying a new car, but users can also just browse if they are interested in what's for sale.
- **Habit:** App is meant to be engaging enough that people who are looking to buy or sell their car would be checking the app daily for new sales/offers.
- **Scope:** App would start with the sale of whole cars. The app could eventually evolve to include compatible parts to be upgraded or repaired in already owned cars.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can create an account and login.
* Users can logout at any time.
* All data related to users and the cars are stored in a database.
* App can access the user's location and find the listings in their area.
* User's can filter listings to match their preferences.
* Listings show the make and model, year, and pictures of the car for sale; will also show the seller preferred contact information.
* Listings have infinite pagination.
* Users can mark posts as favorites for easy identification.
* Users can create their listings with the required information (If signed in).

**Optional Nice-to-have Stories**

* Users can browse the app as a guest without signing in but will only have read permission.
* App will have an integrated chat app where buyers and sellers can communicate.
* Favorites will be saved to a separate screen for ease of access.
* Listings show total favorites so users can see which ones are popular.
* App will use Google's Material Design for visual polish.
* Users can change their passwords and delete their accounts.

### 2. Screen Archetypes

* Login/Signup
    * Allow users to signup or login to gain access to the full app
    * OPTIONAL: Allow a third option where users can continue as a guest but cannot create posts, chat, or add listings to favorites.
* Stream
    * Show all listings by default, sorted by price.
    * Allow the opening of a fragment with filter and sorting settings.
    * Individual listings can be clicked to bring up a detailed view.
* Detail
    * Show all information on the selected listing
    * Implement Google Maps & Directions SDK to show distance to listing and allow for directions
    * IF IMPLEMENTED: Show button to open up private chat with the seller
* Favorites (OPTIONAL)
    * Shows listings that have only been favorited.
* Messaging (OPTIONAL)
    * Allow 1:1 communication between users.
    * Messages will be persisted if closed.
* Creation:
    * Users can fill out a form to create a listing.
    * Certain elements are required for listing to be created.
    * Extra details can be added e.g. VIN, mechanical issues.
* Profile:
    * Allow user to change their name, bio, and profile image.
* Settings:
    * Allow users to sign out.
    * Allow users to change their password.
    * Allow users to delete accounts.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Stream
* Favorites (Optional)
* Profile/Settings (Separate or Combined, haven't made decision yet)

**Optional**

* (Floating Action Button or FAB) Details Screen ---> Chat Screen

**Flow Navigation** (Screen to Screen)

* Forced login ---> Sign up if no account made yet
* Stream ---> FAB ---> Filter/Sort fragment

## Algorithms
1. Filter listings
2. Show related listings
3. Good deal rating

## Wireframes
[Medium Fidelity Wireframe](https://www.figma.com/file/1WdYNyuuzZoarJeDRRKfqt/Abe-s-Car-Dealership-Wireframe?node-id=0%3A1)

## Schema
### Models

#### Listing

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | seller        | Pointer to User | listing's seller |
   | images        | File Array | images of listing |
   | description   | String   | general description of listing |
   | extraInformation | String | extra information that the seller might want to share that doesn't fit in description |
   | favoritesCount | Number  | number of favorites for the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

#### Chat

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the chat session (default field) |
   | initiate      | Pointer to User | person who initiates a chat session |
   | contacted     | Pointer to User | person who contacted in a chat session |
   | chatLog       | String Array | log of chat session |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

### Networking

* Login/Signup
   * (Read/GET) Send credentials to be authenticated and logged in.
   * (Create/Post) Send credentials for account to be created and logged in.
* Stream
   * (Read/Get) Query all listings if no filter or sorting is applied; if applied send only specified listing in given order.
   * (Create/Post) Create new favorite on a listing.
   * (Delete) Delete existing favorite.
* Detail
   * (Read/Get) Query all data on specified listing
   * (Create/Post) Create new favorite on a listing.
   * (Delete) Delete existing favorite.
* Favorites
   * (Read/Get) Query all listings that have been favorited by user.
   * (Delete) Delete existing favorite.
* Messaging
   * (Live Query/Websockets) Open connection to allow to users to communicate.
* Creation:
   * (Create/Post) Create new listing based based on given user input.
* Profile:
   * (Update/Put) Edit profile (profile picture, name, and bio)
* Settings:
   * (Create/Post) Allow user to change their password.
   * (Delete) Allow user to delete account.
