//@flow
import { apiClient } from "@scm-manager/ui-components";

export function getCommitsPerAuthor(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => collection.author)
    .then(authors => {
      return authors.map(a => a);
    })
    .catch(err => {
      return { error: err };
    });
}

export function getCommitsPerHour(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => collection.hour)
    .then(hours => {
      return hours.map(h => h);
    })
    .catch(err => {
      return { error: err };
    });
}

export function getCommitsPerMonth(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => collection.month)
    .then(months => {
      return months.map(m => m);
    })
    .catch(err => {
      return { error: err };
    });
}

export function getCommitsPerYear(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => collection.year)
    .then(years => {
      return years.map(y => y);
    })
    .catch(err => {
      return { error: err };
    });
}

export function getCommitsPerWeekday(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => collection.weekday)
    .then(weekdays => {
      return weekdays.map(d => d);
    })
    .catch(err => {
      return { error: err };
    });
}

export function getFileModificationCount(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => collection.modification)
    .then(modifications => {
      return modifications.map(m => m);
    })
    .catch(err => {
      return { error: err };
    });
}

export function getTopModifiedFiles(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => collection.file)
    .then(files => {
      return files.map(f => f);
    })
    .catch(err => {
      return { error: err };
    });
}

export function getTopWords(url: string) {
  return apiClient
    .get(url)
    .then(response => response.json())
    .then(collection => collection.word)
    .then(words => {
      return words.map(w => w);
    })
    .catch(err => {
      return { error: err };
    });
}
