# csv2scldj

this is a CSV 2 schema conform line-delimited JSON (finc Solr schema) converter

	this tool is intended for converting CSV data to finc Solr schema conform line-delimited JSON

following parameters are available for configuration at the moment:

	-csv-input-file-name : the (absolute) file path to the input CSV file
	-ldj-output-file-name : the (absolute) file path to the output line-delimited JSON file
	-cell-value-delimiter : the value delimiter in CSV cells that include multiple values
	-help : prints this help

	you can also run this application without setting -csv-input-file-name and -ldj-output-file-name, i.e., then you can simply rely on stdin for input and stdout for output

have fun with this tool!

if you observe any problems with this tool or have questions about handling this tool etc. don't hesitate to contact us
(you can find our contact details at http://slub-dresden.de)



## build

    mvn clean package

## run

    java -jar target/csv2scldj-0.0.1-SNAPSHOT-onejar.jar -csv-input-file-name=[FILE PATH TO YOUR CSV INPUT FILE] -ldj-output-file-name=[FILE PATH TO YOUR LINE-DELIMITED JSON OUTPUT FILE] -cell-value-delimiter=[THE CELL VALUE DELIMITER TO BE ABLE TO SPLIT UP MULTIPLE VALUES IN A CELL]

or

    cd bin 
    csv2scldj -csv-input-file-name=[FILE PATH TO YOUR CSV INPUT FILE] -ldj-output-file-name=[FILE PATH TO YOUR LINE-DELIMITED JSON OUTPUT FILE] -cell-value-delimiter=[THE CELL VALUE DELIMITER TO BE ABLE TO SPLIT UP MULTIPLE VALUES IN A CELL]
