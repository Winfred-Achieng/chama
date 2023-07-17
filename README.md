# CHAMA HUB
 
 This is an app that helps family memebers and friends to save money collaboratively and securely. The features included in this application are listed below.
 
 *Authentication Module- This module is used in the verification and validation of the system users. The module employs a registration sub module used to register new users into the system and a login sub module which provides users access to their registered accounts.  <br>
 *Host Module- This module includes a registered user with the capability to form a chama group. <br>
 *User Module- This module focuses on  user-related functionality within the system such contribution to chama groups and managing their user profiles. <br>
 *chat console- where chama group memebrs can effectively communicate with one another and give insights on how to better their group. <br>
 *Payment Module-
 

## Installation Process

To install this application, Kindly follow the steps listed below:
1. Clone the repository to your local machine:
   git clone https://github.com/Winfred-Achieng/chama.git
   

2. Open Android Studio:
- Launch Android Studio on your computer.
- Select "Open an Existing Project" and navigate to the cloned `chama` directory.
- Open the project in Android Studio.

3. Set up Firebase:
- Create a Firebase project on the [Firebase console](https://console.firebase.google.com/).
- Set up Firebase Authentication and Firestore for your project.
- Download the `google-services.json` file from Firebase and place it in the `app` directory of the Chama app.

4. Set the minimum Android version:
- Open the `build.gradle` file for the app module (`chama/app/build.gradle`).
- Set the `minSdkVersion` to `23` (Android 6.0) or higher.

5. Build and run the app:
- Connect your Android device to the computer or set up an emulator.
- Click on the "Run" button in Android Studio to build and run the app on your device/emulator.

Note: The Chama app requires Android version 6.0 (Marshmallow) or above to run.

If you encounter any issues during the installation, please refer to the project's documentation or create a new issue on the [GitHub repository](https://github.com/Winfred-Achieng/chama/issues) for further assistance.


## Usage

<b>Interact with the Chama API:</b> Once the Chama app is running, you can start using the Chama API to perform various Chama-related operations. The app provides a user interface for creating a new Chama, making contributions, and accessing Chama details.

<b>Handle API Responses:</b> The Chama app handles API responses and displays relevant information to the user. It includes error handling for cases where API requests fail or encounter errors. You can customize the app's behavior based on the response received from the Chama API.

Kindly follow the steps below to integrate the Chama API into your Chama app:

1. **Create a Chama Client**: To communicate with the Chama API, you need to create a client instance. Instantiate the client by providing the necessary credentials and environment settings. Here's an example of how to create a Chama client:

    ```kotlin
    val client = ChamaClient(
        apiKey = "YOUR_API_KEY",
        apiSecret = "YOUR_API_SECRET",
        environment = Environment.Sandbox
    )
    ```

2. **Connect to the Chama API**: Before making any API calls, you should ensure that your client is successfully connected to the Chama API. You can use the `isConnected` method to check the connection status. Here's an example:

    ```kotlin
    if (client.isConnected()) {
        // Proceed with API requests
    } else {
        // Handle connection error
    }
    ```

3. **Make Chama API Requests**: Once your client is connected, you can start making API requests to interact with the Chama API endpoints. The Chama client provides methods for different API operations. Here are a few examples:

    - Creating a new Chama:

        ```kotlin
        val chamaName = "My Chama"
        val response = client.createChama(chamaName)
        if (response.isSuccess()) {
            val chamaId = response.getChamaId()
            // Chama created successfully, proceed with further actions
        } else {
            val errorMessage = response.getErrorMessage()
            // Handle creation error
        }
        ```

    - Getting Chama details:

        ```kotlin
        val chamaId = "CHAMA_ID"
        val response = client.getChama(chamaId)
        if (response.isSuccess()) {
            val chama = response.getChama()
            // Process and display Chama details
        } else {
            val errorMessage = response.getErrorMessage()
            // Handle error retrieving Chama details
        }
        ```

    - Making a contribution to a Chama:

        ```kotlin
        val chamaId = "CHAMA_ID"
        val amount = 1000
        val response = client.makeContribution(chamaId, amount)
        if (response.isSuccess()) {
            // Contribution made successfully
        } else {
            val errorMessage = response.getErrorMessage()
            // Handle contribution error
        }
        ```

4. **Handle API Responses**: API requests can result in successful responses with data or error responses. You can use the `isSuccess` method to check if the response is successful and retrieve the relevant data using provided methods like `getChamaId`, `getChama`, etc. For error responses, you can use the `getErrorMessage` method to obtain the error message and handle it accordingly.



## Contributions

Contributions to the Chama app are welcome! If you would like to contribute to the project, please follow these steps:

1. Fork the repository:
   - Click on the "Fork" button at the top right corner of the GitHub repository page.

2. Clone the forked repository:
git clone https://github.com/your-username/chama.git

3. Create a new branch:
git checkout -b feature/your-feature-name

4. Make your changes:
- Make the necessary changes and improvements to the codebase.

5. Commit your changes:
git commit -m "Add your commit message here"

6. Push your changes to your forked repository:
git push origin feature/your-feature-name

7. Create a pull request:
- Go to the original repository on GitHub.
- Click on the "New Pull Request" button.
- Fill in the necessary details and submit the pull request.

Please ensure that your contributions adhere to the coding conventions and guidelines used in the project. Additionally, make sure to test your changes thoroughly before submitting a pull request.

If you have any questions or need further assistance, please open an issue on the [GitHub repository](https://github.com/Winfred-Achieng/chama/issues).

We appreciate your contributions to make the Chama app better!

## Bugs
In case you are working on the project for your own use and you come along some bugs in the system,kindly make a detailed report on the issue.[Click here to view issues](https://github.com/Winfred-Achieng/chama/issues). Your input will be greatly appreciated. <br>
Here are some tips on how to write the bug report:

* Be specific and provide all the details, such as the bug number, title, environment, reproduction steps, expected and actual result, screenshots and/or videos.
* Ensure focus on one bug at a time and list them categorically in a consise manner.
* For better understanding and correction,kindly use the same names and messages as indicated in the application.
* Ensure to write a clear and informationally dense report that contains the details and user steps that allow the reproduction of the bug.

## Known issues

This application was implemented with a focus on Android devices, version 6 and above. Incase there is an issue when you clone the project for your own use, kindly confirm that the project is running on an Android platform for the best results and to ensure compatibility.
