sap.ui.define([ 'jquery.sap.global',
	'sap/m/MessageBox',
	'sap/m/MessageToast',
	'sap/m/Dialog',
	'sap/m/Button',
	'sap/m/Text',
	'sap/ui/core/mvc/Controller',
	'sap/ui/model/SimpleType',
	'sap/ui/model/ValidateException',
	'sap/ui/model/json/JSONModel',
	'sap/ui/model/Sorter',
	'sap/ui/core/format/DateFormat' ], function(jQuery, MessageBox, MessageToast, Dialog, Button, Text, Controller, SimpleType, 
			ValidateException, JSONModel, SortOrder, Sorter, DateFormat) {
	"use strict";
	return Controller.extend("acc.controller.App", {
		onCreateAccount : function(oEvent) {
			var oView = this.getView();
			var oDialog = oView.byId("newAccountDialog");
			if (!oDialog) {
				oDialog = this.createDialog(oView, this);
			}
			oDialog.getModel().setData({});
			oDialog.open();
		},
		onEditAccount : function(oEvent) {
			
			var cntx = oEvent.getSource().getBindingContext()
			
			var oView = this.getView();
			var oDialog = oView.byId("newAccountDialog");
			if (!oDialog) {
				oDialog = this.createDialog(oView, this);
			}
			var oData = cntx.getObject();
			oDialog.getModel().setData(oData);
			
			oDialog.open();
		},
		onDeleteAccount : function() {
			var that = this;
			var oTable = that.getView().byId("accountsTable");
			var aIndices = oTable.getSelectedIndices();

			if (aIndices.length == 0) return;
			
			var dialog = new Dialog({
				title: 'Confirm',
				type: 'Message',
				content: new Text({ text: 'Are you sure you want to delete selected accounts?' }),
				beginButton: new Button({
					text: 'Delete',
					press: function () {
						that._doDelete(that, oTable, aIndices);
						dialog.close();
					}
				}),
				endButton: new Button({
					text: 'Cancel',
					press: function () {
						dialog.close();
					}
				}),
				afterClose: function() {
					dialog.destroy();
				}
			});
 
			dialog.open();
			
		},
		
		_doDelete : function(context, oTable, aIndices) {
			
			var arr = [];
			$.each(aIndices, function( index, obj ) {
				var cntx = oTable.getContextByIndex(index);
				arr.push(cntx.getObject());
			});
			var data = JSON.stringify(arr);
			
			$.ajax({
			    type: "DELETE",
			    url: "./rs/accservice",
			    data: data,
			    context : context,
			    contentType: 'application/json',
			    success: function(data) {
			    	oTable.clearSelection();
			    	context.getView().getModel().loadData("./rs/accservice");
			    },
			    error: function (request, status, error) {
			    	jQuery.sap.require("sap.m.MessageBox");
			    	MessageBox.error(error, {
						styleClass: "sapUiSizeCompact"
					});
			    }
			});
		},
		
		onSubmitDialog : function() {
			var inputs = [
				this.getView().byId("firstName"),
				this.getView().byId("lastName"),
				this.getView().byId("email"),
				this.getView().byId("birthDate")
			];
			
			// check that inputs are not empty
			jQuery.each(inputs, function (i, input) {
				if (!input.getValue()) {
					input.setValueState("Error");
				}
			});

			// check states of inputs
			var canContinue = true;
			jQuery.each(inputs, function (i, input) {
				if ("Error" === input.getValueState()) {
					canContinue = false;
					return false;
				}
			});
			
			if (canContinue) {
				var oJSONModel = this.getView().byId("newAccountDialog").getModel();
				var oJSONData = JSON.stringify(oJSONModel.getData());
				
				var type = "POST";
				if (oJSONModel.getData().id) {
					type = "PUT";
				}
				
				var that = this;
				$.ajax({
				    type: type,
				    url: "./rs/accservice",
				    data: oJSONData,
				    context : this,
				    contentType: 'application/json',
				    success: function(data) {
				    	that.getView().getModel().loadData("./rs/accservice");
				    },
				    error: function (request, status, error) {
				    	jQuery.sap.require("sap.m.MessageBox");
				    	MessageBox.error(request.responseText, {
							styleClass: "sapUiSizeCompact"
						});
				    }
				});
				
				
				this.getView().byId("newAccountDialog").close();
			}
			
			
		},
		onCloseDialog : function() {

			this.getView().byId("newAccountDialog").close();
		},
		createDialog : function(oView, context) {
			var oDialog = sap.ui.xmlfragment(oView.getId(),
					"acc.view.newAccountDialog", context);
			var oJSONModel = new sap.ui.model.json.JSONModel({});
			oView.addDependent(oDialog);
			oDialog.setModel(oJSONModel);
			return oDialog;
		},
		typeEMail : SimpleType.extend("email", {
			formatValue: function (oValue) {
				return oValue;
			},
			parseValue: function (oValue) {
				return oValue;
			},
			validateValue: function (oValue) {
				var mailregex = /^\w+[\w-+\.]*\@\w+([-\.]\w+)*\.[a-zA-Z]{2,}$/;
				if (!oValue.match(mailregex)) {
					throw new ValidateException("'" + oValue + "' is not a valid email address");
				}
			}
		}),
		onDateChange : function(oEvent) {
			var dt = moment(oEvent.getParameter("value"), "DD/MM/YYYY");
			var isafter = dt.isAfter(moment());
			if (isafter) {
				oEvent.getSource().setValueState("Error");
				oEvent.getSource().setValueStateText("Birth date should be in the past");
				$("#birthDate").attr("title","custom text");
			}
		},
		sortBirthDate : function(oEvent) {
			var oCurrentColumn = oEvent.getParameter("column");
			var oBirthDateColumn = this.getView().byId("birthDateClmn");
			if (oCurrentColumn != oBirthDateColumn) {
				oBirthDateColumn.setSorted(false); //No multi-column sorting
				return;
			}
 
			oEvent.preventDefault();
 
			var sOrder = oEvent.getParameter("sortOrder");
 
			this._resetSortingState(); //No multi-column sorting
			oBirthDateColumn.setSorted(true);
			oBirthDateColumn.setSortOrder(sOrder);
 
			var oSorter = new sap.ui.model.Sorter(oBirthDateColumn.getSortProperty(), sOrder === sap.ui.table.SortOrder.Descending);
			//The date data in the JSON model is string based. For a proper sorting the compare function needs to be customized.
			oSorter.fnCompare = function(a, b) {
				if (b == null) {
					return -1;
				}
				if (a == null) {
					return 1;
				}
 
				var aa = moment(a);
				var bb = moment(b);
 
				if (aa < bb) {
					return -1;
				}
				if (aa > bb) {
					return 1;
				}
				return 0;
			};
 
			this.getView().byId("accountsTable").getBinding("rows").sort(oSorter);
		},
		
		_resetSortingState : function() {
			var oTable = this.getView().byId("accountsTable");
			var aColumns = oTable.getColumns();
			for (var i=0; i<aColumns.length; i++) {
				aColumns[i].setSorted(false);
			}
		},
		clearAllFilters : function(oEvent) {
			var oTable = this.getView().byId("accountsTable");
 
			var aColumns = oTable.getColumns();
			for (var i=0; i<aColumns.length; i++) {
				oTable.filter(aColumns[i], null);
			}
		},
	});
});