# CHAMA HUB
 
 This is an app that helps family memebers and friends to save money collaboratively and securely. The features included in this application are listed below.
 
 *Authentication Module- This module is used in the verification and validation of the system users. The module employs a registration sub module used to register new users into the system and a login sub module which provides users access to their registered accounts.  <br>
 *Host Module- This module includes a registered user with the capability to form a chama group. <br>
 *User Module- This module focuses on  user-related functionality within the system such contribution to chama groups and managing their user profiles. <br>
 *chat console- where chama group memebrs can effectively communicate with one another and give insights on how to better their group. <br>
 *Payment Module-
 

## Installation Process

To install this application, Kindly follow the steps listed below:

## Usage

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


## Bugs
In case you are working on the project for your own use and you come along some bugs in the system,kindly make a detailed report on the issue.[Click here to view issues](https://github.com/Winfred-Achieng/chama/issues). Your input will be greatly appreciated. <br>
Here are some tips on how to write the bug report:

* Be specific and provide all the details, such as the bug number, title, environment, reproduction steps, expected and actual result, screenshots and/or videos.
* Ensure focus on one bug at a time and list them categorically in a consise manner.
* For better understanding and correction,kindly use the same names and messages as indicated in the application.
* Ensure to write a clear and informationally dense report that contains the details and user steps that allow the reproduction of the bug.

## Known issues

This application was implemented with a focus on Android devices, version 6 and above. Incase there is an issue when you clone the project for your own use, kindly confirm that the project is running on an Android platform for the best results and to ensure compatibility.
