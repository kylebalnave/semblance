Semblance
=========

[1]: https://github.com/kylebalnave/affirmation        "Affirmation"
[2]: https://github.com/kylebalnave/disparity  "Disparity"
[3]: https://github.com/kylebalnave/snapshot    "Snapshot"

A command line tool used to run helper tasks during frontend development.  

Semblance contains base Classes for running and reporting actions from included modules.

    sembalnace.reporters.JunitReport.java
    sembalnace.reporters.HtmlResport.java
    sembalnace.reporters.SystmeLogReport.java
    sembalnace.reporters.XmlReport.java

    sembalnace.results.ErrorResult.java
    sembalnace.results.FailResult.java
    sembalnace.results.PassResult.java

    sembalnace.runners.MultThreadRunner.java
    sembalnace.runners.Runner.java


### Runner Modules
-   [Affirmation][1]

    Uses the W3C web service to validate html

-   [Disparity][2]

    Compares sets of images and creates a difference image

-   [Snapshot][3]

    Save screenshots of urls using Selenium WebDriver.  When used in conjunction before [Disparity][2], it can be used for CSS regression testing.

### Commandline Usage

    java -jar dist/Semblance.jar -config ./config.json -action dist

### Example Config

The below configuration will take two screenshots of [BBC Homepage](http://www.bbc.co.uk/) and compare it to the previous run.  You will need to make sure the correct module *.jar files are included.

    {
       "classpaths" : [
           "../disparity/dist/",
           "../snapshot/dist/"
       ],
       "dist":{
          "runners":[
             {
                "className":"snapshot.runners.SnapshotRunner",
                "out":"./snaps/",
                "urls":[
                   "http://www.bbc.co.uk/"
                ],
                "dimensions":[
                   [
                      2048,
                      1536
                   ],
                   [
                      1600,
                      1200
                   ]
                ],
                "drivers":[
                   {
                      "name":"firefox",
                      "version":"",
                      "hub":""
                   }
                ],
                "reports":[
                   {
                      "className":"semblance.reporters.JunitReport",
                      "out":"./reports/snapshot.junit"
                   }
                ]
             },
             {
                "className":"disparity.runners.DisparityRunner",
                "in":"./snaps/",
                "out":"./snaps/",
                "fuzzyness":10,
                "reports":[
                   {
                      "className":"semblance.reporters.JunitReport",
                      "out":"./reports/disparity.junit"
                   }
                ]
             }
          ]
       }
    }
