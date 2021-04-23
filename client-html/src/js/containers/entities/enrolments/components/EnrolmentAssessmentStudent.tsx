import React, { useEffect, useMemo, useState } from "react";
import IconButton from "@material-ui/core/IconButton";
import DateRange from "@material-ui/icons/DateRange";
import Grid from "@material-ui/core/Grid";
import clsx from "clsx";
import { Tooltip } from "@material-ui/core";
import { format } from "date-fns";
import { GradingItem, GradingType } from "@api/model";
import AssessmentSubmissionIconButton from "../../courseClasses/components/assessments/AssessmentSubmissionIconButton";
import { III_DD_MMM_YYYY } from "../../../../common/utils/dates/format";
import { EnrolmentAssessmentExtended, EnrolmentExtended } from "../../../../model/entities/Enrolment";
import { StringArgFunction } from "../../../../model/common/CommonFunctions";
import { useGradeErrors } from "../../courseClasses/components/assessments/utils/hooks";
import GradeContent from "../../courseClasses/components/assessments/GradeContent";

interface Props {
  elem: EnrolmentAssessmentExtended;
  values: EnrolmentExtended;
  onChangeStatus: any;
  classes: any;
  setModalOpenedBy: StringArgFunction;
  index: number;
  onToggleGrade: (elem: EnrolmentAssessmentExtended, grade: GradingItem) => void;
  onChangeGrade: (value: number, elem: EnrolmentAssessmentExtended) => void;
  handleGradeMenuOpen: any;
  gradeType: GradingType;
  gradeItems: GradingItem[];
}

const EnrolmentAssessmentStudent: React.FC<Props> = (
  {
    elem,
    values,
    onChangeStatus,
    classes,
    setModalOpenedBy,
    index,
    onToggleGrade,
    onChangeGrade,
    handleGradeMenuOpen,
    gradeType,
    gradeItems
  }
) => {
  const [gradeVal, setGradeVal] = useState<number>(null);

  const submissionIndex = values.submissions.findIndex(s => s.assessmentId === elem.id);
  const submission = submissionIndex !== -1 && values.submissions[submissionIndex];
  const submitStatus = submission && submission.submittedOn ? "Submitted" : "Not submitted";
  const markedStatus = submission && submission.markedOn ? "Submitted" : "Not submitted";

  useEffect(() => {
    setGradeVal(submission?.grade);
  }, [submission?.grade]);

  const submittedContent = (
    <div className="d-flex relative">
      <AssessmentSubmissionIconButton
        status={submitStatus}
        onClick={() => onChangeStatus("Submitted", submissionIndex, submitStatus, elem, index)}
      />
      {submitStatus === "Submitted" && (
        <IconButton
          size="small"
          className={classes.hiddenIcon}
          onClick={() => setModalOpenedBy(`Submitted-${submissionIndex}-${elem.name}-${index}`)}
        >
          <DateRange color="disabled" fontSize="small" />
        </IconButton>
      )}
    </div>
  );

  const markedContent = (
    <div className="d-flex relative">
      <AssessmentSubmissionIconButton
        status={markedStatus}
        onClick={() => onChangeStatus("Marked", submissionIndex, markedStatus, elem, index)}
      />
      {markedStatus === "Submitted" && (
        <IconButton
          size="small"
          className={classes.hiddenIcon}
          onClick={() => setModalOpenedBy(`Marked-${submissionIndex}-${elem.name}-${index}`)}
        >
          <DateRange color="disabled" fontSize="small" />
        </IconButton>
      )}
    </div>
  );

  const currentGrade = useMemo(() => (typeof submission?.grade === "number"
    ? gradeType.entryType === "choice list"
      ? gradeItems?.find(g => g.lowerBound === submission.grade)
      : gradeItems?.find(g => g.lowerBound < submission.grade || (submission.grade === 0 && g.lowerBound === 0))
    : null), [submission?.grade, gradeItems]);

  const gradeErrors = useGradeErrors(submission?.grade, gradeType);

  return (
    <Grid container key={index} className={clsx(classes.rowWrapper, "align-items-center d-inline-flex-center")}>
      <Grid item xs={4} className="d-inline-flex-center pl-1">
        {elem.name}
      </Grid>
      <Grid item xs={2} className={classes.center}>
        {submitStatus === "Submitted"
          ? (
            <Tooltip
              title={(
                <span>
                  Submitted date:
                  {" "}
                  {submission && format(new Date(submission.submittedOn), III_DD_MMM_YYYY)}
                </span>
              )}
              placement="top"
              disableFocusListener
              disableTouchListener
            >
              {submittedContent}
            </Tooltip>
          )
          : submittedContent}
      </Grid>
      <Grid item xs={2} className={classes.center}>
        {markedStatus === "Submitted" ? (
          <Tooltip
            title={(
              <span>
                Marked date:
                {" "}
                {submission && format(new Date(submission.markedOn), III_DD_MMM_YYYY)}
                <br />
                {submission?.markedById && Boolean(elem.tutors?.length) && (
                  <span>
                    Assessor:
                    {" "}
                    {elem.tutors.find(t => t.contactId === submission.markedById)?.tutorName}
                  </span>
                )}
              </span>
            )}
            placement="top"
            disableFocusListener
            disableTouchListener
          >
            {markedContent}
          </Tooltip>
        ) : markedContent}
      </Grid>
      <Grid item xs={2} className={classes.center}>
        <GradeContent
          handleGradeMenuOpen={handleGradeMenuOpen}
          onToggleGrade={onToggleGrade}
          onChangeGrade={onChangeGrade}
          currentGrade={currentGrade}
          gradeErrors={gradeErrors}
          setGradeVal={setGradeVal}
          gradeType={gradeType}
          gradeVal={gradeVal}
          classes={classes}
          index={index}
          elem={elem}
        />
      </Grid>
    </Grid>
  );
};

export default EnrolmentAssessmentStudent;
