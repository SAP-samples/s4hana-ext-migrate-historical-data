sap.ui.define([
		"sap/ui/base/Object",
		"sap/m/MessageBox"
	], function (UI5Object, MessageBox) {
		"use strict";

		return UI5Object.extend("sap.migratedsalesorder.controller.ErrorHandler", {

			/**
			 * Handles application errors by automatically attaching to the model events and displaying errors when needed.
			 * @class
			 * @param {sap.ui.core.UIComponent} oComponent reference to the app's component
			 * @public
			 */
			constructor: function (oComponent) {
				this._oComponent = oComponent;
				this._bMessageOpen = false;
				this._bTechnicalErrorShownAlready = false;

				// handle the default model error
				oComponent.getModel().attachMetadataFailed(function (oEvent) {
					var oParams = oEvent.getParameters();
					this._showServiceError(oParams);
				}, this);
				oComponent.getModel().attachRequestFailed(function (oEvent) {
					var oParams = oEvent.getParameters();
					this._showServiceError(oParams);
				}, this);

			},

			/**
			 * Shows a {@link sap.m.MessageBox} when a service call has failed.
			 * Only the first error message will be display.
			 * @param {object} oParams a technical error object to be displayed on request
			 * @private
			 */
			_showServiceError: function (oParams) {
				if (this._bMessageOpen || this._bTechnicalErrorShownAlready) {
					return;
				}
				var sDetails = this._prepareErrorDetailsToShow(oParams);
				this._bMessageOpen = true;
				this._bTechnicalErrorShownAlready = true;
				MessageBox.error(
					"Sorry, a technical error occurred! Please try again later." +
					"\nIf the error persist, please contact your SAP representative.",
					{
						id: "serviceErrorMessageBox",
						details: sDetails,
						actions: [MessageBox.Action.CLOSE],
						onClose: function (oEvent) {
							this._oComponent.getRouter().getTargets().display("technicalError");
							this._bMessageOpen = false;
							if(!oEvent){
								// which means, error message is not closed by the user!
								// We need to wait until the pages are ready, and show the error message again.
								this._bTechnicalErrorShownAlready = false;
								setTimeout(this._showServiceError.bind(this, oParams), 500);
								return;
							}
						}.bind(this)
					}
				);
			},

			/**
			 * Prepares an object to show in the {@link sap.m.MessageBox}.
			 * @param {object} oParams a technical error object received from backend
			 * @private
			 * @returns {string} formatted text to show in {@link sap.m.MessageBox} as details.
			 */
			_prepareErrorDetailsToShow: function (oParams) {
				var sErrorMessageParsed = "<p>Backend responded with the following information:";

				try {
					if(typeof oParams.statusCode !== "undefined")
						sErrorMessageParsed += "<br><strong>HTTP Status Code:</strong> " + oParams.statusCode;

					if(typeof oParams.statusText !== "undefined")
						sErrorMessageParsed += "<br><strong>Message:</strong> " + oParams.statusText;

					if(typeof oParams.response !== "undefined"){
						if(typeof oParams.response.message !== "undefined")
							sErrorMessageParsed += "<br><strong>Message:</strong> " + oParams.response.message;

						if(typeof oParams.response.responseText !== "undefined")
							sErrorMessageParsed += "<br><strong>Response Text:</strong> " + oParams.response.responseText;

						if(typeof oParams.response.body !== "undefined")
							sErrorMessageParsed += "<br><strong>Response Body:</strong> " + oParams.response.body;

						if(typeof oParams.response.statusCode !== "undefined")
							sErrorMessageParsed += "<br><strong>Status Code:</strong> " + oParams.response.statusCode;

						if(typeof oParams.response.statusText !== "undefined")
							sErrorMessageParsed += "<br><strong>Status Text:</strong> " + oParams.response.statusText;
					}else{
						if(typeof oParams.message !== "undefined")
							sErrorMessageParsed += "<br><strong>Message:</strong> " + oParams.message;

						if(typeof oParams.responseText !== "undefined")
							sErrorMessageParsed += "<br><strong>Response Text:</strong> " + oParams.responseText;

						if(typeof oParams.statusCode !== "undefined")
							sErrorMessageParsed += "<br><strong>Status Code:</strong> " + oParams.statusCode;

						if(typeof oParams.statusText !== "undefined")
							sErrorMessageParsed += "<br><strong>Status Text:</strong> " + oParams.statusText;
					}

					sErrorMessageParsed += "</p>";
				}
				catch (err) {
					sErrorMessageParsed +=
						JSON.stringify(oParams) + "</p>";
				}

				return sErrorMessageParsed;
			},

		});

	}
);