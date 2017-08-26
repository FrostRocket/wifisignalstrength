# wifisignalstrength
Coding Exercise for Nest

Problem: 

Build a working Android app that displays wifi signal strength data in a line graph.

Solution:

From a high-level point of view, to satisfy the home screen requirements I added a simple button used to navigate to LiveActivity and RecyclerView to display a list of graphs (represented by timestamp) used to navigate to SavedActivity. Both are backed by the same view, but each activity updates the dataset differently.

The Live graph screen uses the WifiDataProvider to populate the GraphView. WifiDataProvider (which is a fake wifi data provider) emits a random number between 0-100 (inclusive) every second, which is consumed by a CircularFifoQueue limited to 30 datapoints (as noted in requirements). These updates are then passed to the GraphView. The datapoints (which include a value and a timestamp) from the queue are saved when the user presses the save button.

The Saved graph screen simply reads the datapoints for the saved graph entity and graws the GraphView based on this data. The graph entity in the database is deleted when the user presses the delete button.

The GraphView is a custom view that displays the data in a line chart. The x-axis, y-axis, and dataset are drawn based on window information, datapoint count, and values. No custom library was used. See JavaDocs inside GraphView.java for more info. 

Libraries:

Android Room Persistance Library - https://developer.android.com/topic/libraries/architecture/room.html

Google announced it this year at I/O, and I figured this would be a good chance to try it out. Storing the Graph and DataPoints objects was too complex for Shared Preferences, so database persistance was the logical choice. In a production app, I would have gone with a more stable, battle-tested abstraction layer, such as Sugar, GreenDao, or SQLDelight.

RxJava/RxAndroid - https://github.com/ReactiveX/RxJava

I decided to go with an RxJava based implementation for taking things off the main thread.  I've been using it considerably in my current work projects, so I thought this would make a good exercise in practice. Alternatives to Rx include using traditional AsyncTasks or AsyncTaskLoaders, noting the usual caveats about using the former (lifecycle management, ptasks that don't finish, etc).

Testing Notes / Known Issues:

- Orientation changes automatically scale the x and y axis and cooresponding dataset line.
- The one-to-many relationship between Graphs and DataPoints in the database isn't entirely correct. 
- Data points on the edges of the graph get cut off. It's only cosmetic, but the problem can be solved by making the graph smaller on each side to accomodate the points, or don't draw the point at all on the edges.
- Unit tests are incomplete. I didn't have enough time to completely unit test the data provider and database interactor. 
