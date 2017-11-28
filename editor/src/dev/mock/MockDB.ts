import localForage from "localforage";
import update from 'react-addons-update';
import faker from 'faker';
import {
  Page, Block, MenuItem, Theme, User, Version, CheckoutSettings, WebsiteSettings, RedirectSettings,
  SkillsOnCourseSettings, ThemeSchema, Layout,
} from "../../js/model";
import {ClassCondition} from "../../js/model/settings/ClassCondition";
import {ClassEnrolmentCondition} from "../../js/model/settings/ClassEnrolmentCondition";

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
  checkout: CheckoutSettings;
  website: WebsiteSettings;
  redirect: RedirectSettings;
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
        number: 1,
        title: 'Page - 1',
        visible: true,
        themeId: 2,
        urls: [
          {
            link: '/page1/',
            isDefault: false,
          },
        ],
        content: "<div>\n  <h1>Page Html 1</h1>\n  <p>Page text 1</p>\n</div>",
      },
      {
        number: 2,
        title: 'Page - 2',
        visible: true,
        themeId: 2,
        urls: [
          {
            link: '/page2/',
            isDefault: false,
          },
          {
            link: '/mypage2/',
            isDefault: false,
          },
        ],
        content: "<div>\n  <h2>Page Html 2</h2>\n  <p>\n    <small>Page text 2</small>\n  </p>\n  <p>\n    Lorem ipsum dolor sit amet, consectetur adipisicing elit. \n    Accusantium adipisci autem commodi culpa cupiditate distinctio dolore doloremque \n    eius eveniet exercitationem facere facilis fuga fugit illo illum iste magnam \n    maxime minima nam nemo numquam officia provident quas quidem reprehenderit \n    repudiandae rerum sed totam ullam unde, velit vero vitae voluptate? Error, \n    soluta.\n  </p>\n</div>\n",
      },
      {
        number: 3,
        title: 'Page - 3',
        visible: false,
        themeId: 2,
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
        expanded: true,
        url: '/menu1',
        children: [
          {
            id: 4,
            title: 'Menu 1 - 1',
            url: '/menu1-1',
            expanded: true,
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
        expanded: true,
        url: '/menu2',
        children: [
          {
            id: 6,
            title: 'Menu 2 - 1',
            url: '/menu2-1',
            expanded: true,
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
        expanded: true,
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
        schema: {
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
        schema: {
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
        layoutKey: 'Default Layout',
      },
      {
        id: 2,
        layoutKey: 'Custom Layout',
      },
      {
        id: 3,
        layoutKey: 'Common Layout',
      },
    ];
  }

  mockVersions(): Version[] {
    return [
      {
        id: 1,
        published: false,
        author: faker.name.findName(),
        changes: 25,
        date: '',
      },
      {
        id: 2,
        published: true,
        author: faker.name.findName(),
        changes: 27,
        date: new Date('10/08/2017').toISOString(),
      },
      {
        id: 3,
        published: true,
        author: faker.name.findName(),
        changes: 30,
        date: new Date('05/05/2017').toISOString(),
      },
      {
        id: 4,
        published: true,
        author: faker.name.findName(),
        changes: 43,
        date: new Date('03/03/2017').toISOString(),
      },
      {
        id: 5,
        published: true,
        author: faker.name.findName(),
        changes: 80,
        date: new Date('12/12/2016').toISOString(),
      },
      {
        id: 6,
        published: true,
        author: faker.name.findName(),
        changes: 88,
        date: new Date('08/09/2016').toISOString(),
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
      checkout: {
        successUrl: '',
        refundPolicy: '',
      },
      website: {
        enableSocialMedia: false,
        addThisId: '',
        enableForCourse: false,
        enableForWebpage: false,
        classAge: {
          hideClassDays: 0,
          hideClassCondition: ClassCondition[1],
          stopWebEnrolmentDays: 0,
          stopWebEnrolmentCondition: ClassEnrolmentCondition[1],
        },
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
    };
  }

  deleteThemeById(id: number) {
    const index = this.themes.findIndex(item => item.id === id);
    this.themes = update(this.themes, {
      $splice: [
        [index, 1],
      ],
    });
  }

  deletePageByNumber(pageNumber: number) {
    const index = this.pages.findIndex(item => item.number === pageNumber);
    this.pages = update(this.pages, {
      $splice: [
        [index, 1],
      ],
    });
  }

  deleteBlockByTitle(name: string) {
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
    const layoutKeys = Object.keys(theme.schema);

    layoutKeys.forEach(key => {
      const blockIndex = theme.schema[key].findIndex(key => key.id === blockId);
      if (blockIndex !== -1) {
        deleteBlockFromPart(themeId, key, blockIndex);
      }
    });

    const deleteBlockFromPart = (themeId, part, blockIndex) => {
      const newTheme = this.themes.find(theme => theme.id === themeId);
      newTheme.schema[part] = update(theme.schema[part], {
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
    this.pages = this.pages.map(item => item.number === page.number ? {...item, ...page} : item);
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
    const page = new Page();
    const newNumber = Math.max(...this.pages.map(page => page.number)) + 1;
    page.title = `New Page ${isFinite(newNumber) ? newNumber : 1}`;
    page.number = isFinite(newNumber) ? newNumber : 1;
    page.urls = [];

    this.pages.push(page);
    return page;
  }

  createNewBlock() {
    const block = new Block();
    const newId = Math.max(...this.blocks.map(block => block.id)) + 1;
    block.title = `New Block ${isFinite(newId) ? newId : 1}`;
    block.id = isFinite(newId) ? newId : 1;

    this.blocks.push(block);
    return block;
  }

  createNewTheme(): Theme {
    const theme = new Theme();
    const newId = Math.max(...this.themes.map(theme => theme.id)) + 1;
    theme.title = `New Theme ${isFinite(newId) ? newId : 1}`;
    theme.id = isFinite(newId) ? newId : 1;
    theme.schema = new ThemeSchema();
    theme.schema.top = [];
    theme.schema.footer = [];
    theme.schema.left = [];
    theme.schema.centre = [];
    theme.schema.right = [];

    this.themes.push(theme);
    return theme;
  }

  getPageRender(pageNumber) {
    return this.pages.find(page => page.number === pageNumber).content;
  }

  addContact(contact) {
    // const nc = normalize([contact], ContactsSchema);
    // this.contacts.result = [...this.contacts.result, ...nc.result];
    // this.contacts.entities.contact = {...this.contacts.entities.contact, ...nc.entities.contact};
    // localForage.setItem("MockDB", this);
    // return contact.id;
  }
}

