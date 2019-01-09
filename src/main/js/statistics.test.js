// @flow
import fetchMock from "fetch-mock";
import {
  getCommitsPerAuthor,
  getCommitsPerHour,
  getCommitsPerMonth,
  getCommitsPerYear,
  getCommitsPerWeekday,
  getFileModificationCount,
  getTopModifiedFiles,
  getTopWords
} from "./statistics";

describe("API get statistics", () => {
  const PATH = "/statistic/scmadmin/TestRepo";

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
      "/api/v2" + PATH + "/commits-per-author",
      commitsPerAuthor
    );

    getCommitsPerAuthor(PATH + "/commits-per-author").then(response => {
      expect(response).toEqual(commitsPerAuthor.author); // Is .author notation okay?
      done();
    });
  });

  it("should fail on getting commits per author", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/commits-per-author", {
      status: 500
    });

    getCommitsPerAuthor(PATH + "/commits-per-author").then(response => {
      expect(response.error).toBeDefined();
      done();
    });
  });

  const commitsPerHour: commitsPerHour = {
    hour: [
      {
        count: 2,
        value: 13
      },
      {
        count: 3,
        value: 21
      },
      {
        count: 5,
        value: 14
      },
      {
        count: 8,
        value: 15
      }
    ]
  };

  it("should get commits per hour successfully", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/commits-per-hour", commitsPerHour);

    getCommitsPerHour(PATH + "/commits-per-hour").then(response => {
      expect(response).toEqual(commitsPerHour.hour); // Is .hour notation okay?
      done();
    });
  });

  it("should fail on getting commits per hour", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/commits-per-hour", {
      status: 500
    });

    getCommitsPerHour(PATH + "/commits-per-hour").then(response => {
      expect(response.error).toBeDefined();
      done();
    });
  });

  const commitsPerMonth: commitsPerMonth = {
    month: [
      {
        count: 42,
        value: "2018-12"
      },
      {
        count: 17,
        value: "2019-01"
      },
      {
        count: 1,
        value: "2019-02"
      }
    ]
  };

  it("should get commits per month successfully", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/commits-per-month", commitsPerMonth);

    getCommitsPerMonth(PATH + "/commits-per-month").then(response => {
      expect(response).toEqual(commitsPerMonth.month); // Is .month notation okay?
      done();
    });
  });

  it("should fail on getting commits per month", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/commits-per-month", {
      status: 500
    });

    getCommitsPerMonth(PATH + "/commits-per-month").then(response => {
      expect(response.error).toBeDefined();
      done();
    });
  });

  const commitsPerYear: commitsPerYear = {
    year: [
      {
        count: 42,
        value: "2018-12"
      },
      {
        count: 17,
        value: "2019-01"
      },
      {
        count: 1,
        value: "2019-02"
      }
    ]
  };

  it("should get commits per year successfully", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/commits-per-year", commitsPerYear);

    getCommitsPerYear(PATH + "/commits-per-year").then(response => {
      expect(response).toEqual(commitsPerYear.year); // Is .year notation okay?
      done();
    });
  });

  it("should fail on getting commits per year", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/commits-per-year", {
      status: 500
    });

    getCommitsPerYear(PATH + "/commits-per-year").then(response => {
      expect(response.error).toBeDefined();
      done();
    });
  });

  const commitsPerWeekday: commitsPerWeekday = {
    weekday: [
      {
        count: 1,
        value: "Monday"
      },
      {
        count: 10,
        value: "Tuesday"
      },
      {
        count: 42,
        value: "Wednesday"
      }
    ]
  };

  it("should get commits per weekday successfully", done => {
    fetchMock.getOnce(
      "/api/v2" + PATH + "/commits-per-weekday",
      commitsPerWeekday
    );

    getCommitsPerWeekday(PATH + "/commits-per-weekday").then(response => {
      expect(response).toEqual(commitsPerWeekday.weekday); // Is .weekday notation okay?
      done();
    });
  });

  it("should fail on getting commits per weekday", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/commits-per-weekday", {
      status: 500
    });

    getCommitsPerWeekday(PATH + "/commits-per-weekday").then(response => {
      expect(response.error).toBeDefined();
      done();
    });
  });

  const fileModificationCount: fileModificationCount = {
    modification: [
      {
        count: 20,
        value: "removed"
      },
      {
        count: 100,
        value: "added"
      },
      {
        count: 150,
        value: "modified"
      }
    ]
  };

  it("should get file modification count successfully", done => {
    fetchMock.getOnce(
      "/api/v2" + PATH + "/file-modification-count",
      fileModificationCount
    );

    getFileModificationCount(PATH + "/file-modification-count").then(
      response => {
        expect(response).toEqual(fileModificationCount.modification); // Is .modification notation okay?
        done();
      }
    );
  });

  it("should fail on getting file modification count", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/file-modification-count", {
      status: 500
    });

    getFileModificationCount(PATH + "/file-modification-count").then(
      response => {
        expect(response.error).toBeDefined();
        done();
      }
    );
  });

  const topModifiedFiles: topModifiedFiles = {
    file: [
      {
        count: 20,
        value: "src/foo/bar/index.html"
      },
      {
        count: 10,
        value: "src/test/file.name"
      },
      {
        count: 5,
        value: "css/style.css"
      }
    ]
  };

  it("should get top modified files successfully", done => {
    fetchMock.getOnce(
      "/api/v2" + PATH + "/top-modified-files",
      topModifiedFiles
    );

    getTopModifiedFiles(PATH + "/top-modified-files").then(response => {
      expect(response).toEqual(topModifiedFiles.file); // Is .modification notation okay?
      done();
    });
  });

  it("should fail on getting top modified files", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/top-modified-files", {
      status: 500
    });

    getTopModifiedFiles(PATH + "/top-modified-files").then(response => {
      expect(response.error).toBeDefined();
      done();
    });
  });

  const topWords: topWords = {
    word: [
      {
        count: 20,
        value: "removed"
      },
      {
        count: 100,
        value: "added"
      },
      {
        count: 150,
        value: "modified"
      }
    ]
  };

  it("should get top words successfully", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/top-words", topWords);

    getTopWords(PATH + "/top-words").then(response => {
      expect(response).toEqual(topWords.word); // Is .word notation okay?
      done();
    });
  });

  it("should fail on getting top words", done => {
    fetchMock.getOnce("/api/v2" + PATH + "/top-words", {
      status: 500
    });

    getTopWords(PATH + "/top-words").then(response => {
      expect(response.error).toBeDefined();
      done();
    });
  });
});
