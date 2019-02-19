sap.ui.define([
   "sap/ui/core/UIComponent",
   "sap/ui/model/json/JSONModel",
   "sap/ui/model/resource/ResourceModel",
   "sap/ui/fl/FakeLrepConnectorLocalStorage",
   "sap/migratedsalesorder/controller/ErrorHandler"
], function (UIComponent, JSONModel, ResourceModel, FakeLrepConnectorLocalStorage, ErrorHandler) {
   "use strict";
   return UIComponent.extend("sap.migratedsalesorder.Component", {
	   
	   metadata : {
           manifest: "json"
	   	},
	   	
		init : function () {
			FakeLrepConnectorLocalStorage.enableFakeConnector(jQuery.sap.getModulePath("sap.migratedsalesorder.lrep.component-test-changes") + ".json");
			
			// call the init function of the parent
			UIComponent.prototype.init.apply(this, arguments);
			
			this.getRouter().initialize();
			
	         // set i18n model
	         var i18nModel = new ResourceModel({
	            bundleName : "sap.migratedsalesorder.i18n.i18n"
	         });
	         this.setModel(i18nModel, "i18n");
	         
             this._oErrorHandler = new ErrorHandler(this);
		},
	   	
	   	destroy: function() {
			FakeLrepConnectorLocalStorage.disableFakeConnector();
			UIComponent.prototype.destroy.apply(this, arguments);
		}

		
   });
   
});