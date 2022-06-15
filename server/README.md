### Quality rules

Some ideas described here:

[https://intranet.ish.com.au/development/features/quality_rules.html](https://intranet.ish.com.au/development/features/quality_rules.html)

## Raw implementation plan 

1. Use existing groovy script engine to execute **Quality rules**, they are actually some groovy scripts
2. Move script trigger type to separate table so the single script could has several triggers at a time (start by cron or entity event)
3. each **Quality rules** script should support two methods:
 - execute Quality check for single record `getQuality(record)`
 - fetch number of records `getPossibleRecords()` and execute Quality check for each of them:
```
    getPossibleRecords().each { getQuality(it) }
```
4. script-trigger many-to many relation may has extra attribute (target method) to know whitch method to call for certain case:
 - for entity event trigger make sence to call `getQuality(record)` method to make individual check in real time (check if class cost increase and it became unprofitable)
 - for cron trigger make sence to fetch number of records (classes starts next week) and make check for all of them: `getPossibleRecords().each { getQuality(it) }` (to know if any of them has no enough enrolments to make profit)  
5. each **Quality rule** produce array of **Quality result**s which are reference to some entity (entity name/entity id) plus some message and severity level. Probably reference to  **Quality rule** would be useful as well.
6. put results to solr storage to do not overload db.
7. when open some edit view - make solr query to fetch any **Quality result**s asociated with current recode (CourseClass/200) ad display that.
8. **Quality rule**  might override **Quality result**s for records: there are no needed to keep all historical results for certain record (CourseClass/200) - we interested in actual results (prodused by last run of  **Quality rule**)
9. **Quality rule** might clean up some results if it is noot actual any more (class was unprofitable and becale profitable)
10. **Quality result**s  that has no entity reference could be displayed on dashboard

## Bank accounts types
```
             _                         _ 
            |  ASSET         INCOME     |
left-side - |  EXPENSE       EQUITY     | - right-side
            |_ COS           LIABILITY _|  

```
If transaction is between accounts of equal types (left -> left or right -> right)
then in group of transaction (1. substruct from first account; 2. add to second) amount of second
transaction will be negative amount of first.

Logic is into ```AccountTransactionService```. There are places where it is in use:
1. ```InvoiceLineLifecycleListener```. Creates InvoiceLineTransactionBuilder in onPersist of invoiceLine.
2. ```TransactionApiImpl```. Performs direct TransactionDTO, received from frontend
3. ```TransactionsLifecycleListener```.

      A) preupdate of PaymentIn/PaymentOut if PaymentIn.BANKING was changed:
      DepositTransactionsBuilder, 
      
      primaryAccount: paymentline.payment.undepositedFundsAccount || paymentline.payment.accountIn <br>
      secondary: paymentline.payment.accountIn || paymentline.payment.undeposited...
      (vise versa if new settlement date is null).<br><br>
       B) postpersist of paymentIn/paymentOut<br><br>
       I. PaymentOutTransactionsBuilder,
       primaryAccount: paymentLine.invoice.debtorsAccount
       secondaryAccount: 
       if bankedAutomatically && banking set
          paymentline.payment.accountOut
       else paymentline.payment.undepositedFundsAccount

    II. PaymentInTransactionBuilder
    voucher expenses;
    underpaymentAccount: underpaymentAccount of related voucher or voucherExpense<br>
    secondaryAccount: paymentline.invoice.debtorsAccount<br>
    if paymentType == voucher:<br>
   &nbsp;&nbsp;&nbsp;transaction1: from underpaymentAccount to secondaryAccount<br>
   &nbsp;&nbsp;&nbsp;transaction2: from underpaymentAccount to primaryAccount<br>
    else
    from primary to secondary
5. ```BankingLifecycleListener```. preupdate of Banking, the same as for deposit into preupdates of paymentsIn/out for all
   related paymensin and paymentsout
6. ```VoucherExpireJob```. For all vouchers if they are expired
   VoucherExpiryTransactionsBuilder

   primaryAccount: default vouchersExpiredAccount<br>
   secondaryAccount: voucher.voucherProduct.liablitityAccount
7. ```DelayedEnrolmentIncomePostingJob```. per invoiceline
   DelayedIncomeTransactionsBuilder

   primaryAccount: invoiceline.account<br>
   secondaryAccount: invoiceline.prepaidFeesAccount