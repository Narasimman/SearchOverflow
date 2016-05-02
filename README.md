# SearchOverflow
A search engine for stack overflow queries

A Query Engine to recommend the best answer from stack overflow for a syntax related or any programming related HowTo’s of a developer. 

Category: Specialized search engine: Implement a search engine that gives high quality results for some particular class of queries.

When a developer is writing code and has to check online for a syntax or api related information, typically the developer would:
-	Open the browser
-	Google for the particular question (Ex: how to iterate over a map in Java)
-	From the list of responses, (usually stack overflow) open couple of tabs of stack overflow pages that answers the query.
-	Look for the best answer (Top voted in stack overflow website)
-	Close the other tabs
-	Copy paste the top rated answer to the development environment and continue working

Our search engine will do all the hard work of querying for the best answer and suggest the developer with the top rated response from the stack Overflow website. The answer to the query will be exactly one response (top rated).



Instructions to Execute the Project:
- Download the data dump at: https://archive.org/download/stackexchange/stackoverflow.com-Posts.7z
- Extract the file Posts.xml
- Run the parser db.py. It will ask for the directory where Posts.xml is availble. Provide it in this format: "C:\Eclipse\workspace\SearchOverflow\dataparser". Now a database has been created with the name full_so_dump.db.
- Proceed to the dir with build.gradle
- run : 
	1. gradle clean
	2. gradle build
	3. gradle indexerjar
	4. gradle retrieverjar 
	
- proceed to folder build/libs
- run the command: java -jar indexer-1.0.jar -index <folder where you want to create the index> -db <path to the full_so_dump.db> This will build your the index
- java -jar retriever-1.0.jar -index <folder where you want to create the index> -db <path to the full_so_dump.db> -q <input query terms>

This should return the code snippet that is the best match for the query term.