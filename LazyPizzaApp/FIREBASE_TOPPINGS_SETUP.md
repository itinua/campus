# Firebase Toppings Setup Guide

## How Toppings Work
Toppings are stored in the same `products` collection with category "Toppings"

## Prerequisites
Make sure you have:
1. Firebase Console access to project: `lazzypizza-45597`
2. Firestore Database enabled
3. Firebase Storage enabled

## Adding Toppings to Firestore

1. **Open Firebase Console**
   - Go to https://console.firebase.google.com
   - Select project: `lazzypizza-45597`
   - Navigate to **Firestore Database**

2. **Add Toppings to Products Collection**
   - Go to the `products` collection
   - Add documents with category "Toppings"

## Topping Documents Structure

Add these documents to the `products` collection:

### Bacon
```json
{
  "name": "Bacon",
  "description": "Crispy bacon strips",
  "price": 1.0,
  "category": "Toppings",
  "image": "gs://lazzypizza-45597.firebasestorage.app/o/toppings%2Fbacon.jpg?alt=media",
  "isAvailable": true
}
```

### Extra Cheese
```json
{
  "name": "Extra Cheese",
  "description": "Additional mozzarella cheese",
  "price": 1.0,
  "category": "Toppings",
  "image": "gs://lazzypizza-45597.firebasestorage.app/o/toppings%2Fcheese.jpg?alt=media",
  "isAvailable": true
}
```

### Corn
```json
{
  "name": "Corn",
  "description": "Sweet corn kernels",
  "price": 0.5,
  "category": "Toppings",
  "image": "gs://lazzypizza-45597.firebasestorage.app/o/toppings%2Fcorn.jpg?alt=media",
  "isAvailable": true
}
```

### Tomato
```json
{
  "name": "Tomato",
  "description": "Fresh tomato slices",
  "price": 0.5,
  "category": "Toppings",
  "image": "gs://lazzypizza-45597.firebasestorage.app/o/toppings%2Ftomato.jpg?alt=media",
  "isAvailable": true
}
```

### Olives
```json
{
  "name": "Olives",
  "description": "Black olives",
  "price": 0.5,
  "category": "Toppings",
  "image": "gs://lazzypizza-45597.firebasestorage.app/o/toppings%2Folives.jpg?alt=media",
  "isAvailable": true
}
```

### Pepperoni
```json
{
  "name": "Pepperoni",
  "description": "Spicy pepperoni slices",
  "price": 1.0,
  "category": "Toppings",
  "image": "gs://lazzypizza-45597.firebasestorage.app/o/toppings%2Fpepperoni.jpg?alt=media",
  "isAvailable": true
}
```

### Mushrooms
```json
{
  "name": "Mushrooms",
  "description": "Fresh mushroom slices",
  "price": 0.75,
  "category": "Toppings",
  "image": "gs://lazzypizza-45597.firebasestorage.app/o/toppings%2Fmushrooms.jpg?alt=media",
  "isAvailable": true
}
```

### Pineapple
```json
{
  "name": "Pineapple",
  "description": "Sweet pineapple chunks",
  "price": 0.75,
  "category": "Toppings",
  "image": "gs://lazzypizza-45597.firebasestorage.app/o/toppings%2Fpineapple.jpg?alt=media",
  "isAvailable": true
}
```

### Bell Peppers
```json
{
  "name": "Bell Peppers",
  "description": "Colorful bell pepper strips",
  "price": 0.5,
  "category": "Toppings",
  "image": "gs://lazzypizza-45597.firebasestorage.app/o/toppings%2Fbell_peppers.jpg?alt=media",
  "isAvailable": true
}
```

## Important Notes

1. **Category MUST be "Toppings"** (with capital T)
2. **Same collection as products** - no separate toppings collection needed
3. **isAvailable field** - set to true to show the topping
4. **Price** - will be added to the base pizza price

## Upload Topping Images (Optional)

If you want to use actual images:

1. Go to **Firebase Storage**
2. Create a folder called `toppings`
3. Upload images with these names:
   - bacon.jpg
   - cheese.jpg
   - corn.jpg
   - tomato.jpg
   - olives.jpg
   - pepperoni.jpg
   - mushrooms.jpg
   - pineapple.jpg
   - bell_peppers.jpg

## Test in the App

1. **Add toppings to Firestore** (as shown above)
2. **Run the app**
3. **Click on any Pizza product**
4. **You should see:**
   - Product details at the top
   - "ADD EXTRA TOPPINGS" section
   - Grid of toppings from products with category "Toppings"
   - Ability to add/remove quantities
   - Price updates dynamically

## Troubleshooting

Check Logcat for these messages:
```
LazyPizza: Loading toppings from Firebase...
LazyPizza: Loaded X toppings
LazyPizza: Topping - Name: $Price
```

If toppings don't appear:
1. **Verify category is exactly "Toppings"** (case sensitive)
2. **Check Firebase Console** - ensure products with category "Toppings" exist
3. **Verify isAvailable is true**
4. **Check you're clicking on a Pizza product**

## How It Works

1. When you click a Pizza product, the app:
   - Loads product details
   - Fetches all products with category="Toppings"
   - Converts them to topping options
   - Displays them in a grid

2. The toppings are filtered from the same `products` collection
   - No separate toppings collection needed
   - Uses existing product structure
   - Category "Toppings" identifies them as toppings

This approach keeps all food items in one collection, making it easier to manage!