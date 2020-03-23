/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import { apiClient } from "@scm-manager/ui-components";

export function getLinksForStatistics(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection._links;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}

export function getCommitsPerAuthor(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection.author;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}

export function getCommitsPerHour(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection.hour;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}

export function getCommitsPerMonth(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection.month;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}

export function getCommitsPerYear(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection.year;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}

export function getCommitsPerWeekday(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection.weekday;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}

export function getFileModificationCount(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection.modification;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}

export function getTopModifiedFiles(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection.file;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}

export function getTopWords(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection.word;
    })
    .catch(err => {
      return {
        error: err
      };
    });
}

export function rebuildStatistics(url: string) {
  return apiClient
    .post(url)
    .then(() => {
      return {
        success: true
      };
    })
    .catch(err => {
      return {
        error: err
      };
    });
}
