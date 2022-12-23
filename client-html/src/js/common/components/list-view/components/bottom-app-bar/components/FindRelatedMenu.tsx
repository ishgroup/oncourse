/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  Fragment, useCallback, useEffect, useMemo, useState
} from "react";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import { FindRelatedItem } from "../../../../../../model/common/ListView";

interface Props {
  findRelated: FindRelatedItem[];
  handleRelatedLinkClick: (item: FindRelatedItem) => void;
}

const FindRelatedMenu = React.memo<Props>(({ findRelated, handleRelatedLinkClick }) => {
  const [menuAnchor, setMenuAnchor] = useState(null);

  const changeVisibility = useCallback(e => {
    setMenuAnchor(prev => (prev ? null : e.target));
  }, []);

  const menuOpened = useMemo(() => Boolean(menuAnchor), [menuAnchor]);

  findRelated.sort((a, b) => (a.title > b.title ? 1 : -1));

  return (
    <>
      {findRelated
        ? findRelated.map((findRelatedItem, index) => {
          if (findRelatedItem.items) {
            findRelatedItem.items.sort((a, b) => (a.title > b.title ? 1 : -1));
            
            return (
              <Fragment key={index + findRelatedItem.title}>
                <MenuItem
                  key={index + findRelatedItem.title}
                  classes={{
                    root: "listItemPadding"
                  }}
                  onClick={changeVisibility}
                >
                  {findRelatedItem.title}
                </MenuItem>

                <Menu
                  id="findRelatedNested"
                  anchorEl={menuAnchor}
                  open={menuOpened}
                  onClose={changeVisibility}
                  anchorOrigin={{ vertical: "center", horizontal: "right" }}
                >
                  {findRelatedItem.items.map(item => (
                    <MenuItem
                      key={index + item.title}
                      onClick={() => handleRelatedLinkClick(item)}
                      classes={{
                        root: "listItemPadding"
                      }}
                    >
                      {item.title}
                    </MenuItem>
                  ))}
                </Menu>
              </Fragment>
            );
          }

          return (
            <MenuItem
              key={index + findRelatedItem.title}
              onClick={() => handleRelatedLinkClick(findRelatedItem)}
              classes={{
                root: "listItemPadding"
              }}
            >
              {findRelatedItem.title}
            </MenuItem>
          );
        })
        : null}
    </>
  );
});

export default FindRelatedMenu;