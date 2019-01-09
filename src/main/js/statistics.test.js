// @flow
import fetchMock from "fetch-mock";
import { getCommitsPerAuthor } from "./statistics";

describe("API get statistics", () => {
  const PULLREQUEST_URL = "/repositories/scmadmin/TestRepo/commits-per-author";

  const commitsPerAuthor: commitsPerAuthor = {
    author: [
      {
        count: 42,
        value: "Max Mustermann"
      },
      {
        count: 17,
        value: "Janine Mustermann"
      },
      {
        count: 1,
        value: "Admin Admin"
      }
    ]
  };

  afterEach(() => {
    fetchMock.reset();
    fetchMock.restore();
  });

  it("should get commits per author successfully", done => {
    fetchMock.getOnce(
      "/api/v2/statistic/scmadmin/TestRepo/commits-per-author",
      commitsPerAuthor
    );

    getCommitsPerAuthor("/statistic/scmadmin/TestRepo/commits-per-author").then(
      response => {
        expect(response).toEqual(commitsPerAuthor.author); // Is .author notation okay?
        done();
      }
    );
  });

  it("should fail on getting commits per author", done => {
    fetchMock.getOnce(
      "/api/v2/statistic/scmadmin/TestRepo/commits-per-author",
      {
        status: 500
      }
    );

    getCommitsPerAuthor("/statistic/scmadmin/TestRepo/commits-per-author").then(
      response => {
        expect(response.error).toBeDefined();
        done();
      }
    );
  });
});
