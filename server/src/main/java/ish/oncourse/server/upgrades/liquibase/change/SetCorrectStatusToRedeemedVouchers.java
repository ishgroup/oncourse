package ish.oncourse.server.upgrades.liquibase.change;

import ish.common.types.ProductStatus;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Voucher;
import ish.oncourse.server.db.SchemaUpdateService;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;

public class SetCorrectStatusToRedeemedVouchers extends IshTaskChange {

    @Override
    public void execute(Database database) throws CustomChangeException {
        ICayenneService cayenneService = SchemaUpdateService.sharedCayenneService;
        DataContext context = cayenneService.getNewContext();

        ObjectSelect.query(Voucher.class)
                .where(Voucher.STATUS.eq(ProductStatus.ACTIVE))
                .prefetch(Voucher.PRODUCT.joint())
                .select(context)
                .forEach( voucher -> {
                    if (voucher.isFullyRedeemed()) {
                        voucher.setStatus(ProductStatus.REDEEMED);
                    }
                });

        context.commitChanges();
    }
}
