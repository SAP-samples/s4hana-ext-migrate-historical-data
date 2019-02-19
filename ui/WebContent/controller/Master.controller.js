/*global history */
sap.ui.define([
	"sap/migratedsalesorder/controller/BaseController"			
	], function (BaseController) {
		"use strict";

		return  BaseController.extend("sap.migratedsalesorder.controller.Master", {
			
			
			onInit: function () {
				//first, I have to check if we need different pages?, or should I simply change the binding of smart table?
				sap.ui.core.BusyIndicator.show();
				// create navigation list and set it
				var oNavModel = new sap.ui.model.json.JSONModel();
				oNavModel.setData({
					NavigationCollection: [
						{ Name:"Sales Orders", ListOrder:"1", Route: "salesOrders"},
						{ Name:"Document Flow", ListOrder:"2", Route: "genericDocuments"}
					]
				});
				
				this.getView().setModel(oNavModel, "NavModel");
				this.getRouter().getRoute("master").attachPatternMatched(this._onMasterMatched, this);
				sap.ui.core.BusyIndicator.hide();
			},

			/**
			 * Event handler for the list selection event
			 * @param {sap.ui.base.Event} oEvent the list selectionChange event
			 * @public
			 */
			onSelectionChange : function (oEvent) {
				this.getRouter().navTo(
						this.getModel("NavModel").getObject(oEvent.getParameter("item").oBindingContexts.NavModel.sPath + "/Route"));
			},
			
			/**
			 * If the master route was hit (empty hash) we have to set
			 * the hash to to the first item in the list
			 * @private
			 */
			_onMasterMatched :  function() {				
					var oList = this.byId("masterList");
					var oItems = oList.getItems();
					var firstItem = oItems[0];
					oList.setSelectedItem(firstItem, true);
					
					// Could not make fireSelectionChange work. 
					this.getRouter().navTo(
							this.getModel("NavModel").getObject(firstItem.oBindingContexts.NavModel.sPath + "/Route"));
			}

		});

	}
);