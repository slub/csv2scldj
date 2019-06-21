# csv2scldj

this is a CSV 2 schema conform line-delimited JSON converter

	this tool is intended for converting CSV data to schema conform line-delimited JSON

following parameters are available for configuration at the moment:

	-csv-input-file-name : the (absolute) file path to the input CSV file
	-schema-file-name : the (absolute) file path to the schema file (that should be used for schema conform parsing)
	-ldj-output-file-name : the (absolute) file path to the output line-delimited JSON file
	-cell-value-delimiter : the value delimiter in CSV cells that include multiple values
	-help : prints this help

	you can also run this application without setting -csv-input-file-name and -ldj-output-file-name, i.e., then you can simply rely on stdin for input and stdout for output

have fun with this tool!

if you observe any problems with this tool or have questions about handling this tool etc. don't hesitate to contact us
(you can find our contact details at http://slub-dresden.de)

## schema file

in order to be able to use this tool, you first need to define a schema file, see, e.g., [finc_solr_schema.csv](src/test/resources/finc_solr_schema.csv)

### schema file definition

the schema file is a CSV file that consists of rows and columns, each row is a field definition

| column | column ID | Excel sheet ID | description | allowed values | required |
|--------|-----------|----------------|-------------|----------------|----------|
|1|0|A|the field name, i.e., the column name of a column in the input CSV file|string values without spaces inbetween|yes|
|2|1|B|multivalued: indicates, whether the field is allowed to have multiple values or not (default (if value is not defined, i.e., cell is blank or empty) is 'false', i.e., multiple values are not allowed, i.e., singlevalued)|'true' or 'false'|no|

## build

    mvn clean package

## run

    java -jar target/csv2scldj-0.0.2-SNAPSHOT-onejar.jar -csv-input-file-name=[FILE PATH TO YOUR CSV INPUT FILE] -ldj-output-file-name=[FILE PATH TO YOUR LINE-DELIMITED JSON OUTPUT FILE] -cell-value-delimiter=[THE CELL VALUE DELIMITER TO BE ABLE TO SPLIT UP MULTIPLE VALUES IN A CELL]

or

    cd bin 
    ./csv2scldj -csv-input-file-name=[FILE PATH TO YOUR CSV INPUT FILE] -ldj-output-file-name=[FILE PATH TO YOUR LINE-DELIMITED JSON OUTPUT FILE] -cell-value-delimiter=[THE CELL VALUE DELIMITER TO BE ABLE TO SPLIT UP MULTIPLE VALUES IN A CELL]

You can set the logging root directory with help of parameter ```-logging-dir [INSERT YOUR LOGGING DIRECTORY HERE]``` at ```csv2scldj``` command (```-Dlogging_dir=[INSERT YOUR LOGGING DIRECTORY HERE]``` when calling the java library directly). The default logging root directory is the current path where the command is executed from.

## restrictions

currently, only flat schemata are supported