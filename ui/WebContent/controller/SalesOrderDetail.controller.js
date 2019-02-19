/*global history */
sap.ui.define([
		"sap/migratedsalesorder/controller/BaseController",
		"sap/m/MessageToast",
		"sap/ui/core/routing/History"
	], function (BaseController, MessageToast, History) {
		"use strict";

		return BaseController.extend("sap.migratedsalesorder.controller.SalesOrderDetail", {
			onInit: function () {
				this._oModel = this.getOwnerComponent().getModel();

				this.getRouter().getRoute("salesOrderDetail").attachPatternMatched(this._onPatternMatched, this);

				this._configureSingleSelectSmartTable("SalesOrderDocFlow", this._onRowSelectionChangeDocFlow);

			},
			
		/**
			* Method for Navigation to Doc Flow Detail on Row selection on the Sales Order Detail page.
		*/

			_onRowSelectionChangeDocFlow: function (oControlEvent) {
				var oRowContext = oControlEvent.getParameters("rowContext");

				// the '/' is not allowed in the hash while navigating.
				this.getRouter().navTo("genericDocumentDetail",
					{docFlowPath: oRowContext.rowContext.sPath.substr(1, oRowContext.rowContext.sPath.length)});
			},

			_onPatternMatched: function (oEvent) {
				var sSalesOrderPath = oEvent.getParameter("arguments").salesOrderPath;

				this.getView().bindElement({
					path: "/" + sSalesOrderPath,
					expand: "to_Items",
					events: {
						change: this._onBindingChange.bind(this)
					}
				});
			},
			
			_onBindingChange : function () {
				var oElementBinding = this.getView().getElementBinding();

				// No data for the binding
				if (!oElementBinding.getBoundContext()) {
					this.getRouter().getTargets().display("notFound");
					return;
				}
			},

			onNavBack: function () {
				//if there is a change, ask 'are you sure'
				if (this._modelChanged) {
					this._showUnsavedChangesDialog();
					return;
				}
				this._navigateBack();
			},

			_navigateBack: function () {
				MessageToast.show("Navigating back");

				var sPreviousHash = History.getInstance().getPreviousHash();

				if (sPreviousHash !== undefined) {
					history.go(-1);
				} else {
					this.getRouter().navTo("salesOrders", {}, true);
				}
			}
		});

	}
);