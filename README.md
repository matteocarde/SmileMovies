# SmileMovies
Udacity Nanodegree Project #1 #2

My first (and second) Android's project for the Udacity Android Developer Fast Track Course (EU Scholarship)

To run this project just download it and run it on Android Studio.
Be careful before running to add you own API key of themoviedb.org here:
[https://github.com/smileapplications/SmileMovies/blob/master/app/src/main/java/it/smileapp/smilemovies/utilities/MoviesDB.java#L18]

## Version 2
### Functionalities
* Added the possibility to add a move to your favourites list
    * This functionality is implemented using the internal **SQLiteDatabase** and a **ContentProvider** to fetch the data from the database
* Added the possibility to **add a preference** (using SharedPreferences) to decide which filter show first when opening the app
* Added a **Notification** that every day, if the user is at home (aka the phone is charging) sends a notification to the user to suggest him to re-watch a movie from his favourite list.
    * This functionality is implemented using a **FirebaseJobDispatcher** to schedule an **Intent Service** to display the notification when the app is on background
* Re-designed the movie details activity interface using a **ConstraintLayout** and a **ViewPager** instead of nested layouts and a TabHost

##Screenshots
![screenshot_1488116254](https://cloud.githubusercontent.com/assets/2022691/23340268/983ad63e-fc33-11e6-9739-bf11f596c59b.png)
![screenshot_1488116203](https://cloud.githubusercontent.com/assets/2022691/23340270/9862950c-fc33-11e6-84c9-e30fc0fa688c.png)
![screenshot_1488116222](https://cloud.githubusercontent.com/assets/2022691/23340271/9864c5b6-fc33-11e6-8bfe-466330966875.png)
![screenshot_1488116242](https://cloud.githubusercontent.com/assets/2022691/23340272/986791f6-fc33-11e6-81d7-5577434c4f66.png)
![screenshot_1488116296](https://cloud.githubusercontent.com/assets/2022691/23340269/98572ffa-fc33-11e6-9b83-09c0057b39c5.png)

## Version 1
### Functionalities
* List of movies in a nice grid (Using RecyclerView and Adapters)
* Possibility to choose from the Menu what sort criteria apply to the movies grid. Choose between:
    * Most popular films
    * Most rated films
    * Most popular italian films
    * Most rated italian films
* In the movie detail activity view all the infos about the selected movie together with:
    * Brief synopsis of the movie
    * List of trailers (click on one of them and go to Youtube to see it)
    * List of actors in the case
    * List of reviews 

### Screenshots
![screenshot_1486650852](https://cloud.githubusercontent.com/assets/2022691/22788072/c01e42e2-eede-11e6-922d-b496b0ab75a7.png)
![screenshot_1486650890](https://cloud.githubusercontent.com/assets/2022691/22788071/c001c50e-eede-11e6-9110-70cb38800a81.png)
![screenshot_1486650868](https://cloud.githubusercontent.com/assets/2022691/22788070/bfff9612-eede-11e6-8506-d9f682e735fd.png)
![screenshot_1486650937](https://cloud.githubusercontent.com/assets/2022691/22788069/bffebd1e-eede-11e6-9663-cd78f7497d85.png)
![screenshot_1486650968](https://cloud.githubusercontent.com/assets/2022691/22788068/bff9cf16-eede-11e6-9f1d-b8fd57760b00.png)

<br>
![screenshot_1486668560](https://cloud.githubusercontent.com/assets/2022691/22800305/36f83e0c-ef09-11e6-819d-466c0f44e24e.png)
![screenshot_1486668539](https://cloud.githubusercontent.com/assets/2022691/22800306/36f918fe-ef09-11e6-8e4e-add30b328dbd.png)
