# wifisignalstrength - Nest Coding Challenge

Problem: 

- Build a working Android app that displays wifi signal strength data in a line graph.

Requirements:

- Home screen that has a link to the Live graph screen, and links to each Saved graph
- Live graph screen that displays a line graph of wifi signal strength values (ranging 0-100), with the rolling last 30 seconds of data, and refreshes every second; that screen also contains a button to Save the current graph
- Saved graph screen that displays a static graph that was previously saved (the data is saved, not the graph bitmap itself).
- The live wifi signal strength is actually a fake data provider that returns a random number every time it is invoked
- The live graph screen takes care of refreshing the graph (every second) and requesting a new value to the provider
- The graphs are saved in the app’s private storage, each data point’s value and timestamp are saved
- The app is tested
- No third-party library is used to draw the graphs
- Graphs should scale with the screen (works in portrait/landscape)
- There are no icon, or animation requirements

Solution:

- GraphView is a custom view that displays data in a line chart. The x-axis, y-axis, and dataset are drawn based on view size, datapoint count, and provider range. No custom library was used. See JavaDocs inside GraphView.java for more info. 

- MainActivity includes a simple button used to navigate to LiveActivity and a RecyclerView to display a list of graphs (represented by timestamp) used to navigate to SavedActivity. Both of these activities have the same presentation layer, but are logically different.

- LiveActivity uses the WifiDataProvider to populate the GraphView. WifiDataProvider is a fake data provider that emits a random number between 0-100 (inclusive) every second, which is consumed by a CircularFifoQueue limited to 30 datapoints (which include a value and timestamp). The refreshed data is continuously passed to the GraphView. If the user presses the save button, the current state of the queue is saved to persistent storage.

- SavedActivity reads the datapoints for the saved graph entity and draws the GraphView based on this data. The entry in the database is deleted when the user presses the delete button.

Libraries:

Android Room Persistence Library - https://developer.android.com/topic/libraries/architecture/room.html

- Google announced it this year at I/O, and I figured this would be a good chance to try it out. Storing the Graph and DataPoint objects was too complex for Shared Preferences, so database persistence was the logical choice. In a production app, I would have gone with a more stable, battle-tested abstraction layer, such as Sugar, GreenDao, or SQLDelight.

RxJava/RxAndroid - https://github.com/ReactiveX/RxJava

- I decided to go with an RxJava based implementation for simplifying the interval timer for the data provider and taking things off the main thread. I've been using it considerably in my current work projects, so I thought this would make a good exercise in practice. Alternatives to Rx include using traditional AsyncTasks or AsyncTaskLoaders, noting the usual caveats about using the former concerning lifecycle management and interrupts.

Testing Notes:

- The data provider continuously emits new values every second, even after you leave LiveActivity. This was implemented this way on purpose, so you have the "rolling last 30 seconds of data" readily available when you return to the activity. This can be easily changed (one line of code) to only poll while in the activity, if desired. 
- Grid, datapoints, and axis are all drawn correctly given screen density and orientation.
- Layouts all resize and handle rotation properly.

Known Issues:

- The one-to-many relationship between Graphs and DataPoints in the database isn't entirely correct. Insertion and reads from the db are correctly implemented, but the graph entity is missing the one-to-many delete propogation when an item is removed. This can be solved by creating a proper relation between these two entities (id and graphId fields) so both tables are updated when a db transaction occurs, not just one. 
- Data points on the edges of the graph get cut off. It's only cosmetic, but it can be fixed by making the graph smaller on each side to accommodate the points, or omitting the draw point action on the edges.
- I ran out of time to thoroughly unit test the data provider and database interactor.

Improvement Ideas:

- Graph could have clickable points that show the value and timestamp. You can accomplish this by using a touch listener and figuring out which point is closest to the touch, then updating the UI to show a nice focused state on that point.
- Path drawing is animated so when the line is drawn you get a nice left-to-right draw animation.
