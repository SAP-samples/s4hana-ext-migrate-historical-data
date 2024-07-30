"use strict";

sap.ui.require(['sap/fe/test/JourneyRunner', 'sap/migratedsalesorder/test/integration/FirstJourney', 'sap/migratedsalesorder/test/integration/pages/SalesOrdersList', 'sap/migratedsalesorder/test/integration/pages/SalesOrdersObjectPage', 'sap/migratedsalesorder/test/integration/pages/SalesOrderItemsObjectPage'], function (JourneyRunner, opaJourney, SalesOrdersList, SalesOrdersObjectPage, SalesOrderItemsObjectPage) {
  'use strict';

  var JourneyRunner = new JourneyRunner({
    // start test/flpSandbox.html?sap-ui-xx-viewCache=false#sapmigratedsalesorder-tile in web folder
    launchUrl: sap.ui.require.toUrl('sap/migratedsalesorder') + '/test/flpSandbox.html?sap-ui-xx-viewCache=false#sapmigratedsalesorder-tile'
  });
  JourneyRunner.run({
    pages: {
      onTheSalesOrdersList: SalesOrdersList,
      onTheSalesOrdersObjectPage: SalesOrdersObjectPage,
      onTheSalesOrderItemsObjectPage: SalesOrderItemsObjectPage
    }
  }, opaJourney.run);
});