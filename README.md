# Simple Memory Game for Android

This is an extremely simple memory game for Android with haptic feedback and large images. Initially made for my own grandfather (whose memory is waning), since all comparable applications either had advertisements (virtually anything on Google Play) or had cards that were too small for his fingers (GCompris; though it is otherwise an amazing piece of software).

Posting the code here in case anyone else finds it useful. Grid size and time the cards stay revealed are currently hardcoded, so don't expect it to work for yourcase without at least some rework.

## Building

To build the application, first regenerate the needed image files from the SVG sources using `./images/convert-all.sh`, then run `./gradle assembleRelease`.

## License

MIT-licensed in memory of my grandfather's self-sacrificial willingness to help others. Truly, one of the best men I've had the honor of knowing.

Thanks to [Kenney](https://kenney.nl) and [Gerald G. / OpenClipArt](https://openclipart.org/artist/Gerald_G) for the vast majority of assets used.
