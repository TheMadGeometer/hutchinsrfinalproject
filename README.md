#README

step-by-step instructions for initializing and running the NHL statistics website. 
There are two options for running the application. If you’re using a Mac, you can use either option, otherwise you must 
use option two. In either case, you will need the following software, and you will need to ensure that it can be called 
from your command line.

##Option 1

1. Git – if you do not have git installed on your machine, please follow the instructions here. The code for the 
application is stored in a public Git repository, and you will need git to download it. 

2. Google Chrome – You will need a web browser to view the front end of the application, and so far the application has 
only been tested with Google Chrome. If you need to download Chrome, go here.

3. Java JDK 8 

4. Sbt 1.0+ – sbt is the Scala Build Tool. The NHL Statistics Database is a full-stack application that includes an API 
server written in Scala. sbt is used within the script to compile the project. You can download sbt here. 

5. MySQL Server 8+ – the application is configured to hit MySQL on localhost port 3306, so please run it there or modify 
the source code. 

6. Finally, the application is going to run an API server that is currently configured to run on port 8080, so it assumes
there is nothing bound to that port already. 

Option 1: Run the Bash scripts on your Mac, if you have one:

When you first download the repository, navigate to the root directory of the project, and run:

`./run_setup.sh <username> <password>`

Where <username> and <password> are your mySQL username and password. Each time you run the application after that, run 
the command:

`./run.sh <username> <password>`

When you run the application, an API server begins running in the background. This server mediates requests from the 
website front end to the database that keep the database updated with the current statistics.  

NOTE: that each time you run this script, when you terminate it, you will have to manually kill the APIServer process. The 
best way to do this is to go to the command line and type `ps`, get the pid number for the APIServer process, and then 
type `kill <pid>`, where you type the pid number you just got instead of <pid>.

##Option 2: Run the application using java:

sbt gives you the capability to compile the source code in the repository into one large file called an assembly jar. To
run the application like this you will need JDK 1.8 installed on your machine. If you need to download the JDK, go here.
You will also need Git installed on your machine to retrieve the project. 

Once you have JDK 1.8 and git installed on your machine, you should create a folder to hold the project. Navigate inside 
of the folder that you created for that purpose and enter the following command into your terminal:

`git clone git@github.com:TheMadGeometer/hutchinsrfinalproject.git`

NOTE: The above command will only work if you have configured git to use ssh. If you are using https, which is the 
default, the command will be:

`Git clone https://github.com/TheMadGeometer/hutchinsrfinalproject.git` 

Once the project has downloaded, navigate to the top-level directory of the git repository (it’s the one with build.sbt 
in it), and run the command:

`sbt assembly` -- This will build the assembly jar. 

Start an instance of MySQL server on port 3306, if one is not running there already. 

Run the finalProjectBackEndScript.sql scrip, which is located in the top-level directory of the github project, against 
the running instance of MySQL Server, perhaps by opening MySQL Workbench and running it there. 

Once the database script has run, type the following command into the terminal:

`java -cp "./target/scala-2.12/hutchinsrfinalproject-assembly-0.1.jar" clients.NHLJsonClient <username> <password>`

The above command will run a web scraper that will populate the database with the current standings and player 
statistics for the NHL. When this has finished, enter this command:

`java -cp "./target/scala-2.12/hutchinsrfinalproject-assembly-0.1.jar" APIServer <username> <password>`

The above command runs the API server that mediates communication between the website and the running instance of MySQL 
that’s holding the database backend.

Open a new terminal window and navigate to the top-level directory of the git repository that you cloned. Enter the 
following command:

(Mac or Linux): `open -a "Google Chrome" ./website/index.html`

(Windows): `start chrome ./website/index.html`

This command should open Google Chrome and display the web page. This webpage should include standings for each team in 
the NHL, organized by division. You may have to refresh the browser, depending on the timing with the server initialization. 

