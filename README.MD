# Web site scraper

This is a java based web scraping app to gather information about products and prices from several web sites.

Scraper provides a simple API to get data from the HTML responses.

## Requirements

Scraper depends on Java 13 and Maven.

## Configuration

The app configuration can be changed in ./src/main/resources/application.properties

The following variables need to be configured correctly:

* `proxy.apikey` the API key for https://hidemy.name/en/order/webtools/ 
* `security.user-name` the username for API and WEB access
* `security.user-password` the password for API and WEB access
* `spring.datasource.url` postgres url
* `spring.datasource.username` postgres username
* `spring.datasource.password` postgres password

**environment variables** 

Values could be configured by environment variables also:

* `HIDEMY_NAME_APIKEY` 
* `SCRAPER_USER_NAME`
* `SCRAPER_USER_PASSWORD`

**arguments**

Values could be configured by providing arguments:

For example

`java -jar scraper-1.2-SNAPSHOT.jar --proxy.apikey=00001`

property file

`java -jar scraper-1.2-SNAPSHOT.jar --spring.config.location=file:{somewhere}/application-external.properties`

## Usage

**build from source code**

`mvn package`

**run from jar**

`java -jar scraper-1.2-SNAPSHOT.jar --parser.delete-price-history=false`