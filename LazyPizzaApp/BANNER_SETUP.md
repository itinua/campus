# LazyPizza Banner Image Setup

## How to Upload the Banner Image to Firebase Storage

1. **Open Firebase Console**
   - Go to https://console.firebase.google.com
   - Select your project: `lazzypizza-45597`

2. **Navigate to Storage**
   - Click on "Storage" in the left sidebar
   - Click "Get Started" if it's your first time

3. **Upload the Banner Image**
   - Click "Upload File"
   - Select the pizza banner image you provided
   - Rename it to `banner_pizza.jpg`
   - Upload to the root directory

4. **Image Path**
   The app expects the banner at:
   ```
   gs://lazzypizza-45597.firebasestorage.app/o/banner_pizza.jpg
   ```

5. **Alternative: Direct URL**
   If you have a direct HTTPS URL for the image, you can update line 140 in:
   `src/main/java/pl/lazypizza/presentation/home/HomeScreen.kt`
   
   Replace:
   ```kotlin
   model = "gs://lazzypizza-45597.firebasestorage.app/o/banner_pizza.jpg",
   ```
   
   With:
   ```kotlin
   model = "https://your-direct-url-here.jpg",
   ```

## Banner Image Details
- **Size**: 180dp height, full width
- **Shape**: Rounded corners (16dp)
- **Background**: Orange (#F57C00)
- **Content**: Pizza with toppings on orange background

The app will automatically convert Firebase Storage URLs to download URLs when loading the image.