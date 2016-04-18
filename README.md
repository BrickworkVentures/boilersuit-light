#What is Boilersuit
<b>Boilersuit Light</b> is a front-end application to <b>extract, prepare, transform and analyse structured data.</b> There is no need to set up a database connection to start working -- Boilersuit automatically creates a local <a href="https://www.sqlite.org/">SQLite</a>  database file. This works astonishingly well with data up to a couple of gigabytes (in theory almost <a href="https://www.sqlite.org/faq.html#q8">unlimited</a>).


##Boilerspeech and SQL short-hand notation

  Transformation logic is defined in a special script language which is a superset of SQL. That means you can use native SQL if you like:
  ```
CREATE TABLE germanclients AS SELECT id, name, location FROM clients WHERE location='Germany' ORDER BY name ASC;
  ```
  The shorthand BS like notation would be as in the tutorial script below:
  ```
-- load clients.csv from /home/marcel/sample folder
cd /home/marcel/sample;                 -- change dir
clients := clients.csv;                 -- import into table clients

-- see what's in it (TIP: HIT ENTER TWICE to show it in tabular view)
clients;

-- to simply see tables in the BS light command line, you can also omit the semicolon:
clients                                 -- (although in a runnable script that wouldn't work)

-- manipulate
germanclients := clients(id, name{ASC}, location{='Germany'});

-- how many are they
#germanclients;                         -- count rows in germanclients
#germanclients(name like 'C%')          -- count rows with name starting with C

-- match against list of existing clients, using name
m_result := MATCH germanclients(name) ON allclients(name) USE THRESHOLD(0.8);

-- export
m_result =: m_result.xls;               -- export as excel
m_result =: m_result.csv;               -- export as csv
=:                                      -- export all tables to csv files
  ```
More handy <b>short notations</b> can be found <a href="#HandySQL">here.</a> Apart from the handy short notations for frequently used SQL queries

##Pre-Processing Functions

As an additional feature, BS provides <b>pre-processing functions</b> for attributes, e.g. to 'suck' out parts of an attribute based on regular expressions,
or to conveniently format numbers and dates, and others like so:
```
germanclients2 := germanclients(id, SUCK(name, [A-Za-z]+, 1) AS firstname, SUCK(name, [A-Za-z]+, 1) AS lastname);
```
More on this <a href="#bsf">here.</a>


#Boilerspeech
##Short Notation
  <a name="HandySQL"></a>
  <h3>SELECT</h3>
  
 Transformation logic is defined in a special script language which is a superset of SQL. That means you can use native SQL if you like:
 ```
CREATE TABLE germanclients AS SELECT id, name, location FROM clients WHERE location='Germany' ORDER BY name ASC;
 ```
 The shorthand BS like notation would be
 ```
germanclients := clients(id, name{ASC}, location{='Germany'});
 ```
  
  <h3>COUNT</h3>
  
 ```#table;
#mytable(attr1 like 'some value');
#cars(serialnumber = 12345);
 ```
  
  <h3>LEFT OUTER JOIN</h3>
  ```
-- SELECT * FROM ourcompanies oc LEFT OUTER JOIN companylist cl ON oc.name = cl.name
exactmatch := ourcompanies(name)->companylist(name);
  ```
  <h3>FREQUENCY TABLES</h3>
  
 Often we have a code, or an id, in two tables and we would like to count how often
 they occur in any of both tables (if at all), and also see which keys exist only in the one but
 not in the other, and vice versa.
  
  ```
what_id_exists_where := officialtable.some_id ./. previoustable.some_id;
  ```
  <h3>CREATE / DROP TABLES</h3>
  
 Creating/Dropping a table in Boilersuit is as simple as typing
 ```
+mytable(*id, attribute1, attribute2); -- create
-mytable; -- drop
 ```
  
##Preprocessing Functions
  <a name="bsf"></a>
  A range of preprocessing functions are available:
  <table>
 <tr><th>Function</th><th>Purpose</th></tr>
 <tr>
 <td>bsfMagicDate</td>
 <td>Tries to recognize a date expression from various formats into the BS default format MM.dd.yyyy.
 Works even if the formats in the source data vary from row
 to row. For unrecognized expressions, a warning will be shown.
 ```
 -- checking against common formats
 transformed := source(, magicDate(MYDATE) AS STANDARDIZED_MY_DATE);
  ```
 If a preferred set of allowed formats should be used, they can be added as arguments:
 ```
 -- checking against y-m-d and y.m.d:
 transformed := source(, magicDate(MYDATE, y-m-d, y.m.d[,...]) AS STANDARDIZED_MY_DATE);
  ```
 </td>
 </tr>
 <tr>
 <td>bsfFormatNumber</td>
 <td>Constructs a string using a pre-existing number in a certain way often useful to construct artificial IDs
 for things. Lets assume we have a table with id's
 ```
id
-----
1
239
4000
```
Then, we may want to construct id's of uniform length like so:
```
result := table(id, formatNumber(id, A-DDDD) AS NEW_ID);

-- Result will be as follows:
id NEW_ID
----  -----------
1     A-0001
239   A-0239
4000  A-4000
```
 </td>
 </tr>
 <tr>
 <td>bsfSuck</td>
 <td>"Sucks" out specific patterns from the source data based on a regular expression</td>
 </tr>
 <tr>
 <td>bsfHash</td>
 <td><a href="https://en.wikipedia.org/wiki/Hash_function">Hashes</a> the source data using the String.hashCode method (probably depends on JVM used to run BS).
 If the hash value is > 0, an 'X' is added to the hash as a prefix, otherwise and 'Y' - this explains the Xxxxxx resp. Yxxxxx format of the hash result.
 </td>
 </tr>
 </table>

##Additional Functionality
There is also other elements like fuzzy matching, mapping and other tools. They are described <a href="http://htmlpreview.github.com/?https://github.com/BrickworkVentures/boilersuit-core/blob/master/src/main/doc/interpreters.html" target="_blank">here</a>

#Configuration File
The behaviour of the console can be configured via a configuration file. It's optional; if it's not there, the default values apply.
<b>boilersuit.conf</b> (showing default behaviour):
```
-- show less than 100 entries when displaying result in console:
console.numberOfResultRows = 100
```

 <a name="How to get it"></a>
#How to get it
Boilersuit comes in 3 pieces:
 <table>
<tr>
  <th>Component</th>
  <th>What is it</th>
  <th>Get it</th>
</tr>
<tr>
  <td>boilersuit-core</td>
  <td>
 This is the engine behind BS containing what you need to connect to databases, interpretation logic needed to run scripts etc. It is a pre-requisite of any Boilersuit application and it's a java library.
  </td>
  <td>
 Open Source, at <a href="https://github.com/BrickworkVentures/boilersuit-core/">GitHub</a>
  </td>
</tr>
<tr>
  <td>boilersuit-light</td>
  <td>
 The free version of BoilerSuit consisting of a simple command-line like interface to manipulate and view data, to play around with it all and to run your BS scripts. It contains the boiler-suit core library and won't run without it
  </td>
  <td>
 Open Source, at <a href="https://github.com/BrickworkVentures/boilersuit-light/">GitHub</a>
  </td>
</tr>
<tr>
  <td>boilersuit-professional</td>
  <td>
 The professional version of Boilersuit contains special features targeted at various business needs such as data migration, reconciliation, predicitve modelling.
  </td>
  <td>
 <a href="mailto:info@brickwork.ch">Contact us</a>
  </td>
</tr>
</table>

#Build and Run it
##Build it
###Step 1: Get Maven and Java
If you don't have it (test by launching ```mvn``` in your command line), get it <a href="https://maven.apache.org/guides/getting-started/">here</a>, or should you not have Java, get the <a href="http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html">Java SDK</a>.
###Step 2: Get a clone of boilersuit-light
Create/enter an empty directory, enter it, and launch
```
git clone https://github.com/BrickworkVentures/boilersuit-light.git
```
then <b>go into the boilersuit-light directory</b>!
###Step 3: Dependencies
THE FOLLOWING DEPENDENCIES ARE NOT AVAILABLE IN PUBLICLY AVAILABLE MAVEN REPOSITORIES, SO INSTALL THEM LOCALLY, LIKE SO:
```
mvn install:install-file -Dfile=src/lib/boilersuit-core.jar -DgroupId=ch.brickwork.bsuit -DartifactId=core -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=src/lib/darcula.jar -DgroupId=com.bulenkov.darcula -DartifactId=darcula -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=src/lib/dragonconsole.jar -DgroupId=com.eleet.dragonconsole -DartifactId=dragonconsole -Dversion=3.0.0 -Dpackaging=jar
```
###Step 4: Build
```
mvn clean compile assembly:single
```
###Step 5: Copy native dependencies
THE NATIVE DEPENDENCIES OF SQLITE4JAVA MUST BE COPIED INTO THE SAME DIRECTORY AS THE EXECUTABLE JAR, LIKE SO:
```
cp src/lib/native/* target
```
##RUN IT
```
java -jar target/boilersuit-light.jar
```
