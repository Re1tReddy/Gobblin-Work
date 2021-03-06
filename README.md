# Gobblin-Work


Gobblin is a universal data ingestion framework for extracting, transforming, and loading large volume of data from a variety of data sources, e.g., databases, rest APIs, FTP/SFTP servers, filers, etc., onto Hadoop. Gobblin handles the common routine tasks required for all data ingestion ETLs, including job/task scheduling, task partitioning, error handling, state management, data quality checking, data publishing, etc. Gobblin ingests data from different data sources in the same execution framework, and manages metadata of different sources all in one place. This, combined with other features such as auto scalability, fault tolerance, data quality assurance, extensibility, and the ability of handling data model evolution, makes Gobblin an easy-to-use, self-serving, and efficient data ingestion framework.

Topics Covered :     
----------------

1. Extracts records from JSON files and store them as Avro files in HDFS/Local .                                   
2. Pushing Json data from Kafka to Gobblin and saving them as Avro files in HDFS.       
3. Implementing custom DataWriter , to write data as sequence files in HDFS.                           



This Gobblin project uses the following technologies:
----------------------------------------------------
    * ZooKeeper    
    * Kafka 0.8.2     
    * Hadoop 2.6.0      
    * Gobblin 0.8.0      
    * Maven 3.3.9        

Gobblin Set-up:              
--------------
#Step 1:         
	Download the latest Gobblin release from the <a href="https://github.com/linkedin/gobblin/releases">Release Page</a>    
	or          
	wget https://github.com/linkedin/gobblin/releases/download/gobblin_0.8.0/gobblin-distribution-0.8.0.tar.gz    
	
 #Step 2 :     
 
	Extract gobblin-distribution-0.8.0.tar.gz to directory /opt/gobblin (mkdir /opt/gobblin/)
	tar -zxvf gobblin-distribution-0.8.0.tar.gz
	
#Step 3:                 

	cd /opt/gobblin/gobblin-dist                 
	create following folder :          
	mkdir job_work         
	mkdir job_conf          
	mkdir logs        
	
#Step 4 :           

vi ~/.bash_profile          

	export GOBBLIN_JOB_CONFIG_DIR=/opt/gobblin/gobblin-dist/job_conf                   
	export GOBBLIN_WORK_DIR=/opt/gobblin/gobblin-dist/job_work                     
	export HADOOP_HOME=/opt/hadoop-2.6.0                           
	export PATH=$PATH:$HADOOP_HOME/bin                      
	export KAFKA_HOME=opt/kafka_2.10-0.8.2.0                          
	export PATH=$PATH:$KAFKA_HOME/bin                  
source ~/.bash_profile  

Running Your First Gobblin Job:        
-------------------------------
Goal of this example :             	

To utilize Gobblin that will:      

	1. Extract JSON Messages from a simple JSON file.
	2. Transform the JSON Message payload into the AVRO format.
	3. Push the output data into HDFS/Local .

Steps :               
* Copy <a href="https://github.com/Re1tReddy/Gobblin-Work/blob/master/gobblin-demo/src/main/resources/json-gobblin-hdfs.pull">json-gobblin-hdfs.pull</a> to job_conf folder which is created in step 3, and make sure that the environment variable GOBBLIN_JOB_CONFIG_DIR, JAVA_HOME are set correctly. Make the changes in the json-gobblin-hdfs.pull accordingly .                                 

* Make sure the environment variable GOBBLIN_WORK_DIR is pointed to job_work properly because, Gobblin will write job output as well as other information there, such as locks and state-store (for more information, see the <a href="http://gobblin.readthedocs.io/en/latest/user-guide/Gobblin-Deployment/#Standalone-Deployment"> Standalone Deployment page</a>) .               
* Download this gobblin-demo project,extract it and build the jar.               
	wget https://github.com/Re1tReddy/Gobblin-Work          
	mvn clean install            
	copy the gobblin-demo-0.1.0-SNAPSHOT-jar-with-dependencies.jar to /opt/gobblin/gobblin-dist/lib folder.           
	copy simplejson.json to  opt/gobblin/gobblin-dist/ folder.                 
	
* Launch Gobblin Job:             
	cd /opt/gobblin/gobblin-dist/ 
	and run the command    bin/gobblin-standalone.sh start          

* Observe the OutPut in HDFS :           
	hadoop fs -ls /opt/gobblin/output       

 * To store the output in Local :                 
	comment out the following lines in json-gobblin-hdfs.pull as below and launch the job .                     
	#fs.uri=hdfs://localhost:9000                       
	#writer.fs.uri=hdfs://localhost:9000                       
	#state.store.fs.uri=hdfs://localhost:9000                            

	Now you can observe the output in the /opt/gobblin/output  directory as an Avro file.	   
	To see the content of the job output, use the Avro tools to convert Avro to JSON. Download the latest version of Avro tools             (e.g. avro-tools-1.8.1.jar): and run           
	java -jar avro-tools-1.8.1.jar tojson --pretty [job_output].avro > output.json                


See also for references:            
-----------------------
* http://gobblin.readthedocs.io/en/latest/Getting-Started/
* http://gobblin.readthedocs.io/en/latest/Gobblin-Architecture/ 
* http://gobblin.readthedocs.io/en/latest/case-studies/Kafka-HDFS-Ingestion/
* http://gobblin.readthedocs.io/en/latest/user-guide/Gobblin-Deployment/
------------------------------------------------------------------------------------------------------------------------------------     

You can reach me for any suggestions/clarifications on  : revanthkumar95@gmail.com                                              
Feel free to share any insights or constructive criticism. Cheers!!                                                           
#Happy Gobbling!!!..  
