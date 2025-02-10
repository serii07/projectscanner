# Barcode Scanner App
## Made by Hrishikesh and designed by Rishi
## Overview
This app leverages Google MachineLearning Kit and the Google Custom Search API. By scanning a barcode, the app fetches the corresponding product name and image.

## Features
- **Barcode Scanning**: Uses Google ML Kit to accurately read barcodes.
- **Product Search**: Integrates with the Google Custom Search API to retrieve product names and images.
- **Cleaned Data Output**: Parses and trims API results to display concise product information.

## Limitations
-  **Limited results**: For the moment being it only fetches the product name and image
-  **Might not always be accurate**: The result fetched fromn the Search API might not always be accurate
-  **Copyright issues**: May cause legal issues if used commercially as the images are copyrighted

## How It Works
1. **Scan the Barcode**:
   - The app utilizes Google ML Kit for barcode scanning.
   - Once scanned, the barcode value is captured and bundled into an Intent.
2. **API Request**:
   - The barcode number is passed to another activity where it is input into the Google Custom Search API.
   - The API fetches product data, including the product name and image.
3. **Data Processing**:
   - The raw API response is parsed, cleaned, and trimmed to extract only the neccesary information.
4. **Output**:
   - The app displays the product name and image to the user in the UI.

## Key Dependencies and Libraries used
- Borrowed logic from **Code Scanner library based for Android** by **Yuriy-budiyev**
- **Google ML Kit**
- **Google Custom Search API**
- **Coroutines**
- **AndroidX Preferences Library**

## Acknowledgments
- [Code scanner library](https://github.com/yuriy-budiyev/code-scanner) for the logic of storing products in the product database
- [Google ML Kit](https://developers.google.com/ml-kit) for barcode scanning capabilities.
- [Google Custom Search API](https://developers.google.com/custom-search/) for fetching product data.
