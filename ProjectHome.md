From working on many projects, I've extracted a set of “master” build scripts that encapsulated complexity.  Complex J2EE projects can now have individual Ant scripts that are only 5-10 lines long, but retain all their functionality.

This was done by treating Ant scripts like objects, allowing child scripts to “inherit” functionality from the parents.  By picking the right “parent” script to inherit from, a child script only needed code unique to that application.

This was the subject of my JavaOne 2009 paper (TS-4166) entitled “Object-oriented Ant Scripts for the Enterprise”.

These scripts are designed to be used as Subversion Externals, but if Subversion's not your source control system, you can always just copy them.


New ====> Gradle master scripts!  The build/master-gradle directory has the beginnings of my master Gradle scripts.

Similar to master Ant scripts, the desire is to put common behavior into a Subversion external which is called by the app Gradle script - keeping it small (ideally, one line), with just jar dependencies being specified.

I could have used compiled plugins, but I like the ability for folks to see the Gradle script, allowing them to learn/copy/modify.