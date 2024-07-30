"use strict";

sap.ui.define(["sap/ui/test/opaQunit"], function (opaTest) {
  "use strict";

  var Journey = {
    run: function run() {
      QUnit.module("First journey");
      opaTest("Start application", function (Given, When, Then) {
        Given.iStartMyApp();
        Then.onTheSalesOrdersList.iSeeThisPage();
      });
      opaTest("Navigate to ObjectPage", function (Given, When, Then) {
        // Note: this test will fail if the ListReport page doesn't show any data

        When.onTheSalesOrdersList.onFilterBar().iExecuteSearch();
        Then.onTheSalesOrdersList.onTable().iCheckRows();
        When.onTheSalesOrdersList.onTable().iPressRow(0);
        Then.onTheSalesOrdersObjectPage.iSeeThisPage();
      });
      opaTest("Teardown", function (Given, When, Then) {
        // Cleanup
        Given.iTearDownMyApp();
      });
    }
  };
  return Journey;
});