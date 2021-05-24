[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/s4hana-ext-migrate-historical-data)](https://api.reuse.software/info/github.com/SAP-samples/s4hana-ext-migrate-historical-data)


# SAP S/4HANA Cloud Extensions - Migrated Sales Order Data
This repository contains the sample code for the [Migrated Sales Order Data tutorial](https://tiny.cc/s4-migrate-historic-data).

*This code is only one part of the tutorial, so please follow the tutorial before attempting to use this code.*

## Description

The [Migrated Sales Order Data tutorial](https://tiny.cc/s4-migrate-historic-data) showcases an extension to an SAP S/4HANA Cloud system. The scenario contains a simple sales order UI app that displays historical data of sales orders and other documents that have been migrated from a legacy system to another database system. This app allows you to perform search, filter, and sort operations on the data. Additionally, you can export the data to a Microsoft Excel file. A user with the admin role can also import historical sales order data using the import functionality.

#### SAP Extensibility Explorer

This tutorial is one of multiple tutorials that make up the [SAP Extensibility Explorer](https://sap.com/extends4) for SAP S/4HANA Cloud.
SAP Extensibility Explorer is a central place where anyone involved in the extensibility process can gain insight into various types of extensibility options. At the heart of SAP Extensibility Explorer, there is a rich repository of sample scenarios which show, in a hands-on way, how to realize an extensibility requirement leveraging different extensibility patterns.


Requirements
-------------
- An account in SAP Business Technology Platform (BTP) with a subaccount in the _Cloud Foundry_ environment and Enterpise Messaging, Authorization & Trust Management services enabled _or_ an account in SAP Business Technology Platform (BTP) with a subaccount in the _Neo_ environment, an SAP Business Technology Platform (BTP) Java server of any size and Data Management service (SAP HANA or SAP ASE) enabled.
- [Java SE **8** Development Kit (JDK)](https://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Apache Maven](http://maven.apache.org/download.cgi) to build the application.
- [Cloud Foundry Command Line Interface (CF CLI)](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html) tool in case you want to deploy the application to Cloud Foundry.

Download and Installation
-------------
This repository is a part of the [Download the Application](https://help.sap.com/viewer/1eeef7f3c5c140b1a3b406cf357f316a/SHIP/en-US/aa4a386e18a84c9d90419f887a549204.html) step in the tutorial. Instructions for use can be found in that step.


Known issues
---------------------
There are no known major issues.

How to obtain support
---------------------
If you have issues with this sample, please open a report using [GitHub issues](https://github.com/SAP/s4hana-ext-migrate-historical-data/issues).

License
-------
Copyright © 2020 SAP SE or an SAP affiliate company. All rights reserved.
This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE file](LICENSES/Apache-2.0.txt).
