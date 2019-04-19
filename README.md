# Android app with Google Cloud Firestore and Architecture Components

This is an Android application using the following services and components:

* Google Cloud Firestore as its database to store products;

* Firebase Authentication to authenticate users;

* Some pieces from Android Architecture Components, such as:

  * LifeCycles
  * LiveData
  * ViewModel

  

> Pre-requisites: 
>
> * Android Studio 3.3 or higher
> * Google Firebase free account



## 1) Creating the Android application with Android Studio

With Android Studio, create the application with the `Navigation Drawer Activity` template. This template provides some structure to use the Navigation Drawer component as the main navigation system of the application.



## 2) Creating and configuring the Firebase project

* Create a Firebase project at http://firebase.google.com/
* After the project creation, register the Android application on it;
  * It will be necessary to add the Debug signing certificate SHA-1 for the Android application;
  * This is because the Google Sign-in feature.
* Download the `google-services.json` file and move it to the Android app module root directory.



### 2.1) Configuring the Cloud Firestore database

Inside the Google Firebase console, execute the following steps to configure the Firebase database:

* Access the `Database` menu;
* Press the `Create database` button;
* Select the `Start in locked mode`
* Press the `Enable` button.

At this time, the Firestore database is configured for the application, but it's necessary to set up the access security rules.

### 2.2) Preparing the security rules

To configure the Firestore security rules, inside its section, access the `Rules` tab. The current security rule will be shown. Change it to the following:

```json
service cloud.firestore {
  match /databases/{database}/documents {
    match /products/{product} {
      allow create: if request.auth.uid != null;
      allow read, update, delete: if request.auth.uid != null
      	&& resource.data.userId == request.auth.uid; 
    }      
	}
}
```

This security rule allow any authenticated user to access the Firestore database of this application.



### 2.3) Configuring the Firebase Authentication

The enable the users to authenticate in the Android application, it's necessary to enable the authentication feature in Firebase. To do that, just access the `Authentication` menu and select the `Sign-in method` tab. 

In the sign-in method section, select the `Google` option and enable it.

## 

## 3) Updating the build.gradle files

To implement the Android application it's necessary to import some libraries to the project, following the steps describe in the steps below:

> All library versions should be carefully observed to do not create incompatibilities between them 



### 3.1) build.gradle project file

* Add the `google-services` dependency:

  ```json
  dependencies {
  	classpath 'com.android.tools.build:gradle:3.4.0'
  	classpath 'com.google.gms:google-services:4.2.0'
  }
  ```

* Sync the project

  

### 3.2) build.gradle application file

* Inside the `dependencies` section, add these dependencies:

  ```json
  implementation 'com.android.support:recyclerview-v7:28.0.0'
  
  implementation "android.arch.lifecycle:extensions:1.1.1"
  
  implementation 'com.google.firebase:firebase-core:16.0.8'
  implementation 'com.google.firebase:firebase-core:16.0.8'
  implementation 'com.google.firebase:firebase-auth:16.2.1'
  implementation 'com.google.firebase:firebase-firestore:18.2.0'
  
  implementation 'com.google.android.gms:play-services-auth:16.0.1'
  ```

* After the `dependencies` section, apply this plugins:

  ```json
  apply plugin: 'com.google.gms.google-services'
  ```

* Sync the project



## 4) Creating the SignInActivity view and activity

Create the `SignInActivity` as describe [here](), to be shown when there is no user signed in.



## 5) Adapting the MainActivity class

The `MainActivity` from the template is good, but it's necessary some adjusts to work with multiple fragments as designed here, so adapt it as described [here]().



## 6) Creating the product infrastructure classes

Here are the classes to represent the product and its layers to access the database and present the information to the views.

### 6.1) Product model

The parameters in the model represents the fields in the Firestore collection entity. Just the `id` parameter is excluded as a field because the Firestore generates automatically.

One important field here is the `userId` used by the security rules to allow access to the signed in user through its `id` in Firebase Authentication. 



### 6.2) Product list adapter

This adapter is responsible to convert an instance of a product to an item to be shown in the `Recycle View` component, which is a good choice to be used in this application, since it's expect to update information in realtime, when some information of a product changes or even when a new product is added.

One can check its implementation [here]().



### 6.3) Product repository

This is the responsible to access the database itself. It has all the methods to save, delete or search for a specific product.

To provide realtime updates to the application, the implementations of the search methods use *listeners* to update the views with changes in the product collection. Such methods should only be used by classes who extends the `ViewModel` class.

One can check its implementation [here]().



### 6.4) Product view model

This class is responsible to provide realtime data (a list of products or just a product) to the views such Fragments. They are lifecycle-aware, which means it can manage the changes in the UI, behaving exactly as it view who owner it.

One can check its implementation [here]().

## 

## 7) Creating the products list fragment

This view is responsible to present the list of products to the user, but using the `ProductViewModel`, which is the responsible to update the view with changes from the database.

This approach also uses the `RecycleView` component to create the products list. It is the proper one when the list would suffer updates.

One can check its implementation [here]().



## 8) Creating the product edition fragment

This is view is responsible create or edit a product. It also uses the `ProductViewModel` to update the view when there is some modification in a specific product.

One can check its implementation [here]().



## 9) Executing the application for the first time

When the applications runs for the first time, it's necessary to sign-in on it to access the Firestore database. This process is made by Firestore Authentication service.



### 9.1) Creating the Firestore index

The Firestore uses indexes to perform queries, so when the application runs for the first time, the Android Studio will print in LogCat an error, requesting an index creation. This errors contains a link to Firestore, where the index can be created.







