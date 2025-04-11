/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import { apiClient } from "@scm-manager/ui-api";

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
