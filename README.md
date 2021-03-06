Arkifier
=======

An overwrought tool for minitng NOIDs and binding ARKs for the PUDL.

To Build
---------
 1. Check out the latest TAG from SVN. 
 2. Confirm configuration in ./src/main/resources/arkifier.properties. Make 
    sure you're pointing at the production ARK service 
 3. From this directory (arkifier/) issue "mvn package"
 4. Copy ./target/arkifier-dist.tar.gz to wherever you'd like and unpack 
    (`tar xzvf arkifier-dist.tar.gz`)
 5. Make sure your machine can interact with the ARK service without having to 
   go through CAS. To do this you need to update the Apache configs with your 
   machine's IP.

To Use
------
From wherever you unpacked the distribution, call the shell script 
(./arkify.sh) without any arguments or options to view help.

Put the script on your PATH if you want.

IMPORTANT
--------
When debugging, be sure to keep the Arkifier.uri property set to the test 
instance. When ready for a release:

 1. Take "-SNAPSHOT" off of the version element in the pom
 2. Set the Arkifier.uri property to the production instance
 3. Create a tag named after the version
 4. Set the Arkifier.uri property back to the test instance
 5. Increment the version and reappend "-SNAPSHOT" in the pom
 6. Check back in on the trunk.

Comments should reflect these steps and corrresponding tags, revisions, etc.
