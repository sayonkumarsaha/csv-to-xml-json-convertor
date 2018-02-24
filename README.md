# CSV to JSON and XML convertor

*Summer 2017*:
Test project to generate JSON and XML files from a given CSV file. The aim is to dedicate 24hrs to write modularized code, keeping it clean with error handling and logging.

## Requirement:
The tool (.jar file) can be tested on a x86-64 machine with the most recent unmodified Debian stable release with JAVA installed. All external libraries are already packaged with the code in the .jar file.

## How to execute the tool:
1. Place the **dataConvertor.jar** file in your directory. 
2. Run the command **java -jar dataConvertor.jar** from that directory on your terminal.
3. Enter your choice from the menu that pops up on therminal:
    **1- CSV to JSON
    2- CSV to XML
    3- Both
    Enter Conversion Choice:**
4. Enter the complete path of the **.CSV** (for example: *data/workspace/hotels.csv*) when prompted.

## Result:
The JSON or/and XML file will be generated with the same name as the CSV file (for example: *hotels.json*) and stored in the same source directory (for example: *data/workspace/*) as the csv file. The log file called **DataPipeline_logfile.log** will be generated in the same directory with the logs of information, warnings and exceptions.

## Source Code:
The source code written in JAVA is attached. It can be imported and compiled using any standard IDE like Eclipse.

