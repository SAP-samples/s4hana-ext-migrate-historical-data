sap.ui.define([
	"sap/ui/core/util/MockServer"
], function(MockServer) {
	"use strict";

	var oMockServer,
		_sAppModulePath = "sap/migratedsalesorder/",
		_sJsonFilesModulePath = _sAppModulePath + "localService/mockdata";

	return {
		/**
		 * Initializes the mock server.
		 * You can configure the delay with the URL parameter "serverDelay".
		 * The local mock data in this folder is returned instead of the real data for testing.
		 * @public
		 */

		init: function() {
			var oUriParameters = jQuery.sap.getUriParameters(),
				sJsonFilesUrl = jQuery.sap.getModulePath(_sJsonFilesModulePath),
				sManifestUrl = jQuery.sap.getModulePath(_sAppModulePath + "manifest", ".json"),
				sEntity = "SalesOrders",
				sErrorParam = oUriParameters.get("errorType"),
				iErrorCode = sErrorParam === "badRequest" ? 400 : 500,
				oManifest = jQuery.sap.syncGetJSON(sManifestUrl).data,
				oMainDataSource = oManifest["sap.app"].dataSources.mainService,
				sMetadataUrl = jQuery.sap.getModulePath(_sAppModulePath + oMainDataSource.settings.localUri.replace(".xml", ""), ".xml"),
				// ensure there is a trailing slash
				sMockServerUrl = /.*\/$/.test(oMainDataSource.uri) ? oMainDataSource.uri : oMainDataSource.uri + "/";

			oMockServer = new MockServer({
				rootUri: sMockServerUrl
			});

			// configure mock server with a delay of 1s
			MockServer.config({
				autoRespond: true,
				autoRespondAfter: (oUriParameters.get("serverDelay") || 1000)
			});

			oMockServer.simulate(sMetadataUrl, {
				sMockdataBaseUrl: sJsonFilesUrl,
				bGenerateMissingMockData: true
			});

			var aRequests = oMockServer.getRequests(),
				fnResponse = function(iErrCode, sMessage, aRequest) {
					aRequest.response = function(oXhr) {
						oXhr.respond(iErrCode, {
							"Content-Type": "text/plain;charset=utf-8"
						}, sMessage);
					};
				};

			// handling the metadata error test in case of not found (backend is down)
			if (oUriParameters.get("metadataErrorBackendDown")) {
				aRequests.forEach(function(aEntry) {
					if (aEntry.path.toString().indexOf("$metadata") > -1) {
						fnResponse(404, "404 Not Found: Requested route ('migrate-backend.cfapps.sap.hana.ondemand.com') does not exist.\n", aEntry);
					}
				});
			}

			// handling the metadata error test in case of Viewer role is missing
			if (oUriParameters.get("noViewerRole")) {
				aRequests.forEach(function(aEntry) {
					if (aEntry.path.toString().indexOf("$metadata") > -1) {
						fnResponse(403, "<!DOCTYPE html><html><head><title>Error report</title></head><body><h1>Forbidden</body></html>", aEntry);
					}
				});
			}

			// handling the metadata error test
			if (oUriParameters.get("metadataError")) {
				aRequests.forEach(function(aEntry) {
					if (aEntry.path.toString().indexOf("$metadata") > -1) {
						fnResponse(500, "metadata Error", aEntry);
					}
				});
			}

			// Handling request errors
			if (sErrorParam) {
				aRequests.forEach(function(aEntry) {
					if (aEntry.path.toString().indexOf(sEntity) > -1) {
						fnResponse(iErrorCode, sErrorParam, aEntry);
					}
				});
			}
			oMockServer.start();


			/////////////
			// File upload errors
			var oMockServerFileUpload = new MockServer({
				rootUri: "/",
				requests: this.getFileUploadRequests(oUriParameters)
			});
			oMockServerFileUpload.start();
			// File upload errors
			/////////////


			jQuery.sap.log.info("Running the app with mock data");
		},

		getFileUploadRequests: function(oUriParameters){
			var request =  [];

			// handling the file upload no admin role case
			if (oUriParameters.get("noAdminRole")) {
				request.push({
					method: "POST",
					path: new RegExp("(.*)import"),
					response: function(oXhr, sUrlParams) {
						oXhr.respond(403, {
							"Content-Type": "text/plain;charset=utf-8"
						},  "<!DOCTYPE html><html><head><title>Error report</title></head><body><h1>Forbidden</body></html>");
					}
				});
			}

			return request;
		},

		/**
		 * @public returns the mockserver of the app, should be used in integration tests
		 * @returns {sap.ui.core.util.MockServer} the mockserver instance
		 */
		getMockServer: function() {
			return oMockServer;
		}
	};

});