GalleryInListView
=================

This Android program loads all gallery images from a device and displays them in a listview.  Performance-specific features that are implemented are:

- all images are resampled to 200x200,
- ViewHolder pattern is implemented,
- images are initially loaded in a background asynctask,
- images are added to LRUCache and consecutively loaded from the cache,
- asynctask is limited to 50 concurrent tasks.

While the program does not check for out-of-memory errors, the performance of listview with images is significantly improved and gives a head start for future development.
