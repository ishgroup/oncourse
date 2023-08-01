/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AvetmissExportSettings, FundingStatus, FundingUpload } from "@api/model";
import { Card, CardContent, Divider } from "@mui/material";
import ButtonBase from "@mui/material/ButtonBase";
import Collapse from "@mui/material/Collapse";
import Fade from "@mui/material/Fade";
import Typography from "@mui/material/Typography";
import clsx from "clsx";
import React, { Component } from "react";
import FundingUploadComponent from "../../../../common/components/form/FundingUploadComponent";

interface Props {
  classes: any;
  items: FundingUpload[];
  onStatusChange: (id: number, status: FundingStatus) => void;
  setFirstUploadNode: any;
  previousExportStatusSetted: boolean;
  skipAnimation: boolean;
  onRunAgainClicked: (settings: AvetmissExportSettings) => void;
}

class AvetmissHistory extends Component<Props, any> {
  state = {
    showMore: false
  };

  showMore = () => {
    const { showMore } = this.state;

    this.setState({
      showMore: !showMore
    });
  };

  render() {
    const {
      classes,
      items,
      onStatusChange,
      setFirstUploadNode,
      previousExportStatusSetted,
      skipAnimation,
      onRunAgainClicked
    } = this.props;

    const { showMore } = this.state;

    const visibleItems: any[] = items.slice(0, 8);

    const collapsedItems: any[] = items.slice(8, items.length);

    return (
      <Card>
        <CardContent className={clsx("mb-0 pl-2", classes.settingsWrapper)}>
          <Typography color="inherit" component="div" className="heading mt-1 centeredFlex">
            History
          </Typography>

          <Divider className={classes.divider} />

          <div>
            {Boolean(visibleItems.length)
              && visibleItems.map((upload, index) => {
                if (index === 0) {
                  return (
                    <div key={upload.id}>
                      <Collapse in={previousExportStatusSetted}>
                        <div>
                          <Fade in={previousExportStatusSetted} timeout={skipAnimation ? 0 : 2500}>
                            <div>
                              <FundingUploadComponent
                                fundingUpload={upload}
                                onStatusChange={onStatusChange}
                                onRunAgainClicked={onRunAgainClicked}
                              />
                            </div>
                          </Fade>
                        </div>
                      </Collapse>
                    </div>
                  );
                }

                if (index === 1) {
                  return (
                    <FundingUploadComponent
                      setFirstUploadNode={setFirstUploadNode}
                      fundingUpload={upload}
                      key={upload.id}
                      onStatusChange={onStatusChange}
                      onRunAgainClicked={onRunAgainClicked}
                    />
                  );
                }

                return (
                  <FundingUploadComponent
                    fundingUpload={upload}
                    key={upload.id}
                    onStatusChange={onStatusChange}
                    onRunAgainClicked={onRunAgainClicked}
                  />
                );
              })}

            <Collapse in={showMore}>
              {Boolean(collapsedItems.length)
                && collapsedItems.map(upload => (
                  <FundingUploadComponent
                    fundingUpload={upload}
                    key={upload.id}
                    onStatusChange={onStatusChange}
                    onRunAgainClicked={onRunAgainClicked}
                  />
                  ))}
            </Collapse>
          </div>

          {Boolean(collapsedItems.length) && (
            <ButtonBase onClick={() => this.showMore()}>
              <Typography>{showMore ? "Less..." : "More..."}</Typography>
            </ButtonBase>
          )}
        </CardContent>
      </Card>
    );
  }
}

export default AvetmissHistory;
