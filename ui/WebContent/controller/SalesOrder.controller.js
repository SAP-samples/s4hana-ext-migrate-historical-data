/*global history */
sap.ui.define([
	'sap/m/Button',
	"sap/migratedsalesorder/controller/BaseController",
	"sap/m/MessageToast",
	"sap/m/MessageBox"
	], function (Button, BaseController, MessageToast, MessageBox) {
		"use strict";

		return BaseController.extend("sap.migratedsalesorder.controller.SalesOrder", {
			_oUploadDialog: null,
			
			onInit: function () { 
				// call the base constructor
				BaseController.prototype.onInit.apply(this, arguments);

				this._configureSingleSelectSmartTable("editableContainer", this._onRowSelectionChange);
			},		
			
			_onRowSelectionChange: function (oControlEvent) {
				var oRowContext = oControlEvent.getParameters("rowContext");
				// the '/' is not allowed in the hash while navigating.
				this.getRouter().navTo("salesOrderDetail", 
						{salesOrderPath : oRowContext.rowContext.sPath.substr(1,oRowContext.rowContext.sPath.length)});
			},

			/* =========================================================== */
			/* Upload related functions                                    */
			/* =========================================================== */

			onUploadPress: function (oEvent) {
				var oView = this.getView();
				this._oUploadDialog = oView.byId("UploadDialog");
				// create dialog lazily
				if (!this._oUploadDialog) {
					// create dialog via fragment factory
					this._oUploadDialog = sap.ui.xmlfragment(oView.getId(), "sap.migratedsalesorder.view.fragments.SalesOrderUploadDialog", this);
					oView.addDependent(this._oUploadDialog);
				}
				this._oUploadDialog.open();
			},

			OnUploadCancel: function () {
				this._oUploadDialog.close();
			},

			handleUploadComplete: function (oEvent) {
				this._oUploadDialog.close();

				if(oEvent.getParameter("status") === 204){
					MessageToast.show("Successfully uploaded the file.", {closeOnBrowserNavigation : false});
					this.getModel().refresh();

					return;
				}
				
				var sPreparedErrorMessage = this._prepareErrorMessage(oEvent);
				
				MessageBox.error(
                    "Sorry, a technical error occurred! Please try again later." +
                    "\nIf the error persist, please contact your SAP representative.",
                    {
                        id: "serviceErrorMessageBox",
                        details: sPreparedErrorMessage,
                        actions: [MessageBox.Action.CLOSE],
                        onClose: function () {
                            // this._bMessageOpen = false;
                            this.getOwnerComponent().getRouter().getTargets().display("technicalError");
                        }.bind(this)
                    }
                );
			},
			
			_prepareErrorMessage: function(oEvent){
				var sRawResponse = oEvent.getParameter("responseRaw");
				if(!sRawResponse){
					return "Error Occurred";
				}

				var sErrorMessageParsed = "<p>Backend responded with the following information:<br/>";
				sErrorMessageParsed +=
					"<strong>HTTP Status Code:</strong> " + oEvent.getParameter("status") + "<br/>"+
					"<strong>Error Message: </strong>";

				var oResponse;
				try {
					oResponse = JSON.parse(sRawResponse);
				}catch (e) {
					sErrorMessageParsed += sRawResponse;
					return sErrorMessageParsed;
				}
                
                var oPreparedMessage = {sErrorMessageParsed: sErrorMessageParsed};
                this._flattenErrorMessage(oResponse.error, oPreparedMessage); 
				return oPreparedMessage.sErrorMessageParsed;
			},
			
			_flattenErrorMessage: function(oResponseError, oPreparedMessage){
				oPreparedMessage.sErrorMessageParsed += 
					"<strong>" + oResponseError.code + ":</strong> " + oResponseError.message.value + "<br/><br/>";
				
				if(typeof oResponseError.innererror === "object"){
					this._flattenErrorMessage(oResponseError.innererror, oPreparedMessage);
				}else{
					oPreparedMessage.sErrorMessageParsed += "</p>";
				}
			},
			
		/**
			 * File Import: When upload button is clicked.
		 */			

			OnAttachUpload: function () {
				var oFileUploader = this.byId("AttachUploader");

				var csrfToken = this.getModel().getSecurityToken();
				// check if the token is already there
				var aHeaderParameters = oFileUploader.getHeaderParameters();
				if (aHeaderParameters.length !== 0) {
					var oHeaderParameter = aHeaderParameters[0];
					oHeaderParameter.setValue(csrfToken);
				}
				else {
					var oUploaderParameter = new sap.ui.unified.FileUploaderParameter();
					oUploaderParameter.setName("x-csrf-token");
					oUploaderParameter.setValue(csrfToken);
					oFileUploader.addHeaderParameter(oUploaderParameter);
				}

				if (!oFileUploader.getValue()) {
					MessageToast.show("Choose a file first");
					return;
				}
				oFileUploader.upload();
			}
			
			
		});

	}
);