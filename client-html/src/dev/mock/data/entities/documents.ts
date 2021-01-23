import { generateArraysOfRecords, getEntityResponse } from "../../mockUtils";

export function mockDocuments() {
  this.getDocuments = () => this.documents;

  this.getDocument = id => {
    const row = this.documents.rows.find(row => row.id == id);
    return {
      id: row.id,
      name: row.values[1],
      added: row.values[2],
      versionId: null,
      attachedRecordsCount: null,
      tags: [],
      thumbnail: "/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAA1AIwDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDh/t9mrcT5/wCAt/hUqalaZwJv/HT/AIVgXJtzcO0JPlthgoz8mRnbk9cdM98ZoeApbLcKflY4APXv/hVXMfZo6mLWLNeGmxj/AGT/AIVdi13T0P8ArST/ALpH8645bK8ZQR5WD0zMg/rV+Pwxrk0BlWBPLAzkzxgH/wAeppszlTp9WdbD4s0pD8xm/BP/AK9XV8f6PDgMtzn2Qf4156dJnjSJpLq2Ekj7PJV9zj3OARj8anl0CYqp81SfenzSIeHpPdne/wDCx9FH/LO7/wC+F/8Aiqmt/iJoc8yxkXUYPV3jGB9cEn9K81Ohzr0ZCfTJqkEdELLwucEgHGfrRzyF9UovY9xtvEmj3a5h1GDrjDtsJPsGwTU90yzJ5OQQ/X6V4N5pzhnI+g/+vU1vcyQkkSzoGHLI2Mj+tP2hDwKWqZ7nDZ20AbyYI03ddqgZpJL+ytyEmvII26YkkCn9a8xs9NSVA+5pAwyCeM1oroMEuAyOPo5H8jVcxi6EU9ZHaT+INIt13PqEBH/TNt5/Jc1SbxfosaEi6Zzk8LE/9RVaHwhZvbpvCkMoOCucfrUi+DNPT7iRr9I8f1p+8ZpUVu2ULnx3pgU7YbjPbcFAP/j1czrXiRtVszAlv5cZYMSH3Zx+FdvJ4Z0uBN1xMEX+8xUD9aoNYeFkJK6raZ9PtMY/lUtSN6c6Sd4pnmpXPQH8QRSG3lB/1b/lXe6pa6YlvE1hLDNuLbnjl3gYxxwawWjG7pUOJ1xrcyvY5QVswr5/hu5ATdJbuOcfdUnP8wa1f+EI/wCoj/5B/wDsquWnhhrW3uoRfFluIthHlYwc9evPf86lG802tDtvD0dpdeFtNlVUcC2RS23BDKMH9QazdU8awaXqbaZcWDEK4R38wbQhxzjHoelcx/wiEItoohdy5z++JUYYZyNo/h4+v9K0LvQ4r2FIZbu78qNI0RA4woRdoxkcZ6n3q/aaHEsGuZuWqMrV7aGz1NvKYeUs6spJ/hPH9a2EhUx/dHWo10GJILiD7ZdmO42eaGZSW2nK8lcjB9DWgsCqMAnFJSRpKjOyRlmMCVflHX0rl3C/2W2P+fgdv9iu6NpGWDZbIOetZn/CM2fkeT51xt37/vLnOMelJtFwpyW5wMgxIwPYmpkdWbAByxGc12f/AAiGnk8y3J5J5cd/wp48KaauNvnDH+37g+nt+tSbst6dF5cEaf3VA/StiFeQaih8qGMKLeNiP4mLZP5HFSm6/uQxofUZ/qa1UkcEqFRswtR8NXE9vdtDoEj30t28wleaPbt8wkD7+funBGP5ZrFXwbq6RosujudpBZo5k3MBkkZ3kDOQM4/hB9c95/al1/eH5Uv9q3POSpz7UrxLjHERVrL8Tz648PX4e42+H7iNXUiEKS/lnzMjJz83y/L+uM9W32nXMpkW28KXNshXEe1ZXZWJXkk/e4DADj73tXop1acn7sf5H/GmHU5yCCEwRg8UvdKSr9UvvZy8NnJZaZbWsjEtFHggj7pJLMv4MSKrMnzV0EsCzMWdmJPU5qA6bCTnc/5j/Ci6BUp7suUUUVB1hRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAf/9k=",
      versions: [
        {
          id: 260,
          added: "2011-11-18T11:06:41.000Z",
          createdBy: null,
          size: row.values[3],
          fileName: row.values[5],
          mimeType: row.values[6],
          url: "https://ish-oncourse-sttrianians.s3.amazonaws.com/4e6c5b71-ff96-4d16-a95d-d1d64f290b6e?versionId=FCzUwTzXhghF2NNd9O90LQUe6u_xeUqk",
          thumbnail: "/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAA1AIwDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwDh/t9mrcT5/wCAt/hUqalaZwJv/HT/AIVgXJtzcO0JPlthgoz8mRnbk9cdM98ZoeApbLcKflY4APXv/hVXMfZo6mLWLNeGmxj/AGT/AIVdi13T0P8ArST/ALpH8645bK8ZQR5WD0zMg/rV+Pwxrk0BlWBPLAzkzxgH/wAeppszlTp9WdbD4s0pD8xm/BP/AK9XV8f6PDgMtzn2Qf4156dJnjSJpLq2Ekj7PJV9zj3OARj8anl0CYqp81SfenzSIeHpPdne/wDCx9FH/LO7/wC+F/8Aiqmt/iJoc8yxkXUYPV3jGB9cEn9K81Ohzr0ZCfTJqkEdELLwucEgHGfrRzyF9UovY9xtvEmj3a5h1GDrjDtsJPsGwTU90yzJ5OQQ/X6V4N5pzhnI+g/+vU1vcyQkkSzoGHLI2Mj+tP2hDwKWqZ7nDZ20AbyYI03ddqgZpJL+ytyEmvII26YkkCn9a8xs9NSVA+5pAwyCeM1oroMEuAyOPo5H8jVcxi6EU9ZHaT+INIt13PqEBH/TNt5/Jc1SbxfosaEi6Zzk8LE/9RVaHwhZvbpvCkMoOCucfrUi+DNPT7iRr9I8f1p+8ZpUVu2ULnx3pgU7YbjPbcFAP/j1czrXiRtVszAlv5cZYMSH3Zx+FdvJ4Z0uBN1xMEX+8xUD9aoNYeFkJK6raZ9PtMY/lUtSN6c6Sd4pnmpXPQH8QRSG3lB/1b/lXe6pa6YlvE1hLDNuLbnjl3gYxxwawWjG7pUOJ1xrcyvY5QVswr5/hu5ATdJbuOcfdUnP8wa1f+EI/wCoj/5B/wDsquWnhhrW3uoRfFluIthHlYwc9evPf86lG802tDtvD0dpdeFtNlVUcC2RS23BDKMH9QazdU8awaXqbaZcWDEK4R38wbQhxzjHoelcx/wiEItoohdy5z++JUYYZyNo/h4+v9K0LvQ4r2FIZbu78qNI0RA4woRdoxkcZ6n3q/aaHEsGuZuWqMrV7aGz1NvKYeUs6spJ/hPH9a2EhUx/dHWo10GJILiD7ZdmO42eaGZSW2nK8lcjB9DWgsCqMAnFJSRpKjOyRlmMCVflHX0rl3C/2W2P+fgdv9iu6NpGWDZbIOetZn/CM2fkeT51xt37/vLnOMelJtFwpyW5wMgxIwPYmpkdWbAByxGc12f/AAiGnk8y3J5J5cd/wp48KaauNvnDH+37g+nt+tSbst6dF5cEaf3VA/StiFeQaih8qGMKLeNiP4mLZP5HFSm6/uQxofUZ/qa1UkcEqFRswtR8NXE9vdtDoEj30t28wleaPbt8wkD7+funBGP5ZrFXwbq6RosujudpBZo5k3MBkkZ3kDOQM4/hB9c95/al1/eH5Uv9q3POSpz7UrxLjHERVrL8Tz648PX4e42+H7iNXUiEKS/lnzMjJz83y/L+uM9W32nXMpkW28KXNshXEe1ZXZWJXkk/e4DADj73tXop1acn7sf5H/GmHU5yCCEwRg8UvdKSr9UvvZy8NnJZaZbWsjEtFHggj7pJLMv4MSKrMnzV0EsCzMWdmJPU5qA6bCTnc/5j/Ci6BUp7suUUUVB1hRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQAUUUUAf/9k="
        }
      ],
      description: null,
      access: row.values[4],
      shared: true,
      removed: false,
      attachmentRelations: [],
      createdOn: "2011-11-18T11:06:41.000Z",
      modifiedOn: "2014-08-04T15:02:41.000Z"
    };
  };

  this.addDocument = item => {
    const data = JSON.parse(item);
    const documents = this.documents;
    const totalRows = documents.rows;

    data.id = totalRows.length + 1;

    documents.rows.push({
      id: data.id,
      values: [
        "https://ish-oncourse-sttrianians.s3.amazonaws.com/4e6c5b71-ff96-4d16-a95d-d1d64f290b6e",
        data.name,
        "2011-11-18T11:06:41.000Z",
        "134.40 kb",
        data.access,
        data.fileName,
        "image/jpeg",
        true
      ]
    });

    this.documents = documents;
  };

  const rows = generateArraysOfRecords(20, [
    { name: "id", type: "number" },
    { name: "link", type: "string" },
    { name: "name", type: "string" },
    { name: "added", type: "Datetime" },
    { name: "byteSize", type: "string" },
    { name: "webVisibility", type: "string" },
    { name: "fileName", type: "string" },
    { name: "mimeType", type: "string" },
    { name: "active", type: "boolean" }
  ]).map(l => ({
    id: l.id,
    values: [
      "https://ish-oncourse-sttrianians.s3.amazonaws.com/4e6c5b71-ff96-4d16-a95d-d1d64f290b6e",
      l.name, "2011-11-18T11:06:41.000Z", "134.40 kb", "Website", `${l.fileName}.jpg`, "image/jpeg", true]
  }));

  return getEntityResponse(
    "Document",
    rows,
    [
      {
        title: "Link",
        attribute: "link"
      },
      {
        title: "Document name",
        attribute: "name",
        sortable: true
      },
      {
        title: "Date added",
        attribute: "added",
        type: "Date",
        sortable: true
      },
      {
        title: "Size",
        attribute: "currentVersion.byteSize"
      },
      {
        title: "Security level",
        attribute: "webVisibility",
        sortable: true,
        width: 100
      },
      {
        title: "File name",
        attribute: "currentVersion.fileName",
        width: 100
      },
      {
        title: "Type",
        attribute: "currentVersion.mimeType",
        width: 100
      },
      {
        title: "Active",
        attribute: "currentVersion.active",
        type: "Boolean",
        visible: false,
        system: true,
        width: 100
      }
    ],
    {
      sort: [{ attribute: "name", ascending: true, complexAttribute: [] }]
    }
  );
}