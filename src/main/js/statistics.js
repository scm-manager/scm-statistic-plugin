//@flow
import { apiClient } from "@scm-manager/ui-components";

export function getLinksForStatistics(url: string){
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection._links;
    })
    .catch(err=> {
      return {error: err};
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
      return { error: err };
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
      return { error: err };
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
      return { error: err };
    });
}

export function getCommitsPerYear(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => {
      return collection.month;
    })
    .catch(err => {
      return { error: err };
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
      return { error: err };
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
      return { error: err };
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
      return { error: err };
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
      return { error: err };
    });
}
