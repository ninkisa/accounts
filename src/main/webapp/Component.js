sap.ui.define([ "sap/ui/core/UIComponent", "sap/ui/model/json/JSONModel",
		"sap/ui/model/resource/ResourceModel" ], function(UIComponent,
		JSONModel, ResourceModel) {
	"use strict";
	return UIComponent.extend("acc.Component",
			{
				metadata : {
					rootView : "acc.view.App"
				},

				init : function() {
					// call the init function of the parent
					UIComponent.prototype.init.apply(this, arguments);

					var oJSONModel = new sap.ui.model.json.JSONModel(
							"./rs/accservice");
					this.setModel(oJSONModel);

					var i18nModel = new ResourceModel({
						bundleName : "acc.i18n.i18n"
					});
					this.setModel(i18nModel, "i18n");
					
					// attach handlers for validation errors
					sap.ui.getCore().attachValidationError(function (evt) {
						var control = evt.getParameter("element");
						if (control && control.setValueState) {
							control.setValueState("Error");
						}
					});
					sap.ui.getCore().attachValidationSuccess(function (evt) {
						var control = evt.getParameter("element");
						if (control && control.setValueState) {
							control.setValueState("None");
						}
					});
				}
			});
});