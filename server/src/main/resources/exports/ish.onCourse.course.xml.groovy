xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
	records.each { Course co ->
		course(id: co.id) {
			modifiedOn(co.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(co.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			allowWaitingLists(co.allowWaitingLists)
			code(co.code)
			currentlyOffered(co.currentlyOffered)
			fieldOfEducation(co.fieldOfEducation)
			isWebVisible(co.isShownOnWeb)
			isSufficientForQualification(co.isSufficientForQualification)
			isVET(co.isVET)
			name(co.name)
			printedBrochureDescription(co.printedBrochureDescription)
			reportableHours(co.reportableHours?.format("0.00"))
			webDescription(co.webDescription)
			co.tags.each { Tag t ->
				tag(t.pathName)
			}
			co.courseClasses.each { CourseClass cc ->
				courseClass(id: cc.id) {
					modifiedOn(cc.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					createdOn(cc.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					code(cc.code)
					deliveryMode(cc.deliveryMode?.displayName)
					endDateTime(cc.endDateTime?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					price() {
						amount(cc.feeIncGst?.toPlainString())
						taxAmount(cc.feeGST?.toPlainString())
						taxName(cc.tax?.taxCode ?: "unknown")
					}
					cc.discountCourseClasses.each { DiscountCourseClass dcc ->
						price() {
							def taxRate = cc.tax ? cc.tax.rate : new BigDecimal(0)
							amount(DiscountUtils.getDiscountedFee(dcc, cc.feeIncGst, taxRate).toPlainString())
							taxAmount(DiscountUtils.getDiscountedFee(dcc, cc.feeIncGst, taxRate).multiply(taxRate).toPlainString())
							taxName(cc.tax?.taxCode ?: "unknown")
							discountAmount(DiscountUtils.discountValue(dcc, cc.feeIncGst, taxRate).toPlainString())
							discountName(dcc.discount.name)
						}
					}
					cost() {
						incomeMaximum(ish.budget.ClassBudgetUtil.getClassIncomeExTax(cc, ish.budget.ClassBudgetUtil.MAXIMUM)?.toPlainString())
						incomeBudget(ish.budget.ClassBudgetUtil.getClassIncomeExTax(cc, ish.budget.ClassBudgetUtil.BUDGETED)?.toPlainString())
						incomeActual(ish.budget.ClassBudgetUtil.getClassIncomeExTax(cc, ish.budget.ClassBudgetUtil.ACTUAL)?.toPlainString())
						expensesMaximum(ish.budget.ClassBudgetUtil.getClassCostsExTax(cc, ish.budget.ClassBudgetUtil.MAXIMUM)?.toPlainString())
						expensesBudget(ish.budget.ClassBudgetUtil.getClassCostsExTax(cc, ish.budget.ClassBudgetUtil.BUDGETED)?.toPlainString())
						expensesActual(ish.budget.ClassBudgetUtil.getClassCostsExTax(cc, ish.budget.ClassBudgetUtil.ACTUAL)?.toPlainString())
						profitMaximum(ish.budget.ClassBudgetUtil.getClassProfitExTax(cc, ish.budget.ClassBudgetUtil.MAXIMUM)?.toPlainString())
						profitBudget(ish.budget.ClassBudgetUtil.getClassProfitExTax(cc, ish.budget.ClassBudgetUtil.BUDGETED)?.toPlainString())
						profitActual(ish.budget.ClassBudgetUtil.getClassProfitExTax(cc, ish.budget.ClassBudgetUtil.ACTUAL)?.toPlainString())
					}
					fundingSource(cc.fundingSource?.displayName)
					isCancelled(cc.isCancelled)
					type(cc.type)
					isWebVisible(cc.isShownOnWeb)
					maximumPlaces(cc.maximumPlaces)
					message(cc.message)
					minimumPlaces(cc.minimumPlaces)
					budgetedPlaces(cc.budgetedPlaces)
					notes(cc.notes)
					startDateTime(cc.startDateTime?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					webDescription(cc.webDescription)
					accountCode(cc.incomeAccount?.accountCode)
				}
			}
		}
	}
}
