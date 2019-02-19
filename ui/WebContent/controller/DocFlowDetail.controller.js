/*global history */
sap.ui.define([
		"sap/migratedsalesorder/controller/BaseController",
		"sap/m/MessageToast",
		"sap/ui/core/routing/History"
	], function (BaseController, MessageToast, History) {
		"use strict";

		return BaseController.extend("sap.migratedsalesorder.controller.DocFlowDetail", {
			onInit: function () {
				this._oModel = this.getOwnerComponent().getModel();

				this.getRouter().getRoute("genericDocumentDetail").attachPatternMatched(this._onPatternMatched, this);
				this._configureSingleSelectSmartTable("editableContainer_SalesOrder", this._onRowSelectionChangeSalesOrder);
				this._configureSingleSelectSmartTable("editableContainer_Predecessor", this._onRowSelectionChangeDocFlow);
				this._configureSingleSelectSmartTable("editableContainer_Successor", this._onRowSelectionChangeDocFlow);
			},
		
		/**
			* Method for Navigation to Sales Order Detail on Row selection on the Doc Flow Detail page.
		*/	
			_onRowSelectionChangeSalesOrder: function (oControlEvent) {
				var oRowContext = oControlEvent.getParameters("rowContext");

				// the '/' is not allowed in the hash while navigating.
				this.getRouter().navTo("salesOrderDetail",
					{salesOrderPath: oRowContext.rowContext.sPath.substr(1, oRowContext.rowContext.sPath.length)});
			},
			
		/**
			* Method for Navigation to Doc Flow Detail on Row selection on the Doc Flow Detail page.
		*/

			_onRowSelectionChangeDocFlow: function (oControlEvent) {
				var oRowContext = oControlEvent.getParameters("rowContext");

				// the '/' is not allowed in the hash while navigating.
				this.getRouter().navTo("genericDocumentDetail",
					{docFlowPath: oRowContext.rowContext.sPath.substr(1, oRowContext.rowContext.sPath.length)});
			},

	
			_onPatternMatched: function (oEvent) {
				var sDocFlowPath = oEvent.getParameter("arguments").docFlowPath;
				this.getView().bindElement({
					path: "/" + sDocFlowPath
				});
			},

			onNavBack: function () {
				//if there is a change, ask 'are you sure'
				if (this._modelChanged) {
					this._showUnsavedChangesDialog();
					return;
				}
				this._navigateBack();
			},

		/**
			* Method for Navigation back.
		*/
			_navigateBack: function () {
				MessageToast.show("Navigating back");

				var sPreviousHash = History.getInstance().getPreviousHash();

				if (sPreviousHash !== undefined) {
					history.go(-1);
				} else {
					this.getRouter().navTo("genericDocuments", {}, true);
				}
			}

		});

	}
);