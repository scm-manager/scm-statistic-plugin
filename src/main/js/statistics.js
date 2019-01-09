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
