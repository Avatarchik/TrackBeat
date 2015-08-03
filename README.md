# TrackBeat

TrackBeat is an Android application to extract human pulse information just from a video of the Human head. It does this by measuring subtle head motion caused by the Newtonian reaction to the influx of blood at each beat. The implemented method tracks features and performs Principal Component Analysis (PCA) to decompose trajectories into a set of component motions. It then chooses the component that best corresponds to heartbeats based on its temporal frequency spectrum. Finally, we analyze the motion projected to this component and identify peaks of the trajectorie, which corresponds to heartbeats. All this is optimized to work on a constrained computing platform of an Android Phone.
The app is a portable implementation of the algorithm described in the paper [Detecting pulse from head motions in video](http://people.csail.mit.edu/mrub/vidmag/papers/Balakrishnan_Detecting_Pulse_from_2013_CVPR_paper.pdf).

## Dependencies

- [OpenCV for Android](http://opencv.org/platforms/android.html)
- [EJML](http://ejml.org/wiki/index.php?title=Main_Page)
- [JTransforms](https://sites.google.com/site/piotrwendykier/software/jtransforms)

## Screenshots

![Homescreen](./Screenshots/trackbeat_homescreen.png?raw=true "Homescreen")
![Action1](./Screenshots/trackbeat_samson1.png?raw=true "Trackbeat in Action")
![Action2](./Screenshots/trackbeat_samson2.png?raw=true "Trackbeat in Action")
![Finalscreen](./Screenshots/trackbeat_finalscreen.png?raw=true "Final screen")

## Credits

This work was done as part of the course requirements for [CS290I course on Mobile Imaging, instructed by Prof. Matthew Turk at UCSB in the Winter of 2015](http://www.cs.ucsb.edu/~mturk/imaging/). If you go to this link, you can see that we won the best project award for this class. Yay !!

## Note

We might have inadvertantly used some pictures off the internet without acquiring necessary permissions. Kindly let us know if you are the owner of one of these pictures and want us to take it down.

