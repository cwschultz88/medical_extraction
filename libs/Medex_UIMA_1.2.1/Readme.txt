MedEx UIMA
-------------------------------------

Change History
---------------
Changelog on release 1.2.1 (09/12/2013)
1. Fix error output on handling the input like "0.3 mg - 1 mg"

Changelog on release 1.2 (09/05/2013)
1. Improve the performance of encoding function
2. Add option of printing out the offset information

Changelog on release 1.1.1 (05/16/2013)
1. Modify the Sentence Boundary on handling the medication list 

Changelog on release 1.1 (04/22/2013)
1. Added a simple rule engine as an alternative option to drool engine
2. Solved the issue on encoding multi-ingredients drugs
3. Appended some abbreviations and synonyms of drugs in the lexicon  



Introduction
--------------------
A UIMA-based medication extraction tool implemented in Java


Package Structure
-------------------
bin --- include all the ".class" files
desc --- include configuration files for UIMA framework
doc  --- include java doc files
lib --- include all the library files
log --- include all the log files
resources --- include all the dictionary files
sents --- the output of sentence boundary
src --- directory of source code
input --- a sample input directory
output - a sample output directory
uima-output - default output folder for UIMA mode




Install and running
--------------------
There are two versions available for MedEx UIMA software package:

1>	"User version" -- executable, for end users

2>	"Developer version -- for further development


Install instructions for "user version"

Prerequisites
-------------
	1.	Make sure you have Java 1.7.
	
	2.	Download the MedEx_UIMA-1.0.zip file.  Save the file to a temporary location on your machine.
	
	3.	Unzip (extract the contents of) the compressed file you downloaded into a directory that you want to be the install location. 
		
		For example, 
			Windows: "c:\MedEx_UIMA-1.0"
			LINUX: "/usr/bin/MedEx_UIMA-1.0"
		
		This folder is called [MedEx_HOME] and we will refer it later.
	
	4.	[optional] If you need to run the system in GUI mode, UIMA need to be installed at first, please refer to http://uima.apache.org/downloads.cgi to download the
	                   latest version of "UIMA Java framework & SDK", after UIMA is setup, make sure that environment variable "UIMA_HOME" is set to the
			   home directory of your installed directory of UIMA. 
	
	5.	[optional] We use JBoss drool rule engine to implement the rules in semantic tagging, if you want to modify the rules, you can download guvnor which is a GUI 
	                   interface for management of rules for drool engine via http://sourceforge.net/projects/jbpm/files/designer/ and  refer to 
	                   http://docs.jboss.org/drools/release/5.5.0.Final/drools-guvnor-docs/html_single/index.html on the usage of guvnor
	
	 
	

Run MedEx-UIMA on documents
--------------

You can run system in two following modes, all the commands below are executed in the directory of [Medex_HOME]

	1.	Java application mode; system run as normal java application with input parameters, the command line as follows:
			
		Windows user:
			java -Xmx1024m -cp lib/*;bin org.apache.medex.Main -i [input directory] -o [output directory] -b [use sentence boundary or not 'y' as default (y/n)] -f [normalize frequency or not 'n' as default (y/n)] -d [use drool engine or not 'n' as default (y/n)] -p [print out the offset or not]
			
   		Linux/Unix/Mac user:
			java -Xmx1024m -cp lib/*:bin org.apache.medex.Main -i [input directory] -o [output directory] -b [use sentence boundary or not 'y' as default (y/n)] -f [normalize frequency or not 'n' as default (y/n)] -d [use drool engine or not 'n' as default (y/n)] -p [print out the offset or not]
	    
		(Note: the size of each input file should not exceed 100K)
	    
	2.	GUI mode; System run as an UIMA-based application and result could be shown in the UIMA-based GUI, you can follow the three steps below: 
		
		a.	Modify the input and output directory for UIMA CPE application
			
			i.	Open the file of "[MedEx_HOME]/desc/UIMAMedexcollectionReaderDescriptor.xml", go to the tag of "inputDirectory" which is included in the tag of 
				
				"configurationParameterSettings", you can find the text segment below:
			                
					<configurationParameterSettings>
			      			<nameValuePair>
			        			<name>InputDirectory</name>
			        			<value>
			          				<string>input</string>
			        			</value>
			      			</nameValuePair>
			    		</configurationParameterSettings>
			   	
				here "input" is a relative path to folder of [Medex_HOME], which is the defulat input directory, you can modify it as you need 
			
			ii.	Open the file of "[MedEx_HOME]/desc/UIMAMedexConsumer.xml", go to the tag of "OutputDirectory" which is included in the tag of "configurationParameterSettings", 
				
				you can find the text segment as follows:
			    		
					<configurationParameterSettings>
			      			<nameValuePair>
			        			<name>OutputDirectory</name>
			        			<value>
			          				<string>uima-output</string>
			        			</value>
			      			</nameValuePair>
			    		</configurationParameterSettings>
					
				here "uima-output" is default output directory, you can modify it as you need 
	
		b.	Run UIMA CPE program to generate xml files that can be viewed through GUI provided by UIMA framework, command is as follows:
			
			Windows user:  "java -Xmx1024m -cp lib/*;bin org.apache.UIMA.CPE.medex.MedexUIMACPE" 
		        
			Linux/Unix/Mac user:  "java -cp lib/*:bin org.apache.UIMA.CPE.medex.MedexUIMACPE"
	
	 
		c.	Run UIMA GUI to show the result 
		
			Windows user: Run "[UIMA_HOME]\bin\annotationViewer.bat"
			
			Linux/Unix/Mac user: Run "[UIMA_HOME]/bin/annotationViewer.sh"
			
			Then follow three steps bellow:
			
			i.	Choose  "the output directory" in the step(a) as "input directory", the default setting is "[MedEx_HOME]/uima-output/"
			
			ii.	Choose  "[MedEx_HOME]/desc/UIMAMedexDrugAnnotatorDescriptor.xml" as "Type System or AE descriptor" 
			
			iii.	Click button "View" and then an popup window will be raised to let you choose the output xml file that you want to view, double click the item to check the result
 

	3.  For each line in the output files, the output includes all the drug signatures and their positions (start from 0):
		  
		  Sentence index (start from 1) 
		  Sentence text
		  Drug name      (e.g. 'simvastatin[0, 11]')
                  Brand name     (e.g. 'zocor[12, 17]')
                  Drug form      (e.g. 'tablet[19, 25]')
                  Strength       (e.g. '10mg[20, 24]')
                  Dose amount    (e.g. '2 tablets[2, 11]')
                  Route          (e.g. 'by mouth[10, 18]')
                  Frequency      (normalized frequency) (e.g. 'b.i.d.(R1P12H)[10, 16]', 
                                  'R1P12H' is the TIMEX3 format of 'b.i.d.') 
                  Duration       (e.g. 'for 10 days[10, 21]')
                  Neccessity     (e.g. 'prn[10, 13]')
                  UMLS CUI
                  RXNORM RxCUI
                  RXNORM RxCUI for generic name
		  Generic name   (associated with RXCUI code)


Install instructions for "developer version"
---------------------------

Prerequisites
-------------
	1.	Make sure you have Java 1.7 and Set the JAVA_HOME environment variable as your root directory of JAVA
	
	2.	Refer to http://uima.apache.org/downloads.cgi to download the latest version of "UIMA Java framework & SDK", after UIMA is setup, make sure that environment variable "UIMA_HOME"
	    	
		is set to the home directory of your installed directory of UIMA. 
	
	3.	Download and install Eclipse if you don't already have it.
	
	4.	Find UIMA Eclipse plug-ins
		
		a.	Start Eclipse, pick the menu Help ? Install New Software, Click on "Add" button to add new website for updates and give a name to it. 
		    	
			The website is: "http://www.apache.org/dist/uima/eclipse-update-site", and press OK. On the previous page will now appear this new Site, and it should be checked.

Compile and install
--------------

	1.	Download the MedEx_UIMA_dev-1.0.zip file, extract the zip file in [MedEx_DEV_HOME]
	
	2.	Set up a class path variable named "UIMA_HOME", whose value is the directory where you installed the UIMA SDK. This is done as follows:
		
		a.	Go to Window -> Preferences -> Java -> Build Path ->Classpath Variables click "New" 
		
		b.	Enter UIMA_HOME (all capitals, exactly as written) in the "Name" field
		
		c.	Enter your installation directory (e.g. C:/apache-uima) in the "Path" field
		
		d.	Click "OK" in the "New Variable Entry" dialog
		
		e.	Click "OK" in the "Preferences" dialog
		
		f.	If it asks you if you want to do a full build, click "Yes"
	
	3.	Open Eclipse and import the project of "MedEx_UIMA"
		
		a.	Select the New/Java Project menu option to create java project named "MedEx_UIMA", a folder named "MedEx_UIMA" is created automatically. 
		
		b.	Copy all the files located at [MedEx_DEV_HOME] to MedEx_UIMA folder.

		c.      Right click project "MedEx_UIMA" and select "refresh", then all the folders are shown in current project.
		
		d.	Right click project "MedEx_UIMA" and select "properties", then click tab "Libraries", click button "Add External JARs" on the right side, 
			select all the files in "bin" folder located at the root of project "MedEx_UIMA"
		
	
	4.	Run program in the development environment
		
		a.	Run it as a normal java program, locate "MedEx_UIMA/src/org/apache/medex/Main.java", then right click and run it as java application. 
		    	
			Make sure that you specify the input and output folders before running it (set arguments in runtime configuration i.e. "-i input –o output")
		
		b.	Run it using UIMA CPE, locate "MedEx_UIMA/src/org/apache/UIMA/CPE/medex/MedexUIMACPE.java" (the class load the CPE configuration file and run it), 
		    	
			then right click and run it as java application.
