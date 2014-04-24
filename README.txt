SimpleOCR is a simple OCR app for reading numbers/letters from picture. In given time interval it takes picture, gives that pictures to Tessaract library
for image processing and then the library gives back found letters/numbers. For app to work, you need to include tess-two library to the workspace and include it in this app 
(you can read more at https://github.com/rmtheis/tess-two). You need to compile it first, google it.

-tolerance value can be chosen with PickThresholdActivity. It it used in createBlackAndWhite method for transforming bitmap in to its black&white representation.
-minutes and seconds are saved in settings for Timer class who then fires camera pictures in a given interval
-app in this state is not usable in any given situation. For that purpose, you can use PickThresholdActivity to set tolerance, then scan the scene to find letters/numbers.
-code is easy to read and follow. Need improvements in some areas.

TODO:
	-going on PickThreshold activity will fill Activity stack (back button will go on the same activity). Set Intent flag i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 while launching the activity to fix the problem. That creates a new problem with the editTextBox not getting any input sometimes, easy fix proly.
	-use tolerance in PickThresholdActivity (now it's set to 128)
	-automatic tolerance setter in CameraPreview when letters/numbers not found on picture? 
	-better OCR library?
	-Better algorithm for picture transformation?
	-whatever with the given picture!

Contact me if you have any questions.