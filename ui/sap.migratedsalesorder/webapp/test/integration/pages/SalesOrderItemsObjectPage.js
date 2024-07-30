"use strict";

sap.ui.define(['sap/fe/test/ObjectPage'], function (ObjectPage) {
  'use strict';

  var CustomPageDefinitions = {
    actions: {},
    assertions: {}
  };
  return new ObjectPage({
    appId: 'sap.migratedsalesorder',
    componentId: 'SalesOrderItemsObjectPage',
    contextPath: '/SalesOrders/Items'
  }, CustomPageDefinitions);
});