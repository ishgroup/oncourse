import uuid from "uuid";
import localForage from "localforage";
import {Page, Block, MenuItem, Theme, User} from "../../js/model";

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

export class MockDB {

  private id: string = uuid();

  users: User[];
  pages: Page[];
  blocks: Block[];
  menus: MenuItem[];
  themes: Theme[];

  constructor() {
    this.init();
  }

  init(): void {
    this.users = [this.mockUser()];
    this.pages = this.mockPages();
    this.blocks = this.mockBlocks();
    this.menus = this.mockMenus();
    this.themes = this.mockThemes();
  }

  mockUser() {
    return {
      id: 1,
      firstName: "John",
      lastName: "Doe",
    };
  }

  mockPages() {
    return [
      {
        id: 1,
        title: 'Page - 1',
        visible: true,
        theme: 'first',
        layout: 'custom',
        url: '/page1',
        html: "<div>\n  <h1>Page Html 1</h1>\n  <p>Page text 1</p>\n</div>",
      },
      {
        id: 2,
        title: 'Page - 2',
        visible: true,
        theme: 'first',
        layout: 'custom',
        url: '/page2',
        html: "<div>\n  <h2>Page Html 2</h2>\n  <p>\n    <small>Page text 2</small>\n  </p>\n  <p>\n    Lorem ipsum dolor sit amet, consectetur adipisicing elit. \n    Accusantium adipisci autem commodi culpa cupiditate distinctio dolore doloremque \n    eius eveniet exercitationem facere facilis fuga fugit illo illum iste magnam \n    maxime minima nam nemo numquam officia provident quas quidem reprehenderit \n    repudiandae rerum sed totam ullam unde, velit vero vitae voluptate? Error, \n    soluta.\n  </p>\n</div>\n",
      },
      {
        id: 3,
        title: 'Page - 3',
        visible: false,
        theme: 'first',
        layout: 'custom',
        url: '/page3',
        html: "<div>\n  <h4>Page Html 3</h4>\n  <p>Page text 3</p>\n  <p>Other Page text 3</p>\n  <p>\n    Lorem ipsum dolor sit amet, consectetur adipisicing elit. \n  Beatae distinctio doloremque illum iure neque nisi perspiciatis quas quasi \n  repudiandae sed?\n  </p>\n</div>\n",
      },
    ];
  }

  mockBlocks() {
    return [
      {
        id: 1,
        title: 'Header',
        html: "<div>\n  <h1>Header Title</h1>\n</div>",
      },
      {
        id: 2,
        title: 'Footer',
        html: "<div>\n  <footer>Lorem ipsum dolor sit amet.</footer>\n</div>",
      },
      {
        id: 3,
        title: 'Content1',
        html: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li>\n  <li>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Suscipit!</li>\n</ul>\n</div>",
      },
      {
        id: 4,
        title: 'Content2',
        html: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li>\n  <li>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Suscipit!</li>\n</ul>\n</div>",
      },
      {
        id: 5,
        title: 'Content3',
        html: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li></ul>\n</div>",
      },
      {
        id: 6,
        title: 'Content4',
        html: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li>\n</ul>\n</div>",
      },
      {
        id: 7,
        title: 'Content5',
        html: "<div>\n<ul>\n  <li>Lorem ipsum dolor sit amet.</li>\n  <li>Lorem ipsum.</li>\n</ul>\n</div>",
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

  mockThemes() {
    return [
      {
        id: 1,
        title: 'Custom Theme',
        layout: 'Custom',
        schema: {
          top: [{
            id: 1,
            position: 1,
          }],
          middle1: [],
          middle2: [],
          middle3: [],
          footer: [{
            id: 2,
            position: 2,
          }],
        },
      },
      {
        id: 2,
        title: 'Default Theme',
        layout: 'User',
        schema: {
          top: [],
          middle1: [],
          middle2: [{
            id: 3,
            position: 1,
          },
          {
            id: 4,
            position: 1,
          }],
          middle3: [],
          footer: [{
            id: 5,
            position: 2,
          }],
        },
      },
    ];
  }


  addContact(contact) {
    // const nc = normalize([contact], ContactsSchema);
    // this.contacts.result = [...this.contacts.result, ...nc.result];
    // this.contacts.entities.contact = {...this.contacts.entities.contact, ...nc.entities.contact};
    // localForage.setItem("MockDB", this);
    // return contact.id;
  }
}

