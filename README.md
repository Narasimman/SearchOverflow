# SearchOverflow
A search engine for stack overflow queries


Instructions to Execute the Project:

- Download the data dump at: https://archive.org/download/stackexchange/stackoverflow.com-Posts.7z. 
		- The software to unzip this dump can be found here: http://downloads.sourceforge.net/project/sevenzip/7-Zip/15.14/7z1514.exe?r=&ts=1462420514&use_mirror=liquidtelecom
- Extract the file Posts.xml
- The XML parser db.py can be found in the dataparser folder. 
		- Instruction to run the XML parser db.py: python db.py <dir where the Posts.xml file is available>. 
		- This will create the db dump "full_so_dump.db" in the same location where db.py is present.
- Proceed to the dir with build.gradle
- run : 
	1. gradle clean
	2. gradle build
	3. gradle indexerjar
	4. gradle retrieverjar 
	
- proceed to folder build/libs
- To build the Index:  java -jar indexer-1.0.jar -index <folder where you want to create the index> -db <path to the full_so_dump.db> 
		- This will build your the index in the specified folder.
- To retrieve the solution for a query: java -jar retriever-1.0.jar -index <folder where you want to create the index> -db <path to the full_so_dump.db> -q <input query terms>
- To deploy the website: 
	- go to the folder build/libs/
	- you will find the file SearchOverflow-1.0.war
	- Add it to the web apps directory in Apache Tomcat. 

This should return the code snippet that is the best match for the query term.