/*global history */
sap.ui.define([
	'sap/m/Button',
	"sap/migratedsalesorder/controller/BaseController",
	"sap/m/MessageToast"
	], function (Button, BaseController, MessageToast, Dialog) {
		"use strict";

		return BaseController.extend("sap.migratedsalesorder.controller.DocFlow", {
			onInit: function () { 
				// call the base constructor
				BaseController.prototype.onInit.apply(this, arguments);	
				this._configureSingleSelectSmartTable("editableContainer_docFlow", this._onRowSelectionChange);
			},		
		
	/**
		* Method for the Document Type filter on the document Flow page .
	 */			
		onBeforeRebindTableExtension : function(oEvent) {
			var oBindingParams = oEvent.getParameter("bindingParams");
			oBindingParams.parameters = oBindingParams.parameters || {};
							
			var oSmartTable = oEvent.getSource();
			var oSmartFilterBar = this.byId(oSmartTable.getSmartFilterId());

			if (oSmartFilterBar instanceof sap.ui.comp.smartfilterbar.SmartFilterBar) {
				
				var oCustomControl = oSmartFilterBar.getControlByKey("TypeFilter");
				if (oCustomControl instanceof sap.m.ComboBox) {
					var sValue = oCustomControl.getProperty("value");
						if(sValue === "" || sValue === "All"){
						oBindingParams.filters = [];
					}else{	
						oBindingParams.filters.push(new sap.ui.model.Filter("Type", "EQ", sValue));
					}
				}
			}
		},
		

	/**
		* Navigation to Doc Flow Detail on Row selection on the Doc Flow page.
	 */	
			_onRowSelectionChange: function (oControlEvent) {
				var oRowContext = oControlEvent.getParameters("rowContext");
				
				// the '/' is not allowed in the hash while navigating.
				this.getRouter().navTo("genericDocumentDetail", 
						{docFlowPath : oRowContext.rowContext.sPath.substr(1,oRowContext.rowContext.sPath.length)});
			}
			
		});

	}
);