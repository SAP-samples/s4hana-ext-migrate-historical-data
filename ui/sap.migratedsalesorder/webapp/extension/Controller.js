sap.ui.define(["sap/m/MessageBox", "sap/m/MessageToast"], function (MessageBox, MessageToast) {
  "use strict";

  function _createUploadController(oExtensionAPI) {
    var oUploadDialog;
    var csrfToken;
    function onBeforeSend(oEvent) {
      csrfToken = oEvent.getParameter("XMLHttpRequest").getResponseHeader("X-CSRF-Token");
    }
    function setOkButtonEnabled(bOk) {
      oUploadDialog && oUploadDialog.getBeginButton().setEnabled(bOk);
    }
    function setDialogBusy(bBusy) {
      oUploadDialog.setBusy(bBusy);
    }
    function closeDialog() {
      oUploadDialog && oUploadDialog.close();
    }
    function showError(sMessage) {
      MessageBox.error(sMessage || "Upload failed");
    }
    return {
      onBeforeOpen: function onBeforeOpen(oEvent) {
        oUploadDialog = oEvent.getSource();
        oExtensionAPI.addDependent(oUploadDialog);
      },
      onAfterClose: function onAfterClose(oEvent) {
        oExtensionAPI.removeDependent(oUploadDialog);
        oUploadDialog.destroy();
        oUploadDialog = undefined;
      },
      onOk: function onOk(oEvent) {
        // setDialogBusy(true)
        var oFileUploader = sap.ui.core.Fragment.byId("UploadDialog", "AttachUploader");
        oFileUploader.setUploadUrl(sap.ui.require.toUrl("sap/migratedsalesorder/sap/rest/SalesOrders/import"));
        var csrfToken1 = this.csrfToken;
        // check if the token is already there
        var aHeaderParameters = oFileUploader.getHeaderParameters();
        if (aHeaderParameters.length !== 0) {
          var oHeaderParameter = aHeaderParameters[0];
          oHeaderParameter.setValue(csrfToken1);
        } else {
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
      },
      onCancel: function onCancel(oEvent) {
        closeDialog();
      },
      handleUploadComplete: function handleUploadComplete(oEvent) {
        oUploadDialog.close();
        var sPreparedErrorMessage; 
        if (oEvent.getParameter("status") === 200) {
          MessageToast.show("Successfully uploaded the file.", {
            closeOnBrowserNavigation: false
          });
          return;
        }
        else{
        sPreparedErrorMessage = this._prepareErrorMessage(oEvent); 
        MessageBox.error("Sorry, a technical error occurred! Please try again later." + "\nIf the error persist, please contact your SAP representative.", {
          id: "serviceErrorMessageBox",
          details: sPreparedErrorMessage,
          actions: [MessageBox.Action.CLOSE],
          onClose: function () {
          }.bind(this)
        }); 
      }
      }
      ,
      _prepareErrorMessage: function (oEvent) {
        var sRawResponse = oEvent.getParameter("responseRaw");
        if (!sRawResponse) {
             return "Error Occurred";
        }

        var sErrorMessageParsed = "<p>Backend responded with the following information:<br/>";
        sErrorMessageParsed +=
             "<strong>HTTP Status Code:</strong> " + oEvent.getParameter("status") + "<br/>" +
             "<strong>Error Message: </strong>";

        var oResponse;
        try {
             oResponse = JSON.parse(sRawResponse);
        } catch (e) {
             sErrorMessageParsed += sRawResponse;
             return sErrorMessageParsed;
        }

        var oPreparedMessage = { sErrorMessageParsed: sErrorMessageParsed };
        this._flattenErrorMessage(oResponse.error, oPreparedMessage);
        return oPreparedMessage.sErrorMessageParsed;
   },

   _flattenErrorMessage: function (oResponseError, oPreparedMessage) {
        oPreparedMessage.sErrorMessageParsed +=
             "<strong>" + oResponseError.code + ":</strong> " + oResponseError.message.value + "<br/><br/>";

        if (typeof oResponseError.innererror === "object") {
             this._flattenErrorMessage(oResponseError.innererror, oPreparedMessage);
        } else {
             oPreparedMessage.sErrorMessageParsed += "</p>";
        }
   }
        
     
    };
  }
  return {
    showUploadDialog: function showUploadDialog(oBindingContext, aSelectedContexts) {
      this.loadFragment({
        id: "UploadDialog",
        name: "sap.migratedsalesorder.extension.SalesOrderUploadDialog",
        controller: _createUploadController(this)
      }).then(function (oDialog) {
        oDialog.open();
      });
    }
  };
});