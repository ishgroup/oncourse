import localForage from "localforage";
import update from 'react-addons-update';
import faker from 'faker';
import {
  Page, Block, MenuItem, Theme, User, Version, WebsiteSettings, Redirects, VersionStatus,
  SkillsOnCourseSettings, ThemeBlocks, Layout, Condition, CheckoutSettings, State, SpecialPages,
} from "../../js/model";

export const CreateMockDB = (): MockDB => {
  const result: MockDB = new MockDB();
  localForage.getItem("MockCmsDB").then((db: MockDB) => {
    if (db) {

    } else {
      localForage.setItem("MockCmsDB", result);
    }
  });
  return result;
};

interface Settings {
  skillsOnCourse: SkillsOnCourseSettings;
  website: WebsiteSettings;
  redirect: Redirects;
  checkout: CheckoutSettings;
  specialPages: SpecialPages;
}

export class MockDB {

  users: User[];
  pages: Page[];
  blocks: Block[];
  menus: MenuItem[];
  themes: Theme[];
  layouts: Layout[];
  versions: Version[];
  settings: Settings;

  constructor() {
    this.init();
  }

  init(): void {
    this.users = [this.mockUser()];
    this.pages = this.mockPages();
    this.blocks = this.mockBlocks();
    this.menus = this.mockMenus();
    this.themes = this.mockThemes();
    this.layouts = this.mockLayouts();
    this.versions = this.mockVersions();
    this.settings = this.mockSettings();
  }

  mockUser(): User {
    return {
      firstName: "John",
      lastName: "Doe",
    };
  }

  mockPages(): Page[] {
    return [
      {
        id: 1,
        title: 'page1',
        visible: true,
        urls: [
          {
            link: '/page1/',
            isDefault: false,
          },
        ],
        suppressOnSitemap: true,
        content: "<div>\n  <h1>Page Html 1</h1>\n  <p>Page text 1</p>\n</div>",
      },
      {
        id: 2,
        title: 'Page - 2',
        visible: true,
        urls: [
          {
            link: '/page/2',
            isDefault: false,
          },
          {
            link: '/mypage2/',
            isDefault: false,
          },
        ],
        suppressOnSitemap: true,
        content: "<div>\n  <h2>Page Html 2</h2>\n  <p>\n    <small>Page text 2</small>\n  </p>\n  <p>\n    Lorem ipsum dolor sit amet, consectetur adipisicing elit. \n    Accusantium adipisci autem commodi culpa cupiditate distinctio dolore doloremque \n    eius eveniet exercitationem facere facilis fuga fugit illo illum iste magnam \n    maxime minima nam nemo numquam officia provident quas quidem reprehenderit \n    repudiandae rerum sed totam ullam unde, velit vero vitae voluptate? Error, \n    soluta.\n  </p>\n</div>\n",
      },
      {
        id: 3,
        title: 'Page - 3',
        visible: true,
        urls: [
          {
            link: '/page3/',
            isDefault: false,
          },
          {
            link: '/mypage3/',
            isDefault: false,
          },
        ],
        suppressOnSitemap: true,
        content: "<div>\n  <h4>Page Html 3</h4>\n  <p>Page text 3</p>\n  <p>Other Page text 3</p>\n  <p>\n    Lorem ipsum dolor sit amet, consectetur adipisicing elit. \n  Beatae distinctio doloremque illum iure neque nisi perspiciatis quas quasi \n  repudiandae sed?\n  </p>\n</div>\n",
      },
    ];
  }

  mockBlocks(): Block[] {
    return [
      {
        id: 1,
        title: 'Header',
        content: "<div>\n  <h1>Header Title</h1>\n</div>",
      },
      {
        id: 2,
        title: 'Footer',
        content: "<div>\n  <footer>Lorem ipsum dolor sit amet.</footer>\n</div>",
      },
      {
        id: 3,
        title: 'Content1',
        content: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li>\n  <li>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Suscipit!</li>\n</ul>\n</div>",
      },
      {
        id: 4,
        title: 'Content2',
        content: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li>\n  <li>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Suscipit!</li>\n</ul>\n</div>",
      },
      {
        id: 5,
        title: 'Content3',
        content: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li></ul>\n</div>",
      },
      {
        id: 6,
        title: 'Content4',
        content: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li>\n</ul>\n</div>",
      },
      {
        id: 7,
        title: 'Content5',
        content: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li>\n</ul>\n</div>",
      },
    ];
  }

  mockMenus() {
    return [
      {
        id: 1,
        title: 'Menu - 1',
        url: '/menu1',
        children: [
          {
            id: 4,
            title: 'Menu 1 - 1',
            url: '/menu1-1',
            children: [
              {
                id: 5,
                title: 'Sub menu 1 - 1 - 1',
                url: '/menu1-1-1',
              },
            ],
          },
        ],
      },
      {
        id: 2,
        title: 'Menu - 2',
        url: '/menu2',
        children: [
          {
            id: 6,
            title: 'Menu 2 - 1',
            url: '/menu2-1',
            children: [
              {
                id: 7,
                title: 'Sub menu 2 - 1',
                url: '/menu 2-1-2',
              },
            ],
          },
        ],
      },
      {
        id: 3,
        title: 'Menu - 3',
        url: '/menu3',
      },
    ];
  }

  mockThemes(): Theme[] {
    return [
      {
        id: 1,
        title: 'Custom Theme',
        layoutId: 1,
        blocks: {
          top: [{
            id: 1,
            position: 1,
          }],
          left: [],
          centre: [],
          right: [],
          footer: [{
            id: 2,
            position: 2,
          }],
        },
      },
      {
        id: 2,
        title: 'Default Theme',
        layoutId: 3,
        blocks: {
          top: [],
          left: [],
          centre: [{
            id: 3,
            position: 1,
          },
          {
            id: 4,
            position: 1,
          }],
          right: [],
          footer: [{
            id: 5,
            position: 2,
          }],
        },
      },
    ];
  }

  mockLayouts(): Layout[] {
    return [
      {
        id: 1,
        title: 'Default Layout',
      },
      {
        id: 2,
        title: 'Custom Layout',
      },
      {
        id: 3,
        title: 'Common Layout',
      },
    ];
  }

  mockVersions(): Version[] {
    return [
      {
        id: 1,
        status: VersionStatus.draft,
        author: faker.name.findName(),
        changes: 25,
        publishedOn: '',
      },
      {
        id: 2,
        status: VersionStatus.published,
        author: faker.name.findName(),
        changes: 27,
        publishedOn: new Date('10/08/2017').toISOString(),
      },
      {
        id: 3,
        status: VersionStatus.published,
        author: faker.name.findName(),
        changes: 30,
        publishedOn: new Date('05/05/2017').toISOString(),
      },
      {
        id: 4,
        status: VersionStatus.published,
        author: faker.name.findName(),
        changes: 43,
        publishedOn: new Date('03/03/2017').toISOString(),
      },
      {
        id: 5,
        status: VersionStatus.published,
        author: faker.name.findName(),
        changes: 80,
        publishedOn: new Date('12/12/2016').toISOString(),
      },
      {
        id: 6,
        status: VersionStatus.published,
        author: faker.name.findName(),
        changes: 88,
        publishedOn: new Date('08/09/2016').toISOString(),
      },
    ];
  }

  mockSettings(): Settings {
    return {
      skillsOnCourse: {
        hideStudentDetails: false,
        enableOutcomeMarking: true,
        tutorFeedbackEmail: '',
      },
      website: {
        enableSocialMedia: false,
        addThisId: '',
        enableForCourse: true,
        enableForWebpage: false,
        suburbAutocompleteState: State.NT,
        classAge: {
          hideClass: {
            offset: 0,
            condition: Condition.beforeClassEnds,
          },
          stopWebEnrolment: {
            offset: 1,
            condition: Condition.beforeClassStarts,
          },
        },
      },
      checkout: {
        allowCreateContactOnEnrol: false,
        allowCreateContactOnWaitingList: true,
        allowCreateContactOnMailingList: false,
        collectParentDetails: true,
        contactAgeWhenNeedParent: 12,
        enrolmentMinAge: 18,
      },
      redirect: {
        rules: [
          {
            from: '/page1',
            to: '/page2',
          },
          {
            from: '/page3',
            to: '/page4',
          },
        ],
      },
      specialPages: {
        rules: [
          {
            from: '/page1',
            specialPage: "TUTORS",
            matchType: 'STARTS WITH',
            error: "",
          },
          {
            from: '/page2',
            specialPage: "TUTORS",
            matchType: 'STARTS WITH',
            error: "",
          },
          {
            from: '/page3',
            specialPage: "TUTORS",
            matchType: 'STARTS WITH',
            error: "",
          },
          {
            from: '/page4',
            specialPage: "TUTORS",
            matchType: 'STARTS WITH',
            error: "",
          },

        ],
      },
    };
  }

  deleteThemeById(id: number) {
    const index = this.themes.findIndex(item => item.id == id);

    this.themes = update(this.themes, {
      $splice: [
        [index, 1],
      ],
    });
  }

  deletePageById(pageId: number) {
    const index = this.pages.findIndex(item => item.id == pageId);
    this.pages = update(this.pages, {
      $splice: [
        [index, 1],
      ],
    });
  }

  deleteBlockById(id: string) {
    // const index = this.blocks.findIndex(item => item.title === name);
    // this.blocks = update(this.blocks, {
    //   $splice: [
    //     [index, 1],
    //   ],
    // });

    // this.themes.forEach(theme => this.deleteBlockFromTheme(theme.id, id));
  }

  editBlock(block: Block) {
    this.blocks = this.blocks.map(item => item.id === block.id ? {...item, ...block} : item);
  }

  deleteBlockFromTheme(themeId, blockId) {
    const theme = this.themes.find(theme => theme.id === themeId);
    const layoutKeys = Object.keys(theme.blocks);

    layoutKeys.forEach(key => {
      const blockIndex = theme.blocks[key].findIndex(key => key.id === blockId);
      if (blockIndex !== -1) {
        deleteBlockFromPart(themeId, key, blockIndex);
      }
    });

    const deleteBlockFromPart = (themeId, part, blockIndex) => {
      const newTheme = this.themes.find(theme => theme.id === themeId);
      newTheme.blocks[part] = update(theme.blocks[part], {
        $splice: [
          [blockIndex, 1],
        ],
      });

      this.themes = this.themes.map(theme => theme.id === themeId ? newTheme : theme);
    };

  }

  addPage(page: Page) {
    this.pages = update(this.pages, {
      $push: [
        page,
      ],
    });
  }

  editPage(page: Page) {
    this.pages = this.pages.map(item => item.id === page.id ? {...item, ...page} : item);
  }

  addTheme(theme: Theme) {
    this.themes = update(this.themes, {
      $push: [
        theme,
      ],
    });
  }

  editTheme(theme: Theme) {
    this.themes = this.themes.map(item => item.id === theme.id ? {...item, ...theme} : item);
  }

  saveMenu(items: MenuItem[]) {
    this.menus = items;
  }

  saveSettings(settings, category) {
    this.settings[category] = settings;
  }

  getPageByUrl(url): Page {
    return this.pages.find(page => !!page.urls.find(l => l.link === url));
  }

  createNewPage(): Page {
    const page = {} as Page;
    const newNumber = Math.max(...this.pages.map(page => page.id)) + 1;
    page.title = `New Page ${isFinite(newNumber) ? newNumber : 1}`;
    page.id = isFinite(newNumber) ? newNumber : 1;
    page.urls = [];

    this.pages.push(page);
    return page;
  }

  createNewBlock() {
    const block = {} as Block;
    const newId = Math.max(...this.blocks.map(block => block.id)) + 1;
    block.title = `New Block ${isFinite(newId) ? newId : 1}`;
    block.id = isFinite(newId) ? newId : 1;

    this.blocks.push(block);
    return block;
  }

  createNewTheme(): Theme {
    const theme = {} as Theme;
    const newId = Math.max(...this.themes.map(theme => theme.id)) + 1;
    theme.title = `New Theme ${isFinite(newId) ? newId : 1}`;
    theme.id = isFinite(newId) ? newId : 1;
    theme.blocks = {} as ThemeBlocks;
    theme.blocks.top = [];
    theme.blocks.footer = [];
    theme.blocks.left = [];
    theme.blocks.centre = [];
    theme.blocks.right = [];

    this.themes.push(theme);
    return theme;
  }

  addContact(contact) {
    // const nc = normalize([contact], ContactsSchema);
    // this.contacts.result = [...this.contacts.result, ...nc.result];
    // this.contacts.entities.contact = {...this.contacts.entities.contact, ...nc.entities.contact};
    // localForage.setItem("MockDB", this);
    // return contact.id;
  }
}

