import { StatisticData } from "@api/model";

export function mockDashboardStatistics(): StatisticData {
  this.getStatistic = () => {
    return this.dashboardStatistics;
  };

  return {
    enrolmentsChartLine: [2, 3, 4, 8, 2, 2],
    revenueChartLine: [1, 5, 2, 5, 4, 3],
    studentsCount: 232,
    moneyCount: 500,
    latestEnrolments: [
      {
        title: "Spanish 103 (Level 2)",
        info: "2019-03-18T10:00:25.764Z",
        link: "/swing/enrolments/196234"
      },
      {
        title: "Spanish 102 (Level 1)",
        info: "2019-03-18T10:00:25.764Z",
        link: "/swing/enrolments/196235"
      },
      {
        title: "Children's book illustration",
        info: "2019-03-18T10:00:25.764Z",
        link: "/swing/enrolments/196236"
      },
      {
        title: "University preparation",
        info: "2019-03-18T10:00:25.764Z",
        link: "/swing/enrolments/196237"
      }
    ],
    latestWaitingLists: [
      {
        title: "French 101 (Beginners)",
        info: "F101 220",
        link: "/waitingList?search=course.id=1"
      },
      {
        title: "Changing your thinking",
        info: "CYTH 144",
        link: "/waitingList?search=course.id=2"
      },
      {
        title: "Archeology & Artifacts Works",
        info: "AWNM 125",
        link: "/waitingList?search=course.id=3"
      }
    ],
    openedClasses: 42,
    inDevelopmentClasses: 4,
    cancelledClasses: 34,
    completedClasses: 128,
    commencedClasses: 95
  };
}
