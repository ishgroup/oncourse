import * as React from "react";
import { mount } from "enzyme";
import { mockedAPI, TestEntry } from "../TestEntry";
import College from "../../js/containers/preferences/containers/college/College";
import * as PreferencesModel from "../../js/model/preferences";

describe("College Form component", () => {
  test("it should render correct input values", () => {
    // Rendering complete Virtual DOM tree with testing component as a child
    const wrapper = mount(
      <TestEntry>
        <College />
      </TestEntry>,
    );

    // Returning Promise with timeout to wait for API calls to end and rendered component ti update
    // @ts-ignore
    return new Promise<void>(resolve => {
      setTimeout(() => {
        // Testing rendered HTML inputs value to match one stored in mock DB
        // Using real HTML selectors to find rendered elements
        expect(wrapper.find("#web-url input").getDOMNode().value).toEqual(
          mockedAPI.db.preference[PreferencesModel.CollegeWebsite.uniqueKey],
        );

        expect(wrapper.find("#college-name input").getDOMNode().value).toEqual(
          mockedAPI.db.preference[PreferencesModel.CollegeName.uniqueKey],
        );

        expect(wrapper.find("#oncourse-server-timezone-default div[role='button']").text()).toEqual(
          mockedAPI.db.preference[PreferencesModel.CollegeTimezone.uniqueKey],
        );

        expect(wrapper.find("#college-abn input").getDOMNode().value).toEqual(
          mockedAPI.db.preference[PreferencesModel.CollegeABN.uniqueKey],
        );

        resolve();
      }, 2000);
    });
  });
});
